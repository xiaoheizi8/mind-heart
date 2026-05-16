package com.mindrealm.core.service.impl;

import com.mindrealm.core.service.IRagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @className: RagServiceImpl
 * @description: 简单RAG服务实现
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class RagServiceImpl implements IRagService {

    private static final Logger logger = LoggerFactory.getLogger(RagServiceImpl.class);

    private final List<String> knowledgeContents = new ArrayList<>();

    private volatile boolean initialized = false;

    public RagServiceImpl() {
        knowledgeContents.add("抑郁症的DSM-5诊断标准：持续2周以上的情绪低落或兴趣减退,并伴随其他症状如睡眠障碍、食欲改变、疲劳感、自责自罪等。");
        knowledgeContents.add("焦虑障碍的主要表现：过度担心、紧张、坐立不安,伴有心悸、出汗、颤抖等躯体症状。");
        knowledgeContents.add("认知行为疗法(CBT)核心原则：识别负性自动思维,挑战非理性信念,建立更适应性的认知模式。");
        knowledgeContents.add("青少年心理健康常见问题：学业压力、人际关系困惑、家庭冲突、自我认同困扰、情绪调节困难等。");
        knowledgeContents.add("自杀预警信号：言语暗示(活着没意思)、行为改变(突然平静或告别)、日志/书画暗示、物品整理、托付他人。");
        knowledgeContents.add("心理急救原则：确保安全、倾听陪伴、不评判、提供信息、帮助联系资源。");
        knowledgeContents.add("正念练习：专注于当下呼吸,感受身体感受,不评判地观察思绪流动。");
        knowledgeContents.add("情绪调节技巧：深呼吸、运动、倾诉、书写、冥想、寻求支持。");
        knowledgeContents.add("压力管理策略：时间管理、优先级排序、适度运动、充足睡眠、社交支持。");
        knowledgeContents.add("睡眠卫生建议：固定作息时间、睡前避免电子设备、创造安静环境、午睡不超过30分钟。");
    }

    @PostConstruct
    public void init() {
        try {
            logger.info("初始化RAG服务(简单模式), 知识库条目数: {}", knowledgeContents.size());
            initialized = true;
            logger.info("RAG服务初始化完成");
        } catch (Exception e) {
            logger.warn("RAG服务初始化失败: {}", e.getMessage());
            initialized = false;
        }
    }

    public String answer(String question) {
        return simpleAnswer(question);
    }

    private String simpleAnswer(String question) {
        String lower = question.toLowerCase();

        if (lower.contains("抑郁") || lower.contains("depression")) {
            return "抑郁症需要专业治疗,建议寻求心理咨询师帮助。如果情绪低落超过两周,建议就医。";
        }
        if (lower.contains("焦虑") || lower.contains("anxiety")) {
            return "焦虑时可以尝试深呼吸放松,正念冥想。如果焦虑影响日常生活,建议咨询专业心理医生。";
        }
        if (lower.contains("压力") || lower.contains("stress")) {
            return "缓解压力可以尝试：运动、倾诉、时间管理、寻求支持。如果压力过大,建议调整生活节奏。";
        }
        if (lower.contains("失眠") || lower.contains("sleep")) {
            return "改善睡眠建议：固定作息时间、睡前避免电子设备、创造安静环境、午睡不超过30分钟。";
        }
        if (lower.contains("自杀") || lower.contains("自伤")) {
            return "如果你有伤害自己的想法,请立即寻求帮助。可以拨打心理援助热线400-161-9995。";
        }
        if (lower.contains("失眠") || lower.contains("睡不着")) {
            return "睡眠不好可以尝试：固定作息时间、睡前避免咖啡因和电子设备、创造安静黑暗的睡眠环境。";
        }
        if (lower.contains("学习") || lower.contains("成绩")) {
            return "学习压力很常见，建议：制定合理计划、适当休息、寻求老师同学帮助、保持规律作息。";
        }
        if (lower.contains("人际") || lower.contains("朋友") || lower.contains("孤独")) {
            return "人际关系需要时间和耐心，尝试主动交流、参加集体活动、真诚待人。如果感到孤独，可以多和家人沟通。";
        }
        if (lower.contains("家庭") || lower.contains("父母") || lower.contains("冲突")) {
            return "与家人沟通很重要，尝试表达自己的感受和想法。如果冲突持续，可以寻求学校心理老师帮助。";
        }

        return "建议您详细描述一下您的问题,我可以提供更有针对性的建议。";
    }

    public void addKnowledge(String content) {
        if (content != null && !content.isEmpty()) {
            knowledgeContents.add(content);
            logger.info("知识添加成功: {}", content.substring(0, Math.min(50, content.length())));
        }
    }

    public int getKnowledgeCount() {
        return knowledgeContents.size();
    }

    public boolean isAvailable() {
        return initialized;
    }
}
