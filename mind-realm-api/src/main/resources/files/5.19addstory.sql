-- 1. 匿名故事主表
CREATE TABLE `heart_story` (
                               `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '故事ID',
                               `user_id` BIGINT NOT NULL COMMENT '发布者ID（后台关联，前端匿名）',
                               `title` VARCHAR(200) DEFAULT NULL COMMENT '故事标题',
                               `content` TEXT NOT NULL COMMENT '故事内容',
                               `emotion_type` VARCHAR(20) DEFAULT NULL COMMENT '情绪类型：happy/sad/anxious/angry/calm',
                               `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签，逗号分隔：#学业压力 #人际关系',
                               `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',

    -- 统计字段（冗余设计，避免频繁COUNT）
                               `like_count` INT DEFAULT 0 COMMENT '点赞数',
                               `comment_count` INT DEFAULT 0 COMMENT '评论数',
                               `share_count` INT DEFAULT 0 COMMENT '分享数',
                               `view_count` INT DEFAULT 0 COMMENT '浏览数',

    -- 审核状态
                               `status` TINYINT DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝 3已下架',
                               `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '拒绝原因',
                               `auditor_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
                               `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',

    -- 匿名化配置
                               `is_anonymous` TINYINT DEFAULT 1 COMMENT '是否匿名：1匿名 0实名（默认匿名）',
                               `display_nickname` VARCHAR(50) DEFAULT NULL COMMENT '展示昵称（可自定义或使用系统生成）',

    -- 时间戳
                               `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `published_at` DATETIME DEFAULT NULL COMMENT '发布时间（审核通过后）',

                               PRIMARY KEY (`id`),
                               KEY `idx_user_id` (`user_id`),
                               KEY `idx_status` (`status`),
                               KEY `idx_emotion_type` (`emotion_type`),
                               KEY `idx_created_at` (`created_at`),
                               KEY `idx_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='匿名故事表';

-- 2. 故事点赞表
CREATE TABLE `story_like` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT,
                              `story_id` BIGINT NOT NULL COMMENT '故事ID',
                              `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
                              `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,

                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_story_user` (`story_id`, `user_id`), -- 防止重复点赞
                              KEY `idx_story_id` (`story_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='故事点赞表';

-- 3. 故事评论表
CREATE TABLE `story_comment` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `story_id` BIGINT NOT NULL COMMENT '故事ID',
                                 `user_id` BIGINT NOT NULL COMMENT '评论者ID',
                                 `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID（支持回复）',
                                 `content` VARCHAR(1000) NOT NULL COMMENT '评论内容',

    -- 预设温暖回复模板（可选）
                                 `is_template` TINYINT DEFAULT 0 COMMENT '是否使用模板：1是 0否',
                                 `template_id` INT DEFAULT NULL COMMENT '模板ID',

                                 `like_count` INT DEFAULT 0 COMMENT '点赞数',
                                 `status` TINYINT DEFAULT 1 COMMENT '状态：1正常 2已删除 3已屏蔽',
                                 `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                 PRIMARY KEY (`id`),
                                 KEY `idx_story_id` (`story_id`),
                                 KEY `idx_parent_id` (`parent_id`),
                                 KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='故事评论表';

-- 4. 温暖回复模板表（预设正能量回复）
CREATE TABLE `warm_reply_template` (
                                       `id` INT NOT NULL AUTO_INCREMENT,
                                       `category` VARCHAR(50) DEFAULT NULL COMMENT '分类：encourage/empathy/advice',
                                       `content` VARCHAR(500) NOT NULL COMMENT '模板内容',
                                       `emotion_type` VARCHAR(20) DEFAULT NULL COMMENT '适用情绪类型',
                                       `use_count` INT DEFAULT 0 COMMENT '使用次数',
                                       `is_active` TINYINT DEFAULT 1 COMMENT '是否启用',
                                       `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,

                                       PRIMARY KEY (`id`),
                                       KEY `idx_category` (`category`),
                                       KEY `idx_emotion_type` (`emotion_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='温暖回复模板表';

-- 5. 故事收藏表
CREATE TABLE `story_favorite` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `story_id` BIGINT NOT NULL,
                                  `user_id` BIGINT NOT NULL,
                                  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,

                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_story_user` (`story_id`, `user_id`),
                                  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='故事收藏表';