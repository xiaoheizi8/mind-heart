package com.mindrealm.core.rag.strategy;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mindrealm.core.rag.config.RagProperties;
import com.mindrealm.core.rag.model.RagRequest;
import com.mindrealm.core.rag.model.RagResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BailianKnowledgeStrategy implements RagStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BailianKnowledgeStrategy.class);
    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private final RagProperties.BailianConfig config;
    private final OkHttpClient httpClient = new OkHttpClient();
    private final Map<Long, List<Map<String, String>>> userChatHistories = new ConcurrentHashMap<>();

    private volatile boolean initialized = false;

    public BailianKnowledgeStrategy(RagProperties.BailianConfig config) {
        this.config = config;
    }

    /**
     * 初始化方法,由RagStrategyFactory手动调用(因为不是Spring管理的Bean)
     */
    public void init() {
        try {
            if (config.getApiKey() == null || config.getApiKey().isEmpty()) {
                logger.warn("百炼知识库 API Key 未配置");
                return;
            }
            
            if (config.getKnowledgeBaseId() == null || config.getKnowledgeBaseId().isEmpty()) {
                logger.warn("百炼知识库 ID 未配置");
                return;
            }

            logger.info("百炼知识库策略初始化完成，知识库ID: {}", config.getKnowledgeBaseId());
            initialized = true;
        } catch (Exception e) {
            logger.error("百炼知识库策略初始化失败: {}", e.getMessage());
        }
    }

    @Override
    public String getMode() {
        return config.isUseKnowledgeBase() ? "BAILIAN_KNOWLEDGE" : "BAILIAN_API";
    }

    @Override
    public boolean isAvailable() {
        return initialized && config.isEnabled();
    }

    @Override
    public RagResponse chat(RagRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            List<Map<String, Object>> messages = buildMessages(request);
            
            JSONObject requestBody = buildRequestBody(messages, request);
            
            String responseBody = callApi(requestBody);
            
            if (responseBody == null) {
                return RagResponse.error("API 调用失败");
            }

            String answer = parseResponse(responseBody);
            
            saveChatHistory(request.getUserId(), request.getQuestion(), answer);
            
            long latency = System.currentTimeMillis() - startTime;
            
            return RagResponse.builder()
                    .success(true)
                    .answer(answer)
                    .mode(getMode())
                    .modelInfo(RagResponse.ModelInfo.builder()
                            .model(config.getModel())
                            .latencyMs(latency)
                            .build())
                    .build();
                    
        } catch (Exception e) {
            logger.error("百炼知识库对话失败: {}", e.getMessage());
            return RagResponse.error("服务暂时不可用: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> buildMessages(RagRequest request) {
        List<Map<String, Object>> messages = new ArrayList<>();
        
        messages.add(Map.of("role", "system", "content", buildSystemPrompt()));
        
        List<RagRequest.ChatMessage> history = request.getHistory();
        if (history != null) {
            for (RagRequest.ChatMessage msg : history) {
                messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
            }
        }
        
        messages.add(Map.of("role", "user", "content", request.getQuestion()));
        
        return messages;
    }

    private String buildSystemPrompt() {
        return "你是一位温暖、耐心的心理健康顾问。请根据知识库中的相关内容回答用户问题。\n\n" +
                "【回答原则】\n" +
                "1. 用同理心倾听，提供情感支持\n" +
                "2. 结合知识库内容给出专业建议\n" +
                "3. 识别心理危机信号（如自杀倾向）\n" +
                "4. 适时提供帮助资源（心理热线：400-161-9995）\n" +
                "5. 保持积极、支持性的语气\n\n" +
                "请始终保持关怀和专业的态度。";
    }

    private JSONObject buildRequestBody(List<Map<String, Object>> messages, RagRequest request) {
        JSONObject requestBody = new JSONObject();
        
        if (config.isUseKnowledgeBase()) {
            requestBody.put("model", "qwen-omni-turbo");
            
            JSONObject input = new JSONObject();
            StringBuilder prompt = new StringBuilder();
            prompt.append("你是一位温暖、耐心的心理健康顾问，请根据知识库中的相关内容回答用户问题。\n\n");
            
            for (Map<String, Object> msg : messages) {
                if ("system".equals(msg.get("role"))) continue;
                String role = "user".equals(msg.get("role")) ? "用户" : "助手";
                prompt.append(role).append("：").append(msg.get("content")).append("\n");
            }
            prompt.append("用户：").append(request.getQuestion());
            
            input.put("prompt", prompt.toString());
            requestBody.put("input", input);
            
            JSONObject parameters = new JSONObject();
            parameters.put("knowledge_base_ids", Collections.singletonList(config.getKnowledgeBaseId()));
            parameters.put("search_threshold", 0.5);
            parameters.put("search_topk", 3);
            parameters.put("temperature", config.getTemperature());
            parameters.put("max_tokens", config.getMaxTokens());
            requestBody.put("parameters", parameters);
        } else {
            requestBody.put("model", config.getModel());
            requestBody.put("messages", messages);
            requestBody.put("temperature", config.getTemperature());
            requestBody.put("max_tokens", config.getMaxTokens());
            requestBody.put("stream", false);
        }
        
        return requestBody;
    }

    private String callApi(JSONObject requestBody) throws IOException {
        String url;
        if (config.isUseKnowledgeBase()) {
            url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
        } else {
            url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
        }

        RequestBody body = RequestBody.create(JSON_TYPE, requestBody.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + config.getApiKey())
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.warn("百炼 API 调用失败: {}", response.code());
                return null;
            }
            return response.body().string();
        }
    }

    private String parseResponse(String responseBody) {
        JSONObject jsonResponse = JSON.parseObject(responseBody);
        
        if (config.isUseKnowledgeBase()) {
            if (jsonResponse.containsKey("output")) {
                return jsonResponse.getJSONObject("output").getString("text");
            }
        } else {
            if (jsonResponse.containsKey("choices")) {
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    return choices.getJSONObject(0).getJSONObject("message").getString("content");
                }
            }
        }
        
        return "抱歉，服务暂时无法响应。";
    }

    private void saveChatHistory(Long userId, String userMessage, String assistantMessage) {
        if (userId == null) return;
        
        List<Map<String, String>> chatHistory = userChatHistories.computeIfAbsent(userId, k -> new ArrayList<>());
        chatHistory.add(Map.of("role", "user", "content", userMessage));
        chatHistory.add(Map.of("role", "assistant", "content", assistantMessage));
        
        if (chatHistory.size() > 20) {
            chatHistory.subList(0, 10).clear();
        }
    }

    @Override
    public void clearHistory(Long userId) {
        userChatHistories.remove(userId);
    }

    @Override
    public int getKnowledgeCount() {
        return config.isUseKnowledgeBase() ? 50 : 0;
    }
}
