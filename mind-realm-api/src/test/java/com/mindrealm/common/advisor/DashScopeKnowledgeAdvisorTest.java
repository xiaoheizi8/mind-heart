package com.mindrealm.common.advisor;

import com.mindrealm.MindRealmApplication;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * 测试百炼知识库Advisor与ChatClient的集成
 */
@Slf4j
@SpringBootTest(classes = {MindRealmApplication.class, DashScopeKnowledgeAdvisorTest.TestConfig.class})
class DashScopeKnowledgeAdvisorTest {

    @Configuration
    static class TestConfig {
        @Bean
        public ChatClient chatClient(ChatClient.Builder chatClientBuilder, Advisor knowledgeDashScopeAdvisor) {
            // 使用MessageWindowChatMemory保存对话历史（窗口模式）
            ChatMemory chatMemory = MessageWindowChatMemory.builder()
                    .maxMessages(10)
                    .build();

            // 构建ChatClient，集成知识库Advisor和对话记忆
            return chatClientBuilder
                    .defaultAdvisors(
                            knowledgeDashScopeAdvisor,  // 知识库检索Advisor
                            MessageChatMemoryAdvisor.builder(chatMemory).build()  // 对话记忆Advisor
                    )
                    .build();
        }
    }

    @Resource
    private ChatClient chatClient;

    @Test
    void testKnowledgeBaseChat() {
        String chatId = UUID.randomUUID().toString();
        String question = "如何缓解焦虑";

        log.info("开始测试知识库问答，问题: {}", question);

        try {
            ChatResponse chatResponse = chatClient
                    .prompt(question)
                    .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                    .call()
                    .chatResponse();

            if (chatResponse != null && chatResponse.getResult() != null) {
                String content = chatResponse.getResult().getOutput().getText();
                log.info("知识库回答: {}", content);
            } else {
                log.warn("未获取到有效回答");
            }
        } catch (Exception e) {
            log.error("知识库问答测试失败: {}", e.getMessage(), e);
        }
    }
}
