-- 心域数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS mind_realm DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mind_realm;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE COMMENT '用户名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像 URL',
    age INT COMMENT '年龄',
    gender TINYINT DEFAULT 3 COMMENT '性别 (1 男 2 女 3 未知)',
    role TINYINT DEFAULT 1 COMMENT '角色 (1 用户 2 咨询师 3 管理员)',
    status TINYINT DEFAULT 1 COMMENT '状态 (0 禁用 1 正常)',
    guardian_phone VARCHAR(20) COMMENT '监护人电话',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 情绪日记表
CREATE TABLE IF NOT EXISTS diary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    content TEXT COMMENT '日记内容',
    media_urls VARCHAR(1000) COMMENT '媒体 URL(JSON 数组)',
    emotion_tags VARCHAR(255) COMMENT '情绪标签 (JSON 数组)',
    emotion_score DECIMAL(5,2) DEFAULT 0 COMMENT '情感得分 (-1 到 1)',
    emotion_category VARCHAR(50) COMMENT '情感类别',
    ai_analysis TEXT COMMENT 'AI 分析结果',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情绪日记表';

-- AI 对话记录表
CREATE TABLE IF NOT EXISTS conversation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    ai_persona VARCHAR(20) DEFAULT 'listener' COMMENT 'AI 人格类型 (listener/analyst/healer)',
    role VARCHAR(10) NOT NULL COMMENT '角色 (user/assistant)',
    content TEXT NOT NULL COMMENT '对话内容',
    emotion_state VARCHAR(20) COMMENT '用户情绪状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 对话记录表';

-- 预警记录表
CREATE TABLE IF NOT EXISTS warning_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    risk_level TINYINT DEFAULT 1 COMMENT '风险等级 (1 低 2 中 3 高)',
    trigger_type VARCHAR(50) COMMENT '触发类型 (keyword/emotion/manual)',
    content TEXT COMMENT '触发内容',
    handled TINYINT DEFAULT 0 COMMENT '是否处理 (0 否 1 是)',
    handler_id BIGINT COMMENT '处理人 ID',
    handler_note VARCHAR(500) COMMENT '处理备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    handled_at DATETIME COMMENT '处理时间',
    INDEX idx_user_id (user_id),
    INDEX idx_risk_level (risk_level),
    INDEX idx_handled (handled),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (handler_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警记录表';

-- 心理知识库表
CREATE TABLE IF NOT EXISTS knowledge_base (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    category VARCHAR(50) COMMENT '分类 (DSM5/CBT/case)',
    source VARCHAR(100) COMMENT '来源',
    embedding_id VARCHAR(100) COMMENT '向量 ID',
    tags VARCHAR(255) COMMENT '标签 (JSON 数组)',
    summary VARCHAR(500) COMMENT '摘要',
    status INT DEFAULT 1 COMMENT '状态 (0 草稿 1 已发布 2 已下线)',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心理知识库表';

-- 用户画像表（用于数字孪生）
CREATE TABLE IF NOT EXISTS user_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户 ID',
    personality_tags VARCHAR(500) COMMENT '人格标签 (JSON)',
    emotional_pattern TEXT COMMENT '情绪模式',
    stress_triggers VARCHAR(500) COMMENT '压力触发点',
    last_analysis TEXT COMMENT '最后分析结果',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像表';

-- 验证码表
CREATE TABLE IF NOT EXISTS sms_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    code VARCHAR(10) NOT NULL COMMENT '验证码',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';

-- 用户收藏表
CREATE TABLE IF NOT EXISTS user_collect (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    target_type VARCHAR(20) NOT NULL COMMENT '收藏类型 (diary/knowledge/knowledge)',
    target_id BIGINT NOT NULL COMMENT '收藏目标 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_type (user_id, target_type),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 通知记录表
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    type VARCHAR(20) NOT NULL COMMENT '通知类型 (reminder/warning/system)',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读 (0 否 1 是)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知记录表';

-- 用户设置表
CREATE TABLE IF NOT EXISTS user_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户 ID',
    diary_reminder TINYINT DEFAULT 1 COMMENT '日记提醒',
    ai_push TINYINT DEFAULT 1 COMMENT 'AI 推送',
    weekly_report TINYINT DEFAULT 1 COMMENT '周报推送',
    warning_notify TINYINT DEFAULT 1 COMMENT '预警通知',
    reminder_time VARCHAR(10) DEFAULT '20:00' COMMENT '提醒时间',
    privacy_diary_password TINYINT DEFAULT 0 COMMENT '日记加密',
    privacy_show_mood TINYINT DEFAULT 1 COMMENT '展示情绪',
    privacy_anonymous_data TINYINT DEFAULT 0 COMMENT '匿名数据',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设置表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    module VARCHAR(50) COMMENT '模块',
    operation VARCHAR(100) COMMENT '操作描述',
    method VARCHAR(200) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_method VARCHAR(10) COMMENT '请求方式',
    request_params TEXT COMMENT '请求参数',
    response_result TEXT COMMENT '返回结果',
    ip VARCHAR(50) COMMENT 'IP地址',
    duration BIGINT COMMENT '耗时(毫秒)',
    status TINYINT DEFAULT 1 COMMENT '状态(0失败1成功)',
    error_msg VARCHAR(1000) COMMENT '错误信息',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_module (module),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 初始化心理知识数据
INSERT INTO knowledge_base (title, content, category, source, summary, tags, created_at) VALUES
('如何应对考试焦虑', '考试焦虑是很多学生都会面临的问题。当我们面临重要的考试时，适度的紧张其实是一种正常的身心反应，它可以帮助我们更好地集中注意力。但过度的焦虑就会影响我们的发挥。\n\n那么，如何应对考试焦虑呢？\n\n1. 做好充分准备\n充分的准备是缓解焦虑的最好方法。制定合理的复习计划，按部就班地完成每天的学习任务，这样可以增强自信心。\n\n2. 学会放松技巧\n深呼吸是最简单有效的放松方法。闭上眼睛，深吸一口气，缓慢呼出，重复几次。也可以尝试渐进性肌肉放松法。\n\n3. 调整认知\n很多焦虑来源于对考试的过度担忧。尝试用更理性的想法替代灾难化的思维。\n\n4. 保证充足睡眠\n考试前不要熬夜，保持规律的作息。充足的睡眠可以帮助大脑更好地整合和记忆知识。\n\n5. 寻求支持\n如果焦虑情绪持续加重，不要犹豫，及时向老师、家长或心理咨询师寻求帮助。', 'skill', '心域知识库', '考试焦虑应对指南', '["考试", "焦虑", "调节"]', NOW()),
('正念冥想入门指南', '正念冥想是一种有效的情绪调节方法，源于佛教的禅修传统，现在已经被现代心理学广泛研究和应用。\n\n什么是正念？\n正念是指有意识地、不带评判地关注当下时刻的感受、想法和周围环境。它帮助我们从忙碌的思绪中抽离，专注于此时此刻。\n\n如何开始练习？\n\n1. 选择一个安静的环境\n找一个不会被打扰的地方，坐下或躺下。\n\n2. 设置时间\n初学者可以从 5-10 分钟开始，逐渐延长。\n\n3. 专注于呼吸\n闭上眼睛，将注意力集中在呼吸上。感受空气进入鼻腔、充满肺部、呼出的感觉。\n\n4. 接纳走神的思绪\n当思绪飘走时，不要批评自己，只是温柔地把注意力带回呼吸。\n\n5. 身体扫描\n感受身体每个部位的感觉，从头到脚，依次关注并放松。\n\n坚持练习会让你的情绪更加稳定，注意力更集中。', 'skill', '心域知识库', '正念冥想基础教程', '["冥想", "放松", "正念"]', NOW()),
('了解抑郁症的早期信号', '抑郁症并非一朝一夕形成，了解早期信号有助于及时发现和干预。\n\n常见的早期信号包括：\n\n1. 情绪变化\n- 持续的低落情绪\n- 对曾经喜欢的事物失去兴趣\n- 容易烦躁或发脾气\n\n2. 思维变化\n- 难以集中注意力\n- 觉得自己没有价值\n- 出现消极的念头\n\n3. 身体变化\n- 睡眠问题（失眠或嗜睡）\n- 食欲变化（食欲不振或暴饮暴食）\n- 容易疲劳\n\n4. 行为变化\n- 回避社交活动\n- 拖延逃避责任\n- 兴趣爱好减少\n\n如果你或身边的人出现以上信号持续两周以上，建议寻求专业帮助。学校心理中心、心理咨询师都是可以求助的渠道。\n\n记住：寻求帮助是勇敢的表现，不是软弱。', 'knowledge', '心域知识库', '抑郁症早期识别指南', '["抑郁", "心理健康", "预防"]', NOW()),
('与父母沟通的技巧', '很多青少年与父母存在沟通障碍，本文通过案例分享一些有效的沟通方法。\n\n常见沟通问题：\n- 父母不理解我的想法\n- 说了他们也不听\n- 说着说着就吵起来\n- 他们总是把我当小孩\n\n有效沟通技巧：\n\n1. 选择合适的时机\n不要在父母忙碌或心情不好时谈论重要的事情。选择大家都很放松的时间。\n\n2. 表达感受而不是指责\n不要说你们总是不理解我，而是说我感到有点失落。\n\n3. 主动分享\n主动分享学校的事情、朋友的趣事，让父母了解你的生活。\n\n4. 理解父母的立场\n父母也是第一次做父母，他们的担忧来源于爱。试着理解他们的出发点。\n\n5. 寻求共识\n找到双方都能接受的解决方案，而不是只坚持自己的观点。', 'case', '心域知识库', '亲子沟通实用技巧', '["沟通", "家庭", "人际关系"]', NOW()),
('青少年心理健康保护', '青少年时期是一个充满变化和挑战的阶段，心理健康尤为重要。\n\n为什么心理健康很重要？\n- 心理健康影响学习效率\n- 影响人际关系\n- 关系到未来发展\n- 影响身体健康\n\n如何保护心理健康？\n\n1. 保持规律作息\n充足的睡眠对身体和心理都很重要。\n\n2. 适度运动\n运动可以释放压力，产生让人愉悦的内啡肽。\n\n3. 建立支持系统\n培养良好的朋友关系，在需要时寻求支持。\n\n4. 培养兴趣爱好\n做自己喜欢的事情可以带来满足感。\n\n5. 学会情绪管理\n了解自己的情绪，学习健康的调节方法。\n\n6. 遇到问题及时求助\n有问题及时找老师、家长或专业人士。\n\n记住：照顾好自己，才能更好地面对未来的挑战。', 'knowledge', '心域知识库', '青少年心理健康指南', '["心理健康", "自我保护", "成长"]', NOW());

-- 插入测试用户 (密码：123456, MD5 加密)
-- 123456 的 MD5 = e10adc3949ba59abbe56e057f20f883e
-- 插入测试用户 (密码：123456, MD5 加密)
-- 123456 的 MD5 = e10adc3949ba59abbe56e057f20f883e
INSERT IGNORE INTO users (username, password, nickname, phone, email, age, gender, role, status, created_at) VALUES
('test_user', 'e10adc3949ba59abbe56e057f20f883e', '测试用户', '13800138000', NULL, 18, 1, 1, 1, NOW()),
('xiaoming', 'e10adc3949ba59abbe56e057f20f883e', '小明', '13800138001', NULL, 16, 1, 1, 1, NOW()),
('xiaohua', 'e10adc3949ba59abbe56e057f20f883e', '小花', '13800138002', NULL, 17, 2, 1, 1, NOW()),
('counselor', 'e10adc3949ba59abbe56e057f20f883e', '心理咨询师', '13900139000', NULL, 30, 1, 2, 1, NOW()),
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13900139001', 'admin@mindrealm.com', 35, 1, 3, 1, NOW()),
('yichao', 'e10adc3949ba59abbe56e057f20f883e', '一朝', NULL, '2780059846@qq.com', 25, 1, 3, 1, NOW());

-- 更新现有用户为管理员（如果已存在）
UPDATE users SET role = 3 WHERE email = '2780059846@qq.com';

-- ... existing code ...
-- 插入测试日记数据
INSERT INTO diary (user_id, content, emotion_tags, emotion_score, emotion_category, ai_analysis, created_at) VALUES
(1, '今天考试没考好，心情很低落。但是想了想，一次考试并不能决定什么，下次继续努力吧。人生还有很长的路要走，不应该被一次失败打倒。', '["考试", "失落", "自我安慰"]', -0.30, 'sad', '从你的日记中，我感受到你正在经历考试失利的挫折。这是一个很常见的情绪体验。值得注意的是，你在文字中已经展现出了很好的自我调节能力，能够客观地看待这次经历。继续保持这种积极的思维方式。', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, '和朋友们一起看了电影，笑得很开心。享受当下最简单的快乐。', '["朋友", "电影", "快乐"]', 0.85, 'happy', '很高兴看到你今天心情这么好。与朋友一起观看电影是一种很好的放松方式，可以带来积极的情绪体验。继续珍惜这样的快乐时光。', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, '今天进行了期末汇报，虽然有点紧张但整体表现还不错，给自己点个赞。', '["汇报", "成长", "自信"]', 0.75, 'happy', '你在日记中提到了期末汇报，这是一个挑战但也是成长的机会。你能够认识到自己的进步并给自己积极的反馈，这是很棒的心理品质。', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, '晚上睡不着觉，想了很多事情，关于未来的迷茫和焦虑。', '["失眠", "焦虑", "迷茫"]', -0.45, 'anxious', '感谢你的分享。睡前思绪纷乱是很常见的现象，这可能与压力有关。建议可以尝试一些放松技巧，如深呼吸或冥想，帮助入睡。', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(1, '今天阳光很好，心情也很平静。一个人在公园散步，享受安静的时光。', '["阳光", "平静", "独处"]', 0.60, 'calm', '很高兴你今天找到了内心的平静。阳光和自然环境确实对情绪有积极的影响。享受独处时光是一种很好的自我调节方式。', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, '今天和妈妈吵架了，感觉她一点都不理解我。', '["家庭", "争吵", "委屈"]', -0.55, 'sad', '我理解你感到委屈和不被理解的痛苦。与父母沟通确实是需要技巧的。或许可以试着冷静下来后，用更平和的方式表达自己的感受。', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, '体育课跑步跑了第一名，很开心。', '["运动", "成就", "开心"]', 0.90, 'happy', '太棒了。运动带来的成就感是非常积极的情绪体验。继续保持在运动中的热情，这对你身心健康都很有好处。', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, '今天数学考试考了满分，太激动了。', '["学习", "成就", "激动"]', 0.95, 'happy', '恭喜你。取得好成绩是对努力学习最好的回报。但也要记得，成绩只是生活的一部分，无论好坏都要保持平衡的心态。', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, '有点担心下周的音乐比赛', '["比赛", "紧张", "担忧"]', -0.25, 'anxious', '比赛前的紧张是很正常的反应。这种紧张感其实可以帮助你更好地准备。试着把这种能量转化为练习的动力相信自己，你一定可以的。', DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 插入 AI 对话记录
INSERT INTO conversation (user_id, ai_persona, role, content, emotion_state, created_at) VALUES
(1, 'listener', 'user', '今天考试没考好，心情很不好', 'sad', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 'listener', 'assistant', '我听到你了，考试没考好一定很沮丧吧。这种感觉确实不好受。你愿意和我多说说吗？', 'sad', DATE_SUB(NOW(), INTERVAL 59 MINUTE)),
(1, 'listener', 'user', '我觉得自己很失败什么都做不好', 'sad', DATE_SUB(NOW(), INTERVAL 58 MINUTE)),
(1, 'listener', 'assistant', '考试没考好并不代表你是一个失败的人。每个人都会有状态不好的时候，你愿意来倾诉说明你正在积极面对自己的情绪。试着回想一下过去有没有做得好的事情？', 'sad', DATE_SUB(NOW(), INTERVAL 57 MINUTE)),
(2, 'analyst', 'user', '我和妈妈吵架了', 'angry', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2, 'analyst', 'assistant', '和父母发生冲突是很多人都会经历的。能让阿姨告诉我具体是什么事情吗？我们可以一起分析一下。', 'angry', DATE_SUB(NOW(), INTERVAL 119 MINUTE)),
(2, 'analyst', 'user', '她总是说我学习成绩下降，不让我玩手机', 'angry', DATE_SUB(NOW(), INTERVAL 118 MINUTE)),
(2, 'analyst', 'assistant', '听起来妈妈的出发点是关心你的学习。或许你可以试着和妈妈沟通，告诉她你需要一个放松的方式，比如限定玩手机的时间。', 'angry', DATE_SUB(NOW(), INTERVAL 117 MINUTE)),
(3, 'healer', 'user', '我最近压力好大', 'anxious', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(3, 'healer', 'assistant', '嘿，我在这里。压力太大的时候，记得深呼吸。你已经很努力了，给自己一些肯定吧。', 'anxious', DATE_SUB(NOW(), INTERVAL 29 MINUTE));

-- 插入预警记录
INSERT INTO warning_record (user_id, risk_level, trigger_type, content, handled, created_at) VALUES
(2, 2, 'keyword', '和父母争吵后表达了消极想法', 0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 1, 'emotion', '连续多日记录负面情绪', 0, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 插入用户设置(使用IGNORE避免重复)
INSERT IGNORE INTO user_settings (user_id, diary_reminder, ai_push, weekly_report, warning_notify, reminder_time) VALUES
(1, 1, 1, 1, 1, '20:00'),
(2, 1, 1, 1, 1, '21:00'),
(3, 1, 1, 1, 1, '20:00');

-- 插入通知记录(使用IGNORE避免重复)
INSERT IGNORE INTO notification (user_id, type, title, content, is_read, created_at) VALUES
(1, 'reminder', '日记提醒', '今天还没有写日记哦，记录一下今天的心情吧~', 0, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(1, 'system', '欢迎回来', '欢迎回到心域，继续记录你的情绪旅程吧！', 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 'system', '情绪关怀', '最近你记录的情绪波动较大，如果需要倾诉，我一直在哦~', 0, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 'system', '周报提醒', '本周的情绪报告已生成，点击查看详情~', 1, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 插入用户收藏(使用IGNORE避免重复)
INSERT IGNORE INTO user_collect (user_id, target_type, target_id, created_at) VALUES
(1, 'knowledge', 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 'knowledge', 2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 'knowledge', 3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 'knowledge', 1, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 插入文件记录(使用IGNORE避免重复)
INSERT IGNORE INTO file_record (user_id, file_name, file_url, file_type, file_size, created_at) VALUES
(1, 'avatar.jpg', 'https://mind-heart.oss-cn-beijing.aliyuncs.com/images/abc123.jpg', 'image', 102400, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 'diary_photo.jpg', 'https://mind-heart.oss-cn-beijing.aliyuncs.com/images/def456.jpg', 'image', 204800, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 插入反馈记录(使用IGNORE避免重复)
INSERT IGNORE INTO user_feedback (user_id, type, title, content, status, created_at) VALUES
(1, 'suggestion', '希望增加音乐播放功能', '希望能添加一些舒缓的背景音乐，帮助放松心情', 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 'bug', '日记保存失败', '有时候日记写到一半会丢失，希望能够自动保存', 0, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 插入登录日志(使用IGNORE避免重复)
INSERT IGNORE INTO login_log (user_id, username, login_type, ip, device_type, status, fail_reason, created_at) VALUES
(1, 'test_user', 'password', '127.0.0.1', 'Web', 1, NULL, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 'xiaoming', 'password', '127.0.0.1', 'Android', 1, NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(1, 'test_user', 'password', '127.0.0.1', 'Web', 0, 'wrong_password', DATE_SUB(NOW(), INTERVAL 5 HOUR));

-- 文件记录表（OSS文件）
CREATE TABLE IF NOT EXISTS file_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_url VARCHAR(500) NOT NULL COMMENT '文件URL',
    file_type VARCHAR(20) NOT NULL COMMENT '文件类型 (image/audio/video)',
    file_size BIGINT COMMENT '文件大小(字节)',
    oss_key VARCHAR(255) COMMENT 'OSS对象键',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_file_type (file_type),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件记录表';

-- 用户反馈表
CREATE TABLE IF NOT EXISTS user_feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type VARCHAR(20) NOT NULL COMMENT '反馈类型 (bug/suggestion/complaint)',
    title VARCHAR(100) COMMENT '反馈标题',
    content TEXT NOT NULL COMMENT '反馈内容',
    contact VARCHAR(100) COMMENT '联系方式',
    status TINYINT DEFAULT 0 COMMENT '状态 (0待处理1处理中2已解决)',
    handler_id BIGINT COMMENT '处理人ID',
    handler_note VARCHAR(500) COMMENT '处理备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    handled_at DATETIME COMMENT '处理时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (handler_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';

-- 登录日志表
CREATE TABLE IF NOT EXISTS login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    login_type VARCHAR(20) DEFAULT 'password' COMMENT '登录方式 (password/sms/wechat)',
    ip VARCHAR(50) COMMENT 'IP地址',
    ip_location VARCHAR(200) COMMENT 'IP归属地',
    device_type VARCHAR(50) COMMENT '设备类型',
    device_info VARCHAR(200) COMMENT '设备信息',
    status TINYINT DEFAULT 1 COMMENT '状态 (0失败1成功)',
    fail_reason VARCHAR(200) COMMENT '失败原因',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- 插入文件记录
INSERT INTO file_record (user_id, file_name, file_url, file_type, file_size, created_at) VALUES
(1, 'avatar.jpg', 'https://mind-heart.oss-cn-beijing.aliyuncs.com/images/abc123.jpg', 'image', 102400, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 'diary_photo.jpg', 'https://mind-heart.oss-cn-beijing.aliyuncs.com/images/def456.jpg', 'image', 204800, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 插入反馈记录
INSERT INTO user_feedback (user_id, type, title, content, status, created_at) VALUES
(1, 'suggestion', '希望增加音乐播放功能', '希望能添加一些舒缓的背景音乐，帮助放松心情', 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 'bug', '日记保存失败', '有时候日记写到一半会丢失，希望能够自动保存', 0, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 插入登录日志
INSERT INTO login_log (user_id, username, login_type, ip, device_type, status, fail_reason, created_at) VALUES
(1, 'test_user', 'password', '127.0.0.1', 'Web', 1, NULL, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 'xiaoming', 'password', '127.0.0.1', 'Android', 1, NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(1, 'test_user', 'password', '127.0.0.1', 'Web', 0, 'wrong_password', DATE_SUB(NOW(), INTERVAL 5 HOUR));

