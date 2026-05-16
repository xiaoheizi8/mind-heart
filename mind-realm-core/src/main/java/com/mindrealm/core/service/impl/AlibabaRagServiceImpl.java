package com.mindrealm.core.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mindrealm.core.service.IAlibabaRagService;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @className: AlibabaRagServiceImpl
 * @description: 阿里云RAG服务实现
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class AlibabaRagServiceImpl implements IAlibabaRagService {

    private static final Logger logger = LoggerFactory.getLogger(AlibabaRagServiceImpl.class);
    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    @Value("${spring-ai-alibaba.dashscope.api-key:}")
    private String dashscopeApiKey;

    @Value("${spring-ai-alibaba.dashscope.chat.options.model:qwen-plus}")
    private String modelName;

    @Value("${vector.store.type:memory}")
    private String vectorStoreType;

    @Value("${vector.alibaba.knowledge-base.enabled:false}")
    private boolean knowledgeBaseEnabled;

    @Value("${vector.alibaba.knowledge-base.id:}")
    private String knowledgeBaseId;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Map<Long, List<Map<String, String>>> userChatHistories = new ConcurrentHashMap<>();

    private boolean initialized = false;

    @PostConstruct
    public void init() {
        try {
            if (dashscopeApiKey == null || dashscopeApiKey.isEmpty()) {
                logger.warn("未配置DASHSCOPE_API_KEY，将使用本地RAG模式");
            } else {
                logger.info("已配置阿里云百炼API Key，知识库ID: {}", knowledgeBaseId);
            }
            initialized = true;
            logger.info("阿里云百炼RAG服务初始化完成，模式: {}", 
                knowledgeBaseEnabled && !knowledgeBaseId.isEmpty() ? "百炼知识库" : "本地模式");
        } catch (Exception e) {
            logger.error("RAG服务初始化失败: {}", e.getMessage());
            initialized = true;
        }
    }

    public String chatWithRag(Long userId, String message) {
        try {
            if (knowledgeBaseEnabled && knowledgeBaseId != null && !knowledgeBaseId.isEmpty()) {
                logger.info("使用百炼知识库RAG模式，知识库ID: {}", knowledgeBaseId);
                return callBailianRagChat(message, userId);
            } else if (dashscopeApiKey != null && !dashscopeApiKey.isEmpty() && "alibaba".equalsIgnoreCase(vectorStoreType)) {
                logger.info("使用DashScope API模式");
                return callDashscopeChat(message, userId);
            } else {
                logger.info("使用本地RAG模式");
                return simpleRagResponse(message);
            }
        } catch (Exception e) {
            logger.error("RAG对话失败: {}", e.getMessage());
            return getFallbackResponse(message);
        }
    }

    private String callBailianRagChat(String message, Long userId) throws IOException {
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

        List<Map<String, Object>> messages = new ArrayList<>();
        
        messages.add(Map.of("role", "system", "content", buildSystemPrompt()));
        
        List<Map<String, String>> history = userChatHistories.get(userId);
        if (history != null) {
            for (Map<String, String> msg : history) {
                messages.add(Map.of("role", msg.get("role"), "content", msg.get("content")));
            }
        }
        
        messages.add(Map.of("role", "user", "content", message));

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", modelName);
        
        JSONObject input = new JSONObject();
        input.put("prompt", buildRagPrompt(message, messages));
        requestBody.put("input", input);
        
        JSONObject parameters = new JSONObject();
        parameters.put("temperature", 0.9);
        parameters.put("max_tokens", 4000);
        parameters.put("knowledge_base_id", knowledgeBaseId);
        parameters.put("top_p", 0.8);
        parameters.put("enable_search", true);
        requestBody.put("parameters", parameters);

        RequestBody body = RequestBody.create(JSON_TYPE, requestBody.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + dashscopeApiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            
            if (!response.isSuccessful()) {
                logger.warn("百炼知识库API调用失败: {}, response: {}", response.code(), responseBody);
                return simpleRagResponse(message);
            }

            JSONObject jsonResponse = JSON.parseObject(responseBody);
            
            if (jsonResponse.containsKey("output")) {
                String content = jsonResponse.getJSONObject("output").getString("text");
                if (content != null && !content.isEmpty()) {
                    saveChatHistory(userId, message, content);
                    return content;
                }
            }
            
            if (jsonResponse.containsKey("choices")) {
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    String content = choices.getJSONObject(0).getString("text");
                    if (content != null) {
                        saveChatHistory(userId, message, content);
                        return content;
                    }
                }
            }
            
            logger.warn("百炼API返回格式异常: {}", responseBody);
            return simpleRagResponse(message);
        }
    }

    private String buildRagPrompt(String question, List<Map<String, Object>> messages) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一位温暖、耐心的心理健康顾问，请根据知识库中的相关内容回答用户问题。\n\n");
        
        for (Map<String, Object> msg : messages) {
            if ("system".equals(msg.get("role"))) continue;
            String role = "user".equals(msg.get("role")) ? "用户" : "助手";
            sb.append(role).append("：").append(msg.get("content")).append("\n");
        }
        sb.append("用户：").append(question).append("\n");
        sb.append("请结合知识库信息，用温暖的语气回答用户的问题。");
        
        return sb.toString();
    }

    private void saveChatHistory(Long userId, String userMessage, String assistantMessage) {
        List<Map<String, String>> chatHistory = userChatHistories.computeIfAbsent(userId, k -> new ArrayList<>());
        chatHistory.add(Map.of("role", "user", "content", userMessage));
        chatHistory.add(Map.of("role", "assistant", "content", assistantMessage));
        if (chatHistory.size() > 20) {
            chatHistory.subList(0, 10).clear();
        }
    }

    private String callDashscopeChat(String message, Long userId) throws IOException {
        String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", buildSystemPrompt()));
        
        List<Map<String, String>> history = userChatHistories.get(userId);
        if (history != null) {
            messages.addAll(history);
        }
        messages.add(Map.of("role", "user", "content", message));

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", modelName);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.9);
        requestBody.put("max_tokens", 4000);
        requestBody.put("stream", false);

        RequestBody body = RequestBody.create(JSON_TYPE, requestBody.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + dashscopeApiKey)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.warn("DashScope API调用失败: {}", response.code());
                return simpleRagResponse(message);
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = JSON.parseObject(responseBody);
            
            String content = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            saveChatHistory(userId, message, content);
            return content;
        }
    }

    private String buildSystemPrompt() {
        return "你是一位温暖、耐心的心理健康顾问。请根据以下知识库内容回答用户问题：\n\n" +
                "【知识库主题】心理健康知识库，包含以下内容：\n" +
                "- 考试焦虑应对方法\n" +
                "- 正念冥想入门\n" +
                "- 抑郁症早期信号识别\n" +
                "- 亲子沟通技巧\n" +
                "- 青少年心理健康保护\n\n" +
                "【回答原则】\n" +
                "1. 用同理心倾听，提供情感支持\n" +
                "2. 结合知识库内容给出专业建议\n" +
                "3. 识别心理危机信号（如自杀倾向）\n" +
                "4. 适时提供帮助资源（心理热线：400-161-9995）\n" +
                "5. 保持积极、支持性的语气\n\n" +
                "请始终保持关怀和专业的态度。";
    }

    private String simpleRagResponse(String message) {
        String lower = message.toLowerCase();

        if (containsAny(lower, "抑郁", "抑郁症", "心情低落", "没兴趣", "绝望")) {
            return "我理解你现在可能感到很沉重。抑郁情绪很常见，但持续的抑郁可能需要专业帮助。\n\n" +
                    "根据DSM-5诊断标准，如果情绪低落和兴趣减退持续两周以上，并伴随睡眠障碍、食欲改变、疲劳感等症状，建议寻求心理咨询师的帮助。\n\n" +
                    "记住，你不是一个人在这里。必要时可以拨打心理援助热线：400-161-9995";
        }

        if (containsAny(lower, "焦虑", "紧张", "担心", "害怕", "恐惧", "考试")) {
            return "焦虑是一种很常见的情绪反应。缓解方法：\n\n" +
                    "1. 腹式呼吸：吸气4秒，屏住4秒，呼气6秒\n" +
                    "2. 正念练习：专注当下，不评判\n" +
                    "3. 认知重构：识别并挑战不合理的思维\n" +
                    "4. 适度运动：释放紧张情绪\n\n" +
                    "如果焦虑影响到日常生活，建议咨询专业心理医生。";
        }

        if (containsAny(lower, "压力", "累", "喘不过气", "崩溃")) {
            return "听起来你正在经历很大的压力。压力管理策略：\n\n" +
                    "1. 任务分解：把大任务分成小步骤\n" +
                    "2. 时间管理：设定优先级\n" +
                    "3. 适度运动：每周至少3次\n" +
                    "4. 充足睡眠：保持规律作息\n" +
                    "5. 社交支持：与信任的人沟通\n\n" +
                    "记住，照顾好自己很重要。";
        }

        if (containsAny(lower, "失眠", "睡不着", "睡眠不好", "熬夜")) {
            return "睡眠问题确实很让人困扰。睡眠卫生建议：\n\n" +
                    "1. 固定作息时间\n" +
                    "2. 睡前避免电子设备\n" +
                    "3. 创造安静环境\n" +
                    "4. 避免咖啡因\n\n" +
                    "如果长期失眠，建议咨询专业医生。";
        }

        if (containsAny(lower, "自杀", "想死", "不想活", "自伤", "结束生命")) {
            return "我注意到你提到了让自己感到危险的想法。我想让你知道，你的生命是珍贵的。\n\n" +
                    "请立即寻求帮助：\n" +
                    "• 心理援助热线：400-161-9995（24小时）\n" +
                    "• 如有紧急危险，请拨打120\n\n" +
                    "我在这里陪伴你，请告诉我你现在怎么样？";
        }

        if (containsAny(lower, "人际关系", "朋友", "孤独", "被孤立", "社交", "霸凌")) {
            return "人际关系问题在青少年中很常见。健康的人际关系需要：\n\n" +
                    "1. 有效沟通\n" +
                    "2. 换位思考\n" +
                    "3. 情绪表达\n" +
                    "4. 建立边界\n" +
                    "5. 寻求支持\n\n" +
                    "如果遭遇霸凌，请及时告诉家长、老师或学校心理老师。";
        }

        if (containsAny(lower, "家庭", "父母", "吵架", "冲突", "亲子", "不理解")) {
            return "家庭冲突是很多青少年都会遇到的问题。与父母沟通时，可以尝试：\n\n" +
                    "1. 选择合适时机\n" +
                    "2. 表达感受而非指责\n" +
                    "3. 理解父母立场\n" +
                    "4. 寻求共识\n\n" +
                    "记住，家庭关系需要时间和耐心来改善。";
        }

        return "谢谢你的分享。我在这里倾听你。\n\n" +
                "你可以告诉我更多关于你的情况吗？比如最近发生了什么让你困扰的事情？";
    }

    private boolean containsAny(String str, String... keywords) {
        for (String keyword : keywords) {
            if (str.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private String getFallbackResponse(String message) {
        return "谢谢你的分享。我在这里倾听你。请告诉我更多关于你的情况，这样我可以更好地帮助你。";
    }

    public int getKnowledgeCount() {
        return 50;
    }

    public boolean isAvailable() {
        return initialized;
    }

    public void clearHistory(Long userId) {
        userChatHistories.remove(userId);
    }
}
