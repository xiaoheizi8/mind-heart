-- 添加情绪报告表
-- 创建时间: 2026-04-25

USE mind_realm;

CREATE TABLE IF NOT EXISTS emotion_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    report_type VARCHAR(20) NOT NULL COMMENT '报告类型: week/month/quarter',
    period_start DATE NOT NULL COMMENT '周期开始日期',
    period_end DATE NOT NULL COMMENT '周期结束日期',
    summary TEXT COMMENT '报告摘要',
    emotion_stats JSON COMMENT '情绪统计数据',
    emotion_distribution JSON COMMENT '情绪分布',
    trend_analysis TEXT COMMENT '趋势分析',
    ai_analysis TEXT COMMENT 'AI分析报告',
    diary_count INT DEFAULT 0 COMMENT '日记数量',
    avg_emotion_score DECIMAL(5,2) COMMENT '平均情绪得分',
    main_emotion VARCHAR(50) COMMENT '主要情绪',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_report_type (report_type),
    INDEX idx_period (period_start, period_end),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情绪报告表';
