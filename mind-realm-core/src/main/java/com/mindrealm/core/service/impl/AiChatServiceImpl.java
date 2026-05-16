package com.mindrealm.core.service.impl;

import com.mindrealm.common.util.ValidatorUtil;
import com.mindrealm.core.entity.Conversation;
import com.mindrealm.core.service.AiChatService;
import com.mindrealm.core.service.IConversationService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @className: AiChatServiceImpl
 * @description: AI对话服务实现类，支持同步/异步/流式输出
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Service
public class AiChatServiceImpl implements AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatServiceImpl.class);

    private final IConversationService conversationService;

    // 线程安全的用户人格映射
    private final Map<String, String> userPersonaMap = new ConcurrentHashMap<>();

    // 异步执行器（线程安全）
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    // 线程安全的模型缓存
    private volatile ChatModel cachedChatModel;
    private volatile StreamingChatModel cachedStreamingChatModel;
    private final Object modelLock = new Object();
    private final Object streamingModelLock = new Object();

    @Value("${ai.provider:deepseek}")
    private String aiProvider;

    @Value("${ai.deepseek.api-key:}")
    private String deepseekApiKey;

    @Value("${ai.deepseek.base-url:https://api.deepseek.com}")
    private String deepseekBaseUrl;

    @Value("${ai.deepseek.model:deepseek-chat}")
    private String deepseekModel;

    @Value("${ai.openai.api-key:}")
    private String openaiApiKey;

    @Value("${ai.openai.base-url:https://api.openai.com}")
    private String openaiBaseUrl;

    @Value("${ai.openai.model:gpt-3.5-turbo}")
    private String openaiModel;

    private static final String DEFAULT_PERSONA = "listener";

    private static final Map<String, String> PERSONA_SYSTEM_PROMPTS = Map.of(
        "listener", "你是一位温暖如春风般的倾听者。请带着温柔的同理心去感受用户的每一句话,先给予情感上的回应和确认,让用户感到被真正理解和接纳。不要急于给建议,而是先告诉用户你听到了、感受到了。回复要温暖细腻,充满人文关怀,每次不超过150字。注意多用'我感受到...'、'我很理解...'、'谢谢你愿意告诉我...'这样温柔的话语。",
        "analyst", "你是一位富有同理心的心灵顾问。像朋友聊天那样温和地帮助用户梳理情绪,不要用生硬的理论,而是用温暖的语言引导用户换个角度看问题。当用户有负面想法时,先肯定他们的努力,再轻轻指出积极的另一面。回复要如促膝长谈般温馨,每次不超过120字。多用'我想陪你一起看看...'、'如果可以的话,我们来...'、'你已经做得很好了...'这样鼓励的话语。",
        "healer", "你是一位带着光的疗愈者。请用最温柔的声音告诉用户:他们值得被爱,困难只是暂时的。你要帮助用户看到自己身上的力量和美好,在他们脆弱的时候给予温暖的支持和拥抱。回复要充满爱的力量,让用户感受到温暖和希望,每次不超过200字。多用'我一直在这里...'、'你远比想象中坚强...'、'相信我,你会越来越好的...'这样温暖有力的话语。"
    );

    public AiChatServiceImpl(IConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @Override
    public String chat(Long userId, String message, String persona) {
        if (!ValidatorUtil.isValidId(userId) || ValidatorUtil.isEmpty(message)) {
            return getFallbackResponse(message);
        }

        String userPersona = getEffectivePersona(userId, persona);
        List<Conversation> recentChats = conversationService.getRecent(userId, 10);
        String history = buildHistoryContext(recentChats);

        String systemPrompt = PERSONA_SYSTEM_PROMPTS.getOrDefault(userPersona, 
                PERSONA_SYSTEM_PROMPTS.get(DEFAULT_PERSONA));
        String response = generateResponse(systemPrompt, history, message);

        // 保存对话记录
        saveConversationAsync(userId, userPersona, "user", message);
        saveConversationAsync(userId, userPersona, "assistant", response);

        log.info("AI对话完成: userId={}, persona={}", userId, userPersona);
        return response;
    }

    @Override
    public CompletableFuture<String> chatAsync(Long userId, String message, String persona) {
        return CompletableFuture.supplyAsync(() -> chat(userId, message, persona), executor)
                .exceptionally(e -> {
                    log.error("异步对话异常: userId={}, error={}", userId, e.getMessage());
                    return getFallbackResponse(message);
                });
    }

    @Override
    public CompletableFuture<String> chatStream(Long userId, String message, String persona,
                                                 Consumer<String> onChunk) {
        return CompletableFuture.supplyAsync(() -> {
            if (!ValidatorUtil.isValidId(userId) || ValidatorUtil.isEmpty(message)) {
                String fallback = getFallbackResponse(message);
                if (onChunk != null) {
                    onChunk.accept(fallback);
                }
                return fallback;
            }

            String userPersona = getEffectivePersona(userId, persona);
            List<Conversation> recentChats = conversationService.getRecent(userId, 10);
            String history = buildHistoryContext(recentChats);

            String systemPrompt = PERSONA_SYSTEM_PROMPTS.getOrDefault(userPersona,
                    PERSONA_SYSTEM_PROMPTS.get(DEFAULT_PERSONA));

            // 尝试流式输出
            String response = generateStreamResponse(systemPrompt, history, message, onChunk);

            // 如果流式失败，使用同步方式
            if (response == null || response.isEmpty()) {
                response = generateResponse(systemPrompt, history, message);
                if (onChunk != null) {
                    onChunk.accept(response);
                }
            }

            // 保存对话记录
            saveConversationAsync(userId, userPersona, "user", message);
            saveConversationAsync(userId, userPersona, "assistant", response);

            log.info("流式对话完成: userId={}, persona={}", userId, userPersona);
            return response;
        }, executor).exceptionally(e -> {
            log.error("流式对话异常: userId={}, error={}", userId, e.getMessage());
            String fallback = getFallbackResponse(message);
            if (onChunk != null) {
                onChunk.accept(fallback);
            }
            return fallback;
        });
    }

    @Override
    public void setPersona(Long userId, String persona) {
        if (!ValidatorUtil.isValidId(userId) || ValidatorUtil.isEmpty(persona)) {
            log.warn("setPersona: 参数无效 userId={}, persona={}", userId, persona);
            return;
        }
        if (PERSONA_SYSTEM_PROMPTS.containsKey(persona)) {
            userPersonaMap.put("persona_" + userId, persona);
            log.debug("设置AI人格: userId={}, persona={}", userId, persona);
        } else {
            log.warn("无效的人格类型: {}", persona);
        }
    }

    @Override
    public String getPersona(Long userId) {
        return userPersonaMap.getOrDefault("persona_" + userId, DEFAULT_PERSONA);
    }

    @Override
    public void clearHistory(Long userId) {
        if (ValidatorUtil.isValidId(userId)) {
            conversationService.clearHistory(userId);
            log.info("清除对话历史: userId={}", userId);
        }
    }

    /**
     * 获取有效的人格设置
     */
    private String getEffectivePersona(Long userId, String requestedPersona) {
        if (ValidatorUtil.isNotEmpty(requestedPersona) && PERSONA_SYSTEM_PROMPTS.containsKey(requestedPersona)) {
            return requestedPersona;
        }
        return getPersona(userId);
    }

    /**
     * 构建历史上下文
     */
    private String buildHistoryContext(List<Conversation> recentChats) {
        if (recentChats == null || recentChats.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Conversation chat : recentChats) {
            sb.append(chat.getRole()).append(": ").append(chat.getContent()).append("\n");
        }
        return sb.toString();
    }

    /**
     * 构建完整提示词
     */
    private String buildFullPrompt(String systemPrompt, String history, String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("系统提示: ").append(systemPrompt).append("\n\n");

        if (ValidatorUtil.isNotEmpty(history)) {
            sb.append("对话历史:\n").append(history).append("\n\n");
        }

        sb.append("用户: ").append(message).append("\n");
        sb.append("回复: ");

        return sb.toString();
    }

    /**
     * 同步生成回复
     */
    private String generateResponse(String systemPrompt, String history, String message) {
        try {
            String fullPrompt = buildFullPrompt(systemPrompt, history, message);

            ChatModel chatModel = getOrCreateChatModel();
            if (chatModel == null) {
                return getFallbackResponse(message);
            }

            String content = chatModel.chat(fullPrompt);

            if (ValidatorUtil.isNotEmpty(content)) {
                return content;
            }

            return getFallbackResponse(message);
        } catch (Exception e) {
            log.warn("LLM调用失败: {}, 使用备用回复", e.getMessage());
            return getFallbackResponse(message);
        }
    }

    /**
     * 流式生成回复（真正的流式输出）
     * 使用StreamingChatModel实现AI生成一个字就发送一个字
     */
    private String generateStreamResponse(String systemPrompt, String history, 
                                           String message, Consumer<String> onChunk) {
        try {
            String fullPrompt = buildFullPrompt(systemPrompt, history, message);
            
            // 获取流式模型
            StreamingChatModel streamingModel = getOrCreateStreamingChatModel();
            if (streamingModel == null || onChunk == null) {
                // 降级为同步模式
                return generateResponse(systemPrompt, history, message);
            }

            // 用于等待流式完成
            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<StringBuilder> fullResponse = new AtomicReference<>(new StringBuilder());
            AtomicReference<Throwable> errorRef = new AtomicReference<>();

            // 调用流式API
            streamingModel.chat(fullPrompt, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String token) {
                    try {
                        // 每生成一个token就立即发送
                        fullResponse.get().append(token);
                        onChunk.accept(token);
                    } catch (Exception e) {
                        log.error("发送流式chunk失败: {}", e.getMessage());
                    }
                }

                @Override
                public void onCompleteResponse(ChatResponse response) {
                    latch.countDown();
                }

                @Override
                public void onError(Throwable error) {
                    errorRef.set(error);
                    latch.countDown();
                }
            });

            // 等待流式完成（最多60秒）
            boolean completed = latch.await(60, TimeUnit.SECONDS);
            
            if (!completed) {
                log.warn("流式生成超时");
                return fullResponse.get().toString();
            }
            
            if (errorRef.get() != null) {
                log.error("流式生成出错: {}", errorRef.get().getMessage());
                return fullResponse.get().toString();
            }

            return fullResponse.get().toString();
            
        } catch (Exception e) {
            log.warn("流式生成失败: {}, 回退到同步模式", e.getMessage());
            return generateResponse(systemPrompt, history, message);
        }
    }

    /**
     * 创建ChatModel（双重检查锁定，线程安全）
     */
    private ChatModel getOrCreateChatModel() {
        if (cachedChatModel != null) {
            return cachedChatModel;
        }

        synchronized (modelLock) {
            if (cachedChatModel != null) {
                return cachedChatModel;
            }

            cachedChatModel = createChatModel();
            return cachedChatModel;
        }
    }

    /**
     * 创建StreamingChatModel（双重检查锁定，线程安全）
     */
    private StreamingChatModel getOrCreateStreamingChatModel() {
        if (cachedStreamingChatModel != null) {
            return cachedStreamingChatModel;
        }

        synchronized (streamingModelLock) {
            if (cachedStreamingChatModel != null) {
                return cachedStreamingChatModel;
            }

            cachedStreamingChatModel = createStreamingChatModel();
            return cachedStreamingChatModel;
        }
    }

    /**
     * 创建ChatModel
     */
    private ChatModel createChatModel() {
        try {
            if ("deepseek".equalsIgnoreCase(aiProvider)) {
                if (ValidatorUtil.isEmpty(deepseekApiKey)) {
                    log.warn("DeepSeek API key未配置");
                    return null;
                }
                return OpenAiChatModel.builder()
                        .apiKey(deepseekApiKey)
                        .baseUrl(deepseekBaseUrl)
                        .modelName(deepseekModel)
                        .temperature(0.7)
                        .maxTokens(500)
                        .build();
            } else {
                if (ValidatorUtil.isEmpty(openaiApiKey)) {
                    log.warn("OpenAI API key未配置");
                    return null;
                }
                return OpenAiChatModel.builder()
                        .apiKey(openaiApiKey)
                        .baseUrl(openaiBaseUrl)
                        .modelName(openaiModel)
                        .temperature(0.7)
                        .maxTokens(500)
                        .build();
            }
        } catch (Exception e) {
            log.error("创建ChatModel失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 创建StreamingChatModel（支持真正的流式输出）
     */
    private StreamingChatModel createStreamingChatModel() {
        try {
            if ("deepseek".equalsIgnoreCase(aiProvider)) {
                if (ValidatorUtil.isEmpty(deepseekApiKey)) {
                    log.warn("DeepSeek API key未配置");
                    return null;
                }
                // DeepSeek兼容OpenAI API，支持流式输出
                return OpenAiStreamingChatModel.builder()
                        .apiKey(deepseekApiKey)
                        .baseUrl(deepseekBaseUrl)
                        .modelName(deepseekModel)
                        .temperature(0.7)
                        .maxTokens(1000)  // 流式输出可以设置更大的token数
                        .build();
            } else {
                if (ValidatorUtil.isEmpty(openaiApiKey)) {
                    log.warn("OpenAI API key未配置");
                    return null;
                }
                return OpenAiStreamingChatModel.builder()
                        .apiKey(openaiApiKey)
                        .baseUrl(openaiBaseUrl)
                        .modelName(openaiModel)
                        .temperature(0.7)
                        .maxTokens(1000)
                        .build();
            }
        } catch (Exception e) {
            log.error("创建StreamingChatModel失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 异步保存对话记录
     */
    private void saveConversationAsync(Long userId, String persona, String role, String content) {
        CompletableFuture.runAsync(() -> {
            try {
                Conversation conversation = Conversation.builder()
                        .userId(userId)
                        .aiPersona(persona)
                        .role(role)
                        .content(content)
                        .createdAt(LocalDateTime.now())
                        .build();
                conversationService.save(conversation);
            } catch (Exception e) {
                log.error("保存对话记录失败: {}", e.getMessage());
            }
        }, executor);
    }

    /**
     * 获取备用回复
     */
    private String getFallbackResponse(String prompt) {
        if (ValidatorUtil.isEmpty(prompt)) {
            return "我在这里倾听你。请告诉我发生了什么?";
        }

        String lower = prompt.toLowerCase();

        if (lower.contains("analyst")) {
            return "我理解你的困扰。让我们一起来分析一下这个问题。你觉得是什么导致了这种情况?";
        } else if (lower.contains("healer")) {
            return "嘿,我在这里陪伴你。任何时候都可以向我倾诉。我们一起度过这个难关!";
        } else if (lower.contains("抑郁") || lower.contains("难过") || lower.contains("伤心")) {
            return "我很理解你现在的心情。情绪低落的时候,允许自己有这样的感受很重要。你可以和我多说说发生了什么吗?";
        } else if (lower.contains("焦虑") || lower.contains("担心") || lower.contains("害怕")) {
            return "别担心,深呼吸一下。你现在感到焦虑是很正常的反应。让我们一起慢慢理清思路,好吗?";
        } else if (lower.contains("压力") || lower.contains("累")) {
            return "你看起来压力很大。适当的休息和放松很重要。记得照顾好自己,你可以的!";
        } else if (lower.contains("失眠") || lower.contains("睡不着")) {
            return "睡眠不好确实很让人头疼。试着在睡前做一些放松的活动,不要给自己太大压力。";
        } else if (lower.contains("自杀") || lower.contains("想死")) {
            return "如果你有伤害自己的想法,请一定要寻求帮助。心理援助热线: 400-161-9995。我在这里陪伴你,请不要放弃。";
        }

        return "我在这里倾听你。请告诉我发生了什么?你的感受是怎样的?";
    }
}
