package com.mindrealm.api.controller;

import com.mindrealm.api.dto.response.ChatResponse;
import com.mindrealm.api.dto.response.PersonaResponse;
import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.util.ValidatorUtil;
import com.mindrealm.core.service.AiChatService;
import com.mindrealm.warning.service.WarningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @className: ChatController
 * @description: AI对话控制器,支持同步/异步/流式输出
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    // 跟踪用户活跃请求,防止重复提交
    private static final Map<Long, AtomicBoolean> activeRequests = new ConcurrentHashMap<>();

    private final AiChatService aiChatService;
    private final WarningService warningService;

    @Autowired
    public ChatController(AiChatService aiChatService, WarningService warningService) {
        this.aiChatService = aiChatService;
        this.warningService = warningService;
    }

    /**
     * 发送消息给AI（同步接口）
     */
    @PostMapping("/send")
    public Result<ChatResponse> send(@RequestParam(required = false) String message,
                                      @RequestParam(defaultValue = "listener") String persona,
                                      @RequestBody(required = false) Map<String, String> body) {
        // 优先从请求体获取
        if (message == null && body != null) {
            message = body.get("message");
        }
        if (body != null && body.containsKey("persona")) {
            persona = body.get("persona");
        }

        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }

        if (ValidatorUtil.isEmpty(message)) {
            return Result.badRequest("消息不能为空");
        }

        // 风险预警检测
        warningService.analyzeRisk(userId, message);

        // AI生成回复
        String response = aiChatService.chat(userId, message, persona);

        return Result.success(ChatResponse.builder().reply(response).build());
    }

    /**
     * 发送消息给AI（异步接口）
     * 适用于前端需要异步处理的场景
     */
    @PostMapping("/send-async")
    public CompletableFuture<Result<ChatResponse>> sendAsync(
            @RequestParam(required = false) String message,
            @RequestParam(defaultValue = "listener") String persona,
            @RequestBody(required = false) Map<String, String> body) {

        if (message == null && body != null) {
            message = body.get("message");
        }
        if (body != null && body.containsKey("persona")) {
            persona = body.get("persona");
        }

        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return CompletableFuture.completedFuture(Result.unauthorized());
        }

        if (ValidatorUtil.isEmpty(message)) {
            return CompletableFuture.completedFuture(Result.badRequest("消息不能为空"));
        }

        final String finalMessage = message;
        final String finalPersona = persona;

        // 风险预警检测
        warningService.analyzeRisk(userId, message);

        // 异步AI对话
        return aiChatService.chatAsync(userId, message, persona)
                .thenApply(response -> Result.success(ChatResponse.builder().reply(response).build()))
                .exceptionally(e -> {
                    log.error("异步对话失败: userId={}, error={}", userId, e.getMessage());
                    return Result.error("对话服务暂时不可用");
                });
    }

    /**
     * 发送消息给AI(流式输出)
     * 使用Server-Sent Events (SSE)实现实时流式输出
     * 使用CompletableFuture处理异步请求,避免Tomcat异步分派的Security问题
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public CompletableFuture<SseEmitter> stream(
            @RequestParam(required = false) String message,
            @RequestParam(defaultValue = "listener") String persona,
            @RequestBody(required = false) Map<String, String> body) {
    
        if (message == null && body != null) {
            message = body.get("message");
        }
        if (body != null && body.containsKey("persona")) {
            persona = body.get("persona");
        }
    
        Long userId = RequestContext.getCurrentUserId();
    
        if (userId == null) {
            SseEmitter emitter = new SseEmitter();
            try {
                emitter.send(SseEmitter.event().name("error").data("{\"error\":\"未授权访问\"}"));
                emitter.complete();
            } catch (Exception e) {
                // ignore
            }
            return CompletableFuture.completedFuture(emitter);
        }
    
        // 防止同一用户重复提交
        AtomicBoolean isActive = activeRequests.computeIfAbsent(userId, k -> new AtomicBoolean(false));
        if (!isActive.compareAndSet(false, true)) {
            log.warn("用户 {} 已有正在处理的流式请求,拒绝重复提交", userId);
            SseEmitter emitter = new SseEmitter();
            try {
                emitter.send(SseEmitter.event().name("error").data("{\"error\":\"请勿重复提交，请等待当前对话完成\"}"));
                emitter.complete();
            } catch (Exception e) {
                // ignore
            }
            return CompletableFuture.completedFuture(emitter);
        }
    
        if (ValidatorUtil.isEmpty(message)) {
            activeRequests.remove(userId);  // 释放锁
            SseEmitter emitter = new SseEmitter();
            try {
                emitter.send(SseEmitter.event().name("error").data("{\"error\":\"消息不能为空\"}"));
                emitter.complete();
            } catch (Exception e) {
                // ignore
            }
            return CompletableFuture.completedFuture(emitter);
        }
    
        final String finalMessage = message;
        final String finalPersona = persona;
    
        // 创建SSE发射器,30分钟超时(0表示无超时)
        SseEmitter emitter = new SseEmitter(0L);
    
        // 使用CompletableFuture异步处理,完全避免Tomcat异步分派
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 风险预警检测
                warningService.analyzeRisk(userId, finalMessage);
    
                // 发送开始事件
                emitter.send(SseEmitter.event().name("start").data("{\"status\":\"started\"}"));
    
                // 流式对话
                AtomicBoolean connectionAlive = new AtomicBoolean(true);
    
                // 异步执行流式对话,不阻塞主线程
                aiChatService.chatStream(userId, finalMessage, finalPersona, chunk -> {
                    if (!connectionAlive.get()) {
                        return; // 连接已断开,停止发送
                    }
                    try {
                        log.debug("发送chunk: {}", chunk.length());
                        emitter.send(SseEmitter.event().name("chunk").data("{\"content\":\"" + escapeJson(chunk) + "\"}"));
                    } catch (Exception e) {
                        log.error("发送chunk失败: {}", e.getMessage());
                        connectionAlive.set(false); // 标记连接已断开
                    }
                }).whenComplete((result, error) -> {
                    // 流式完成后执行
                    if (error != null) {
                        log.error("流式对话异常: userId={}, error={}", userId, error.getMessage());
                        try {
                            emitter.send(SseEmitter.event().name("error").data("{\"error\":\"对话服务异常\"}"));
                        } catch (Exception ex) {
                            // ignore
                        }
                    } else {
                        // 发送完成事件
                        if (connectionAlive.get()) {
                            try {
                                emitter.send(SseEmitter.event().name("done").data("{\"status\":\"completed\"}"));
                            } catch (Exception e) {
                                log.error("发送done事件失败: {}", e.getMessage());
                            }
                        }
                        emitter.complete();
                        log.info("✅ 流式对话完成: userId={}", userId);
                    }
                    // 释放用户请求锁
                    activeRequests.remove(userId);
                });
    
                // 立即返回emitter,不阻塞

            } catch (Exception e) {
                log.error("流式对话异常: userId={}, error={}", userId, e.getMessage());
                try {
                    emitter.send(SseEmitter.event().name("error").data("{\"error\":\"对话服务异常\"}"));
                } catch (Exception ex) {
                    // ignore
                }
                emitter.complete();
            } finally {
                // 释放用户请求锁,允许下一次请求
                activeRequests.remove(userId);
                log.debug("释放用户 {} 的流式请求锁", userId);
            }
            return emitter;
        });
    }

    /**
     * 转换为JSON chunk格式
     */
    private String toJsonChunk(String content) {
        return "{\"content\":\"" + escapeJson(content) + "\"}";
    }

    /**
     * 转换为JSON done格式
     */
    private String toJsonDone(String fullContent) {
        return "{\"status\":\"completed\",\"length\":" + fullContent.length() + "}";
    }

    /**
     * 转换为JSON error格式
     */
    private String toJsonError(String message) {
        return "{\"error\":\"" + escapeJson(message) + "\"}";
    }

    /**
     * 转义JSON字符串
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * 设置AI人格
     */
    @PostMapping("/persona")
    public Result<Void> setPersona(@RequestParam(required = false) String persona,
                                            @RequestBody(required = false) Map<String, String> body) {
        if (persona == null && body != null) {
            persona = body.get("persona");
        }

        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }

        if (ValidatorUtil.isEmpty(persona)) {
            return Result.badRequest("人格类型不能为空");
        }

        aiChatService.setPersona(userId, persona);

        return Result.ok("人格设置成功");
    }

    /**
     * 获取当前AI人格
     */
    @GetMapping("/persona")
    public Result<PersonaResponse> getPersona() {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }

        String persona = aiChatService.getPersona(userId);

        return Result.success(PersonaResponse.builder().persona(persona).build());
    }

    /**
     * 清除对话历史
     */
    @DeleteMapping("/history")
    public Result<Void> clearHistory() {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }

        aiChatService.clearHistory(userId);

        return Result.ok("对话历史已清除");
    }
}
