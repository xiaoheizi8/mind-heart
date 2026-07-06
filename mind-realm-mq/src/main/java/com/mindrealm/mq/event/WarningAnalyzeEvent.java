package com.mindrealm.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @className: WarningAnalyzeEvent
 * @description: 风险预警异步分析事件载荷
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarningAnalyzeEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 待分析内容 */
    private String content;

    /** 内容来源: diary / story / chat */
    private String contextType;

    /** 关联的业务实体ID（如 diaryId、storyId） */
    private Long contextId;
}
