/*
 RBAC权限管理系统初始化脚本
 Date: 2026-04-08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(50) NOT NULL COMMENT '角色编码(ADMIN/COUNSELOR/OPERATOR)',
  `role_name` varchar(100) NOT NULL COMMENT '角色名称',
  `description` varchar(255) COMMENT '角色描述',
  `status` tinyint DEFAULT 1 COMMENT '状态(0禁用1正常)',
  `sort` int DEFAULT 0 COMMENT '排序',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 菜单表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT 0 COMMENT '父菜单ID(0表示顶级)',
  `menu_name` varchar(100) NOT NULL COMMENT '菜单名称',
  `menu_code` varchar(100) NOT NULL COMMENT '菜单编码',
  `menu_type` tinyint NOT NULL COMMENT '类型(1目录2菜单3按钮)',
  `path` varchar(200) COMMENT '路由路径',
  `component` varchar(200) COMMENT '组件路径',
  `icon` varchar(100) COMMENT '图标',
  `sort` int DEFAULT 0 COMMENT '排序',
  `visible` tinyint DEFAULT 1 COMMENT '是否可见(0隐藏1显示)',
  `permission` varchar(200) COMMENT '权限标识(user:list,user:add)',
  `status` tinyint DEFAULT 1 COMMENT '状态(0禁用1正常)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menu_code` (`menu_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ----------------------------
-- 角色菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ----------------------------
-- 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- 系统监控日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_monitor_log`;
CREATE TABLE `sys_monitor_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `monitor_type` varchar(50) NOT NULL COMMENT '监控类型(CPU/MEMORY/DISK/DB/REDIS)',
  `metric_name` varchar(100) NOT NULL COMMENT '指标名称',
  `metric_value` decimal(10,2) COMMENT '指标值',
  `metric_unit` varchar(20) COMMENT '单位',
  `status` tinyint DEFAULT 1 COMMENT '状态(0异常1正常)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_monitor_type` (`monitor_type`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统监控日志表';

-- ----------------------------
-- 初始化角色数据
-- ----------------------------
INSERT INTO `sys_role` VALUES 
(1, 'ADMIN', '系统管理员', '拥有系统全部权限', 1, 1, NOW(), NOW()),
(2, 'COUNSELOR', '心理咨询师', '可以查看用户数据和预警信息', 1, 2, NOW(), NOW()),
(3, 'OPERATOR', '运营人员', '可以管理知识库和内容', 1, 3, NOW(), NOW());

-- ----------------------------
-- 初始化菜单数据
-- ----------------------------
INSERT INTO `sys_menu` VALUES 
-- 一级菜单（目录）
(1, 0, '数据概览', 'dashboard', 2, '/', 'Dashboard', 'DashboardOutlined', 1, 1, 'dashboard:view', 1, NOW()),
(2, 0, '用户管理', 'user', 2, '/users', 'UserManagement', 'UserOutlined', 2, 1, 'user:list', 1, NOW()),
(3, 0, '日记管理', 'diary', 2, '/diaries', 'DiaryManagement', 'FileTextOutlined', 3, 1, 'diary:list', 1, NOW()),
(4, 0, '知识库管理', 'knowledge', 2, '/knowledge', 'KnowledgeManagement', 'BookOutlined', 4, 1, 'knowledge:list', 1, NOW()),
(5, 0, '预警管理', 'warning', 2, '/warnings', 'WarningManagement', 'WarningOutlined', 5, 1, 'warning:list', 1, NOW()),
(6, 0, '角色管理', 'role', 2, '/roles', 'RoleManagement', 'TeamOutlined', 6, 1, 'role:list', 1, NOW()),
(7, 0, '系统监控', 'monitor', 2, '/monitor', 'SystemMonitor', 'MonitorOutlined', 7, 1, 'monitor:view', 1, NOW());

-- ----------------------------
-- 初始化角色菜单关联（管理员拥有全部权限）
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES 
(1, 1, 1, NOW()),
(2, 1, 2, NOW()),
(3, 1, 3, NOW()),
(4, 1, 4, NOW()),
(5, 1, 5, NOW()),
(6, 1, 6, NOW()),
(7, 1, 7, NOW());

-- 咨询师权限（数据概览、用户、日记、预警、监控）
INSERT INTO `sys_role_menu` VALUES 
(8, 2, 1, NOW()),
(9, 2, 2, NOW()),
(10, 2, 3, NOW()),
(11, 2, 5, NOW()),
(12, 2, 7, NOW());

-- 运营人员权限（数据概览、知识库）
INSERT INTO `sys_role_menu` VALUES 
(13, 3, 1, NOW()),
(14, 3, 4, NOW());

-- ----------------------------
-- 初始化用户角色关联（假设用户ID 1是管理员，2是咨询师）
-- ----------------------------
INSERT INTO `sys_user_role` VALUES 
(1, 1, 1, NOW()),
(2, 2, 2, NOW());

SET FOREIGN_KEY_CHECKS = 1;
