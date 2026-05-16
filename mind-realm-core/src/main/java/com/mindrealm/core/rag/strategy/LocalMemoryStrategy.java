package com.mindrealm.core.rag.strategy;

import com.mindrealm.core.rag.model.RagRequest;
import com.mindrealm.core.rag.model.RagResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 生产环境启用本地
 */
//@Deprecated
public class LocalMemoryStrategy implements RagStrategy {

    private static final Logger logger = LoggerFactory.getLogger(LocalMemoryStrategy.class);

    private final List<KnowledgeEntry> knowledgeBase = new ArrayList<>();
    private final Map<Long, List<RagRequest.ChatMessage>> userHistories = new ConcurrentHashMap<>();
    
    private volatile boolean initialized = false;

    public LocalMemoryStrategy() {
        initKnowledgeBase();
    }

    @PostConstruct
    public void init() {
        logger.info("本地内存策略初始化完成，知识库条目数: {}", knowledgeBase.size());
        initialized = true;
    }

    private void initKnowledgeBase() {
        addKnowledge("抑郁症", "抑郁", "情绪低落", 
            "我理解你现在可能感到很沉重。抑郁情绪很常见，但持续的抑郁可能需要专业帮助。\n\n" +
            "根据DSM-5诊断标准，如果情绪低落和兴趣减退持续两周以上，并伴随睡眠障碍、食欲改变、疲劳感等症状，建议寻求心理咨询师的帮助。\n\n" +
            "记住，你不是一个人在这里。必要时可以拨打心理援助热线：400-161-9995");
        
        addKnowledge("焦虑", "紧张", "担心", "害怕", "恐惧", "考试",
            "焦虑是一种很常见的情绪反应。缓解方法：\n\n" +
            "1. 腹式呼吸：吸气4秒，屏住4秒，呼气6秒\n" +
            "2. 正念练习：专注当下，不评判\n" +
            "3. 认知重构：识别并挑战不合理的思维\n" +
            "4. 适度运动：释放紧张情绪\n\n" +
            "如果焦虑影响到日常生活，建议咨询专业心理医生。");
        
        addKnowledge("压力", "累", "喘不过气", "崩溃", "解压",
            "听起来你正在经历很大的压力。压力管理策略：\n\n" +
            "1. 任务分解：把大任务分成小步骤\n" +
            "2. 时间管理：设定优先级\n" +
            "3. 适度运动：每周至少3次\n" +
            "4. 充足睡眠：保持规律作息\n" +
            "5. 社交支持：与信任的人沟通\n\n" +
            "记住，照顾好自己很重要。");
        
        addKnowledge("失眠", "睡不着", "睡眠不好", "熬夜",
            "睡眠问题确实很让人困扰。睡眠卫生建议：\n\n" +
            "1. 固定作息时间\n" +
            "2. 睡前避免电子设备\n" +
            "3. 创造安静环境\n" +
            "4. 避免咖啡因\n\n" +
            "如果长期失眠，建议咨询专业医生。");
        
        addKnowledge("自杀", "想死", "不想活", "自伤", "结束生命",
            "我注意到你提到了让自己感到危险的想法。我想让你知道，你的生命是珍贵的。\n\n" +
            "请立即寻求帮助：\n" +
            "• 心理援助热线：400-161-9995（24小时）\n" +
            "• 如有紧急危险，请拨打120\n\n" +
            "我在这里陪伴你，请告诉我你现在怎么样？");
        
        addKnowledge("学习", "成绩", "考试", "学不进去", "考砸",
            "学习压力是很多学生都会面临的挑战。应对建议：\n\n" +
            "1. 制定合理计划\n" +
            "2. 寻求帮助\n" +
            "3. 适度休息\n" +
            "4. 保持规律作息\n" +
            "5. 调整心态\n\n" +
            "成绩不是衡量你价值的唯一标准。");
        
        addKnowledge("人际关系", "朋友", "孤独", "被孤立", "社交", "霸凌",
            "人际关系问题在青少年中很常见。健康的人际关系需要：\n\n" +
            "1. 有效沟通\n" +
            "2. 换位思考\n" +
            "3. 情绪表达\n" +
            "4. 建立边界\n" +
            "5. 寻求支持\n\n" +
            "如果遭遇霸凌，请及时告诉家长、老师或学校心理老师。");
        
        addKnowledge("家庭", "父母", "吵架", "冲突", "亲子", "不理解",
            "家庭冲突是很多青少年都会遇到的问题。与父母沟通时，可以尝试：\n\n" +
            "1. 选择合适时机\n" +
            "2. 表达感受而非指责\n" +
            "3. 理解父母立场\n" +
            "4. 寻求共识\n\n" +
            "记住，家庭关系需要时间和耐心来改善。");
        
        addKnowledge("愤怒", "生气", "脾气", "控制不住", "想发火",
            "愤怒是一种正常情绪，但如何表达很重要。情绪调节技巧：\n\n" +
            "1. 暂停法：深呼吸，暂停一下\n" +
            "2. 离开现场\n" +
            "3. 释放能量：运动、书写\n" +
            "4. 认知重构\n" +
            "5. 表达需求\n\n" +
            "如果经常感到难以控制愤怒，可能需要学习更多情绪管理技巧。");
        
        addKnowledge("迷茫", "未来", "没有目标", "困惑", "不知道怎么办",
            "感到迷茫是很正常的。应对建议：\n\n" +
            "1. 探索兴趣\n" +
            "2. 设定小目标\n" +
            "3. 寻求榜样\n" +
            "4. 允许迷茫\n" +
            "5. 专业支持\n\n" +
            "每个人的人生路径都不同，不必急于找到所有答案。");
        
        addKnowledge("自卑", "不自信", "没价值", "觉得自己不行",
            "每个人都有独特的价值。提升自信的方法：\n\n" +
            "1. 记录成就：每天写下3件做得好的事\n" +
            "2. 停止自我批评：用积极的想法替代\n" +
            "3. 设定可达成的小目标\n" +
            "4. 学会接受赞美\n" +
            "5. 关注自己的成长\n\n" +
            "你是独一无二的，有自己的闪光点。");
        
        addKnowledge("情绪", "心情", "调节",
            "情绪调节是心理健康的重要部分。一些实用的情绪调节方法：\n\n" +
            "1. 情绪识别：给情绪命名\n" +
            "2. 深呼吸：缓慢而深长的呼吸\n" +
            "3. 身体扫描：感受身体各部位\n" +
            "4. 表达性写作：写下你的感受\n" +
            "5. 寻求支持：与信任的人分享\n\n" +
            "每种情绪都有它的价值，学会与情绪共处。");
    }

    private void addKnowledge(String category, String response, String... keywords) {
        knowledgeBase.add(new KnowledgeEntry(category, response, keywords));
    }

    @Override
    public String getMode() {
        return "LOCAL_MEMORY";
    }

    @Override
    public boolean isAvailable() {
        return initialized;
    }

    @Override
    public RagResponse chat(RagRequest request) {
        long startTime = System.currentTimeMillis();
        String question = request.getQuestion();
        
        String bestResponse = findBestResponse(question);
        List<RagResponse.KnowledgeSnippet> sources = findRelevantSources(question, 3);
        
        saveHistory(request.getUserId(), request.getQuestion(), bestResponse);
        
        long latency = System.currentTimeMillis() - startTime;
        
        return RagResponse.builder()
                .success(true)
                .answer(bestResponse)
                .mode(getMode())
                .sources(sources)
                .modelInfo(RagResponse.ModelInfo.builder()
                        .model("local-memory")
                        .latencyMs(latency)
                        .build())
                .build();
    }

    private String findBestResponse(String question) {
        String lower = question.toLowerCase();
        
        for (KnowledgeEntry entry : knowledgeBase) {
            for (String keyword : entry.keywords) {
                if (lower.contains(keyword.toLowerCase())) {
                    return entry.response;
                }
            }
        }
        
        return "谢谢你的分享。我在这里倾听你。\n\n" +
                "你可以告诉我更多关于你的情况吗？比如最近发生了什么让你困扰的事情？";
    }

    private List<RagResponse.KnowledgeSnippet> findRelevantSources(String question, int limit) {
        String lower = question.toLowerCase();
        List<RagResponse.KnowledgeSnippet> sources = new ArrayList<>();
        
        for (KnowledgeEntry entry : knowledgeBase) {
            for (String keyword : entry.keywords) {
                if (lower.contains(keyword.toLowerCase())) {
                    sources.add(RagResponse.KnowledgeSnippet.builder()
                            .content(entry.category)
                            .source("本地知识库")
                            .score(1.0f)
                            .build());
                    break;
                }
            }
        }
        
        return sources.size() > limit ? sources.subList(0, limit) : sources;
    }

    private void saveHistory(Long userId, String question, String response) {
        if (userId == null) return;
        
        List<RagRequest.ChatMessage> history = userHistories.computeIfAbsent(userId, k -> new ArrayList<>());
        history.add(RagRequest.ChatMessage.builder()
                .role("user")
                .content(question)
                .build());
        history.add(RagRequest.ChatMessage.builder()
                .role("assistant")
                .content(response)
                .build());
        
        if (history.size() > 20) {
            history.subList(0, 10).clear();
        }
    }

    @Override
    public void clearHistory(Long userId) {
        userHistories.remove(userId);
    }

    @Override
    public int getKnowledgeCount() {
        return knowledgeBase.size();
    }

    private static class KnowledgeEntry {
        final String category;
        final String response;
        final String[] keywords;

        KnowledgeEntry(String category, String response, String... keywords) {
            this.category = category;
            this.response = response;
            this.keywords = keywords;
        }
    }
}
