-- ============================================
-- 修复user_settings表缺失created_at字段
-- 执行时间: 2026-04-25
-- 说明: 为user_settings表添加created_at字段
-- ============================================

-- 添加created_at字段(如果不存在)
ALTER TABLE user_settings 
ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' 
AFTER privacy_anonymous_data;

-- 为已有数据设置创建时间(使用updated_at的值)
UPDATE user_settings 
SET created_at = updated_at 
WHERE created_at IS NULL;
