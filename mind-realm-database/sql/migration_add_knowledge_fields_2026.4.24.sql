-- =====================================================
-- 知识库表结构升级脚本
-- 版本: 2026.4.24
-- 说明: 添加 status 和 view_count 字段
-- =====================================================

-- 添加状态字段 (0 草稿 1 已发布 2 已下线)
ALTER TABLE `knowledge_base` 
ADD COLUMN `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 (0 草稿 1 已发布 2 已下线)' 
AFTER `summary`;

-- 添加浏览次数字段
ALTER TABLE `knowledge_base` 
ADD COLUMN `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数' 
AFTER `status`;

-- 为status字段添加索引（可选，提升查询性能）
ALTER TABLE `knowledge_base` 
ADD INDEX `idx_status` (`status`);

-- 验证字段是否添加成功
SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_DEFAULT, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'knowledge_base' 
AND COLUMN_NAME IN ('status', 'view_count');
