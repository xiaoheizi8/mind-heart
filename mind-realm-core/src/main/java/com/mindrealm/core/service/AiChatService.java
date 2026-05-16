package com.mindrealm.core.service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @className: AiChatService
 * @description: AI对话服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public interface AiChatService {

    /**
     * 同步对话
     * @param userId 用户ID
     * @param message 用户消息
     * @param persona AI人格
     * @return AI回复
     */
    String chat(Long userId, String message, String persona);

    /**
     * 异步对话
     * @param userId 用户ID
     * @param message 用户消息
     * @param persona AI人格
     * @return 异步回复结果
     */
    CompletableFuture<String> chatAsync(Long userId, String message, String persona);

    /**
     * 流式对话
     * @param userId 用户ID
     * @param message 用户消息
     * @param persona AI人格
     * @param onChunk 每个chunk的回调
     * @return 完整回复
     */
    CompletableFuture<String> chatStream(Long userId, String message, String persona, 
                                          Consumer<String> onChunk);

    /**
     * 设置AI人格
     * @param userId 用户ID
     * @param persona 人格类型
     */
    void setPersona(Long userId, String persona);

    /**
     * 获取AI人格
     * @param userId 用户ID
     * @return 人格类型
     */
    String getPersona(Long userId);

    /**
     * 清除对话历史
     * @param userId 用户ID
     */
    void clearHistory(Long userId);
}
