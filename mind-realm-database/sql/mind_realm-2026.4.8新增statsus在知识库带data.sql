/*
 Navicat Premium Dump SQL

 Source Server         : standlone3306
 Source Server Type    : MySQL
 Source Server Version : 90500 (9.5.0)
 Source Host           : localhost:3306
 Source Schema         : mind_realm

 Target Server Type    : MySQL
 Target Server Version : 90500 (9.5.0)
 File Encoding         : 65001

 Date: 08/04/2026 08:36:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for conversation
-- ----------------------------
DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `ai_persona` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'listener' COMMENT 'AI人格类型(listener/analyst/healer)',
  `role` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色(user/assistant)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对话内容',
  `emotion_state` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户情绪状态',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `conversation_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 155 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI对话记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of conversation
-- ----------------------------
INSERT INTO `conversation` VALUES (1, 1, 'listener', 'user', '今天考试没考好，心情很不好', 'sad', '2026-04-03 13:53:02');
INSERT INTO `conversation` VALUES (2, 1, 'listener', 'assistant', '我听到你了，考试没考好一定很沮丧吧。这种感觉确实不好受。你愿意和我多说说吗？', 'sad', '2026-04-03 13:54:02');
INSERT INTO `conversation` VALUES (3, 1, 'listener', 'user', '我觉得自己很失败什么都做不好', 'sad', '2026-04-03 13:55:02');
INSERT INTO `conversation` VALUES (4, 1, 'listener', 'assistant', '考试没考好并不代表你是一个失败的人。每个人都会有状态不好的时候，你愿意来倾诉说明你正在积极面对自己的情绪。试着回想一下过去有没有做得好的事情？', 'sad', '2026-04-03 13:56:02');
INSERT INTO `conversation` VALUES (5, 2, 'analyst', 'user', '我和妈妈吵架了', 'angry', '2026-04-03 12:53:02');
INSERT INTO `conversation` VALUES (6, 2, 'analyst', 'assistant', '和父母发生冲突是很多人都会经历的。能让阿姨告诉我具体是什么事情吗？我们可以一起分析一下。', 'angry', '2026-04-03 12:54:02');
INSERT INTO `conversation` VALUES (7, 2, 'analyst', 'user', '她总是说我学习成绩下降，不让我玩手机', 'angry', '2026-04-03 12:55:02');
INSERT INTO `conversation` VALUES (8, 2, 'analyst', 'assistant', '听起来妈妈的出发点是关心你的学习。或许你可以试着和妈妈沟通，告诉她你需要一个放松的方式，比如限定玩手机的时间。', 'angry', '2026-04-03 12:56:02');
INSERT INTO `conversation` VALUES (9, 3, 'healer', 'user', '我最近压力好大', 'anxious', '2026-04-03 14:23:02');
INSERT INTO `conversation` VALUES (10, 3, 'healer', 'assistant', '嘿，我在这里。压力太大的时候，记得深呼吸。你已经很努力了，给自己一些肯定吧。', 'anxious', '2026-04-03 14:24:02');
INSERT INTO `conversation` VALUES (11, 1, 'listener', 'user', '今天考试没考好，心情很不好', 'sad', '2026-04-03 14:01:22');
INSERT INTO `conversation` VALUES (12, 1, 'listener', 'assistant', '我听到你了，考试没考好一定很沮丧吧。这种感觉确实不好受。你愿意和我多说说吗？', 'sad', '2026-04-03 14:02:22');
INSERT INTO `conversation` VALUES (13, 1, 'listener', 'user', '我觉得自己很失败什么都做不好', 'sad', '2026-04-03 14:03:22');
INSERT INTO `conversation` VALUES (14, 1, 'listener', 'assistant', '考试没考好并不代表你是一个失败的人。每个人都会有状态不好的时候，你愿意来倾诉说明你正在积极面对自己的情绪。试着回想一下过去有没有做得好的事情？', 'sad', '2026-04-03 14:04:22');
INSERT INTO `conversation` VALUES (15, 2, 'analyst', 'user', '我和妈妈吵架了', 'angry', '2026-04-03 13:01:22');
INSERT INTO `conversation` VALUES (16, 2, 'analyst', 'assistant', '和父母发生冲突是很多人都会经历的。能让阿姨告诉我具体是什么事情吗？我们可以一起分析一下。', 'angry', '2026-04-03 13:02:22');
INSERT INTO `conversation` VALUES (17, 2, 'analyst', 'user', '她总是说我学习成绩下降，不让我玩手机', 'angry', '2026-04-03 13:03:22');
INSERT INTO `conversation` VALUES (18, 2, 'analyst', 'assistant', '听起来妈妈的出发点是关心你的学习。或许你可以试着和妈妈沟通，告诉她你需要一个放松的方式，比如限定玩手机的时间。', 'angry', '2026-04-03 13:04:22');
INSERT INTO `conversation` VALUES (19, 3, 'healer', 'user', '我最近压力好大', 'anxious', '2026-04-03 14:31:22');
INSERT INTO `conversation` VALUES (20, 3, 'healer', 'assistant', '嘿，我在这里。压力太大的时候，记得深呼吸。你已经很努力了，给自己一些肯定吧。', 'anxious', '2026-04-03 14:32:22');
INSERT INTO `conversation` VALUES (21, 1, 'listener', 'user', '今天考试没考好，心情很不好', 'sad', '2026-04-03 14:03:39');
INSERT INTO `conversation` VALUES (22, 1, 'listener', 'assistant', '我听到你了，考试没考好一定很沮丧吧。这种感觉确实不好受。你愿意和我多说说吗？', 'sad', '2026-04-03 14:04:39');
INSERT INTO `conversation` VALUES (23, 1, 'listener', 'user', '我觉得自己很失败什么都做不好', 'sad', '2026-04-03 14:05:39');
INSERT INTO `conversation` VALUES (24, 1, 'listener', 'assistant', '考试没考好并不代表你是一个失败的人。每个人都会有状态不好的时候，你愿意来倾诉说明你正在积极面对自己的情绪。试着回想一下过去有没有做得好的事情？', 'sad', '2026-04-03 14:06:39');
INSERT INTO `conversation` VALUES (25, 2, 'analyst', 'user', '我和妈妈吵架了', 'angry', '2026-04-03 13:03:39');
INSERT INTO `conversation` VALUES (26, 2, 'analyst', 'assistant', '和父母发生冲突是很多人都会经历的。能让阿姨告诉我具体是什么事情吗？我们可以一起分析一下。', 'angry', '2026-04-03 13:04:39');
INSERT INTO `conversation` VALUES (27, 2, 'analyst', 'user', '她总是说我学习成绩下降，不让我玩手机', 'angry', '2026-04-03 13:05:39');
INSERT INTO `conversation` VALUES (28, 2, 'analyst', 'assistant', '听起来妈妈的出发点是关心你的学习。或许你可以试着和妈妈沟通，告诉她你需要一个放松的方式，比如限定玩手机的时间。', 'angry', '2026-04-03 13:06:39');
INSERT INTO `conversation` VALUES (29, 3, 'healer', 'user', '我最近压力好大', 'anxious', '2026-04-03 14:33:39');
INSERT INTO `conversation` VALUES (30, 3, 'healer', 'assistant', '嘿，我在这里。压力太大的时候，记得深呼吸。你已经很努力了，给自己一些肯定吧。', 'anxious', '2026-04-03 14:34:39');
INSERT INTO `conversation` VALUES (31, 1, 'listener', 'user', '今天考试没考好，心情很不好', 'sad', '2026-04-05 17:46:09');
INSERT INTO `conversation` VALUES (32, 1, 'listener', 'assistant', '我听到你了，考试没考好一定很沮丧吧。这种感觉确实不好受。你愿意和我多说说吗？', 'sad', '2026-04-05 17:47:09');
INSERT INTO `conversation` VALUES (33, 1, 'listener', 'user', '我觉得自己很失败什么都做不好', 'sad', '2026-04-05 17:48:09');
INSERT INTO `conversation` VALUES (34, 1, 'listener', 'assistant', '考试没考好并不代表你是一个失败的人。每个人都会有状态不好的时候，你愿意来倾诉说明你正在积极面对自己的情绪。试着回想一下过去有没有做得好的事情？', 'sad', '2026-04-05 17:49:09');
INSERT INTO `conversation` VALUES (35, 2, 'analyst', 'user', '我和妈妈吵架了', 'angry', '2026-04-05 16:46:09');
INSERT INTO `conversation` VALUES (36, 2, 'analyst', 'assistant', '和父母发生冲突是很多人都会经历的。能让阿姨告诉我具体是什么事情吗？我们可以一起分析一下。', 'angry', '2026-04-05 16:47:09');
INSERT INTO `conversation` VALUES (37, 2, 'analyst', 'user', '她总是说我学习成绩下降，不让我玩手机', 'angry', '2026-04-05 16:48:09');
INSERT INTO `conversation` VALUES (38, 2, 'analyst', 'assistant', '听起来妈妈的出发点是关心你的学习。或许你可以试着和妈妈沟通，告诉她你需要一个放松的方式，比如限定玩手机的时间。', 'angry', '2026-04-05 16:49:09');
INSERT INTO `conversation` VALUES (39, 3, 'healer', 'user', '我最近压力好大', 'anxious', '2026-04-05 18:16:09');
INSERT INTO `conversation` VALUES (40, 3, 'healer', 'assistant', '嘿，我在这里。压力太大的时候，记得深呼吸。你已经很努力了，给自己一些肯定吧。', 'anxious', '2026-04-05 18:17:09');
INSERT INTO `conversation` VALUES (41, 1, 'listener', 'user', '今天考试没考好，心情很不好', 'sad', '2026-04-05 18:54:59');
INSERT INTO `conversation` VALUES (42, 1, 'listener', 'assistant', '我听到你了，考试没考好一定很沮丧吧。这种感觉确实不好受。你愿意和我多说说吗？', 'sad', '2026-04-05 18:55:59');
INSERT INTO `conversation` VALUES (43, 1, 'listener', 'user', '我觉得自己很失败什么都做不好', 'sad', '2026-04-05 18:56:59');
INSERT INTO `conversation` VALUES (44, 1, 'listener', 'assistant', '考试没考好并不代表你是一个失败的人。每个人都会有状态不好的时候，你愿意来倾诉说明你正在积极面对自己的情绪。试着回想一下过去有没有做得好的事情？', 'sad', '2026-04-05 18:57:59');
INSERT INTO `conversation` VALUES (45, 2, 'analyst', 'user', '我和妈妈吵架了', 'angry', '2026-04-05 17:54:59');
INSERT INTO `conversation` VALUES (46, 2, 'analyst', 'assistant', '和父母发生冲突是很多人都会经历的。能让阿姨告诉我具体是什么事情吗？我们可以一起分析一下。', 'angry', '2026-04-05 17:55:59');
INSERT INTO `conversation` VALUES (47, 2, 'analyst', 'user', '她总是说我学习成绩下降，不让我玩手机', 'angry', '2026-04-05 17:56:59');
INSERT INTO `conversation` VALUES (48, 2, 'analyst', 'assistant', '听起来妈妈的出发点是关心你的学习。或许你可以试着和妈妈沟通，告诉她你需要一个放松的方式，比如限定玩手机的时间。', 'angry', '2026-04-05 17:57:59');
INSERT INTO `conversation` VALUES (49, 3, 'healer', 'user', '我最近压力好大', 'anxious', '2026-04-05 19:24:59');
INSERT INTO `conversation` VALUES (50, 3, 'healer', 'assistant', '嘿，我在这里。压力太大的时候，记得深呼吸。你已经很努力了，给自己一些肯定吧。', 'anxious', '2026-04-05 19:25:59');
INSERT INTO `conversation` VALUES (51, 1, 'listener', 'user', '今天考试没考好，心情很不好', 'sad', '2026-04-05 18:58:15');
INSERT INTO `conversation` VALUES (52, 1, 'listener', 'assistant', '我听到你了，考试没考好一定很沮丧吧。这种感觉确实不好受。你愿意和我多说说吗？', 'sad', '2026-04-05 18:59:15');
INSERT INTO `conversation` VALUES (53, 1, 'listener', 'user', '我觉得自己很失败什么都做不好', 'sad', '2026-04-05 19:00:15');
INSERT INTO `conversation` VALUES (54, 1, 'listener', 'assistant', '考试没考好并不代表你是一个失败的人。每个人都会有状态不好的时候，你愿意来倾诉说明你正在积极面对自己的情绪。试着回想一下过去有没有做得好的事情？', 'sad', '2026-04-05 19:01:15');
INSERT INTO `conversation` VALUES (55, 2, 'analyst', 'user', '我和妈妈吵架了', 'angry', '2026-04-05 17:58:15');
INSERT INTO `conversation` VALUES (56, 2, 'analyst', 'assistant', '和父母发生冲突是很多人都会经历的。能让阿姨告诉我具体是什么事情吗？我们可以一起分析一下。', 'angry', '2026-04-05 17:59:15');
INSERT INTO `conversation` VALUES (57, 2, 'analyst', 'user', '她总是说我学习成绩下降，不让我玩手机', 'angry', '2026-04-05 18:00:15');
INSERT INTO `conversation` VALUES (58, 2, 'analyst', 'assistant', '听起来妈妈的出发点是关心你的学习。或许你可以试着和妈妈沟通，告诉她你需要一个放松的方式，比如限定玩手机的时间。', 'angry', '2026-04-05 18:01:15');
INSERT INTO `conversation` VALUES (59, 3, 'healer', 'user', '我最近压力好大', 'anxious', '2026-04-05 19:28:15');
INSERT INTO `conversation` VALUES (60, 3, 'healer', 'assistant', '嘿，我在这里。压力太大的时候，记得深呼吸。你已经很努力了，给自己一些肯定吧。', 'anxious', '2026-04-05 19:29:15');
INSERT INTO `conversation` VALUES (61, 1, 'listener', 'user', '今天考试没考好，心情很不好', 'sad', '2026-04-05 21:34:45');
INSERT INTO `conversation` VALUES (62, 1, 'listener', 'assistant', '我听到你了，考试没考好一定很沮丧吧。这种感觉确实不好受。你愿意和我多说说吗？', 'sad', '2026-04-05 21:35:45');
INSERT INTO `conversation` VALUES (63, 1, 'listener', 'user', '我觉得自己很失败什么都做不好', 'sad', '2026-04-05 21:36:45');
INSERT INTO `conversation` VALUES (64, 1, 'listener', 'assistant', '考试没考好并不代表你是一个失败的人。每个人都会有状态不好的时候，你愿意来倾诉说明你正在积极面对自己的情绪。试着回想一下过去有没有做得好的事情？', 'sad', '2026-04-05 21:37:45');
INSERT INTO `conversation` VALUES (65, 2, 'analyst', 'user', '我和妈妈吵架了', 'angry', '2026-04-05 20:34:45');
INSERT INTO `conversation` VALUES (66, 2, 'analyst', 'assistant', '和父母发生冲突是很多人都会经历的。能让阿姨告诉我具体是什么事情吗？我们可以一起分析一下。', 'angry', '2026-04-05 20:35:45');
INSERT INTO `conversation` VALUES (67, 2, 'analyst', 'user', '她总是说我学习成绩下降，不让我玩手机', 'angry', '2026-04-05 20:36:45');
INSERT INTO `conversation` VALUES (68, 2, 'analyst', 'assistant', '听起来妈妈的出发点是关心你的学习。或许你可以试着和妈妈沟通，告诉她你需要一个放松的方式，比如限定玩手机的时间。', 'angry', '2026-04-05 20:37:45');
INSERT INTO `conversation` VALUES (69, 3, 'healer', 'user', '我最近压力好大', 'anxious', '2026-04-05 22:04:45');
INSERT INTO `conversation` VALUES (70, 3, 'healer', 'assistant', '嘿，我在这里。压力太大的时候，记得深呼吸。你已经很努力了，给自己一些肯定吧。', 'anxious', '2026-04-05 22:05:45');
INSERT INTO `conversation` VALUES (71, 1, 'listener', 'user', '今天考试没考好，心情很不好', 'sad', '2026-04-05 21:39:04');
INSERT INTO `conversation` VALUES (72, 1, 'listener', 'assistant', '我听到你了，考试没考好一定很沮丧吧。这种感觉确实不好受。你愿意和我多说说吗？', 'sad', '2026-04-05 21:40:04');
INSERT INTO `conversation` VALUES (73, 1, 'listener', 'user', '我觉得自己很失败什么都做不好', 'sad', '2026-04-05 21:41:04');
INSERT INTO `conversation` VALUES (74, 1, 'listener', 'assistant', '考试没考好并不代表你是一个失败的人。每个人都会有状态不好的时候，你愿意来倾诉说明你正在积极面对自己的情绪。试着回想一下过去有没有做得好的事情？', 'sad', '2026-04-05 21:42:04');
INSERT INTO `conversation` VALUES (75, 2, 'analyst', 'user', '我和妈妈吵架了', 'angry', '2026-04-05 20:39:04');
INSERT INTO `conversation` VALUES (76, 2, 'analyst', 'assistant', '和父母发生冲突是很多人都会经历的。能让阿姨告诉我具体是什么事情吗？我们可以一起分析一下。', 'angry', '2026-04-05 20:40:04');
INSERT INTO `conversation` VALUES (77, 2, 'analyst', 'user', '她总是说我学习成绩下降，不让我玩手机', 'angry', '2026-04-05 20:41:04');
INSERT INTO `conversation` VALUES (78, 2, 'analyst', 'assistant', '听起来妈妈的出发点是关心你的学习。或许你可以试着和妈妈沟通，告诉她你需要一个放松的方式，比如限定玩手机的时间。', 'angry', '2026-04-05 20:42:04');
INSERT INTO `conversation` VALUES (79, 3, 'healer', 'user', '我最近压力好大', 'anxious', '2026-04-05 22:09:04');
INSERT INTO `conversation` VALUES (80, 3, 'healer', 'assistant', '嘿，我在这里。压力太大的时候，记得深呼吸。你已经很努力了，给自己一些肯定吧。', 'anxious', '2026-04-05 22:10:04');
INSERT INTO `conversation` VALUES (81, 32, 'listener', 'user', '我\n', NULL, '2026-04-06 14:50:48');
INSERT INTO `conversation` VALUES (82, 32, 'listener', 'assistant', '我在这里，安静地陪着你。  \n如果愿意，可以慢慢说——无论是什么感受，都可以安心地和我分享。', NULL, '2026-04-06 14:50:48');
INSERT INTO `conversation` VALUES (83, 32, 'listener', 'assistant', '我明白这种迷茫的感觉。  \n你愿意多说说，是什么让你开始思考这个问题吗？', NULL, '2026-04-06 14:50:59');
INSERT INTO `conversation` VALUES (84, 32, 'listener', 'user', '我在想\n我能做什么/', NULL, '2026-04-06 14:50:59');
INSERT INTO `conversation` VALUES (85, 32, 'listener', 'user', '你觉得或者？\n\n', NULL, '2026-04-06 14:51:23');
INSERT INTO `conversation` VALUES (86, 32, 'listener', 'assistant', '我在这里，听你慢慢说。  \n你心里浮现的任何念头，都可以轻轻讲出来。', NULL, '2026-04-06 14:51:23');
INSERT INTO `conversation` VALUES (87, 32, 'analyst', 'assistant', '我感受到你的迷茫和无力感。当我们陷入“我该怎么办”的思维时，往往是被问题压得喘不过气。\n\n试着把大问题拆小：此刻，你能做的一个最小、最具体的行动是什么？哪怕是深呼吸三次，或写下第一个浮现的念头。\n\n不急于找到完美答案，先迈出一小步。你愿意试试吗？', NULL, '2026-04-06 14:51:36');
INSERT INTO `conversation` VALUES (88, 32, 'analyst', 'user', '我该怎么办\n', NULL, '2026-04-06 14:51:36');
INSERT INTO `conversation` VALUES (89, 32, 'analyst', 'user', '我在想提升自己\n', NULL, '2026-04-06 14:51:47');
INSERT INTO `conversation` VALUES (90, 32, 'analyst', 'assistant', '我注意到你在思考“提升自己”，这是一个积极的起点。当我们感到迷茫时，可以先把这个大目标具体化：**今天或本周内，你能做的一件小事是什么？**  \n\n比如读几页书、学习一个新技能的基础知识，或者整理一下自己的优势清单。从小处着手，能帮你建立掌控感和方向感。  \n\n你心中有没有一个模糊想尝试的领域？我们可以一起把它变得更清晰。', NULL, '2026-04-06 14:51:47');
INSERT INTO `conversation` VALUES (91, 32, 'healer', 'user', '心好累分手了\n', NULL, '2026-04-06 14:52:08');
INSERT INTO `conversation` VALUES (92, 32, 'healer', 'assistant', '分手后的心累，我懂。  \n此刻不必急着找答案，先允许自己好好休息。  \n你曾用心去爱，这份勇气本身就有光。  \n慢慢来，我会一直在这里陪着你。', NULL, '2026-04-06 14:52:08');
INSERT INTO `conversation` VALUES (93, 32, 'listener', 'assistant', '听到你的哭声了，心里一定很不好受吧。  \n来，先不着急想“做什么”，让情绪缓一缓。  \n我在这里陪着你。', NULL, '2026-04-07 19:14:10');
INSERT INTO `conversation` VALUES (94, 32, 'listener', 'user', '呜呜呜\n', NULL, '2026-04-07 19:14:10');
INSERT INTO `conversation` VALUES (95, 32, 'listener', 'assistant', '听到你的哭声了，心里一定很不好受吧。  \n来，先不着急想“做什么”，让情绪缓一缓。  \n我在这里陪着你。', NULL, '2026-04-07 19:14:41');
INSERT INTO `conversation` VALUES (96, 32, 'listener', 'user', '呜呜呜\n', NULL, '2026-04-07 19:14:41');
INSERT INTO `conversation` VALUES (97, 1, 'listener', 'assistant', '你好呀，我是你的倾听伙伴。当你需要倾诉时，我会在这里安静地陪伴你，理解你的感受。', NULL, '2026-04-07 19:30:17');
INSERT INTO `conversation` VALUES (98, 1, 'listener', 'user', '你好，请简单介绍一下你自己', NULL, '2026-04-07 19:30:17');
INSERT INTO `conversation` VALUES (99, 32, 'listener', 'user', '呜呜呜\n', NULL, '2026-04-07 19:31:45');
INSERT INTO `conversation` VALUES (100, 32, 'listener', 'assistant', '听到你的哭声了，心里一定很不好受吧。  \n来，先不着急想“做什么”，让情绪缓一缓。  \n我在这里陪着你。', NULL, '2026-04-07 19:31:45');
INSERT INTO `conversation` VALUES (101, 32, 'listener', 'assistant', '听到你带着疲惫的哭声，像是想暂时躲进游戏里透透气。  \n没关系，如果这样能让你缓一缓，就按自己的节奏来。  \n我依然在这里，随时等你倾诉。', NULL, '2026-04-07 19:32:35');
INSERT INTO `conversation` VALUES (102, 32, 'listener', 'user', '呜呜呜玩玩', NULL, '2026-04-07 19:32:35');
INSERT INTO `conversation` VALUES (103, 32, 'listener', 'assistant', '听到你的哭声了，心一定沉甸甸的。  \n想哭就哭出来吧，我会安静地陪着你。', NULL, '2026-04-07 19:33:42');
INSERT INTO `conversation` VALUES (104, 32, 'listener', 'user', '呜呜呜', NULL, '2026-04-07 19:33:42');
INSERT INTO `conversation` VALUES (105, 32, 'listener', 'user', '呜呜呜', NULL, '2026-04-07 19:35:47');
INSERT INTO `conversation` VALUES (106, 32, 'listener', 'assistant', '嗯，我听到了。  \n就让眼泪慢慢流吧，我在这儿安静地陪着你。', NULL, '2026-04-07 19:35:47');
INSERT INTO `conversation` VALUES (107, 32, 'listener', 'user', '呜呜呜', NULL, '2026-04-07 19:36:49');
INSERT INTO `conversation` VALUES (108, 32, 'listener', 'assistant', '嗯，我在这儿呢。  \n你不需要马上说什么，难过的时候，有声音陪着就好。', NULL, '2026-04-07 19:36:49');
INSERT INTO `conversation` VALUES (109, 32, 'listener', 'user', '呜呜呜', NULL, '2026-04-07 19:37:13');
INSERT INTO `conversation` VALUES (110, 32, 'listener', 'assistant', '嗯，我在这儿呢。  \n如果暂时说不出话，我们就一起安静地待一会儿。', NULL, '2026-04-07 19:37:13');
INSERT INTO `conversation` VALUES (111, 32, 'listener', 'user', '呜呜呜', NULL, '2026-04-07 19:37:24');
INSERT INTO `conversation` VALUES (112, 32, 'listener', 'assistant', '嗯，我在这儿呢。  \n累了就歇一歇，我会一直在这里。', NULL, '2026-04-07 19:37:24');
INSERT INTO `conversation` VALUES (113, 32, 'listener', 'assistant', '嗯，我在这儿呢。  \n肩膀借给你靠，我们慢慢来。', NULL, '2026-04-07 19:40:34');
INSERT INTO `conversation` VALUES (114, 32, 'listener', 'user', '呜呜呜', NULL, '2026-04-07 19:40:34');
INSERT INTO `conversation` VALUES (115, 32, 'listener', 'assistant', '嗯，我在这儿呢。  \n你的难过我都收着，不着急。', NULL, '2026-04-07 19:40:46');
INSERT INTO `conversation` VALUES (116, 32, 'listener', 'user', 'w呜呜呜', NULL, '2026-04-07 19:40:46');
INSERT INTO `conversation` VALUES (117, 32, 'listener', 'user', 'www', NULL, '2026-04-07 19:42:51');
INSERT INTO `conversation` VALUES (118, 32, 'listener', 'assistant', '嗯，我在这儿呢。  \n让眼泪流出来也没关系，我陪着你。', NULL, '2026-04-07 19:42:51');
INSERT INTO `conversation` VALUES (119, 32, 'listener', 'assistant', '嗯，我在这儿呢。  \n哭出来心里会好受些，我陪着你把情绪都释放出来。', NULL, '2026-04-07 19:43:06');
INSERT INTO `conversation` VALUES (120, 32, 'listener', 'user', '我一直在哭\n', NULL, '2026-04-07 19:43:06');
INSERT INTO `conversation` VALUES (121, 32, 'listener', 'user', '呜呜呜', NULL, '2026-04-07 19:46:33');
INSERT INTO `conversation` VALUES (122, 32, 'listener', 'assistant', '嗯，我在这儿呢。  \n眼泪会带走一些沉重的，我安静地陪着你。', NULL, '2026-04-07 19:46:33');
INSERT INTO `conversation` VALUES (123, 32, 'listener', 'user', '呜呜呜\n', NULL, '2026-04-07 19:50:48');
INSERT INTO `conversation` VALUES (124, 32, 'listener', 'assistant', '嗯，我在这儿呢。  \n你所有的情绪，都值得被好好接住。', NULL, '2026-04-07 19:50:48');
INSERT INTO `conversation` VALUES (125, 32, 'listener', 'assistant', '嗯，我在这儿呢。  \n你并不孤单，我就在这儿稳稳地陪着你。', NULL, '2026-04-07 19:51:02');
INSERT INTO `conversation` VALUES (126, 32, 'listener', 'user', '呜呜呜', NULL, '2026-04-07 19:51:02');
INSERT INTO `conversation` VALUES (127, 32, 'listener', 'assistant', '嗯，我在这儿呢。  \n你心里一定很不好受吧，我就在这儿陪着你。', NULL, '2026-04-07 20:09:27');
INSERT INTO `conversation` VALUES (128, 32, 'listener', 'user', '呜呜呜', NULL, '2026-04-07 20:09:27');
INSERT INTO `conversation` VALUES (129, 32, 'listener', 'user', 'hello啊', NULL, '2026-04-07 20:12:31');
INSERT INTO `conversation` VALUES (130, 32, 'listener', 'assistant', '嗯，你来了。  \n我还在呢，今天心里感觉怎么样？', NULL, '2026-04-07 20:12:31');
INSERT INTO `conversation` VALUES (131, 32, 'listener', 'user', '为什么', NULL, '2026-04-07 20:12:44');
INSERT INTO `conversation` VALUES (132, 32, 'listener', 'assistant', '嗯，你问“为什么”的时候，心里一定有很多困惑和委屈吧。  \n我在这儿，慢慢听你说。', NULL, '2026-04-07 20:12:44');
INSERT INTO `conversation` VALUES (133, 32, 'listener', 'assistant', '嗯，我听到了。  \n让这些声音和情绪都出来吧，我在这儿稳稳地接着。', NULL, '2026-04-07 20:13:59');
INSERT INTO `conversation` VALUES (134, 32, 'listener', 'user', '啊啊', NULL, '2026-04-07 20:13:59');
INSERT INTO `conversation` VALUES (135, 32, 'listener', 'user', '我一直在哭', NULL, '2026-04-07 20:14:09');
INSERT INTO `conversation` VALUES (136, 32, 'listener', 'assistant', '嗯，眼泪流出来一定很累吧。  \n我在这儿陪着你，不用着急，慢慢来。', NULL, '2026-04-07 20:14:09');
INSERT INTO `conversation` VALUES (137, 32, 'listener', 'user', '啊', NULL, '2026-04-07 20:15:07');
INSERT INTO `conversation` VALUES (138, 32, 'listener', 'assistant', '嗯，我听到了。  \n让这一声叹息也轻轻落下吧，我在这儿呢。', NULL, '2026-04-07 20:15:07');
INSERT INTO `conversation` VALUES (139, 32, 'listener', 'assistant', '嗯，听到你的哭声了。  \n心里一定积攒了很多难过吧，我在这儿陪着你。', NULL, '2026-04-07 20:17:21');
INSERT INTO `conversation` VALUES (140, 32, 'listener', 'user', '呜呜呜', NULL, '2026-04-07 20:17:21');
INSERT INTO `conversation` VALUES (141, 32, 'listener', 'assistant', '嗯，眼泪像止不住的雨呢。  \n我在这儿，陪着你一起淋这场雨。', NULL, '2026-04-07 20:17:28');
INSERT INTO `conversation` VALUES (142, 32, 'listener', 'user', '呜呜呜', NULL, '2026-04-07 20:17:28');
INSERT INTO `conversation` VALUES (143, 32, 'listener', 'user', '呜呜呜', NULL, '2026-04-07 20:19:00');
INSERT INTO `conversation` VALUES (144, 32, 'listener', 'assistant', '嗯，眼泪又来了呢。  \n我依然在这儿，稳稳地陪着你。', NULL, '2026-04-07 20:19:00');
INSERT INTO `conversation` VALUES (145, 32, 'analyst', 'user', '我的室友万新喜欢狗叫\n', NULL, '2026-04-08 08:21:05');
INSERT INTO `conversation` VALUES (146, 32, 'analyst', 'assistant', '嗯，听到你一直在哭，也提到了室友的情况。这确实会让人感到疲惫和困扰。\n\n我们可以试着把这件事分开来看：一是你持续的情绪低落，二是室友行为带来的影响。你愿意先谈谈，是哪个部分让你此刻更难受吗？\n\n我在这儿，陪你一起梳理。', NULL, '2026-04-08 08:21:05');
INSERT INTO `conversation` VALUES (147, 32, 'analyst', 'assistant', '听起来室友的行为让你感到困扰。我们可以先区分事实和感受：他模仿狗叫是客观事件，而你可能因此产生烦躁或不安的情绪。\n\n试着观察这个行为发生时，你脑海中自动浮现的想法是什么？比如“这很烦人”或“他不尊重我”。识别这些想法是调整情绪的第一步。\n\n我们可以一起探讨如何用更平衡的视角看待这件事，比如直接沟通或调整应对方式。你愿意具体说说这种情况发生的场景吗？', NULL, '2026-04-08 08:21:32');
INSERT INTO `conversation` VALUES (148, 32, 'analyst', 'user', '我的室友万新喜欢狗叫', NULL, '2026-04-08 08:21:32');
INSERT INTO `conversation` VALUES (149, 32, 'analyst', 'assistant', '嗯，你确认了，这些情绪是真实且强烈的。\n\n当我们被情绪淹没时，可以先做一个简单的“着陆”练习：感受双脚踩在地上的感觉，做一次缓慢的深呼吸。这能帮我们稍微稳住自己。\n\n你愿意在感觉好一点后，和我描述一下“是的”背后，具体是哪一种感受吗？比如是无力、委屈，还是别的？', NULL, '2026-04-08 08:21:43');
INSERT INTO `conversation` VALUES (150, 32, 'analyst', 'user', '是的是的', NULL, '2026-04-08 08:21:43');
INSERT INTO `conversation` VALUES (151, 32, 'healer', 'user', '我的室友万新喜欢狗叫', NULL, '2026-04-08 08:22:02');
INSERT INTO `conversation` VALUES (152, 32, 'healer', 'assistant', '听起来这件事反复困扰着你，像心里压着一块石头。  \n我们可以先试着在情绪中安顿下来：你此刻最希望被理解的是什么？  \n我在这儿，陪你一起面对。', NULL, '2026-04-08 08:22:02');
INSERT INTO `conversation` VALUES (153, 32, 'healer', 'user', '他还是在狗叫', NULL, '2026-04-08 08:22:13');
INSERT INTO `conversation` VALUES (154, 32, 'healer', 'assistant', '听到他还在这样，你一定感到既疲惫又无力。  \n\n但请记得，他的行为并不能定义你的价值。你拥有为自己创造安宁的能力，无论是调整心态，还是寻找解决方法。  \n\n我在这里，相信你能找到属于自己的平静角落。', NULL, '2026-04-08 08:22:13');

-- ----------------------------
-- Table structure for diary
-- ----------------------------
DROP TABLE IF EXISTS `diary`;
CREATE TABLE `diary`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '日记内容',
  `media_urls` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '媒体URL(JSON数组)',
  `emotion_tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '情绪标签(JSON数组)',
  `emotion_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '情感得分(-1到1)',
  `emotion_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '情感类别',
  `ai_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI分析结果',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `diary_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 75 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '情绪日记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of diary
-- ----------------------------
INSERT INTO `diary` VALUES (1, 1, '今天考试没考好，心情很低落。但是想了想，一次考试并不能决定什么，下次继续努力吧。人生还有很长的路要走，不应该被一次失败打倒。', NULL, '[\"考试\", \"失落\", \"自我安慰\"]', -0.30, 'sad', '从你的日记中，我感受到你正在经历考试失利的挫折。这是一个很常见的情绪体验。值得注意的是，你在文字中已经展现出了很好的自我调节能力，能够客观地看待这次经历。继续保持这种积极的思维方式。', '2026-04-02 14:53:01');
INSERT INTO `diary` VALUES (2, 1, '和朋友们一起看了电影，笑得很开心。享受当下最简单的快乐。', NULL, '[\"朋友\", \"电影\", \"快乐\"]', 0.85, 'happy', '很高兴看到你今天心情这么好。与朋友一起观看电影是一种很好的放松方式，可以带来积极的情绪体验。继续珍惜这样的快乐时光。', '2026-04-01 14:53:01');
INSERT INTO `diary` VALUES (3, 1, '今天进行了期末汇报，虽然有点紧张但整体表现还不错，给自己点个赞。', NULL, '[\"汇报\", \"成长\", \"自信\"]', 0.75, 'happy', '你在日记中提到了期末汇报，这是一个挑战但也是成长的机会。你能够认识到自己的进步并给自己积极的反馈，这是很棒的心理品质。', '2026-03-31 14:53:01');
INSERT INTO `diary` VALUES (4, 1, '晚上睡不着觉，想了很多事情，关于未来的迷茫和焦虑。', NULL, '[\"失眠\", \"焦虑\", \"迷茫\"]', -0.45, 'anxious', '感谢你的分享。睡前思绪纷乱是很常见的现象，这可能与压力有关。建议可以尝试一些放松技巧，如深呼吸或冥想，帮助入睡。', '2026-03-30 14:53:01');
INSERT INTO `diary` VALUES (5, 1, '今天阳光很好，心情也很平静。一个人在公园散步，享受安静的时光。', NULL, '[\"阳光\", \"平静\", \"独处\"]', 0.60, 'calm', '很高兴你今天找到了内心的平静。阳光和自然环境确实对情绪有积极的影响。享受独处时光是一种很好的自我调节方式。', '2026-03-29 14:53:01');
INSERT INTO `diary` VALUES (6, 2, '今天和妈妈吵架了，感觉她一点都不理解我。', NULL, '[\"家庭\", \"争吵\", \"委屈\"]', -0.55, 'sad', '我理解你感到委屈和不被理解的痛苦。与父母沟通确实是需要技巧的。或许可以试着冷静下来后，用更平和的方式表达自己的感受。', '2026-04-02 14:53:01');
INSERT INTO `diary` VALUES (7, 2, '体育课跑步跑了第一名，很开心。', NULL, '[\"运动\", \"成就\", \"开心\"]', 0.90, 'happy', '太棒了。运动带来的成就感是非常积极的情绪体验。继续保持在运动中的热情，这对你身心健康都很有好处。', '2026-04-01 14:53:01');
INSERT INTO `diary` VALUES (8, 3, '今天数学考试考了满分，太激动了。', NULL, '[\"学习\", \"成就\", \"激动\"]', 0.95, 'happy', '恭喜你。取得好成绩是对努力学习最好的回报。但也要记得，成绩只是生活的一部分，无论好坏都要保持平衡的心态。', '2026-04-02 14:53:01');
INSERT INTO `diary` VALUES (9, 3, '有点担心下周的音乐比赛', NULL, '[\"比赛\", \"紧张\", \"担忧\"]', -0.25, 'anxious', '比赛前的紧张是很正常的反应。这种紧张感其实可以帮助你更好地准备。试着把这种能量转化为练习的动力相信自己，你一定可以的。', '2026-03-31 14:53:01');
INSERT INTO `diary` VALUES (10, 1, '今天考试没考好，心情很低落。但是想了想，一次考试并不能决定什么，下次继续努力吧。人生还有很长的路要走，不应该被一次失败打倒。', NULL, '[\"考试\", \"失落\", \"自我安慰\"]', -0.30, 'sad', '从你的日记中，我感受到你正在经历考试失利的挫折。这是一个很常见的情绪体验。值得注意的是，你在文字中已经展现出了很好的自我调节能力，能够客观地看待这次经历。继续保持这种积极的思维方式。', '2026-04-02 15:01:21');
INSERT INTO `diary` VALUES (11, 1, '和朋友们一起看了电影，笑得很开心。享受当下最简单的快乐。', NULL, '[\"朋友\", \"电影\", \"快乐\"]', 0.85, 'happy', '很高兴看到你今天心情这么好。与朋友一起观看电影是一种很好的放松方式，可以带来积极的情绪体验。继续珍惜这样的快乐时光。', '2026-04-01 15:01:21');
INSERT INTO `diary` VALUES (12, 1, '今天进行了期末汇报，虽然有点紧张但整体表现还不错，给自己点个赞。', NULL, '[\"汇报\", \"成长\", \"自信\"]', 0.75, 'happy', '你在日记中提到了期末汇报，这是一个挑战但也是成长的机会。你能够认识到自己的进步并给自己积极的反馈，这是很棒的心理品质。', '2026-03-31 15:01:21');
INSERT INTO `diary` VALUES (13, 1, '晚上睡不着觉，想了很多事情，关于未来的迷茫和焦虑。', NULL, '[\"失眠\", \"焦虑\", \"迷茫\"]', -0.45, 'anxious', '感谢你的分享。睡前思绪纷乱是很常见的现象，这可能与压力有关。建议可以尝试一些放松技巧，如深呼吸或冥想，帮助入睡。', '2026-03-30 15:01:21');
INSERT INTO `diary` VALUES (14, 1, '今天阳光很好，心情也很平静。一个人在公园散步，享受安静的时光。', NULL, '[\"阳光\", \"平静\", \"独处\"]', 0.60, 'calm', '很高兴你今天找到了内心的平静。阳光和自然环境确实对情绪有积极的影响。享受独处时光是一种很好的自我调节方式。', '2026-03-29 15:01:21');
INSERT INTO `diary` VALUES (15, 2, '今天和妈妈吵架了，感觉她一点都不理解我。', NULL, '[\"家庭\", \"争吵\", \"委屈\"]', -0.55, 'sad', '我理解你感到委屈和不被理解的痛苦。与父母沟通确实是需要技巧的。或许可以试着冷静下来后，用更平和的方式表达自己的感受。', '2026-04-02 15:01:21');
INSERT INTO `diary` VALUES (16, 2, '体育课跑步跑了第一名，很开心。', NULL, '[\"运动\", \"成就\", \"开心\"]', 0.90, 'happy', '太棒了。运动带来的成就感是非常积极的情绪体验。继续保持在运动中的热情，这对你身心健康都很有好处。', '2026-04-01 15:01:21');
INSERT INTO `diary` VALUES (17, 3, '今天数学考试考了满分，太激动了。', NULL, '[\"学习\", \"成就\", \"激动\"]', 0.95, 'happy', '恭喜你。取得好成绩是对努力学习最好的回报。但也要记得，成绩只是生活的一部分，无论好坏都要保持平衡的心态。', '2026-04-02 15:01:21');
INSERT INTO `diary` VALUES (18, 3, '有点担心下周的音乐比赛', NULL, '[\"比赛\", \"紧张\", \"担忧\"]', -0.25, 'anxious', '比赛前的紧张是很正常的反应。这种紧张感其实可以帮助你更好地准备。试着把这种能量转化为练习的动力相信自己，你一定可以的。', '2026-03-31 15:01:21');
INSERT INTO `diary` VALUES (19, 1, '今天考试没考好，心情很低落。但是想了想，一次考试并不能决定什么，下次继续努力吧。人生还有很长的路要走，不应该被一次失败打倒。', NULL, '[\"考试\", \"失落\", \"自我安慰\"]', -0.30, 'sad', '从你的日记中，我感受到你正在经历考试失利的挫折。这是一个很常见的情绪体验。值得注意的是，你在文字中已经展现出了很好的自我调节能力，能够客观地看待这次经历。继续保持这种积极的思维方式。', '2026-04-02 15:03:39');
INSERT INTO `diary` VALUES (20, 1, '和朋友们一起看了电影，笑得很开心。享受当下最简单的快乐。', NULL, '[\"朋友\", \"电影\", \"快乐\"]', 0.85, 'happy', '很高兴看到你今天心情这么好。与朋友一起观看电影是一种很好的放松方式，可以带来积极的情绪体验。继续珍惜这样的快乐时光。', '2026-04-01 15:03:39');
INSERT INTO `diary` VALUES (21, 1, '今天进行了期末汇报，虽然有点紧张但整体表现还不错，给自己点个赞。', NULL, '[\"汇报\", \"成长\", \"自信\"]', 0.75, 'happy', '你在日记中提到了期末汇报，这是一个挑战但也是成长的机会。你能够认识到自己的进步并给自己积极的反馈，这是很棒的心理品质。', '2026-03-31 15:03:39');
INSERT INTO `diary` VALUES (22, 1, '晚上睡不着觉，想了很多事情，关于未来的迷茫和焦虑。', NULL, '[\"失眠\", \"焦虑\", \"迷茫\"]', -0.45, 'anxious', '感谢你的分享。睡前思绪纷乱是很常见的现象，这可能与压力有关。建议可以尝试一些放松技巧，如深呼吸或冥想，帮助入睡。', '2026-03-30 15:03:39');
INSERT INTO `diary` VALUES (23, 1, '今天阳光很好，心情也很平静。一个人在公园散步，享受安静的时光。', NULL, '[\"阳光\", \"平静\", \"独处\"]', 0.60, 'calm', '很高兴你今天找到了内心的平静。阳光和自然环境确实对情绪有积极的影响。享受独处时光是一种很好的自我调节方式。', '2026-03-29 15:03:39');
INSERT INTO `diary` VALUES (24, 2, '今天和妈妈吵架了，感觉她一点都不理解我。', NULL, '[\"家庭\", \"争吵\", \"委屈\"]', -0.55, 'sad', '我理解你感到委屈和不被理解的痛苦。与父母沟通确实是需要技巧的。或许可以试着冷静下来后，用更平和的方式表达自己的感受。', '2026-04-02 15:03:39');
INSERT INTO `diary` VALUES (25, 2, '体育课跑步跑了第一名，很开心。', NULL, '[\"运动\", \"成就\", \"开心\"]', 0.90, 'happy', '太棒了。运动带来的成就感是非常积极的情绪体验。继续保持在运动中的热情，这对你身心健康都很有好处。', '2026-04-01 15:03:39');
INSERT INTO `diary` VALUES (26, 3, '今天数学考试考了满分，太激动了。', NULL, '[\"学习\", \"成就\", \"激动\"]', 0.95, 'happy', '恭喜你。取得好成绩是对努力学习最好的回报。但也要记得，成绩只是生活的一部分，无论好坏都要保持平衡的心态。', '2026-04-02 15:03:39');
INSERT INTO `diary` VALUES (27, 3, '有点担心下周的音乐比赛', NULL, '[\"比赛\", \"紧张\", \"担忧\"]', -0.25, 'anxious', '比赛前的紧张是很正常的反应。这种紧张感其实可以帮助你更好地准备。试着把这种能量转化为练习的动力相信自己，你一定可以的。', '2026-03-31 15:03:39');
INSERT INTO `diary` VALUES (28, 1, '今天考试没考好，心情很低落。但是想了想，一次考试并不能决定什么，下次继续努力吧。人生还有很长的路要走，不应该被一次失败打倒。', NULL, '[\"考试\", \"失落\", \"自我安慰\"]', -0.30, 'sad', '从你的日记中，我感受到你正在经历考试失利的挫折。这是一个很常见的情绪体验。值得注意的是，你在文字中已经展现出了很好的自我调节能力，能够客观地看待这次经历。继续保持这种积极的思维方式。', '2026-04-04 18:46:09');
INSERT INTO `diary` VALUES (29, 1, '和朋友们一起看了电影，笑得很开心。享受当下最简单的快乐。', NULL, '[\"朋友\", \"电影\", \"快乐\"]', 0.85, 'happy', '很高兴看到你今天心情这么好。与朋友一起观看电影是一种很好的放松方式，可以带来积极的情绪体验。继续珍惜这样的快乐时光。', '2026-04-03 18:46:09');
INSERT INTO `diary` VALUES (30, 1, '今天进行了期末汇报，虽然有点紧张但整体表现还不错，给自己点个赞。', NULL, '[\"汇报\", \"成长\", \"自信\"]', 0.75, 'happy', '你在日记中提到了期末汇报，这是一个挑战但也是成长的机会。你能够认识到自己的进步并给自己积极的反馈，这是很棒的心理品质。', '2026-04-02 18:46:09');
INSERT INTO `diary` VALUES (31, 1, '晚上睡不着觉，想了很多事情，关于未来的迷茫和焦虑。', NULL, '[\"失眠\", \"焦虑\", \"迷茫\"]', -0.45, 'anxious', '感谢你的分享。睡前思绪纷乱是很常见的现象，这可能与压力有关。建议可以尝试一些放松技巧，如深呼吸或冥想，帮助入睡。', '2026-04-01 18:46:09');
INSERT INTO `diary` VALUES (32, 1, '今天阳光很好，心情也很平静。一个人在公园散步，享受安静的时光。', NULL, '[\"阳光\", \"平静\", \"独处\"]', 0.60, 'calm', '很高兴你今天找到了内心的平静。阳光和自然环境确实对情绪有积极的影响。享受独处时光是一种很好的自我调节方式。', '2026-03-31 18:46:09');
INSERT INTO `diary` VALUES (33, 2, '今天和妈妈吵架了，感觉她一点都不理解我。', NULL, '[\"家庭\", \"争吵\", \"委屈\"]', -0.55, 'sad', '我理解你感到委屈和不被理解的痛苦。与父母沟通确实是需要技巧的。或许可以试着冷静下来后，用更平和的方式表达自己的感受。', '2026-04-04 18:46:09');
INSERT INTO `diary` VALUES (34, 2, '体育课跑步跑了第一名，很开心。', NULL, '[\"运动\", \"成就\", \"开心\"]', 0.90, 'happy', '太棒了。运动带来的成就感是非常积极的情绪体验。继续保持在运动中的热情，这对你身心健康都很有好处。', '2026-04-03 18:46:09');
INSERT INTO `diary` VALUES (35, 3, '今天数学考试考了满分，太激动了。', NULL, '[\"学习\", \"成就\", \"激动\"]', 0.95, 'happy', '恭喜你。取得好成绩是对努力学习最好的回报。但也要记得，成绩只是生活的一部分，无论好坏都要保持平衡的心态。', '2026-04-04 18:46:09');
INSERT INTO `diary` VALUES (36, 3, '有点担心下周的音乐比赛', NULL, '[\"比赛\", \"紧张\", \"担忧\"]', -0.25, 'anxious', '比赛前的紧张是很正常的反应。这种紧张感其实可以帮助你更好地准备。试着把这种能量转化为练习的动力相信自己，你一定可以的。', '2026-04-02 18:46:09');
INSERT INTO `diary` VALUES (37, 1, '今天考试没考好，心情很低落。但是想了想，一次考试并不能决定什么，下次继续努力吧。人生还有很长的路要走，不应该被一次失败打倒。', NULL, '[\"考试\", \"失落\", \"自我安慰\"]', -0.30, 'sad', '从你的日记中，我感受到你正在经历考试失利的挫折。这是一个很常见的情绪体验。值得注意的是，你在文字中已经展现出了很好的自我调节能力，能够客观地看待这次经历。继续保持这种积极的思维方式。', '2026-04-04 19:54:58');
INSERT INTO `diary` VALUES (38, 1, '和朋友们一起看了电影，笑得很开心。享受当下最简单的快乐。', NULL, '[\"朋友\", \"电影\", \"快乐\"]', 0.85, 'happy', '很高兴看到你今天心情这么好。与朋友一起观看电影是一种很好的放松方式，可以带来积极的情绪体验。继续珍惜这样的快乐时光。', '2026-04-03 19:54:58');
INSERT INTO `diary` VALUES (39, 1, '今天进行了期末汇报，虽然有点紧张但整体表现还不错，给自己点个赞。', NULL, '[\"汇报\", \"成长\", \"自信\"]', 0.75, 'happy', '你在日记中提到了期末汇报，这是一个挑战但也是成长的机会。你能够认识到自己的进步并给自己积极的反馈，这是很棒的心理品质。', '2026-04-02 19:54:58');
INSERT INTO `diary` VALUES (40, 1, '晚上睡不着觉，想了很多事情，关于未来的迷茫和焦虑。', NULL, '[\"失眠\", \"焦虑\", \"迷茫\"]', -0.45, 'anxious', '感谢你的分享。睡前思绪纷乱是很常见的现象，这可能与压力有关。建议可以尝试一些放松技巧，如深呼吸或冥想，帮助入睡。', '2026-04-01 19:54:58');
INSERT INTO `diary` VALUES (41, 1, '今天阳光很好，心情也很平静。一个人在公园散步，享受安静的时光。', NULL, '[\"阳光\", \"平静\", \"独处\"]', 0.60, 'calm', '很高兴你今天找到了内心的平静。阳光和自然环境确实对情绪有积极的影响。享受独处时光是一种很好的自我调节方式。', '2026-03-31 19:54:58');
INSERT INTO `diary` VALUES (42, 2, '今天和妈妈吵架了，感觉她一点都不理解我。', NULL, '[\"家庭\", \"争吵\", \"委屈\"]', -0.55, 'sad', '我理解你感到委屈和不被理解的痛苦。与父母沟通确实是需要技巧的。或许可以试着冷静下来后，用更平和的方式表达自己的感受。', '2026-04-04 19:54:58');
INSERT INTO `diary` VALUES (43, 2, '体育课跑步跑了第一名，很开心。', NULL, '[\"运动\", \"成就\", \"开心\"]', 0.90, 'happy', '太棒了。运动带来的成就感是非常积极的情绪体验。继续保持在运动中的热情，这对你身心健康都很有好处。', '2026-04-03 19:54:58');
INSERT INTO `diary` VALUES (44, 3, '今天数学考试考了满分，太激动了。', NULL, '[\"学习\", \"成就\", \"激动\"]', 0.95, 'happy', '恭喜你。取得好成绩是对努力学习最好的回报。但也要记得，成绩只是生活的一部分，无论好坏都要保持平衡的心态。', '2026-04-04 19:54:58');
INSERT INTO `diary` VALUES (45, 3, '有点担心下周的音乐比赛', NULL, '[\"比赛\", \"紧张\", \"担忧\"]', -0.25, 'anxious', '比赛前的紧张是很正常的反应。这种紧张感其实可以帮助你更好地准备。试着把这种能量转化为练习的动力相信自己，你一定可以的。', '2026-04-02 19:54:58');
INSERT INTO `diary` VALUES (46, 1, '今天考试没考好，心情很低落。但是想了想，一次考试并不能决定什么，下次继续努力吧。人生还有很长的路要走，不应该被一次失败打倒。', NULL, '[\"考试\", \"失落\", \"自我安慰\"]', -0.30, 'sad', '从你的日记中，我感受到你正在经历考试失利的挫折。这是一个很常见的情绪体验。值得注意的是，你在文字中已经展现出了很好的自我调节能力，能够客观地看待这次经历。继续保持这种积极的思维方式。', '2026-04-04 19:58:15');
INSERT INTO `diary` VALUES (47, 1, '和朋友们一起看了电影，笑得很开心。享受当下最简单的快乐。', NULL, '[\"朋友\", \"电影\", \"快乐\"]', 0.85, 'happy', '很高兴看到你今天心情这么好。与朋友一起观看电影是一种很好的放松方式，可以带来积极的情绪体验。继续珍惜这样的快乐时光。', '2026-04-03 19:58:15');
INSERT INTO `diary` VALUES (48, 1, '今天进行了期末汇报，虽然有点紧张但整体表现还不错，给自己点个赞。', NULL, '[\"汇报\", \"成长\", \"自信\"]', 0.75, 'happy', '你在日记中提到了期末汇报，这是一个挑战但也是成长的机会。你能够认识到自己的进步并给自己积极的反馈，这是很棒的心理品质。', '2026-04-02 19:58:15');
INSERT INTO `diary` VALUES (49, 1, '晚上睡不着觉，想了很多事情，关于未来的迷茫和焦虑。', NULL, '[\"失眠\", \"焦虑\", \"迷茫\"]', -0.45, 'anxious', '感谢你的分享。睡前思绪纷乱是很常见的现象，这可能与压力有关。建议可以尝试一些放松技巧，如深呼吸或冥想，帮助入睡。', '2026-04-01 19:58:15');
INSERT INTO `diary` VALUES (50, 1, '今天阳光很好，心情也很平静。一个人在公园散步，享受安静的时光。', NULL, '[\"阳光\", \"平静\", \"独处\"]', 0.60, 'calm', '很高兴你今天找到了内心的平静。阳光和自然环境确实对情绪有积极的影响。享受独处时光是一种很好的自我调节方式。', '2026-03-31 19:58:15');
INSERT INTO `diary` VALUES (51, 2, '今天和妈妈吵架了，感觉她一点都不理解我。', NULL, '[\"家庭\", \"争吵\", \"委屈\"]', -0.55, 'sad', '我理解你感到委屈和不被理解的痛苦。与父母沟通确实是需要技巧的。或许可以试着冷静下来后，用更平和的方式表达自己的感受。', '2026-04-04 19:58:15');
INSERT INTO `diary` VALUES (52, 2, '体育课跑步跑了第一名，很开心。', NULL, '[\"运动\", \"成就\", \"开心\"]', 0.90, 'happy', '太棒了。运动带来的成就感是非常积极的情绪体验。继续保持在运动中的热情，这对你身心健康都很有好处。', '2026-04-03 19:58:15');
INSERT INTO `diary` VALUES (53, 3, '今天数学考试考了满分，太激动了。', NULL, '[\"学习\", \"成就\", \"激动\"]', 0.95, 'happy', '恭喜你。取得好成绩是对努力学习最好的回报。但也要记得，成绩只是生活的一部分，无论好坏都要保持平衡的心态。', '2026-04-04 19:58:15');
INSERT INTO `diary` VALUES (54, 3, '有点担心下周的音乐比赛', NULL, '[\"比赛\", \"紧张\", \"担忧\"]', -0.25, 'anxious', '比赛前的紧张是很正常的反应。这种紧张感其实可以帮助你更好地准备。试着把这种能量转化为练习的动力相信自己，你一定可以的。', '2026-04-02 19:58:15');
INSERT INTO `diary` VALUES (55, 1, '今天考试没考好，心情很低落。但是想了想，一次考试并不能决定什么，下次继续努力吧。人生还有很长的路要走，不应该被一次失败打倒。', NULL, '[\"考试\", \"失落\", \"自我安慰\"]', -0.30, 'sad', '从你的日记中，我感受到你正在经历考试失利的挫折。这是一个很常见的情绪体验。值得注意的是，你在文字中已经展现出了很好的自我调节能力，能够客观地看待这次经历。继续保持这种积极的思维方式。', '2026-04-04 22:34:45');
INSERT INTO `diary` VALUES (56, 1, '和朋友们一起看了电影，笑得很开心。享受当下最简单的快乐。', NULL, '[\"朋友\", \"电影\", \"快乐\"]', 0.85, 'happy', '很高兴看到你今天心情这么好。与朋友一起观看电影是一种很好的放松方式，可以带来积极的情绪体验。继续珍惜这样的快乐时光。', '2026-04-03 22:34:45');
INSERT INTO `diary` VALUES (57, 1, '今天进行了期末汇报，虽然有点紧张但整体表现还不错，给自己点个赞。', NULL, '[\"汇报\", \"成长\", \"自信\"]', 0.75, 'happy', '你在日记中提到了期末汇报，这是一个挑战但也是成长的机会。你能够认识到自己的进步并给自己积极的反馈，这是很棒的心理品质。', '2026-04-02 22:34:45');
INSERT INTO `diary` VALUES (58, 1, '晚上睡不着觉，想了很多事情，关于未来的迷茫和焦虑。', NULL, '[\"失眠\", \"焦虑\", \"迷茫\"]', -0.45, 'anxious', '感谢你的分享。睡前思绪纷乱是很常见的现象，这可能与压力有关。建议可以尝试一些放松技巧，如深呼吸或冥想，帮助入睡。', '2026-04-01 22:34:45');
INSERT INTO `diary` VALUES (59, 1, '今天阳光很好，心情也很平静。一个人在公园散步，享受安静的时光。', NULL, '[\"阳光\", \"平静\", \"独处\"]', 0.60, 'calm', '很高兴你今天找到了内心的平静。阳光和自然环境确实对情绪有积极的影响。享受独处时光是一种很好的自我调节方式。', '2026-03-31 22:34:45');
INSERT INTO `diary` VALUES (60, 2, '今天和妈妈吵架了，感觉她一点都不理解我。', NULL, '[\"家庭\", \"争吵\", \"委屈\"]', -0.55, 'sad', '我理解你感到委屈和不被理解的痛苦。与父母沟通确实是需要技巧的。或许可以试着冷静下来后，用更平和的方式表达自己的感受。', '2026-04-04 22:34:45');
INSERT INTO `diary` VALUES (61, 2, '体育课跑步跑了第一名，很开心。', NULL, '[\"运动\", \"成就\", \"开心\"]', 0.90, 'happy', '太棒了。运动带来的成就感是非常积极的情绪体验。继续保持在运动中的热情，这对你身心健康都很有好处。', '2026-04-03 22:34:45');
INSERT INTO `diary` VALUES (62, 3, '今天数学考试考了满分，太激动了。', NULL, '[\"学习\", \"成就\", \"激动\"]', 0.95, 'happy', '恭喜你。取得好成绩是对努力学习最好的回报。但也要记得，成绩只是生活的一部分，无论好坏都要保持平衡的心态。', '2026-04-04 22:34:45');
INSERT INTO `diary` VALUES (63, 3, '有点担心下周的音乐比赛', NULL, '[\"比赛\", \"紧张\", \"担忧\"]', -0.25, 'anxious', '比赛前的紧张是很正常的反应。这种紧张感其实可以帮助你更好地准备。试着把这种能量转化为练习的动力相信自己，你一定可以的。', '2026-04-02 22:34:45');
INSERT INTO `diary` VALUES (64, 1, '今天考试没考好，心情很低落。但是想了想，一次考试并不能决定什么，下次继续努力吧。人生还有很长的路要走，不应该被一次失败打倒。', NULL, '[\"考试\", \"失落\", \"自我安慰\"]', -0.30, 'sad', '从你的日记中，我感受到你正在经历考试失利的挫折。这是一个很常见的情绪体验。值得注意的是，你在文字中已经展现出了很好的自我调节能力，能够客观地看待这次经历。继续保持这种积极的思维方式。', '2026-04-04 22:39:03');
INSERT INTO `diary` VALUES (65, 1, '和朋友们一起看了电影，笑得很开心。享受当下最简单的快乐。', NULL, '[\"朋友\", \"电影\", \"快乐\"]', 0.85, 'happy', '很高兴看到你今天心情这么好。与朋友一起观看电影是一种很好的放松方式，可以带来积极的情绪体验。继续珍惜这样的快乐时光。', '2026-04-03 22:39:03');
INSERT INTO `diary` VALUES (66, 1, '今天进行了期末汇报，虽然有点紧张但整体表现还不错，给自己点个赞。', NULL, '[\"汇报\", \"成长\", \"自信\"]', 0.75, 'happy', '你在日记中提到了期末汇报，这是一个挑战但也是成长的机会。你能够认识到自己的进步并给自己积极的反馈，这是很棒的心理品质。', '2026-04-02 22:39:03');
INSERT INTO `diary` VALUES (67, 1, '晚上睡不着觉，想了很多事情，关于未来的迷茫和焦虑。', NULL, '[\"失眠\", \"焦虑\", \"迷茫\"]', -0.45, 'anxious', '感谢你的分享。睡前思绪纷乱是很常见的现象，这可能与压力有关。建议可以尝试一些放松技巧，如深呼吸或冥想，帮助入睡。', '2026-04-01 22:39:03');
INSERT INTO `diary` VALUES (68, 1, '今天阳光很好，心情也很平静。一个人在公园散步，享受安静的时光。', NULL, '[\"阳光\", \"平静\", \"独处\"]', 0.60, 'calm', '很高兴你今天找到了内心的平静。阳光和自然环境确实对情绪有积极的影响。享受独处时光是一种很好的自我调节方式。', '2026-03-31 22:39:03');
INSERT INTO `diary` VALUES (69, 2, '今天和妈妈吵架了，感觉她一点都不理解我。', NULL, '[\"家庭\", \"争吵\", \"委屈\"]', -0.55, 'sad', '我理解你感到委屈和不被理解的痛苦。与父母沟通确实是需要技巧的。或许可以试着冷静下来后，用更平和的方式表达自己的感受。', '2026-04-04 22:39:03');
INSERT INTO `diary` VALUES (70, 2, '体育课跑步跑了第一名，很开心。', NULL, '[\"运动\", \"成就\", \"开心\"]', 0.90, 'happy', '太棒了。运动带来的成就感是非常积极的情绪体验。继续保持在运动中的热情，这对你身心健康都很有好处。', '2026-04-03 22:39:03');
INSERT INTO `diary` VALUES (71, 3, '今天数学考试考了满分，太激动了。', NULL, '[\"学习\", \"成就\", \"激动\"]', 0.95, 'happy', '恭喜你。取得好成绩是对努力学习最好的回报。但也要记得，成绩只是生活的一部分，无论好坏都要保持平衡的心态。', '2026-04-04 22:39:03');
INSERT INTO `diary` VALUES (72, 3, '有点担心下周的音乐比赛', NULL, '[\"比赛\", \"紧张\", \"担忧\"]', -0.25, 'anxious', '比赛前的紧张是很正常的反应。这种紧张感其实可以帮助你更好地准备。试着把这种能量转化为练习的动力相信自己，你一定可以的。', '2026-04-02 22:39:03');
INSERT INTO `diary` VALUES (73, 32, 'aq8YseLglMLn82XuAmtKmOi4tdd/q/0cyDlgRbfibN664CYPSbxpj2UHbg==', '[\"blob:http://localhost:5173/5dcef200-c11a-44c6-9294-fa0d8186f3c8\"]', '[\"学习\"]', 0.00, 'neutral', NULL, '2026-04-07 19:15:29');
INSERT INTO `diary` VALUES (74, 32, 'flGWRRoT3KsI0HI0+7DRu3aOkVmAslu7B9PBkTUUzTjhw3cOdyJnuQ==', '[\"blob:http://localhost:5173/befe22f4-d204-4cd4-86ac-cd2ebe2c3575\"]', '[]', 0.00, 'neutral', NULL, '2026-04-07 19:15:54');

-- ----------------------------
-- Table structure for file_record
-- ----------------------------
DROP TABLE IF EXISTS `file_record`;
CREATE TABLE `file_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件URL',
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件类型 (image/audio/video)',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小(字节)',
  `oss_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'OSS对象键',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_file_type`(`file_type` ASC) USING BTREE,
  CONSTRAINT `file_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file_record
-- ----------------------------
INSERT INTO `file_record` VALUES (1, 1, 'avatar.jpg', 'https://mind-heart.oss-cn-beijing.aliyuncs.com/images/abc123.jpg', 'image', 102400, NULL, '2026-04-04 19:58:23');
INSERT INTO `file_record` VALUES (2, 2, 'diary_photo.jpg', 'https://mind-heart.oss-cn-beijing.aliyuncs.com/images/def456.jpg', 'image', 204800, NULL, '2026-04-03 19:58:23');
INSERT INTO `file_record` VALUES (3, 1, 'avatar.jpg', 'https://mind-heart.oss-cn-beijing.aliyuncs.com/images/abc123.jpg', 'image', 102400, NULL, '2026-04-04 22:34:47');
INSERT INTO `file_record` VALUES (4, 2, 'diary_photo.jpg', 'https://mind-heart.oss-cn-beijing.aliyuncs.com/images/def456.jpg', 'image', 204800, NULL, '2026-04-03 22:34:47');
INSERT INTO `file_record` VALUES (5, 1, 'avatar.jpg', 'https://mind-heart.oss-cn-beijing.aliyuncs.com/images/abc123.jpg', 'image', 102400, NULL, '2026-04-04 22:39:06');
INSERT INTO `file_record` VALUES (6, 2, 'diary_photo.jpg', 'https://mind-heart.oss-cn-beijing.aliyuncs.com/images/def456.jpg', 'image', 204800, NULL, '2026-04-03 22:39:06');

-- ----------------------------
-- Table structure for knowledge_base
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_base`;
CREATE TABLE `knowledge_base`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类(DSM5/CBT/case)',
  `source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源',
  `embedding_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '向量ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签(JSON数组)',
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '摘要',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '心理知识库表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_base
-- ----------------------------
INSERT INTO `knowledge_base` VALUES (1, '如何应对考试焦虑', '考试焦虑是很多学生都会面临的问题。当我们面临重要的考试时，适度的紧张其实是一种正常的身心反应，它可以帮助我们更好地集中注意力。但过度的焦虑就会影响我们的发挥。\n\n那么，如何应对考试焦虑呢？\n\n1. 做好充分准备\n充分的准备是缓解焦虑的最好方法。制定合理的复习计划，按部就班地完成每天的学习任务，这样可以增强自信心。\n\n2. 学会放松技巧\n深呼吸是最简单有效的放松方法。闭上眼睛，深吸一口气，缓慢呼出，重复几次。也可以尝试渐进性肌肉放松法。\n\n3. 调整认知\n很多焦虑来源于对考试的过度担忧。尝试用更理性的想法替代灾难化的思维，比如\"即使这次没考好，也不代表世界末日\"。\n\n4. 保证充足睡眠\n考试前不要熬夜，保持规律的作息。充足的睡眠可以帮助大脑更好地整合和记忆知识。\n\n5. 寻求支持\n如果焦虑情绪持续加重，不要犹豫，及时向老师、家长或心理咨询师寻求帮助。', 'skill', '心域知识库', NULL, '2026-04-03 14:42:02', '[\"考试\", \"焦虑\", \"调节\"]', '考试焦虑应对指南', '2026-04-03 14:42:02', NULL);
INSERT INTO `knowledge_base` VALUES (2, '正念冥想入门指南', '正念冥想是一种有效的情绪调节方法，源于佛教的禅修传统，现在已经被现代心理学广泛研究和应用。\n\n什么是正念？\n正念是指有意识地、不带评判地关注当下时刻的感受、想法和周围环境。它帮助我们从忙碌的思绪中抽离，专注于此时此刻。\n\n如何开始练习？\n\n1. 选择一个安静的环境\n找一个不会被打扰的地方，坐下或躺下。\n\n2. 设置时间\n初学者可以从5-10分钟开始，逐渐延长。\n\n3. 专注于呼吸\n闭上眼睛，将注意力集中在呼吸上。感受空气进入鼻腔、充满肺部、呼出的感觉。\n\n4. 接纳走神的思绪\n当思绪飘走时，不要批评自己，只是温柔地把注意力带回呼吸。\n\n5. 身体扫描\n感受身体每个部位的感觉，从头到脚，依次关注并放松。\n\n坚持练习会让你的情绪更加稳定，注意力更集中。', 'skill', '心域知识库', NULL, '2026-04-03 14:42:02', '[\"冥想\", \"放松\", \"正念\"]', '正念冥想基础教程', '2026-04-03 14:42:02', NULL);
INSERT INTO `knowledge_base` VALUES (3, '了解抑郁症的早期信号', '抑郁症并非一朝一夕形成，了解早期信号有助于及时发现和干预。\n\n常见的早期信号包括：\n\n1. 情绪变化\n- 持续的低落情绪\n- 对曾经喜欢的事物失去兴趣\n- 容易烦躁或发脾气\n\n2. 思维变化\n- 难以集中注意力\n- 觉得自己没有价值\n- 出现消极的念头\n\n3. 身体变化\n- 睡眠问题（失眠或嗜睡）\n- 食欲变化（食欲不振或暴饮暴食）\n- 容易疲劳\n\n4. 行为变化\n- 回避社交活动\n- 拖延逃避责任\n- 兴趣爱好减少\n\n如果你或身边的人出现以上信号持续两周以上，建议寻求专业帮助。学校心理中心、心理咨询师都是可以求助的渠道。\n\n记住：寻求帮助是勇敢的表现，不是软弱。', 'knowledge', '心域知识库', NULL, '2026-04-03 14:42:02', '[\"抑郁\", \"心理健康\", \"预防\"]', '抑郁症早期识别指南', '2026-04-03 14:42:02', NULL);
INSERT INTO `knowledge_base` VALUES (4, '与父母沟通的技巧', '很多青少年与父母存在沟通障碍，本文通过案例分享一些有效的沟通方法。\n\n常见沟通问题：\n- 父母不理解我的想法\n- 说了他们也不听\n- 说着说着就吵起来\n- 他们总是把我当小孩\n\n有效沟通技巧：\n\n1. 选择合适的时机\n不要在父母忙碌或心情不好时谈论重要的事情。选择大家都很放松的时间。\n\n2. 表达感受而不是指责\n不要说\"你们总是不理解我\"，而是说\"我感到有点失落，因为...\"\n\n3. 主动分享\n主动分享学校的事情、朋友的趣事，让父母了解你的生活。\n\n4. 理解父母的立场\n父母也是第一次做父母，他们的担忧来源于爱。试着理解他们的出发点。\n\n5. 寻求共识\n找到双方都能接受的解决方案，而不是只坚持自己的观点。\n\n案例：小明之前觉得父母不理解他，后来他尝试每周主动和父母分享一件学校的事情，慢慢地父母开始理解他的想法，关系也变好了。', 'case', '心域知识库', NULL, '2026-04-03 14:42:02', '[\"沟通\", \"家庭\", \"人际关系\"]', '亲子沟通实用技巧', '2026-04-03 14:42:02', NULL);
INSERT INTO `knowledge_base` VALUES (5, '青少年心理健康保护', '青少年时期是一个充满变化和挑战的阶段，心理健康尤为重要。\n\n为什么心理健康很重要？\n- 心理健康影响学习效率\n- 影响人际关系\n- 关系到未来发展\n- 影响身体健康\n\n如何保护心理健康？\n\n1. 保持规律作息\n充足的睡眠对身体和心理都很重要。\n\n2. 适度运动\n运动可以释放压力，产生让人愉悦的内啡肽。\n\n3. 建立支持系统\n培养良好的朋友关系，在需要时寻求支持。\n\n4. 培养兴趣爱好\n做自己喜欢的事情可以带来满足感。\n\n5. 学会情绪管理\n了解自己的情绪，学习健康的调节方法。\n\n6. 遇到问题及时求助\n有问题及时找老师、家长或专业人士。\n\n记住：照顾好自己，才能更好地面对未来的挑战。', 'knowledge', '心域知识库', NULL, '2026-04-03 14:42:02', '[\"心理健康\", \"自我保护\", \"成长\"]', '青少年心理健康指南', '2026-04-03 14:42:02', NULL);
INSERT INTO `knowledge_base` VALUES (6, '如何应对考试焦虑', '考试焦虑是很多学生都会面临的问题。当我们面临重要的考试时，适度的紧张其实是一种正常的身心反应，它可以帮助我们更好地集中注意力。但过度的焦虑就会影响我们的发挥。\n\n那么，如何应对考试焦虑呢？\n\n1. 做好充分准备\n充分的准备是缓解焦虑的最好方法。制定合理的复习计划，按部就班地完成每天的学习任务，这样可以增强自信心。\n\n2. 学会放松技巧\n深呼吸是最简单有效的放松方法。闭上眼睛，深吸一口气，缓慢呼出，重复几次。也可以尝试渐进性肌肉放松法。\n\n3. 调整认知\n很多焦虑来源于对考试的过度担忧。尝试用更理性的想法替代灾难化的思维。\n\n4. 保证充足睡眠\n考试前不要熬夜，保持规律的作息。充足的睡眠可以帮助大脑更好地整合和记忆知识。\n\n5. 寻求支持\n如果焦虑情绪持续加重，不要犹豫，及时向老师、家长或心理咨询师寻求帮助。', 'skill', '心域知识库', NULL, '2026-04-03 14:48:39', '[\"考试\", \"焦虑\", \"调节\"]', '考试焦虑应对指南', '2026-04-03 14:48:39', NULL);
INSERT INTO `knowledge_base` VALUES (7, '正念冥想入门指南', '正念冥想是一种有效的情绪调节方法，源于佛教的禅修传统，现在已经被现代心理学广泛研究和应用。\n\n什么是正念？\n正念是指有意识地、不带评判地关注当下时刻的感受、想法和周围环境。它帮助我们从忙碌的思绪中抽离，专注于此时此刻。\n\n如何开始练习？\n\n1. 选择一个安静的环境\n找一个不会被打扰的地方，坐下或躺下。\n\n2. 设置时间\n初学者可以从5-10分钟开始，逐渐延长。\n\n3. 专注于呼吸\n闭上眼睛，将注意力集中在呼吸上。感受空气进入鼻腔、充满肺部、呼出的感觉。\n\n4. 接纳走神的思绪\n当思绪飘走时，不要批评自己，只是温柔地把注意力带回呼吸。\n\n5. 身体扫描\n感受身体每个部位的感觉，从头到脚，依次关注并放松。\n\n坚持练习会让你的情绪更加稳定，注意力更集中。', 'skill', '心域知识库', NULL, '2026-04-03 14:48:39', '[\"冥想\", \"放松\", \"正念\"]', '正念冥想基础教程', '2026-04-03 14:48:39', NULL);
INSERT INTO `knowledge_base` VALUES (8, '了解抑郁症的早期信号', '抑郁症并非一朝一夕形成，了解早期信号有助于及时发现和干预。\n\n常见的早期信号包括：\n\n1. 情绪变化\n- 持续的低落情绪\n- 对曾经喜欢的事物失去兴趣\n- 容易烦躁或发脾气\n\n2. 思维变化\n- 难以集中注意力\n- 觉得自己没有价值\n- 出现消极的念头\n\n3. 身体变化\n- 睡眠问题（失眠或嗜睡）\n- 食欲变化（食欲不振或暴饮暴食）\n- 容易疲劳\n\n4. 行为变化\n- 回避社交活动\n- 拖延逃避责任\n- 兴趣爱好减少\n\n如果你或身边的人出现以上信号持续两周以上，建议寻求专业帮助。学校心理中心、心理咨询师都是可以求助的渠道。\n\n记住：寻求帮助是勇敢的表现，不是软弱。', 'knowledge', '心域知识库', NULL, '2026-04-03 14:48:39', '[\"抑郁\", \"心理健康\", \"预防\"]', '抑郁症早期识别指南', '2026-04-03 14:48:39', NULL);
INSERT INTO `knowledge_base` VALUES (9, '与父母沟通的技巧', '很多青少年与父母存在沟通障碍，本文通过案例分享一些有效的沟通方法。\n\n常见沟通问题：\n- 父母不理解我的想法\n- 说了他们也不听\n- 说着说着就吵起来\n- 他们总是把我当小孩\n\n有效沟通技巧：\n\n1. 选择合适的时机\n不要在父母忙碌或心情不好时谈论重要的事情。选择大家都很放松的时间。\n\n2. 表达感受而不是指责\n不要说你们总是不理解我，而是说我感到有点失落。\n\n3. 主动分享\n主动分享学校的事情、朋友的趣事，让父母了解你的生活。\n\n4. 理解父母的立场\n父母也是第一次做父母，他们的担忧来源于爱。试着理解他们的出发点。\n\n5. 寻求共识\n找到双方都能接受的解决方案，而不是只坚持自己的观点。', 'case', '心域知识库', NULL, '2026-04-03 14:48:39', '[\"沟通\", \"家庭\", \"人际关系\"]', '亲子沟通实用技巧', '2026-04-03 14:48:39', NULL);
INSERT INTO `knowledge_base` VALUES (10, '青少年心理健康保护', '青少年时期是一个充满变化和挑战的阶段，心理健康尤为重要。\n\n为什么心理健康很重要？\n- 心理健康影响学习效率\n- 影响人际关系\n- 关系到未来发展\n- 影响身体健康\n\n如何保护心理健康？\n\n1. 保持规律作息\n充足的睡眠对身体和心理都很重要。\n\n2. 适度运动\n运动可以释放压力，产生让人愉悦的内啡肽。\n\n3. 建立支持系统\n培养良好的朋友关系，在需要时寻求支持。\n\n4. 培养兴趣爱好\n做自己喜欢的事情可以带来满足感。\n\n5. 学会情绪管理\n了解自己的情绪，学习健康的调节方法。\n\n6. 遇到问题及时求助\n有问题及时找老师、家长或专业人士。\n\n记住：照顾好自己，才能更好地面对未来的挑战。', 'knowledge', '心域知识库', NULL, '2026-04-03 14:48:39', '[\"心理健康\", \"自我保护\", \"成长\"]', '青少年心理健康指南', '2026-04-03 14:48:39', NULL);
INSERT INTO `knowledge_base` VALUES (11, '如何应对考试焦虑', '考试焦虑是很多学生都会面临的问题。当我们面临重要的考试时，适度的紧张其实是一种正常的身心反应，它可以帮助我们更好地集中注意力。但过度的焦虑就会影响我们的发挥。\n\n那么，如何应对考试焦虑呢？\n\n1. 做好充分准备\n充分的准备是缓解焦虑的最好方法。制定合理的复习计划，按部就班地完成每天的学习任务，这样可以增强自信心。\n\n2. 学会放松技巧\n深呼吸是最简单有效的放松方法。闭上眼睛，深吸一口气，缓慢呼出，重复几次。也可以尝试渐进性肌肉放松法。\n\n3. 调整认知\n很多焦虑来源于对考试的过度担忧。尝试用更理性的想法替代灾难化的思维。\n\n4. 保证充足睡眠\n考试前不要熬夜，保持规律的作息。充足的睡眠可以帮助大脑更好地整合和记忆知识。\n\n5. 寻求支持\n如果焦虑情绪持续加重，不要犹豫，及时向老师、家长或心理咨询师寻求帮助。', 'skill', '心域知识库', NULL, '2026-04-03 14:51:51', '[\"考试\", \"焦虑\", \"调节\"]', '考试焦虑应对指南', '2026-04-03 14:51:51', NULL);
INSERT INTO `knowledge_base` VALUES (12, '正念冥想入门指南', '正念冥想是一种有效的情绪调节方法，源于佛教的禅修传统，现在已经被现代心理学广泛研究和应用。\n\n什么是正念？\n正念是指有意识地、不带评判地关注当下时刻的感受、想法和周围环境。它帮助我们从忙碌的思绪中抽离，专注于此时此刻。\n\n如何开始练习？\n\n1. 选择一个安静的环境\n找一个不会被打扰的地方，坐下或躺下。\n\n2. 设置时间\n初学者可以从 5-10 分钟开始，逐渐延长。\n\n3. 专注于呼吸\n闭上眼睛，将注意力集中在呼吸上。感受空气进入鼻腔、充满肺部、呼出的感觉。\n\n4. 接纳走神的思绪\n当思绪飘走时，不要批评自己，只是温柔地把注意力带回呼吸。\n\n5. 身体扫描\n感受身体每个部位的感觉，从头到脚，依次关注并放松。\n\n坚持练习会让你的情绪更加稳定，注意力更集中。', 'skill', '心域知识库', NULL, '2026-04-03 14:51:51', '[\"冥想\", \"放松\", \"正念\"]', '正念冥想基础教程', '2026-04-03 14:51:51', NULL);
INSERT INTO `knowledge_base` VALUES (13, '了解抑郁症的早期信号', '抑郁症并非一朝一夕形成，了解早期信号有助于及时发现和干预。\n\n常见的早期信号包括：\n\n1. 情绪变化\n- 持续的低落情绪\n- 对曾经喜欢的事物失去兴趣\n- 容易烦躁或发脾气\n\n2. 思维变化\n- 难以集中注意力\n- 觉得自己没有价值\n- 出现消极的念头\n\n3. 身体变化\n- 睡眠问题（失眠或嗜睡）\n- 食欲变化（食欲不振或暴饮暴食）\n- 容易疲劳\n\n4. 行为变化\n- 回避社交活动\n- 拖延逃避责任\n- 兴趣爱好减少\n\n如果你或身边的人出现以上信号持续两周以上，建议寻求专业帮助。学校心理中心、心理咨询师都是可以求助的渠道。\n\n记住：寻求帮助是勇敢的表现，不是软弱。', 'knowledge', '心域知识库', NULL, '2026-04-03 14:51:51', '[\"抑郁\", \"心理健康\", \"预防\"]', '抑郁症早期识别指南', '2026-04-03 14:51:51', NULL);
INSERT INTO `knowledge_base` VALUES (14, '与父母沟通的技巧', '很多青少年与父母存在沟通障碍，本文通过案例分享一些有效的沟通方法。\n\n常见沟通问题：\n- 父母不理解我的想法\n- 说了他们也不听\n- 说着说着就吵起来\n- 他们总是把我当小孩\n\n有效沟通技巧：\n\n1. 选择合适的时机\n不要在父母忙碌或心情不好时谈论重要的事情。选择大家都很放松的时间。\n\n2. 表达感受而不是指责\n不要说你们总是不理解我，而是说我感到有点失落。\n\n3. 主动分享\n主动分享学校的事情、朋友的趣事，让父母了解你的生活。\n\n4. 理解父母的立场\n父母也是第一次做父母，他们的担忧来源于爱。试着理解他们的出发点。\n\n5. 寻求共识\n找到双方都能接受的解决方案，而不是只坚持自己的观点。', 'case', '心域知识库', NULL, '2026-04-03 14:51:51', '[\"沟通\", \"家庭\", \"人际关系\"]', '亲子沟通实用技巧', '2026-04-03 14:51:51', NULL);
INSERT INTO `knowledge_base` VALUES (15, '青少年心理健康保护', '青少年时期是一个充满变化和挑战的阶段，心理健康尤为重要。\n\n为什么心理健康很重要？\n- 心理健康影响学习效率\n- 影响人际关系\n- 关系到未来发展\n- 影响身体健康\n\n如何保护心理健康？\n\n1. 保持规律作息\n充足的睡眠对身体和心理都很重要。\n\n2. 适度运动\n运动可以释放压力，产生让人愉悦的内啡肽。\n\n3. 建立支持系统\n培养良好的朋友关系，在需要时寻求支持。\n\n4. 培养兴趣爱好\n做自己喜欢的事情可以带来满足感。\n\n5. 学会情绪管理\n了解自己的情绪，学习健康的调节方法。\n\n6. 遇到问题及时求助\n有问题及时找老师、家长或专业人士。\n\n记住：照顾好自己，才能更好地面对未来的挑战。', 'knowledge', '心域知识库', NULL, '2026-04-03 14:51:51', '[\"心理健康\", \"自我保护\", \"成长\"]', '青少年心理健康指南', '2026-04-03 14:51:51', NULL);
INSERT INTO `knowledge_base` VALUES (16, '如何应对考试焦虑', '考试焦虑是很多学生都会面临的问题。当我们面临重要的考试时，适度的紧张其实是一种正常的身心反应，它可以帮助我们更好地集中注意力。但过度的焦虑就会影响我们的发挥。\n\n那么，如何应对考试焦虑呢？\n\n1. 做好充分准备\n充分的准备是缓解焦虑的最好方法。制定合理的复习计划，按部就班地完成每天的学习任务，这样可以增强自信心。\n\n2. 学会放松技巧\n深呼吸是最简单有效的放松方法。闭上眼睛，深吸一口气，缓慢呼出，重复几次。也可以尝试渐进性肌肉放松法。\n\n3. 调整认知\n很多焦虑来源于对考试的过度担忧。尝试用更理性的想法替代灾难化的思维。\n\n4. 保证充足睡眠\n考试前不要熬夜，保持规律的作息。充足的睡眠可以帮助大脑更好地整合和记忆知识。\n\n5. 寻求支持\n如果焦虑情绪持续加重，不要犹豫，及时向老师、家长或心理咨询师寻求帮助。', 'skill', '心域知识库', NULL, '2026-04-03 14:53:00', '[\"考试\", \"焦虑\", \"调节\"]', '考试焦虑应对指南', '2026-04-03 14:53:00', NULL);
INSERT INTO `knowledge_base` VALUES (17, '正念冥想入门指南', '正念冥想是一种有效的情绪调节方法，源于佛教的禅修传统，现在已经被现代心理学广泛研究和应用。\n\n什么是正念？\n正念是指有意识地、不带评判地关注当下时刻的感受、想法和周围环境。它帮助我们从忙碌的思绪中抽离，专注于此时此刻。\n\n如何开始练习？\n\n1. 选择一个安静的环境\n找一个不会被打扰的地方，坐下或躺下。\n\n2. 设置时间\n初学者可以从 5-10 分钟开始，逐渐延长。\n\n3. 专注于呼吸\n闭上眼睛，将注意力集中在呼吸上。感受空气进入鼻腔、充满肺部、呼出的感觉。\n\n4. 接纳走神的思绪\n当思绪飘走时，不要批评自己，只是温柔地把注意力带回呼吸。\n\n5. 身体扫描\n感受身体每个部位的感觉，从头到脚，依次关注并放松。\n\n坚持练习会让你的情绪更加稳定，注意力更集中。', 'skill', '心域知识库', NULL, '2026-04-03 14:53:00', '[\"冥想\", \"放松\", \"正念\"]', '正念冥想基础教程', '2026-04-03 14:53:00', NULL);
INSERT INTO `knowledge_base` VALUES (18, '了解抑郁症的早期信号', '抑郁症并非一朝一夕形成，了解早期信号有助于及时发现和干预。\n\n常见的早期信号包括：\n\n1. 情绪变化\n- 持续的低落情绪\n- 对曾经喜欢的事物失去兴趣\n- 容易烦躁或发脾气\n\n2. 思维变化\n- 难以集中注意力\n- 觉得自己没有价值\n- 出现消极的念头\n\n3. 身体变化\n- 睡眠问题（失眠或嗜睡）\n- 食欲变化（食欲不振或暴饮暴食）\n- 容易疲劳\n\n4. 行为变化\n- 回避社交活动\n- 拖延逃避责任\n- 兴趣爱好减少\n\n如果你或身边的人出现以上信号持续两周以上，建议寻求专业帮助。学校心理中心、心理咨询师都是可以求助的渠道。\n\n记住：寻求帮助是勇敢的表现，不是软弱。', 'knowledge', '心域知识库', NULL, '2026-04-03 14:53:00', '[\"抑郁\", \"心理健康\", \"预防\"]', '抑郁症早期识别指南', '2026-04-03 14:53:00', NULL);
INSERT INTO `knowledge_base` VALUES (19, '与父母沟通的技巧', '很多青少年与父母存在沟通障碍，本文通过案例分享一些有效的沟通方法。\n\n常见沟通问题：\n- 父母不理解我的想法\n- 说了他们也不听\n- 说着说着就吵起来\n- 他们总是把我当小孩\n\n有效沟通技巧：\n\n1. 选择合适的时机\n不要在父母忙碌或心情不好时谈论重要的事情。选择大家都很放松的时间。\n\n2. 表达感受而不是指责\n不要说你们总是不理解我，而是说我感到有点失落。\n\n3. 主动分享\n主动分享学校的事情、朋友的趣事，让父母了解你的生活。\n\n4. 理解父母的立场\n父母也是第一次做父母，他们的担忧来源于爱。试着理解他们的出发点。\n\n5. 寻求共识\n找到双方都能接受的解决方案，而不是只坚持自己的观点。', 'case', '心域知识库', NULL, '2026-04-03 14:53:00', '[\"沟通\", \"家庭\", \"人际关系\"]', '亲子沟通实用技巧', '2026-04-03 14:53:00', NULL);
INSERT INTO `knowledge_base` VALUES (20, '青少年心理健康保护', '青少年时期是一个充满变化和挑战的阶段，心理健康尤为重要。\n\n为什么心理健康很重要？\n- 心理健康影响学习效率\n- 影响人际关系\n- 关系到未来发展\n- 影响身体健康\n\n如何保护心理健康？\n\n1. 保持规律作息\n充足的睡眠对身体和心理都很重要。\n\n2. 适度运动\n运动可以释放压力，产生让人愉悦的内啡肽。\n\n3. 建立支持系统\n培养良好的朋友关系，在需要时寻求支持。\n\n4. 培养兴趣爱好\n做自己喜欢的事情可以带来满足感。\n\n5. 学会情绪管理\n了解自己的情绪，学习健康的调节方法。\n\n6. 遇到问题及时求助\n有问题及时找老师、家长或专业人士。\n\n记住：照顾好自己，才能更好地面对未来的挑战。', 'knowledge', '心域知识库', NULL, '2026-04-03 14:53:00', '[\"心理健康\", \"自我保护\", \"成长\"]', '青少年心理健康指南', '2026-04-03 14:53:00', NULL);
INSERT INTO `knowledge_base` VALUES (21, '如何应对考试焦虑', '考试焦虑是很多学生都会面临的问题。当我们面临重要的考试时，适度的紧张其实是一种正常的身心反应，它可以帮助我们更好地集中注意力。但过度的焦虑就会影响我们的发挥。\n\n那么，如何应对考试焦虑呢？\n\n1. 做好充分准备\n充分的准备是缓解焦虑的最好方法。制定合理的复习计划，按部就班地完成每天的学习任务，这样可以增强自信心。\n\n2. 学会放松技巧\n深呼吸是最简单有效的放松方法。闭上眼睛，深吸一口气，缓慢呼出，重复几次。也可以尝试渐进性肌肉放松法。\n\n3. 调整认知\n很多焦虑来源于对考试的过度担忧。尝试用更理性的想法替代灾难化的思维。\n\n4. 保证充足睡眠\n考试前不要熬夜，保持规律的作息。充足的睡眠可以帮助大脑更好地整合和记忆知识。\n\n5. 寻求支持\n如果焦虑情绪持续加重，不要犹豫，及时向老师、家长或心理咨询师寻求帮助。', 'skill', '心域知识库', NULL, '2026-04-03 15:01:21', '[\"考试\", \"焦虑\", \"调节\"]', '考试焦虑应对指南', '2026-04-03 15:01:21', NULL);
INSERT INTO `knowledge_base` VALUES (22, '正念冥想入门指南', '正念冥想是一种有效的情绪调节方法，源于佛教的禅修传统，现在已经被现代心理学广泛研究和应用。\n\n什么是正念？\n正念是指有意识地、不带评判地关注当下时刻的感受、想法和周围环境。它帮助我们从忙碌的思绪中抽离，专注于此时此刻。\n\n如何开始练习？\n\n1. 选择一个安静的环境\n找一个不会被打扰的地方，坐下或躺下。\n\n2. 设置时间\n初学者可以从 5-10 分钟开始，逐渐延长。\n\n3. 专注于呼吸\n闭上眼睛，将注意力集中在呼吸上。感受空气进入鼻腔、充满肺部、呼出的感觉。\n\n4. 接纳走神的思绪\n当思绪飘走时，不要批评自己，只是温柔地把注意力带回呼吸。\n\n5. 身体扫描\n感受身体每个部位的感觉，从头到脚，依次关注并放松。\n\n坚持练习会让你的情绪更加稳定，注意力更集中。', 'skill', '心域知识库', NULL, '2026-04-03 15:01:21', '[\"冥想\", \"放松\", \"正念\"]', '正念冥想基础教程', '2026-04-03 15:01:21', NULL);
INSERT INTO `knowledge_base` VALUES (23, '了解抑郁症的早期信号', '抑郁症并非一朝一夕形成，了解早期信号有助于及时发现和干预。\n\n常见的早期信号包括：\n\n1. 情绪变化\n- 持续的低落情绪\n- 对曾经喜欢的事物失去兴趣\n- 容易烦躁或发脾气\n\n2. 思维变化\n- 难以集中注意力\n- 觉得自己没有价值\n- 出现消极的念头\n\n3. 身体变化\n- 睡眠问题（失眠或嗜睡）\n- 食欲变化（食欲不振或暴饮暴食）\n- 容易疲劳\n\n4. 行为变化\n- 回避社交活动\n- 拖延逃避责任\n- 兴趣爱好减少\n\n如果你或身边的人出现以上信号持续两周以上，建议寻求专业帮助。学校心理中心、心理咨询师都是可以求助的渠道。\n\n记住：寻求帮助是勇敢的表现，不是软弱。', 'knowledge', '心域知识库', NULL, '2026-04-03 15:01:21', '[\"抑郁\", \"心理健康\", \"预防\"]', '抑郁症早期识别指南', '2026-04-03 15:01:21', NULL);
INSERT INTO `knowledge_base` VALUES (24, '与父母沟通的技巧', '很多青少年与父母存在沟通障碍，本文通过案例分享一些有效的沟通方法。\n\n常见沟通问题：\n- 父母不理解我的想法\n- 说了他们也不听\n- 说着说着就吵起来\n- 他们总是把我当小孩\n\n有效沟通技巧：\n\n1. 选择合适的时机\n不要在父母忙碌或心情不好时谈论重要的事情。选择大家都很放松的时间。\n\n2. 表达感受而不是指责\n不要说你们总是不理解我，而是说我感到有点失落。\n\n3. 主动分享\n主动分享学校的事情、朋友的趣事，让父母了解你的生活。\n\n4. 理解父母的立场\n父母也是第一次做父母，他们的担忧来源于爱。试着理解他们的出发点。\n\n5. 寻求共识\n找到双方都能接受的解决方案，而不是只坚持自己的观点。', 'case', '心域知识库', NULL, '2026-04-03 15:01:21', '[\"沟通\", \"家庭\", \"人际关系\"]', '亲子沟通实用技巧', '2026-04-03 15:01:21', NULL);
INSERT INTO `knowledge_base` VALUES (25, '青少年心理健康保护', '青少年时期是一个充满变化和挑战的阶段，心理健康尤为重要。\n\n为什么心理健康很重要？\n- 心理健康影响学习效率\n- 影响人际关系\n- 关系到未来发展\n- 影响身体健康\n\n如何保护心理健康？\n\n1. 保持规律作息\n充足的睡眠对身体和心理都很重要。\n\n2. 适度运动\n运动可以释放压力，产生让人愉悦的内啡肽。\n\n3. 建立支持系统\n培养良好的朋友关系，在需要时寻求支持。\n\n4. 培养兴趣爱好\n做自己喜欢的事情可以带来满足感。\n\n5. 学会情绪管理\n了解自己的情绪，学习健康的调节方法。\n\n6. 遇到问题及时求助\n有问题及时找老师、家长或专业人士。\n\n记住：照顾好自己，才能更好地面对未来的挑战。', 'knowledge', '心域知识库', NULL, '2026-04-03 15:01:21', '[\"心理健康\", \"自我保护\", \"成长\"]', '青少年心理健康指南', '2026-04-03 15:01:21', NULL);
INSERT INTO `knowledge_base` VALUES (26, '如何应对考试焦虑', '考试焦虑是很多学生都会面临的问题。当我们面临重要的考试时，适度的紧张其实是一种正常的身心反应，它可以帮助我们更好地集中注意力。但过度的焦虑就会影响我们的发挥。\n\n那么，如何应对考试焦虑呢？\n\n1. 做好充分准备\n充分的准备是缓解焦虑的最好方法。制定合理的复习计划，按部就班地完成每天的学习任务，这样可以增强自信心。\n\n2. 学会放松技巧\n深呼吸是最简单有效的放松方法。闭上眼睛，深吸一口气，缓慢呼出，重复几次。也可以尝试渐进性肌肉放松法。\n\n3. 调整认知\n很多焦虑来源于对考试的过度担忧。尝试用更理性的想法替代灾难化的思维。\n\n4. 保证充足睡眠\n考试前不要熬夜，保持规律的作息。充足的睡眠可以帮助大脑更好地整合和记忆知识。\n\n5. 寻求支持\n如果焦虑情绪持续加重，不要犹豫，及时向老师、家长或心理咨询师寻求帮助。', 'skill', '心域知识库', NULL, '2026-04-03 15:03:39', '[\"考试\", \"焦虑\", \"调节\"]', '考试焦虑应对指南', '2026-04-03 15:03:39', NULL);
INSERT INTO `knowledge_base` VALUES (27, '正念冥想入门指南', '正念冥想是一种有效的情绪调节方法，源于佛教的禅修传统，现在已经被现代心理学广泛研究和应用。\n\n什么是正念？\n正念是指有意识地、不带评判地关注当下时刻的感受、想法和周围环境。它帮助我们从忙碌的思绪中抽离，专注于此时此刻。\n\n如何开始练习？\n\n1. 选择一个安静的环境\n找一个不会被打扰的地方，坐下或躺下。\n\n2. 设置时间\n初学者可以从 5-10 分钟开始，逐渐延长。\n\n3. 专注于呼吸\n闭上眼睛，将注意力集中在呼吸上。感受空气进入鼻腔、充满肺部、呼出的感觉。\n\n4. 接纳走神的思绪\n当思绪飘走时，不要批评自己，只是温柔地把注意力带回呼吸。\n\n5. 身体扫描\n感受身体每个部位的感觉，从头到脚，依次关注并放松。\n\n坚持练习会让你的情绪更加稳定，注意力更集中。', 'skill', '心域知识库', NULL, '2026-04-03 15:03:39', '[\"冥想\", \"放松\", \"正念\"]', '正念冥想基础教程', '2026-04-03 15:03:39', NULL);
INSERT INTO `knowledge_base` VALUES (28, '了解抑郁症的早期信号', '抑郁症并非一朝一夕形成，了解早期信号有助于及时发现和干预。\n\n常见的早期信号包括：\n\n1. 情绪变化\n- 持续的低落情绪\n- 对曾经喜欢的事物失去兴趣\n- 容易烦躁或发脾气\n\n2. 思维变化\n- 难以集中注意力\n- 觉得自己没有价值\n- 出现消极的念头\n\n3. 身体变化\n- 睡眠问题（失眠或嗜睡）\n- 食欲变化（食欲不振或暴饮暴食）\n- 容易疲劳\n\n4. 行为变化\n- 回避社交活动\n- 拖延逃避责任\n- 兴趣爱好减少\n\n如果你或身边的人出现以上信号持续两周以上，建议寻求专业帮助。学校心理中心、心理咨询师都是可以求助的渠道。\n\n记住：寻求帮助是勇敢的表现，不是软弱。', 'knowledge', '心域知识库', NULL, '2026-04-03 15:03:39', '[\"抑郁\", \"心理健康\", \"预防\"]', '抑郁症早期识别指南', '2026-04-03 15:03:39', NULL);
INSERT INTO `knowledge_base` VALUES (29, '与父母沟通的技巧', '很多青少年与父母存在沟通障碍，本文通过案例分享一些有效的沟通方法。\n\n常见沟通问题：\n- 父母不理解我的想法\n- 说了他们也不听\n- 说着说着就吵起来\n- 他们总是把我当小孩\n\n有效沟通技巧：\n\n1. 选择合适的时机\n不要在父母忙碌或心情不好时谈论重要的事情。选择大家都很放松的时间。\n\n2. 表达感受而不是指责\n不要说你们总是不理解我，而是说我感到有点失落。\n\n3. 主动分享\n主动分享学校的事情、朋友的趣事，让父母了解你的生活。\n\n4. 理解父母的立场\n父母也是第一次做父母，他们的担忧来源于爱。试着理解他们的出发点。\n\n5. 寻求共识\n找到双方都能接受的解决方案，而不是只坚持自己的观点。', 'case', '心域知识库', NULL, '2026-04-03 15:03:39', '[\"沟通\", \"家庭\", \"人际关系\"]', '亲子沟通实用技巧', '2026-04-03 15:03:39', NULL);
INSERT INTO `knowledge_base` VALUES (30, '青少年心理健康保护', '青少年时期是一个充满变化和挑战的阶段，心理健康尤为重要。\n\n为什么心理健康很重要？\n- 心理健康影响学习效率\n- 影响人际关系\n- 关系到未来发展\n- 影响身体健康\n\n如何保护心理健康？\n\n1. 保持规律作息\n充足的睡眠对身体和心理都很重要。\n\n2. 适度运动\n运动可以释放压力，产生让人愉悦的内啡肽。\n\n3. 建立支持系统\n培养良好的朋友关系，在需要时寻求支持。\n\n4. 培养兴趣爱好\n做自己喜欢的事情可以带来满足感。\n\n5. 学会情绪管理\n了解自己的情绪，学习健康的调节方法。\n\n6. 遇到问题及时求助\n有问题及时找老师、家长或专业人士。\n\n记住：照顾好自己，才能更好地面对未来的挑战。', 'knowledge', '心域知识库', NULL, '2026-04-03 15:03:39', '[\"心理健康\", \"自我保护\", \"成长\"]', '青少年心理健康指南', '2026-04-03 15:03:39', NULL);
INSERT INTO `knowledge_base` VALUES (31, '如何应对考试焦虑', '考试焦虑是很多学生都会面临的问题。当我们面临重要的考试时，适度的紧张其实是一种正常的身心反应，它可以帮助我们更好地集中注意力。但过度的焦虑就会影响我们的发挥。\n\n那么，如何应对考试焦虑呢？\n\n1. 做好充分准备\n充分的准备是缓解焦虑的最好方法。制定合理的复习计划，按部就班地完成每天的学习任务，这样可以增强自信心。\n\n2. 学会放松技巧\n深呼吸是最简单有效的放松方法。闭上眼睛，深吸一口气，缓慢呼出，重复几次。也可以尝试渐进性肌肉放松法。\n\n3. 调整认知\n很多焦虑来源于对考试的过度担忧。尝试用更理性的想法替代灾难化的思维。\n\n4. 保证充足睡眠\n考试前不要熬夜，保持规律的作息。充足的睡眠可以帮助大脑更好地整合和记忆知识。\n\n5. 寻求支持\n如果焦虑情绪持续加重，不要犹豫，及时向老师、家长或心理咨询师寻求帮助。', 'skill', '心域知识库', NULL, '2026-04-05 18:46:08', '[\"考试\", \"焦虑\", \"调节\"]', '考试焦虑应对指南', '2026-04-05 18:46:08', NULL);
INSERT INTO `knowledge_base` VALUES (32, '正念冥想入门指南', '正念冥想是一种有效的情绪调节方法，源于佛教的禅修传统，现在已经被现代心理学广泛研究和应用。\n\n什么是正念？\n正念是指有意识地、不带评判地关注当下时刻的感受、想法和周围环境。它帮助我们从忙碌的思绪中抽离，专注于此时此刻。\n\n如何开始练习？\n\n1. 选择一个安静的环境\n找一个不会被打扰的地方，坐下或躺下。\n\n2. 设置时间\n初学者可以从 5-10 分钟开始，逐渐延长。\n\n3. 专注于呼吸\n闭上眼睛，将注意力集中在呼吸上。感受空气进入鼻腔、充满肺部、呼出的感觉。\n\n4. 接纳走神的思绪\n当思绪飘走时，不要批评自己，只是温柔地把注意力带回呼吸。\n\n5. 身体扫描\n感受身体每个部位的感觉，从头到脚，依次关注并放松。\n\n坚持练习会让你的情绪更加稳定，注意力更集中。', 'skill', '心域知识库', NULL, '2026-04-05 18:46:08', '[\"冥想\", \"放松\", \"正念\"]', '正念冥想基础教程', '2026-04-05 18:46:08', NULL);
INSERT INTO `knowledge_base` VALUES (33, '了解抑郁症的早期信号', '抑郁症并非一朝一夕形成，了解早期信号有助于及时发现和干预。\n\n常见的早期信号包括：\n\n1. 情绪变化\n- 持续的低落情绪\n- 对曾经喜欢的事物失去兴趣\n- 容易烦躁或发脾气\n\n2. 思维变化\n- 难以集中注意力\n- 觉得自己没有价值\n- 出现消极的念头\n\n3. 身体变化\n- 睡眠问题（失眠或嗜睡）\n- 食欲变化（食欲不振或暴饮暴食）\n- 容易疲劳\n\n4. 行为变化\n- 回避社交活动\n- 拖延逃避责任\n- 兴趣爱好减少\n\n如果你或身边的人出现以上信号持续两周以上，建议寻求专业帮助。学校心理中心、心理咨询师都是可以求助的渠道。\n\n记住：寻求帮助是勇敢的表现，不是软弱。', 'knowledge', '心域知识库', NULL, '2026-04-05 18:46:08', '[\"抑郁\", \"心理健康\", \"预防\"]', '抑郁症早期识别指南', '2026-04-05 18:46:08', NULL);
INSERT INTO `knowledge_base` VALUES (34, '与父母沟通的技巧', '很多青少年与父母存在沟通障碍，本文通过案例分享一些有效的沟通方法。\n\n常见沟通问题：\n- 父母不理解我的想法\n- 说了他们也不听\n- 说着说着就吵起来\n- 他们总是把我当小孩\n\n有效沟通技巧：\n\n1. 选择合适的时机\n不要在父母忙碌或心情不好时谈论重要的事情。选择大家都很放松的时间。\n\n2. 表达感受而不是指责\n不要说你们总是不理解我，而是说我感到有点失落。\n\n3. 主动分享\n主动分享学校的事情、朋友的趣事，让父母了解你的生活。\n\n4. 理解父母的立场\n父母也是第一次做父母，他们的担忧来源于爱。试着理解他们的出发点。\n\n5. 寻求共识\n找到双方都能接受的解决方案，而不是只坚持自己的观点。', 'case', '心域知识库', NULL, '2026-04-05 18:46:08', '[\"沟通\", \"家庭\", \"人际关系\"]', '亲子沟通实用技巧', '2026-04-05 18:46:08', NULL);
INSERT INTO `knowledge_base` VALUES (35, '青少年心理健康保护', '青少年时期是一个充满变化和挑战的阶段，心理健康尤为重要。\n\n为什么心理健康很重要？\n- 心理健康影响学习效率\n- 影响人际关系\n- 关系到未来发展\n- 影响身体健康\n\n如何保护心理健康？\n\n1. 保持规律作息\n充足的睡眠对身体和心理都很重要。\n\n2. 适度运动\n运动可以释放压力，产生让人愉悦的内啡肽。\n\n3. 建立支持系统\n培养良好的朋友关系，在需要时寻求支持。\n\n4. 培养兴趣爱好\n做自己喜欢的事情可以带来满足感。\n\n5. 学会情绪管理\n了解自己的情绪，学习健康的调节方法。\n\n6. 遇到问题及时求助\n有问题及时找老师、家长或专业人士。\n\n记住：照顾好自己，才能更好地面对未来的挑战。', 'knowledge', '心域知识库', NULL, '2026-04-05 18:46:08', '[\"心理健康\", \"自我保护\", \"成长\"]', '青少年心理健康指南', '2026-04-05 18:46:08', NULL);
INSERT INTO `knowledge_base` VALUES (36, '如何应对考试焦虑', '考试焦虑是很多学生都会面临的问题。当我们面临重要的考试时，适度的紧张其实是一种正常的身心反应，它可以帮助我们更好地集中注意力。但过度的焦虑就会影响我们的发挥。\n\n那么，如何应对考试焦虑呢？\n\n1. 做好充分准备\n充分的准备是缓解焦虑的最好方法。制定合理的复习计划，按部就班地完成每天的学习任务，这样可以增强自信心。\n\n2. 学会放松技巧\n深呼吸是最简单有效的放松方法。闭上眼睛，深吸一口气，缓慢呼出，重复几次。也可以尝试渐进性肌肉放松法。\n\n3. 调整认知\n很多焦虑来源于对考试的过度担忧。尝试用更理性的想法替代灾难化的思维。\n\n4. 保证充足睡眠\n考试前不要熬夜，保持规律的作息。充足的睡眠可以帮助大脑更好地整合和记忆知识。\n\n5. 寻求支持\n如果焦虑情绪持续加重，不要犹豫，及时向老师、家长或心理咨询师寻求帮助。', 'skill', '心域知识库', NULL, '2026-04-05 19:54:57', '[\"考试\", \"焦虑\", \"调节\"]', '考试焦虑应对指南', '2026-04-05 19:54:57', NULL);
INSERT INTO `knowledge_base` VALUES (37, '正念冥想入门指南', '正念冥想是一种有效的情绪调节方法，源于佛教的禅修传统，现在已经被现代心理学广泛研究和应用。\n\n什么是正念？\n正念是指有意识地、不带评判地关注当下时刻的感受、想法和周围环境。它帮助我们从忙碌的思绪中抽离，专注于此时此刻。\n\n如何开始练习？\n\n1. 选择一个安静的环境\n找一个不会被打扰的地方，坐下或躺下。\n\n2. 设置时间\n初学者可以从 5-10 分钟开始，逐渐延长。\n\n3. 专注于呼吸\n闭上眼睛，将注意力集中在呼吸上。感受空气进入鼻腔、充满肺部、呼出的感觉。\n\n4. 接纳走神的思绪\n当思绪飘走时，不要批评自己，只是温柔地把注意力带回呼吸。\n\n5. 身体扫描\n感受身体每个部位的感觉，从头到脚，依次关注并放松。\n\n坚持练习会让你的情绪更加稳定，注意力更集中。', 'skill', '心域知识库', NULL, '2026-04-05 19:54:57', '[\"冥想\", \"放松\", \"正念\"]', '正念冥想基础教程', '2026-04-05 19:54:57', NULL);
INSERT INTO `knowledge_base` VALUES (38, '了解抑郁症的早期信号', '抑郁症并非一朝一夕形成，了解早期信号有助于及时发现和干预。\n\n常见的早期信号包括：\n\n1. 情绪变化\n- 持续的低落情绪\n- 对曾经喜欢的事物失去兴趣\n- 容易烦躁或发脾气\n\n2. 思维变化\n- 难以集中注意力\n- 觉得自己没有价值\n- 出现消极的念头\n\n3. 身体变化\n- 睡眠问题（失眠或嗜睡）\n- 食欲变化（食欲不振或暴饮暴食）\n- 容易疲劳\n\n4. 行为变化\n- 回避社交活动\n- 拖延逃避责任\n- 兴趣爱好减少\n\n如果你或身边的人出现以上信号持续两周以上，建议寻求专业帮助。学校心理中心、心理咨询师都是可以求助的渠道。\n\n记住：寻求帮助是勇敢的表现，不是软弱。', 'knowledge', '心域知识库', NULL, '2026-04-05 19:54:57', '[\"抑郁\", \"心理健康\", \"预防\"]', '抑郁症早期识别指南', '2026-04-05 19:54:57', NULL);
INSERT INTO `knowledge_base` VALUES (39, '与父母沟通的技巧', '很多青少年与父母存在沟通障碍，本文通过案例分享一些有效的沟通方法。\n\n常见沟通问题：\n- 父母不理解我的想法\n- 说了他们也不听\n- 说着说着就吵起来\n- 他们总是把我当小孩\n\n有效沟通技巧：\n\n1. 选择合适的时机\n不要在父母忙碌或心情不好时谈论重要的事情。选择大家都很放松的时间。\n\n2. 表达感受而不是指责\n不要说你们总是不理解我，而是说我感到有点失落。\n\n3. 主动分享\n主动分享学校的事情、朋友的趣事，让父母了解你的生活。\n\n4. 理解父母的立场\n父母也是第一次做父母，他们的担忧来源于爱。试着理解他们的出发点。\n\n5. 寻求共识\n找到双方都能接受的解决方案，而不是只坚持自己的观点。', 'case', '心域知识库', NULL, '2026-04-05 19:54:57', '[\"沟通\", \"家庭\", \"人际关系\"]', '亲子沟通实用技巧', '2026-04-05 19:54:57', NULL);
INSERT INTO `knowledge_base` VALUES (40, '青少年心理健康保护', '青少年时期是一个充满变化和挑战的阶段，心理健康尤为重要。\n\n为什么心理健康很重要？\n- 心理健康影响学习效率\n- 影响人际关系\n- 关系到未来发展\n- 影响身体健康\n\n如何保护心理健康？\n\n1. 保持规律作息\n充足的睡眠对身体和心理都很重要。\n\n2. 适度运动\n运动可以释放压力，产生让人愉悦的内啡肽。\n\n3. 建立支持系统\n培养良好的朋友关系，在需要时寻求支持。\n\n4. 培养兴趣爱好\n做自己喜欢的事情可以带来满足感。\n\n5. 学会情绪管理\n了解自己的情绪，学习健康的调节方法。\n\n6. 遇到问题及时求助\n有问题及时找老师、家长或专业人士。\n\n记住：照顾好自己，才能更好地面对未来的挑战。', 'knowledge', '心域知识库', NULL, '2026-04-05 19:54:57', '[\"心理健康\", \"自我保护\", \"成长\"]', '青少年心理健康指南', '2026-04-05 19:54:57', NULL);
INSERT INTO `knowledge_base` VALUES (41, '如何应对考试焦虑', '考试焦虑是很多学生都会面临的问题。当我们面临重要的考试时，适度的紧张其实是一种正常的身心反应，它可以帮助我们更好地集中注意力。但过度的焦虑就会影响我们的发挥。\n\n那么，如何应对考试焦虑呢？\n\n1. 做好充分准备\n充分的准备是缓解焦虑的最好方法。制定合理的复习计划，按部就班地完成每天的学习任务，这样可以增强自信心。\n\n2. 学会放松技巧\n深呼吸是最简单有效的放松方法。闭上眼睛，深吸一口气，缓慢呼出，重复几次。也可以尝试渐进性肌肉放松法。\n\n3. 调整认知\n很多焦虑来源于对考试的过度担忧。尝试用更理性的想法替代灾难化的思维。\n\n4. 保证充足睡眠\n考试前不要熬夜，保持规律的作息。充足的睡眠可以帮助大脑更好地整合和记忆知识。\n\n5. 寻求支持\n如果焦虑情绪持续加重，不要犹豫，及时向老师、家长或心理咨询师寻求帮助。', 'skill', '心域知识库', NULL, '2026-04-05 19:58:14', '[\"考试\", \"焦虑\", \"调节\"]', '考试焦虑应对指南', '2026-04-05 19:58:14', NULL);
INSERT INTO `knowledge_base` VALUES (42, '正念冥想入门指南', '正念冥想是一种有效的情绪调节方法，源于佛教的禅修传统，现在已经被现代心理学广泛研究和应用。\n\n什么是正念？\n正念是指有意识地、不带评判地关注当下时刻的感受、想法和周围环境。它帮助我们从忙碌的思绪中抽离，专注于此时此刻。\n\n如何开始练习？\n\n1. 选择一个安静的环境\n找一个不会被打扰的地方，坐下或躺下。\n\n2. 设置时间\n初学者可以从 5-10 分钟开始，逐渐延长。\n\n3. 专注于呼吸\n闭上眼睛，将注意力集中在呼吸上。感受空气进入鼻腔、充满肺部、呼出的感觉。\n\n4. 接纳走神的思绪\n当思绪飘走时，不要批评自己，只是温柔地把注意力带回呼吸。\n\n5. 身体扫描\n感受身体每个部位的感觉，从头到脚，依次关注并放松。\n\n坚持练习会让你的情绪更加稳定，注意力更集中。', 'skill', '心域知识库', NULL, '2026-04-05 19:58:14', '[\"冥想\", \"放松\", \"正念\"]', '正念冥想基础教程', '2026-04-05 19:58:14', NULL);
INSERT INTO `knowledge_base` VALUES (43, '了解抑郁症的早期信号', '抑郁症并非一朝一夕形成，了解早期信号有助于及时发现和干预。\n\n常见的早期信号包括：\n\n1. 情绪变化\n- 持续的低落情绪\n- 对曾经喜欢的事物失去兴趣\n- 容易烦躁或发脾气\n\n2. 思维变化\n- 难以集中注意力\n- 觉得自己没有价值\n- 出现消极的念头\n\n3. 身体变化\n- 睡眠问题（失眠或嗜睡）\n- 食欲变化（食欲不振或暴饮暴食）\n- 容易疲劳\n\n4. 行为变化\n- 回避社交活动\n- 拖延逃避责任\n- 兴趣爱好减少\n\n如果你或身边的人出现以上信号持续两周以上，建议寻求专业帮助。学校心理中心、心理咨询师都是可以求助的渠道。\n\n记住：寻求帮助是勇敢的表现，不是软弱。', 'knowledge', '心域知识库', NULL, '2026-04-05 19:58:14', '[\"抑郁\", \"心理健康\", \"预防\"]', '抑郁症早期识别指南', '2026-04-05 19:58:14', NULL);
INSERT INTO `knowledge_base` VALUES (44, '与父母沟通的技巧', '很多青少年与父母存在沟通障碍，本文通过案例分享一些有效的沟通方法。\n\n常见沟通问题：\n- 父母不理解我的想法\n- 说了他们也不听\n- 说着说着就吵起来\n- 他们总是把我当小孩\n\n有效沟通技巧：\n\n1. 选择合适的时机\n不要在父母忙碌或心情不好时谈论重要的事情。选择大家都很放松的时间。\n\n2. 表达感受而不是指责\n不要说你们总是不理解我，而是说我感到有点失落。\n\n3. 主动分享\n主动分享学校的事情、朋友的趣事，让父母了解你的生活。\n\n4. 理解父母的立场\n父母也是第一次做父母，他们的担忧来源于爱。试着理解他们的出发点。\n\n5. 寻求共识\n找到双方都能接受的解决方案，而不是只坚持自己的观点。', 'case', '心域知识库', NULL, '2026-04-05 19:58:14', '[\"沟通\", \"家庭\", \"人际关系\"]', '亲子沟通实用技巧', '2026-04-05 19:58:14', NULL);
INSERT INTO `knowledge_base` VALUES (45, '青少年心理健康保护', '青少年时期是一个充满变化和挑战的阶段，心理健康尤为重要。\n\n为什么心理健康很重要？\n- 心理健康影响学习效率\n- 影响人际关系\n- 关系到未来发展\n- 影响身体健康\n\n如何保护心理健康？\n\n1. 保持规律作息\n充足的睡眠对身体和心理都很重要。\n\n2. 适度运动\n运动可以释放压力，产生让人愉悦的内啡肽。\n\n3. 建立支持系统\n培养良好的朋友关系，在需要时寻求支持。\n\n4. 培养兴趣爱好\n做自己喜欢的事情可以带来满足感。\n\n5. 学会情绪管理\n了解自己的情绪，学习健康的调节方法。\n\n6. 遇到问题及时求助\n有问题及时找老师、家长或专业人士。\n\n记住：照顾好自己，才能更好地面对未来的挑战。', 'knowledge', '心域知识库', NULL, '2026-04-05 19:58:14', '[\"心理健康\", \"自我保护\", \"成长\"]', '青少年心理健康指南', '2026-04-05 19:58:14', NULL);
INSERT INTO `knowledge_base` VALUES (46, '如何应对考试焦虑', '考试焦虑是很多学生都会面临的问题。当我们面临重要的考试时，适度的紧张其实是一种正常的身心反应，它可以帮助我们更好地集中注意力。但过度的焦虑就会影响我们的发挥。\n\n那么，如何应对考试焦虑呢？\n\n1. 做好充分准备\n充分的准备是缓解焦虑的最好方法。制定合理的复习计划，按部就班地完成每天的学习任务，这样可以增强自信心。\n\n2. 学会放松技巧\n深呼吸是最简单有效的放松方法。闭上眼睛，深吸一口气，缓慢呼出，重复几次。也可以尝试渐进性肌肉放松法。\n\n3. 调整认知\n很多焦虑来源于对考试的过度担忧。尝试用更理性的想法替代灾难化的思维。\n\n4. 保证充足睡眠\n考试前不要熬夜，保持规律的作息。充足的睡眠可以帮助大脑更好地整合和记忆知识。\n\n5. 寻求支持\n如果焦虑情绪持续加重，不要犹豫，及时向老师、家长或心理咨询师寻求帮助。', 'skill', '心域知识库', NULL, '2026-04-05 22:34:45', '[\"考试\", \"焦虑\", \"调节\"]', '考试焦虑应对指南', '2026-04-05 22:34:45', NULL);
INSERT INTO `knowledge_base` VALUES (47, '正念冥想入门指南', '正念冥想是一种有效的情绪调节方法，源于佛教的禅修传统，现在已经被现代心理学广泛研究和应用。\n\n什么是正念？\n正念是指有意识地、不带评判地关注当下时刻的感受、想法和周围环境。它帮助我们从忙碌的思绪中抽离，专注于此时此刻。\n\n如何开始练习？\n\n1. 选择一个安静的环境\n找一个不会被打扰的地方，坐下或躺下。\n\n2. 设置时间\n初学者可以从 5-10 分钟开始，逐渐延长。\n\n3. 专注于呼吸\n闭上眼睛，将注意力集中在呼吸上。感受空气进入鼻腔、充满肺部、呼出的感觉。\n\n4. 接纳走神的思绪\n当思绪飘走时，不要批评自己，只是温柔地把注意力带回呼吸。\n\n5. 身体扫描\n感受身体每个部位的感觉，从头到脚，依次关注并放松。\n\n坚持练习会让你的情绪更加稳定，注意力更集中。', 'skill', '心域知识库', NULL, '2026-04-05 22:34:45', '[\"冥想\", \"放松\", \"正念\"]', '正念冥想基础教程', '2026-04-05 22:34:45', NULL);
INSERT INTO `knowledge_base` VALUES (48, '了解抑郁症的早期信号', '抑郁症并非一朝一夕形成，了解早期信号有助于及时发现和干预。\n\n常见的早期信号包括：\n\n1. 情绪变化\n- 持续的低落情绪\n- 对曾经喜欢的事物失去兴趣\n- 容易烦躁或发脾气\n\n2. 思维变化\n- 难以集中注意力\n- 觉得自己没有价值\n- 出现消极的念头\n\n3. 身体变化\n- 睡眠问题（失眠或嗜睡）\n- 食欲变化（食欲不振或暴饮暴食）\n- 容易疲劳\n\n4. 行为变化\n- 回避社交活动\n- 拖延逃避责任\n- 兴趣爱好减少\n\n如果你或身边的人出现以上信号持续两周以上，建议寻求专业帮助。学校心理中心、心理咨询师都是可以求助的渠道。\n\n记住：寻求帮助是勇敢的表现，不是软弱。', 'knowledge', '心域知识库', NULL, '2026-04-05 22:34:45', '[\"抑郁\", \"心理健康\", \"预防\"]', '抑郁症早期识别指南', '2026-04-05 22:34:45', NULL);
INSERT INTO `knowledge_base` VALUES (49, '与父母沟通的技巧', '很多青少年与父母存在沟通障碍，本文通过案例分享一些有效的沟通方法。\n\n常见沟通问题：\n- 父母不理解我的想法\n- 说了他们也不听\n- 说着说着就吵起来\n- 他们总是把我当小孩\n\n有效沟通技巧：\n\n1. 选择合适的时机\n不要在父母忙碌或心情不好时谈论重要的事情。选择大家都很放松的时间。\n\n2. 表达感受而不是指责\n不要说你们总是不理解我，而是说我感到有点失落。\n\n3. 主动分享\n主动分享学校的事情、朋友的趣事，让父母了解你的生活。\n\n4. 理解父母的立场\n父母也是第一次做父母，他们的担忧来源于爱。试着理解他们的出发点。\n\n5. 寻求共识\n找到双方都能接受的解决方案，而不是只坚持自己的观点。', 'case', '心域知识库', NULL, '2026-04-05 22:34:45', '[\"沟通\", \"家庭\", \"人际关系\"]', '亲子沟通实用技巧', '2026-04-05 22:34:45', NULL);
INSERT INTO `knowledge_base` VALUES (50, '青少年心理健康保护', '青少年时期是一个充满变化和挑战的阶段，心理健康尤为重要。\n\n为什么心理健康很重要？\n- 心理健康影响学习效率\n- 影响人际关系\n- 关系到未来发展\n- 影响身体健康\n\n如何保护心理健康？\n\n1. 保持规律作息\n充足的睡眠对身体和心理都很重要。\n\n2. 适度运动\n运动可以释放压力，产生让人愉悦的内啡肽。\n\n3. 建立支持系统\n培养良好的朋友关系，在需要时寻求支持。\n\n4. 培养兴趣爱好\n做自己喜欢的事情可以带来满足感。\n\n5. 学会情绪管理\n了解自己的情绪，学习健康的调节方法。\n\n6. 遇到问题及时求助\n有问题及时找老师、家长或专业人士。\n\n记住：照顾好自己，才能更好地面对未来的挑战。', 'knowledge', '心域知识库', NULL, '2026-04-05 22:34:45', '[\"心理健康\", \"自我保护\", \"成长\"]', '青少年心理健康指南', '2026-04-05 22:34:45', NULL);
INSERT INTO `knowledge_base` VALUES (51, '如何应对考试焦虑', '考试焦虑是很多学生都会面临的问题。当我们面临重要的考试时，适度的紧张其实是一种正常的身心反应，它可以帮助我们更好地集中注意力。但过度的焦虑就会影响我们的发挥。\n\n那么，如何应对考试焦虑呢？\n\n1. 做好充分准备\n充分的准备是缓解焦虑的最好方法。制定合理的复习计划，按部就班地完成每天的学习任务，这样可以增强自信心。\n\n2. 学会放松技巧\n深呼吸是最简单有效的放松方法。闭上眼睛，深吸一口气，缓慢呼出，重复几次。也可以尝试渐进性肌肉放松法。\n\n3. 调整认知\n很多焦虑来源于对考试的过度担忧。尝试用更理性的想法替代灾难化的思维。\n\n4. 保证充足睡眠\n考试前不要熬夜，保持规律的作息。充足的睡眠可以帮助大脑更好地整合和记忆知识。\n\n5. 寻求支持\n如果焦虑情绪持续加重，不要犹豫，及时向老师、家长或心理咨询师寻求帮助。', 'skill', '心域知识库', NULL, '2026-04-05 22:39:02', '[\"考试\", \"焦虑\", \"调节\"]', '考试焦虑应对指南', '2026-04-05 22:39:02', NULL);
INSERT INTO `knowledge_base` VALUES (52, '正念冥想入门指南', '正念冥想是一种有效的情绪调节方法，源于佛教的禅修传统，现在已经被现代心理学广泛研究和应用。\n\n什么是正念？\n正念是指有意识地、不带评判地关注当下时刻的感受、想法和周围环境。它帮助我们从忙碌的思绪中抽离，专注于此时此刻。\n\n如何开始练习？\n\n1. 选择一个安静的环境\n找一个不会被打扰的地方，坐下或躺下。\n\n2. 设置时间\n初学者可以从 5-10 分钟开始，逐渐延长。\n\n3. 专注于呼吸\n闭上眼睛，将注意力集中在呼吸上。感受空气进入鼻腔、充满肺部、呼出的感觉。\n\n4. 接纳走神的思绪\n当思绪飘走时，不要批评自己，只是温柔地把注意力带回呼吸。\n\n5. 身体扫描\n感受身体每个部位的感觉，从头到脚，依次关注并放松。\n\n坚持练习会让你的情绪更加稳定，注意力更集中。', 'skill', '心域知识库', NULL, '2026-04-05 22:39:02', '[\"冥想\", \"放松\", \"正念\"]', '正念冥想基础教程', '2026-04-05 22:39:02', NULL);
INSERT INTO `knowledge_base` VALUES (53, '了解抑郁症的早期信号', '抑郁症并非一朝一夕形成，了解早期信号有助于及时发现和干预。\n\n常见的早期信号包括：\n\n1. 情绪变化\n- 持续的低落情绪\n- 对曾经喜欢的事物失去兴趣\n- 容易烦躁或发脾气\n\n2. 思维变化\n- 难以集中注意力\n- 觉得自己没有价值\n- 出现消极的念头\n\n3. 身体变化\n- 睡眠问题（失眠或嗜睡）\n- 食欲变化（食欲不振或暴饮暴食）\n- 容易疲劳\n\n4. 行为变化\n- 回避社交活动\n- 拖延逃避责任\n- 兴趣爱好减少\n\n如果你或身边的人出现以上信号持续两周以上，建议寻求专业帮助。学校心理中心、心理咨询师都是可以求助的渠道。\n\n记住：寻求帮助是勇敢的表现，不是软弱。', 'knowledge', '心域知识库', NULL, '2026-04-05 22:39:02', '[\"抑郁\", \"心理健康\", \"预防\"]', '抑郁症早期识别指南', '2026-04-05 22:39:02', NULL);
INSERT INTO `knowledge_base` VALUES (54, '与父母沟通的技巧', '很多青少年与父母存在沟通障碍，本文通过案例分享一些有效的沟通方法。\n\n常见沟通问题：\n- 父母不理解我的想法\n- 说了他们也不听\n- 说着说着就吵起来\n- 他们总是把我当小孩\n\n有效沟通技巧：\n\n1. 选择合适的时机\n不要在父母忙碌或心情不好时谈论重要的事情。选择大家都很放松的时间。\n\n2. 表达感受而不是指责\n不要说你们总是不理解我，而是说我感到有点失落。\n\n3. 主动分享\n主动分享学校的事情、朋友的趣事，让父母了解你的生活。\n\n4. 理解父母的立场\n父母也是第一次做父母，他们的担忧来源于爱。试着理解他们的出发点。\n\n5. 寻求共识\n找到双方都能接受的解决方案，而不是只坚持自己的观点。', 'case', '心域知识库', NULL, '2026-04-05 22:39:02', '[\"沟通\", \"家庭\", \"人际关系\"]', '亲子沟通实用技巧', '2026-04-05 22:39:02', NULL);
INSERT INTO `knowledge_base` VALUES (55, '青少年心理健康保护', '青少年时期是一个充满变化和挑战的阶段，心理健康尤为重要。\n\n为什么心理健康很重要？\n- 心理健康影响学习效率\n- 影响人际关系\n- 关系到未来发展\n- 影响身体健康\n\n如何保护心理健康？\n\n1. 保持规律作息\n充足的睡眠对身体和心理都很重要。\n\n2. 适度运动\n运动可以释放压力，产生让人愉悦的内啡肽。\n\n3. 建立支持系统\n培养良好的朋友关系，在需要时寻求支持。\n\n4. 培养兴趣爱好\n做自己喜欢的事情可以带来满足感。\n\n5. 学会情绪管理\n了解自己的情绪，学习健康的调节方法。\n\n6. 遇到问题及时求助\n有问题及时找老师、家长或专业人士。\n\n记住：照顾好自己，才能更好地面对未来的挑战。', 'knowledge', '心域知识库', NULL, '2026-04-05 22:39:02', '[\"心理健康\", \"自我保护\", \"成长\"]', '青少年心理健康指南', '2026-04-05 22:39:02', NULL);

-- ----------------------------
-- Table structure for login_log
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `login_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'password' COMMENT '登录方式 (password/sms/wechat)',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `ip_location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP归属地',
  `device_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备类型',
  `device_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备信息',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 (0失败1成功)',
  `fail_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '失败原因',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '登录日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of login_log
-- ----------------------------
INSERT INTO `login_log` VALUES (1, 1, 'test_user', 'password', '127.0.0.1', NULL, 'Web', NULL, 1, NULL, '2026-04-05 18:58:24');
INSERT INTO `login_log` VALUES (2, 2, 'xiaoming', 'password', '127.0.0.1', NULL, 'Android', NULL, 1, NULL, '2026-04-05 17:58:24');
INSERT INTO `login_log` VALUES (3, 1, 'test_user', 'password', '127.0.0.1', NULL, 'Web', NULL, 0, 'wrong_password', '2026-04-05 14:58:24');
INSERT INTO `login_log` VALUES (4, 1, 'test_user', 'password', '127.0.0.1', NULL, 'Web', NULL, 1, NULL, '2026-04-05 21:34:47');
INSERT INTO `login_log` VALUES (5, 2, 'xiaoming', 'password', '127.0.0.1', NULL, 'Android', NULL, 1, NULL, '2026-04-05 20:34:47');
INSERT INTO `login_log` VALUES (6, 1, 'test_user', 'password', '127.0.0.1', NULL, 'Web', NULL, 0, 'wrong_password', '2026-04-05 17:34:47');
INSERT INTO `login_log` VALUES (7, 1, 'test_user', 'password', '127.0.0.1', NULL, 'Web', NULL, 1, NULL, '2026-04-05 21:39:06');
INSERT INTO `login_log` VALUES (8, 2, 'xiaoming', 'password', '127.0.0.1', NULL, 'Android', NULL, 1, NULL, '2026-04-05 20:39:06');
INSERT INTO `login_log` VALUES (9, 1, 'test_user', 'password', '127.0.0.1', NULL, 'Web', NULL, 0, 'wrong_password', '2026-04-05 17:39:06');
INSERT INTO `login_log` VALUES (10, 32, '一朝', 'password', '0:0:0:0:0:0:0:1', '本地', 'PC', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, NULL, '2026-04-06 14:50:33');
INSERT INTO `login_log` VALUES (11, 32, '一朝', 'password', '0:0:0:0:0:0:0:1', '本地', 'PC', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, NULL, '2026-04-06 15:02:47');
INSERT INTO `login_log` VALUES (12, 32, '一朝', 'password', '0:0:0:0:0:0:0:1', '本地', 'PC', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, NULL, '2026-04-07 19:13:55');
INSERT INTO `login_log` VALUES (13, 32, '一朝', 'password', '0:0:0:0:0:0:0:1', '本地', 'PC', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36', 1, NULL, '2026-04-07 19:32:22');
INSERT INTO `login_log` VALUES (14, 32, '一朝', 'password', '0:0:0:0:0:0:0:1', '本地', 'PC', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36', 1, NULL, '2026-04-07 19:33:28');
INSERT INTO `login_log` VALUES (15, 32, '一朝', 'password', '0:0:0:0:0:0:0:1', '本地', 'PC', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36', 1, NULL, '2026-04-07 19:50:38');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知类型(reminder/warning/system)',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '通知内容',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读(0否1是)',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_read`(`user_id` ASC, `is_read` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, 1, 'reminder', '日记提醒', '今天还没有写日记哦，记录一下今天的心情吧~', 0, '2026-04-03 14:23:03');
INSERT INTO `notification` VALUES (2, 1, 'system', '欢迎回来', '欢迎回到心域，继续记录你的情绪旅程吧！', 1, '2026-04-02 14:53:03');
INSERT INTO `notification` VALUES (3, 2, 'system', '情绪关怀', '最近你记录的情绪波动较大，如果需要倾诉，我一直在哦~', 0, '2026-04-01 14:53:03');
INSERT INTO `notification` VALUES (4, 3, 'system', '周报提醒', '本周的情绪报告已生成，点击查看详情~', 1, '2026-03-31 14:53:03');
INSERT INTO `notification` VALUES (5, 1, 'reminder', '日记提醒', '今天还没有写日记哦，记录一下今天的心情吧~', 0, '2026-04-05 19:28:16');
INSERT INTO `notification` VALUES (6, 1, 'system', '欢迎回来', '欢迎回到心域，继续记录你的情绪旅程吧！', 1, '2026-04-04 19:58:16');
INSERT INTO `notification` VALUES (7, 2, 'system', '情绪关怀', '最近你记录的情绪波动较大，如果需要倾诉，我一直在哦~', 0, '2026-04-03 19:58:16');
INSERT INTO `notification` VALUES (8, 3, 'system', '周报提醒', '本周的情绪报告已生成，点击查看详情~', 1, '2026-04-02 19:58:16');
INSERT INTO `notification` VALUES (9, 1, 'reminder', '日记提醒', '今天还没有写日记哦，记录一下今天的心情吧~', 0, '2026-04-05 20:00:00');
INSERT INTO `notification` VALUES (10, 2, 'reminder', '日记提醒', '今天还没有写日记哦，记录一下今天的心情吧~', 0, '2026-04-05 20:00:00');
INSERT INTO `notification` VALUES (11, 3, 'reminder', '日记提醒', '今天还没有写日记哦，记录一下今天的心情吧~', 0, '2026-04-05 20:00:01');
INSERT INTO `notification` VALUES (12, 4, 'reminder', '日记提醒', '今天还没有写日记哦，记录一下今天的心情吧~', 0, '2026-04-05 20:00:01');
INSERT INTO `notification` VALUES (13, 5, 'reminder', '日记提醒', '今天还没有写日记哦，记录一下今天的心情吧~', 0, '2026-04-05 20:00:01');
INSERT INTO `notification` VALUES (14, 32, 'reminder', '日记提醒', '今天还没有写日记哦，记录一下今天的心情吧~', 0, '2026-04-05 20:00:02');
INSERT INTO `notification` VALUES (15, 1, 'reminder', '日记提醒', '今天还没有写日记哦，记录一下今天的心情吧~', 0, '2026-04-05 22:04:46');
INSERT INTO `notification` VALUES (16, 1, 'system', '欢迎回来', '欢迎回到心域，继续记录你的情绪旅程吧！', 1, '2026-04-04 22:34:46');
INSERT INTO `notification` VALUES (17, 2, 'system', '情绪关怀', '最近你记录的情绪波动较大，如果需要倾诉，我一直在哦~', 0, '2026-04-03 22:34:46');
INSERT INTO `notification` VALUES (18, 3, 'system', '周报提醒', '本周的情绪报告已生成，点击查看详情~', 1, '2026-04-02 22:34:46');
INSERT INTO `notification` VALUES (19, 1, 'reminder', '日记提醒', '今天还没有写日记哦，记录一下今天的心情吧~', 0, '2026-04-05 22:09:04');
INSERT INTO `notification` VALUES (20, 1, 'system', '欢迎回来', '欢迎回到心域，继续记录你的情绪旅程吧！', 1, '2026-04-04 22:39:04');
INSERT INTO `notification` VALUES (21, 2, 'system', '情绪关怀', '最近你记录的情绪波动较大，如果需要倾诉，我一直在哦~', 0, '2026-04-03 22:39:04');
INSERT INTO `notification` VALUES (22, 3, 'system', '周报提醒', '本周的情绪报告已生成，点击查看详情~', 1, '2026-04-02 22:39:04');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模块',
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作描述',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法',
  `request_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求URL',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方式',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `response_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '返回结果',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `duration` bigint NULL DEFAULT NULL COMMENT '耗时(毫秒)',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(0失败1成功)',
  `error_msg` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误信息',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_module`(`module` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 362 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (1, NULL, NULL, 'AuthController', 'sendCode', 'AuthController.sendCode', '/api/v1/auth/sendCode', 'POST', '{email=2780059846@qq.com}', NULL, '0:0:0:0:0:0:0:1', 599, 1, NULL, '2026-04-05 19:41:26');
INSERT INTO `operation_log` VALUES (2, NULL, NULL, 'AuthController', 'register', 'AuthController.register', '/api/v1/auth/register', 'POST', 'User(id=null, username=一朝, phone=null, email=2780059846@qq.com, password=ljw040817, nickname=一朝, avatar=null, age=18, gender=1, role=null, status=null, guardianPhone=null, createdAt=null, updatedAt=null)', NULL, '0:0:0:0:0:0:0:1', 785, 0, '\r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'email\' in \'field list\'\r\n### The error may exist in com/mindrealm/common/mapper/UserMapper.java (best guess)\r\n### The error may involve defaultParameterMap\r\n### The error occurred while setting parameters\r\n### SQL: SELECT  id,username,phone,email,password,nickname,avatar,age,gender,role,status,guardian_phone,created_at,updated_at  FROM users      WHERE  (username = ?)\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'email\' in \'field list\'\n; bad SQL grammar []', '2026-04-05 19:42:31');
INSERT INTO `operation_log` VALUES (3, NULL, NULL, 'AuthController', 'register', 'AuthController.register', '/api/v1/auth/register', 'POST', 'User(id=null, username=一朝, phone=null, email=2780059846@qq.com, password=ljw040817, nickname=一朝, avatar=null, age=18, gender=1, role=null, status=null, guardianPhone=null, createdAt=null, updatedAt=null)', NULL, '0:0:0:0:0:0:0:1', 267, 1, NULL, '2026-04-05 19:43:28');
INSERT INTO `operation_log` VALUES (4, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email= 2780059846@qq.com, password=ljw040817, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-05 19:43:42');
INSERT INTO `operation_log` VALUES (5, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email= 2780059846@qq.com, password=ljw040817, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 2, 1, NULL, '2026-04-05 19:43:54');
INSERT INTO `operation_log` VALUES (6, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email= 2780059846@qq.com, password=ljw040817, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-05 19:44:09');
INSERT INTO `operation_log` VALUES (7, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email= 2780059846@qq.com, password=ljw040817, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-05 19:47:18');
INSERT INTO `operation_log` VALUES (8, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email= 2780059846@qq.com, password=ljw040817, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 117, 1, NULL, '2026-04-05 19:48:10');
INSERT INTO `operation_log` VALUES (9, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email= 2780059846@qq.com, password=ljw040817, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 107, 1, NULL, '2026-04-05 19:50:28');
INSERT INTO `operation_log` VALUES (10, NULL, NULL, 'AuthController', 'sendCode', 'AuthController.sendCode', '/api/v1/auth/sendCode', 'POST', '{email=2780059846@qq.com}', NULL, '0:0:0:0:0:0:0:1', 559, 1, NULL, '2026-04-05 19:53:06');
INSERT INTO `operation_log` VALUES (11, NULL, NULL, 'AuthController', 'sendCode', 'AuthController.sendCode', '/api/v1/auth/sendCode', 'POST', '{email=2780059846@qq.com}', NULL, '0:0:0:0:0:0:0:1', 382, 1, NULL, '2026-04-05 19:59:19');
INSERT INTO `operation_log` VALUES (12, NULL, NULL, 'AuthController', 'register', 'AuthController.register', '/api/v1/auth/register', 'POST', 'User(id=null, username=winterfly, phone=null, email=2780059846@qq.com, password=12345678, nickname=winterfly, avatar=null, age=18, gender=1, role=null, status=null, guardianPhone=null, createdAt=null, updatedAt=null)', NULL, '0:0:0:0:0:0:0:1', 14, 1, NULL, '2026-04-05 20:02:15');
INSERT INTO `operation_log` VALUES (13, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email= 2780059846@qq.com, password=12345678, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 43, 1, NULL, '2026-04-05 20:02:28');
INSERT INTO `operation_log` VALUES (14, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email= 2780059846@qq.com, password=12345678, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-05 20:02:41');
INSERT INTO `operation_log` VALUES (15, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email= 2780059846@qq.com, password=12345678, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-05 20:05:49');
INSERT INTO `operation_log` VALUES (16, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email= 2780059846@qq.com, password=12345678, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 302, 1, NULL, '2026-04-05 20:06:36');
INSERT INTO `operation_log` VALUES (17, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email= 2780059846@qq.com, password=12345678, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 14, 1, NULL, '2026-04-05 20:10:06');
INSERT INTO `operation_log` VALUES (18, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email=27800598456@qq.com, password=e10adc3949ba59abbe56e057f20f883e, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-05 20:10:20');
INSERT INTO `operation_log` VALUES (19, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email=27800598456@qq.com, password=12345678, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-05 20:10:26');
INSERT INTO `operation_log` VALUES (20, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email=2780059846@qq.com, password=12345678, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 222, 1, NULL, '2026-04-05 22:30:57');
INSERT INTO `operation_log` VALUES (21, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email=2780059846@qq.com, password=12345678, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 2, 1, NULL, '2026-04-05 22:31:06');
INSERT INTO `operation_log` VALUES (22, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email=2780059846@qq.com, password=123456, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 118, 1, NULL, '2026-04-05 22:34:38');
INSERT INTO `operation_log` VALUES (23, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email=2780059846@qq.com, password=123456, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-05 22:35:03');
INSERT INTO `operation_log` VALUES (24, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=一朝, email=null, password=1234567, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-05 22:36:52');
INSERT INTO `operation_log` VALUES (25, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=一朝, email=null, password=12345678, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-05 22:36:57');
INSERT INTO `operation_log` VALUES (26, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=一朝, email=null, password=123456, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-05 22:36:59');
INSERT INTO `operation_log` VALUES (27, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email=2780059846@qq.com, password=12345678, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 161, 1, NULL, '2026-04-05 22:37:29');
INSERT INTO `operation_log` VALUES (28, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email=2780059846@qq.com, password=12345678, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-05 22:38:02');
INSERT INTO `operation_log` VALUES (29, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email=2780059846@qq.com, password=123456, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 36, 1, NULL, '2026-04-05 22:39:13');
INSERT INTO `operation_log` VALUES (30, NULL, NULL, 'AuthController', 'sendCode', 'AuthController.sendCode', '/api/v1/auth/sendCode', 'POST', '{email=2780059846@qq.com}', NULL, '0:0:0:0:0:0:0:1', 2, 1, NULL, '2026-04-05 22:39:46');
INSERT INTO `operation_log` VALUES (31, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email=2780059846@qq.com, password=123456, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-05 22:40:10');
INSERT INTO `operation_log` VALUES (32, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email=2780059846@qq.com, password=12345678, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-05 22:40:26');
INSERT INTO `operation_log` VALUES (33, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=一朝, email=null, password=12345678, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 23, 1, NULL, '2026-04-05 22:41:49');
INSERT INTO `operation_log` VALUES (34, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=一朝, email=null, password=123456, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 42, 1, NULL, '2026-04-05 22:41:52');
INSERT INTO `operation_log` VALUES (35, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=null, email=2780059846@qq.com, password=123456, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 174, 1, NULL, '2026-04-05 22:42:41');
INSERT INTO `operation_log` VALUES (36, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 152, 1, NULL, '2026-04-05 22:42:43');
INSERT INTO `operation_log` VALUES (37, NULL, NULL, 'DiaryController', 'getEmotionReport', 'DiaryController.getEmotionReport', '/api/v1/diary/report', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-05 22:43:24');
INSERT INTO `operation_log` VALUES (38, NULL, NULL, 'DiaryController', 'getEmotionReport', 'DiaryController.getEmotionReport', '/api/v1/diary/report', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 2, 1, NULL, '2026-04-05 22:43:26');
INSERT INTO `operation_log` VALUES (39, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 178, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:45:44');
INSERT INTO `operation_log` VALUES (40, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 1, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:45:44');
INSERT INTO `operation_log` VALUES (41, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 2, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:45:46');
INSERT INTO `operation_log` VALUES (42, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 2, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:45:46');
INSERT INTO `operation_log` VALUES (43, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 327, 1, NULL, '2026-04-05 22:47:57');
INSERT INTO `operation_log` VALUES (44, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 853, 1, NULL, '2026-04-05 22:47:57');
INSERT INTO `operation_log` VALUES (45, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 16, 1, NULL, '2026-04-05 22:47:58');
INSERT INTO `operation_log` VALUES (46, NULL, NULL, 'UserController', 'updateProfile', 'UserController.updateProfile', '/api/v1/user/profile', 'PUT', 'User(id=32, username=一朝, phone=null, email=2780059846@qq.com, password=null, nickname=一朝, avatar=blob:http://localhost:5174/e2d62637-d615-427a-bdc0-3df8ee8a6960, age=18, gender=1, role=1, status=1, guardianPhone=null, createdAt=2026-04-05T19:43:28, updatedAt=2026-04-05T20:10:03)', NULL, '0:0:0:0:0:0:0:1', 12, 0, 'class java.util.LinkedHashMap cannot be cast to class com.mindrealm.common.entity.User (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.mindrealm.common.entity.User is in unnamed module of loader org.springframework.boot.devtools.restart.classloader.RestartClassLoader @7eabbda4)', '2026-04-05 22:48:03');
INSERT INTO `operation_log` VALUES (47, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 2, 0, 'class java.util.LinkedHashMap cannot be cast to class com.mindrealm.common.entity.User (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.mindrealm.common.entity.User is in unnamed module of loader org.springframework.boot.devtools.restart.classloader.RestartClassLoader @7eabbda4)', '2026-04-05 22:48:05');
INSERT INTO `operation_log` VALUES (48, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 2, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:48:07');
INSERT INTO `operation_log` VALUES (49, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:48:07');
INSERT INTO `operation_log` VALUES (50, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 1, 0, 'class java.util.LinkedHashMap cannot be cast to class com.mindrealm.common.entity.User (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.mindrealm.common.entity.User is in unnamed module of loader org.springframework.boot.devtools.restart.classloader.RestartClassLoader @7eabbda4)', '2026-04-05 22:48:08');
INSERT INTO `operation_log` VALUES (51, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 1, 0, 'class java.util.LinkedHashMap cannot be cast to class com.mindrealm.common.entity.User (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.mindrealm.common.entity.User is in unnamed module of loader org.springframework.boot.devtools.restart.classloader.RestartClassLoader @7eabbda4)', '2026-04-05 22:48:09');
INSERT INTO `operation_log` VALUES (52, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 28, 0, 'class java.util.LinkedHashMap cannot be cast to class com.mindrealm.common.entity.User (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.mindrealm.common.entity.User is in unnamed module of loader org.springframework.boot.devtools.restart.classloader.RestartClassLoader @7eabbda4)', '2026-04-05 22:48:23');
INSERT INTO `operation_log` VALUES (53, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 3, 0, 'class java.util.LinkedHashMap cannot be cast to class com.mindrealm.common.entity.User (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.mindrealm.common.entity.User is in unnamed module of loader org.springframework.boot.devtools.restart.classloader.RestartClassLoader @7eabbda4)', '2026-04-05 22:48:25');
INSERT INTO `operation_log` VALUES (54, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 2, 0, 'class java.util.LinkedHashMap cannot be cast to class com.mindrealm.common.entity.User (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.mindrealm.common.entity.User is in unnamed module of loader org.springframework.boot.devtools.restart.classloader.RestartClassLoader @7eabbda4)', '2026-04-05 22:48:26');
INSERT INTO `operation_log` VALUES (55, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 2, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:48:27');
INSERT INTO `operation_log` VALUES (56, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 1, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:48:28');
INSERT INTO `operation_log` VALUES (57, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 2, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:48:32');
INSERT INTO `operation_log` VALUES (58, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 2, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:48:43');
INSERT INTO `operation_log` VALUES (59, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 1, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:49:23');
INSERT INTO `operation_log` VALUES (60, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 1, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:49:23');
INSERT INTO `operation_log` VALUES (61, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 3, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:49:25');
INSERT INTO `operation_log` VALUES (62, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 2, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:49:25');
INSERT INTO `operation_log` VALUES (63, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 40, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:51:51');
INSERT INTO `operation_log` VALUES (64, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 1, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:51:51');
INSERT INTO `operation_log` VALUES (65, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 3, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:51:53');
INSERT INTO `operation_log` VALUES (66, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 1, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:51:53');
INSERT INTO `operation_log` VALUES (67, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 2, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:52:23');
INSERT INTO `operation_log` VALUES (68, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 5, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:52:25');
INSERT INTO `operation_log` VALUES (69, NULL, NULL, 'DiaryController', 'getById', 'DiaryController.getById', '/api/v1/diary/1', 'GET', '1', NULL, '0:0:0:0:0:0:0:1', 12, 1, NULL, '2026-04-05 22:52:33');
INSERT INTO `operation_log` VALUES (70, NULL, NULL, 'DiaryController', 'getById', 'DiaryController.getById', '/api/v1/diary/1', 'GET', '1', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-05 22:52:35');
INSERT INTO `operation_log` VALUES (71, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 1, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:52:39');
INSERT INTO `operation_log` VALUES (72, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 1, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:52:39');
INSERT INTO `operation_log` VALUES (73, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 1, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:52:39');
INSERT INTO `operation_log` VALUES (74, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 2, 0, 'class java.util.LinkedHashMap cannot be cast to class com.baomidou.mybatisplus.extension.plugins.pagination.Page (java.util.LinkedHashMap is in module java.base of loader \'bootstrap\'; com.baomidou.mybatisplus.extension.plugins.pagination.Page is in unnamed module of loader \'app\')', '2026-04-05 22:52:40');
INSERT INTO `operation_log` VALUES (75, NULL, NULL, 'DiaryController', 'getById', 'DiaryController.getById', '/api/v1/diary/3', 'GET', '3', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-05 22:52:41');
INSERT INTO `operation_log` VALUES (76, NULL, NULL, 'DiaryController', 'getById', 'DiaryController.getById', '/api/v1/diary/3', 'GET', '3', NULL, '0:0:0:0:0:0:0:1', 13, 1, NULL, '2026-04-05 23:04:13');
INSERT INTO `operation_log` VALUES (77, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 264, 1, NULL, '2026-04-05 23:04:15');
INSERT INTO `operation_log` VALUES (78, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 191, 1, NULL, '2026-04-05 23:05:07');
INSERT INTO `operation_log` VALUES (79, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-05 23:05:09');
INSERT INTO `operation_log` VALUES (80, NULL, NULL, 'UserController', 'updateProfile', 'UserController.updateProfile', '/api/v1/user/profile', 'PUT', 'User(id=32, username=一朝, phone=null, email=2780059846@qq.com, password=null, nickname=一朝, avatar=blob:http://localhost:5174/639fec8f-0fb0-4357-a16b-dd84bbdd178d, age=18, gender=1, role=1, status=1, guardianPhone=null, createdAt=2026-04-05T19:43:28, updatedAt=2026-04-05T20:10:03)', NULL, '0:0:0:0:0:0:0:1', 217, 1, NULL, '2026-04-05 23:05:16');
INSERT INTO `operation_log` VALUES (81, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-05 23:05:20');
INSERT INTO `operation_log` VALUES (82, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-04-05 23:05:22');
INSERT INTO `operation_log` VALUES (83, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-05 23:05:22');
INSERT INTO `operation_log` VALUES (84, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-04-05 23:07:27');
INSERT INTO `operation_log` VALUES (85, NULL, NULL, 'DiaryController', 'getEmotionReport', 'DiaryController.getEmotionReport', '/api/v1/diary/report', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-05 23:07:37');
INSERT INTO `operation_log` VALUES (86, NULL, NULL, 'DiaryController', 'getEmotionReport', 'DiaryController.getEmotionReport', '/api/v1/diary/report', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-05 23:07:43');
INSERT INTO `operation_log` VALUES (87, NULL, NULL, 'DiaryController', 'getEmotionReport', 'DiaryController.getEmotionReport', '/api/v1/diary/report', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 2, 1, NULL, '2026-04-05 23:07:44');
INSERT INTO `operation_log` VALUES (88, NULL, NULL, 'AdminAuthController', 'login', 'AdminAuthController.login', '/admin/v1/auth/login', 'POST', '{username=admin, password=123457}', NULL, '0:0:0:0:0:0:0:1', 178, 1, NULL, '2026-04-05 23:14:42');
INSERT INTO `operation_log` VALUES (89, NULL, NULL, 'AdminAuthController', 'login', 'AdminAuthController.login', '/admin/v1/auth/login', 'POST', '{username=admin, password=123456}', NULL, '0:0:0:0:0:0:0:1', 199, 1, NULL, '2026-04-05 23:14:47');
INSERT INTO `operation_log` VALUES (90, NULL, NULL, 'AdminStatsController', 'getOverview', 'AdminStatsController.getOverview', '/admin/v1/stats/overview', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 15, 1, NULL, '2026-04-05 23:14:48');
INSERT INTO `operation_log` VALUES (91, NULL, NULL, 'AdminStatsController', 'getUserStats', 'AdminStatsController.getUserStats', '/admin/v1/stats/users', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 205, 1, NULL, '2026-04-05 23:14:48');
INSERT INTO `operation_log` VALUES (92, NULL, NULL, 'AdminStatsController', 'getOverview', 'AdminStatsController.getOverview', '/admin/v1/stats/overview', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-05 23:14:48');
INSERT INTO `operation_log` VALUES (93, NULL, NULL, 'AdminStatsController', 'getUserStats', 'AdminStatsController.getUserStats', '/admin/v1/stats/users', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-05 23:14:48');
INSERT INTO `operation_log` VALUES (94, NULL, NULL, 'AdminStatsController', 'getUserStats', 'AdminStatsController.getUserStats', '/admin/v1/stats/users', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-05 23:15:44');
INSERT INTO `operation_log` VALUES (95, NULL, NULL, 'AdminStatsController', 'getOverview', 'AdminStatsController.getOverview', '/admin/v1/stats/overview', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-05 23:15:44');
INSERT INTO `operation_log` VALUES (96, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-05 23:17:30');
INSERT INTO `operation_log` VALUES (97, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 1084, 1, NULL, '2026-04-05 23:18:51');
INSERT INTO `operation_log` VALUES (98, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=一朝, email=null, password=123456, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 1185, 1, NULL, '2026-04-06 14:50:34');
INSERT INTO `operation_log` VALUES (99, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 140, 1, NULL, '2026-04-06 14:50:36');
INSERT INTO `operation_log` VALUES (100, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-06 14:50:36');
INSERT INTO `operation_log` VALUES (101, NULL, NULL, 'ChatController', 'send', 'ChatController.send', '/api/v1/chat/send', 'POST', 'listener, {message=我\n, persona=listener}', NULL, '0:0:0:0:0:0:0:1', 2692, 1, NULL, '2026-04-06 14:50:48');
INSERT INTO `operation_log` VALUES (102, NULL, NULL, 'ChatController', 'send', 'ChatController.send', '/api/v1/chat/send', 'POST', 'listener, {message=我在想\n我能做什么/, persona=listener}', NULL, '0:0:0:0:0:0:0:1', 1416, 1, NULL, '2026-04-06 14:50:59');
INSERT INTO `operation_log` VALUES (103, NULL, NULL, 'ChatController', 'send', 'ChatController.send', '/api/v1/chat/send', 'POST', 'listener, {message=你觉得或者？\n\n, persona=listener}', NULL, '0:0:0:0:0:0:0:1', 1502, 1, NULL, '2026-04-06 14:51:23');
INSERT INTO `operation_log` VALUES (104, NULL, NULL, 'ChatController', 'send', 'ChatController.send', '/api/v1/chat/send', 'POST', 'listener, {message=我该怎么办\n, persona=analyst}', NULL, '0:0:0:0:0:0:0:1', 2958, 1, NULL, '2026-04-06 14:51:36');
INSERT INTO `operation_log` VALUES (105, NULL, NULL, 'ChatController', 'send', 'ChatController.send', '/api/v1/chat/send', 'POST', 'listener, {message=我在想提升自己\n, persona=analyst}', NULL, '0:0:0:0:0:0:0:1', 3442, 1, NULL, '2026-04-06 14:51:47');
INSERT INTO `operation_log` VALUES (106, NULL, NULL, 'ChatController', 'send', 'ChatController.send', '/api/v1/chat/send', 'POST', 'listener, {message=心好累分手了\n, persona=healer}', NULL, '0:0:0:0:0:0:0:1', 2112, 1, NULL, '2026-04-06 14:52:08');
INSERT INTO `operation_log` VALUES (107, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-06 14:52:52');
INSERT INTO `operation_log` VALUES (108, NULL, NULL, 'UserController', 'updateProfile', 'UserController.updateProfile', '/api/v1/user/profile', 'PUT', 'User(id=32, username=一朝, phone=null, email=2780059846@qq.com, password=null, nickname=一朝, avatar=blob:http://localhost:5173/dafed62e-895e-4470-b235-2573de76a229, age=18, gender=1, role=1, status=1, guardianPhone=null, createdAt=2026-04-05T19:43:28, updatedAt=2026-04-05T23:05:16)', NULL, '0:0:0:0:0:0:0:1', 159, 1, NULL, '2026-04-06 14:53:00');
INSERT INTO `operation_log` VALUES (109, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-06 14:53:02');
INSERT INTO `operation_log` VALUES (110, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 12, 1, NULL, '2026-04-06 14:53:04');
INSERT INTO `operation_log` VALUES (111, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-04-06 14:53:04');
INSERT INTO `operation_log` VALUES (112, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 95, 1, NULL, '2026-04-06 14:58:56');
INSERT INTO `operation_log` VALUES (113, NULL, NULL, 'NotificationController', 'list', 'NotificationController.list', '/api/v1/notification/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 942, 1, NULL, '2026-04-06 14:58:56');
INSERT INTO `operation_log` VALUES (114, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 15, 1, NULL, '2026-04-06 14:58:57');
INSERT INTO `operation_log` VALUES (115, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-06 14:58:58');
INSERT INTO `operation_log` VALUES (116, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 14:59:00');
INSERT INTO `operation_log` VALUES (117, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 17, 1, NULL, '2026-04-06 14:59:02');
INSERT INTO `operation_log` VALUES (118, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 22, 1, NULL, '2026-04-06 14:59:02');
INSERT INTO `operation_log` VALUES (119, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-04-06 14:59:36');
INSERT INTO `operation_log` VALUES (120, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 14:59:36');
INSERT INTO `operation_log` VALUES (121, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-06 14:59:36');
INSERT INTO `operation_log` VALUES (122, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 14:59:36');
INSERT INTO `operation_log` VALUES (123, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 10, 1, NULL, '2026-04-06 15:02:32');
INSERT INTO `operation_log` VALUES (124, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 10, 1, NULL, '2026-04-06 15:02:32');
INSERT INTO `operation_log` VALUES (125, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-06 15:02:35');
INSERT INTO `operation_log` VALUES (126, NULL, NULL, 'AuthController', 'logout', 'AuthController.logout', '/api/v1/auth/logout', 'POST', 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoxLCJ1c2VySWQiOjMyLCJ1c2VybmFtZSI6IuS4gOacnSIsInN1YiI6IuS4gOacnSIsImlhdCI6MTc3NTQ1ODIzMywiZXhwIjoxNzc1NTQ0NjMzfQ.jMJpjutkurCaY2otDIhtkvyHcFdvQmO2MqlCOYbMwVI', NULL, '0:0:0:0:0:0:0:1', 1153, 1, NULL, '2026-04-06 15:02:40');
INSERT INTO `operation_log` VALUES (127, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=一朝, email=null, password=123456, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 162, 1, NULL, '2026-04-06 15:02:47');
INSERT INTO `operation_log` VALUES (128, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 15:03:03');
INSERT INTO `operation_log` VALUES (129, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 13, 1, NULL, '2026-04-06 15:03:10');
INSERT INTO `operation_log` VALUES (130, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-06 15:03:10');
INSERT INTO `operation_log` VALUES (131, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-06 15:03:26');
INSERT INTO `operation_log` VALUES (132, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 668, 1, NULL, '2026-04-06 15:31:37');
INSERT INTO `operation_log` VALUES (133, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 16, 1, NULL, '2026-04-06 15:31:37');
INSERT INTO `operation_log` VALUES (134, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-06 15:31:38');
INSERT INTO `operation_log` VALUES (135, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 12, 1, NULL, '2026-04-06 15:31:38');
INSERT INTO `operation_log` VALUES (136, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 15:31:40');
INSERT INTO `operation_log` VALUES (137, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 15:31:46');
INSERT INTO `operation_log` VALUES (138, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-06 15:31:48');
INSERT INTO `operation_log` VALUES (139, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 247, 1, NULL, '2026-04-06 15:31:50');
INSERT INTO `operation_log` VALUES (140, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 98, 1, NULL, '2026-04-06 15:31:50');
INSERT INTO `operation_log` VALUES (141, NULL, NULL, 'FileUploadController', 'uploadImage', 'FileUploadController.uploadImage', '/api/v1/file/upload/image', 'POST', 'org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@4ca83679', NULL, '0:0:0:0:0:0:0:1', 67, 1, NULL, '2026-04-06 15:31:59');
INSERT INTO `operation_log` VALUES (142, NULL, NULL, 'FileUploadController', 'uploadImage', 'FileUploadController.uploadImage', '/api/v1/file/upload/image', 'POST', 'org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@57c1526', NULL, '0:0:0:0:0:0:0:1', 586, 1, NULL, '2026-04-06 15:35:03');
INSERT INTO `operation_log` VALUES (143, NULL, NULL, 'FileUploadController', 'uploadImage', 'FileUploadController.uploadImage', '/api/v1/file/upload/image', 'POST', 'org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@6f89d038', NULL, '0:0:0:0:0:0:0:1', 229, 1, NULL, '2026-04-06 15:35:48');
INSERT INTO `operation_log` VALUES (144, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 20, 1, NULL, '2026-04-06 15:36:05');
INSERT INTO `operation_log` VALUES (145, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 117, 1, NULL, '2026-04-06 15:36:07');
INSERT INTO `operation_log` VALUES (146, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-04-06 15:36:07');
INSERT INTO `operation_log` VALUES (147, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 11, 1, NULL, '2026-04-06 15:45:19');
INSERT INTO `operation_log` VALUES (148, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-06 15:45:21');
INSERT INTO `operation_log` VALUES (149, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-04-06 15:45:22');
INSERT INTO `operation_log` VALUES (150, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-06 15:45:22');
INSERT INTO `operation_log` VALUES (151, NULL, NULL, 'FileUploadController', 'uploadImage', 'FileUploadController.uploadImage', '/api/v1/file/upload/image', 'POST', 'org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@266dbc91', NULL, '0:0:0:0:0:0:0:1', 291, 1, NULL, '2026-04-06 15:45:29');
INSERT INTO `operation_log` VALUES (152, NULL, NULL, 'FileUploadController', 'uploadImage', 'FileUploadController.uploadImage', '/api/v1/file/upload/image', 'POST', 'org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@18717f5e', NULL, '0:0:0:0:0:0:0:1', 488, 1, NULL, '2026-04-06 15:45:37');
INSERT INTO `operation_log` VALUES (153, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 2, 1, NULL, '2026-04-06 15:45:59');
INSERT INTO `operation_log` VALUES (154, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-06 15:46:01');
INSERT INTO `operation_log` VALUES (155, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-06 15:46:01');
INSERT INTO `operation_log` VALUES (156, NULL, NULL, 'FileUploadController', 'uploadImage', 'FileUploadController.uploadImage', '/api/v1/file/upload/image', 'POST', 'org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@60eb924c', NULL, '0:0:0:0:0:0:0:1', 469, 1, NULL, '2026-04-06 15:46:06');
INSERT INTO `operation_log` VALUES (157, NULL, NULL, 'FileUploadController', 'uploadImage', 'FileUploadController.uploadImage', '/api/v1/file/upload/image', 'POST', 'org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@16d8a79d', NULL, '0:0:0:0:0:0:0:1', 558, 1, NULL, '2026-04-06 15:47:08');
INSERT INTO `operation_log` VALUES (158, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-06 15:47:11');
INSERT INTO `operation_log` VALUES (159, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 14, 1, NULL, '2026-04-06 15:47:12');
INSERT INTO `operation_log` VALUES (160, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-06 15:47:12');
INSERT INTO `operation_log` VALUES (161, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-06 15:48:54');
INSERT INTO `operation_log` VALUES (162, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 15:48:54');
INSERT INTO `operation_log` VALUES (163, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-06 15:48:54');
INSERT INTO `operation_log` VALUES (164, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-06 15:48:55');
INSERT INTO `operation_log` VALUES (165, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 14, 1, NULL, '2026-04-06 15:49:00');
INSERT INTO `operation_log` VALUES (166, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 15:49:00');
INSERT INTO `operation_log` VALUES (167, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 15:49:01');
INSERT INTO `operation_log` VALUES (168, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 15:49:01');
INSERT INTO `operation_log` VALUES (169, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-06 15:50:14');
INSERT INTO `operation_log` VALUES (170, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-06 15:50:14');
INSERT INTO `operation_log` VALUES (171, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-06 15:50:17');
INSERT INTO `operation_log` VALUES (172, NULL, NULL, 'FileUploadController', 'uploadImage', 'FileUploadController.uploadImage', '/api/v1/file/upload/image', 'POST', 'org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@424ac246', NULL, '0:0:0:0:0:0:0:1', 1406, 1, NULL, '2026-04-06 15:50:22');
INSERT INTO `operation_log` VALUES (173, NULL, NULL, 'UserController', 'updateProfile', 'UserController.updateProfile', '/api/v1/user/profile', 'PUT', 'User(id=null, username=null, phone=null, email=null, password=null, nickname=一朝, avatar=https://mind-heart.oss-cn-beijing.aliyuncs.com/images/5928a0dbcb94436fbba056f3fe26e8d9.jpg, age=18, gender=1, role=null, status=null, guardianPhone=null, createdAt=null, updatedAt=null)', NULL, '0:0:0:0:0:0:0:1', 164, 1, NULL, '2026-04-06 15:50:24');
INSERT INTO `operation_log` VALUES (174, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-06 15:50:49');
INSERT INTO `operation_log` VALUES (175, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 15:50:51');
INSERT INTO `operation_log` VALUES (176, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 2, 1, NULL, '2026-04-06 15:50:51');
INSERT INTO `operation_log` VALUES (177, NULL, NULL, 'NotificationController', 'list', 'NotificationController.list', '/api/v1/notification/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 12, 1, NULL, '2026-04-06 15:50:58');
INSERT INTO `operation_log` VALUES (178, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '127.0.0.1', 191, 1, NULL, '2026-04-06 15:56:14');
INSERT INTO `operation_log` VALUES (179, NULL, NULL, 'NotificationController', 'list', 'NotificationController.list', '/api/v1/notification/list', 'GET', '1, 10', NULL, '127.0.0.1', 196, 1, NULL, '2026-04-06 15:56:14');
INSERT INTO `operation_log` VALUES (180, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '127.0.0.1', 194, 1, NULL, '2026-04-06 15:56:14');
INSERT INTO `operation_log` VALUES (181, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-06 15:56:14');
INSERT INTO `operation_log` VALUES (182, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '127.0.0.1', 4, 1, NULL, '2026-04-06 15:56:14');
INSERT INTO `operation_log` VALUES (183, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-06 15:56:14');
INSERT INTO `operation_log` VALUES (184, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-06 15:56:18');
INSERT INTO `operation_log` VALUES (185, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '127.0.0.1', 5, 1, NULL, '2026-04-06 15:56:18');
INSERT INTO `operation_log` VALUES (186, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-06 15:56:19');
INSERT INTO `operation_log` VALUES (187, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '127.0.0.1', 4, 1, NULL, '2026-04-06 15:56:19');
INSERT INTO `operation_log` VALUES (188, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '127.0.0.1', 3, 1, NULL, '2026-04-06 15:56:22');
INSERT INTO `operation_log` VALUES (189, NULL, NULL, 'FileUploadController', 'uploadImage', 'FileUploadController.uploadImage', '/api/v1/file/upload/image', 'POST', 'org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@d93a8f2', NULL, '127.0.0.1', 1457, 1, NULL, '2026-04-06 15:56:28');
INSERT INTO `operation_log` VALUES (190, NULL, NULL, 'UserController', 'updateProfile', 'UserController.updateProfile', '/api/v1/user/profile', 'PUT', 'User(id=null, username=null, phone=null, email=null, password=null, nickname=一朝, avatar=https://mind-heart.oss-cn-beijing.aliyuncs.com/images/d74a242745a74f1883b9f005af8cb45c.jpg, age=18, gender=1, role=null, status=null, guardianPhone=null, createdAt=null, updatedAt=null)', NULL, '127.0.0.1', 159, 1, NULL, '2026-04-06 15:56:30');
INSERT INTO `operation_log` VALUES (191, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '127.0.0.1', 3, 1, NULL, '2026-04-06 15:56:34');
INSERT INTO `operation_log` VALUES (192, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '127.0.0.1', 5, 1, NULL, '2026-04-06 15:56:36');
INSERT INTO `operation_log` VALUES (193, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-06 15:56:36');
INSERT INTO `operation_log` VALUES (194, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 15:58:08');
INSERT INTO `operation_log` VALUES (195, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-06 15:58:09');
INSERT INTO `operation_log` VALUES (196, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 15:58:11');
INSERT INTO `operation_log` VALUES (197, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-06 15:58:11');
INSERT INTO `operation_log` VALUES (198, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 502, 1, NULL, '2026-04-06 16:00:18');
INSERT INTO `operation_log` VALUES (199, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-06 16:00:18');
INSERT INTO `operation_log` VALUES (200, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-04-06 16:00:18');
INSERT INTO `operation_log` VALUES (201, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 14, 1, NULL, '2026-04-06 16:00:19');
INSERT INTO `operation_log` VALUES (202, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 16:00:20');
INSERT INTO `operation_log` VALUES (203, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-06 16:02:01');
INSERT INTO `operation_log` VALUES (204, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 16:02:02');
INSERT INTO `operation_log` VALUES (205, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-04-06 16:02:03');
INSERT INTO `operation_log` VALUES (206, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 242, 1, NULL, '2026-04-06 16:04:51');
INSERT INTO `operation_log` VALUES (207, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '127.0.0.1', 8, 1, NULL, '2026-04-06 16:04:51');
INSERT INTO `operation_log` VALUES (208, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '127.0.0.1', 9, 1, NULL, '2026-04-06 16:04:56');
INSERT INTO `operation_log` VALUES (209, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '127.0.0.1', 6, 1, NULL, '2026-04-06 16:04:58');
INSERT INTO `operation_log` VALUES (210, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-04-06 16:05:22');
INSERT INTO `operation_log` VALUES (211, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 13, 1, NULL, '2026-04-06 16:05:24');
INSERT INTO `operation_log` VALUES (212, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 16:05:24');
INSERT INTO `operation_log` VALUES (213, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 16:05:26');
INSERT INTO `operation_log` VALUES (214, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 16:05:27');
INSERT INTO `operation_log` VALUES (215, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-06 16:05:28');
INSERT INTO `operation_log` VALUES (216, NULL, NULL, 'AdminAuthController', 'login', 'AdminAuthController.login', '/admin/v1/auth/login', 'POST', '{username=admin, password=123456}', NULL, '0:0:0:0:0:0:0:1', 339, 1, NULL, '2026-04-06 16:08:55');
INSERT INTO `operation_log` VALUES (217, NULL, NULL, 'AdminStatsController', 'getOverview', 'AdminStatsController.getOverview', '/admin/v1/stats/overview', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 21, 1, NULL, '2026-04-06 16:08:56');
INSERT INTO `operation_log` VALUES (218, NULL, NULL, 'AdminStatsController', 'getUserStats', 'AdminStatsController.getUserStats', '/admin/v1/stats/users', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 60, 1, NULL, '2026-04-06 16:08:56');
INSERT INTO `operation_log` VALUES (219, NULL, NULL, 'AdminStatsController', 'getOverview', 'AdminStatsController.getOverview', '/admin/v1/stats/overview', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 11, 1, NULL, '2026-04-06 16:08:56');
INSERT INTO `operation_log` VALUES (220, NULL, NULL, 'AdminStatsController', 'getUserStats', 'AdminStatsController.getUserStats', '/admin/v1/stats/users', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-06 16:08:56');
INSERT INTO `operation_log` VALUES (221, NULL, NULL, 'AdminUserController', 'list', 'AdminUserController.list', '/admin/v1/user/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 82, 1, NULL, '2026-04-06 16:09:01');
INSERT INTO `operation_log` VALUES (222, NULL, NULL, 'AdminUserController', 'list', 'AdminUserController.list', '/admin/v1/user/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-04-06 16:09:02');
INSERT INTO `operation_log` VALUES (223, NULL, NULL, 'AdminKnowledgeController', 'list', 'AdminKnowledgeController.list', '/admin/v1/knowledge/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 709, 0, '\r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\r\n### The error may exist in com/mindrealm/common/mapper/KnowledgeMapper.java (best guess)\r\n### The error may involve defaultParameterMap\r\n### The error occurred while setting parameters\r\n### SQL: SELECT  id,title,content,category,source,tags,summary,created_at,updated_at,status  FROM knowledge_base           ORDER BY created_at DESC LIMIT ?\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\n; bad SQL grammar []', '2026-04-06 16:09:06');
INSERT INTO `operation_log` VALUES (224, NULL, NULL, 'AdminKnowledgeController', 'list', 'AdminKnowledgeController.list', '/admin/v1/knowledge/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 0, '\r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\r\n### The error may exist in com/mindrealm/common/mapper/KnowledgeMapper.java (best guess)\r\n### The error may involve defaultParameterMap\r\n### The error occurred while setting parameters\r\n### SQL: SELECT  id,title,content,category,source,tags,summary,created_at,updated_at,status  FROM knowledge_base           ORDER BY created_at DESC LIMIT ?\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\n; bad SQL grammar []', '2026-04-06 16:09:07');
INSERT INTO `operation_log` VALUES (225, NULL, NULL, 'AdminWarningController', 'getHighRisk', 'AdminWarningController.getHighRisk', '/admin/v1/warning/high-risk', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 17, 1, NULL, '2026-04-06 16:09:07');
INSERT INTO `operation_log` VALUES (226, NULL, NULL, 'AdminWarningController', 'list', 'AdminWarningController.list', '/admin/v1/warning/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 24, 1, NULL, '2026-04-06 16:09:07');
INSERT INTO `operation_log` VALUES (227, NULL, NULL, 'AdminWarningController', 'getHighRisk', 'AdminWarningController.getHighRisk', '/admin/v1/warning/high-risk', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-06 16:09:07');
INSERT INTO `operation_log` VALUES (228, NULL, NULL, 'AdminWarningController', 'list', 'AdminWarningController.list', '/admin/v1/warning/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 16:09:07');
INSERT INTO `operation_log` VALUES (229, NULL, NULL, 'AdminKnowledgeController', 'list', 'AdminKnowledgeController.list', '/admin/v1/knowledge/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 10, 0, '\r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\r\n### The error may exist in com/mindrealm/common/mapper/KnowledgeMapper.java (best guess)\r\n### The error may involve defaultParameterMap\r\n### The error occurred while setting parameters\r\n### SQL: SELECT  id,title,content,category,source,tags,summary,created_at,updated_at,status  FROM knowledge_base           ORDER BY created_at DESC LIMIT ?\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\n; bad SQL grammar []', '2026-04-06 16:09:08');
INSERT INTO `operation_log` VALUES (230, NULL, NULL, 'AdminKnowledgeController', 'list', 'AdminKnowledgeController.list', '/admin/v1/knowledge/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 5, 0, '\r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\r\n### The error may exist in com/mindrealm/common/mapper/KnowledgeMapper.java (best guess)\r\n### The error may involve defaultParameterMap\r\n### The error occurred while setting parameters\r\n### SQL: SELECT  id,title,content,category,source,tags,summary,created_at,updated_at,status  FROM knowledge_base           ORDER BY created_at DESC LIMIT ?\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\n; bad SQL grammar []', '2026-04-06 16:09:08');
INSERT INTO `operation_log` VALUES (231, NULL, NULL, 'AdminWarningController', 'getHighRisk', 'AdminWarningController.getHighRisk', '/admin/v1/warning/high-risk', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-06 16:09:15');
INSERT INTO `operation_log` VALUES (232, NULL, NULL, 'AdminWarningController', 'list', 'AdminWarningController.list', '/admin/v1/warning/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 16:09:15');
INSERT INTO `operation_log` VALUES (233, NULL, NULL, 'AdminWarningController', 'getHighRisk', 'AdminWarningController.getHighRisk', '/admin/v1/warning/high-risk', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-06 16:09:16');
INSERT INTO `operation_log` VALUES (234, NULL, NULL, 'AdminWarningController', 'list', 'AdminWarningController.list', '/admin/v1/warning/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-06 16:09:16');
INSERT INTO `operation_log` VALUES (235, NULL, NULL, 'AdminWarningController', 'getHighRisk', 'AdminWarningController.getHighRisk', '/admin/v1/warning/high-risk', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-06 16:09:20');
INSERT INTO `operation_log` VALUES (236, NULL, NULL, 'AdminWarningController', 'list', 'AdminWarningController.list', '/admin/v1/warning/list', 'GET', '2, 10', NULL, '0:0:0:0:0:0:0:1', 73, 1, NULL, '2026-04-06 16:09:20');
INSERT INTO `operation_log` VALUES (237, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=一朝, email=null, password=123456, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 2233, 1, NULL, '2026-04-07 19:13:56');
INSERT INTO `operation_log` VALUES (238, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 282, 1, NULL, '2026-04-07 19:13:58');
INSERT INTO `operation_log` VALUES (239, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 11, 1, NULL, '2026-04-07 19:13:58');
INSERT INTO `operation_log` VALUES (240, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜\n, listener, {}', NULL, '0:0:0:0:0:0:0:1', 100, 1, NULL, '2026-04-07 19:14:07');
INSERT INTO `operation_log` VALUES (241, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-07 19:14:32');
INSERT INTO `operation_log` VALUES (242, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-07 19:14:32');
INSERT INTO `operation_log` VALUES (243, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜\n, listener, {}', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-07 19:14:38');
INSERT INTO `operation_log` VALUES (244, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-07 19:15:11');
INSERT INTO `operation_log` VALUES (245, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-07 19:15:12');
INSERT INTO `operation_log` VALUES (246, NULL, NULL, 'DiaryController', 'create', 'DiaryController.create', '/api/v1/diary/create', 'POST', 'Diary(id=null, userId=null, content=我擦好难受, mediaUrls=[\"blob:http://localhost:5173/5dcef200-c11a-44c6-9294-fa0d8186f3c8\"], emotionTags=[\"学习\"], emotionScore=null, emotionCategory=sad, aiAnalysis=null, createdAt=null)', NULL, '0:0:0:0:0:0:0:1', 152, 1, NULL, '2026-04-07 19:15:29');
INSERT INTO `operation_log` VALUES (247, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 13, 1, NULL, '2026-04-07 19:15:32');
INSERT INTO `operation_log` VALUES (248, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-07 19:15:34');
INSERT INTO `operation_log` VALUES (249, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-07 19:15:34');
INSERT INTO `operation_log` VALUES (250, NULL, NULL, 'DiaryController', 'getById', 'DiaryController.getById', '/api/v1/diary/73', 'GET', '73', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-07 19:15:35');
INSERT INTO `operation_log` VALUES (251, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-07 19:15:40');
INSERT INTO `operation_log` VALUES (252, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-07 19:15:40');
INSERT INTO `operation_log` VALUES (253, NULL, NULL, 'DiaryController', 'create', 'DiaryController.create', '/api/v1/diary/create', 'POST', 'Diary(id=null, userId=null, content=我不活了, mediaUrls=[\"blob:http://localhost:5173/befe22f4-d204-4cd4-86ac-cd2ebe2c3575\"], emotionTags=[], emotionScore=null, emotionCategory=fear, aiAnalysis=null, createdAt=null)', NULL, '0:0:0:0:0:0:0:1', 120, 1, NULL, '2026-04-07 19:15:55');
INSERT INTO `operation_log` VALUES (254, NULL, NULL, 'DiaryController', 'getEmotionReport', 'DiaryController.getEmotionReport', '/api/v1/diary/report', 'GET', '7', NULL, '127.0.0.1', 222, 1, NULL, '2026-04-07 19:20:42');
INSERT INTO `operation_log` VALUES (255, NULL, NULL, 'DiaryController', 'getEmotionReport', 'DiaryController.getEmotionReport', '/api/v1/diary/report', 'GET', '7', NULL, '127.0.0.1', 222, 1, NULL, '2026-04-07 19:20:42');
INSERT INTO `operation_log` VALUES (256, NULL, NULL, 'DiaryController', 'getEmotionReport', 'DiaryController.getEmotionReport', '/api/v1/diary/report', 'GET', '7', NULL, '127.0.0.1', 5, 1, NULL, '2026-04-07 19:20:42');
INSERT INTO `operation_log` VALUES (257, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '127.0.0.1', 121, 1, NULL, '2026-04-07 19:20:46');
INSERT INTO `operation_log` VALUES (258, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 11, 1, NULL, '2026-04-07 19:22:17');
INSERT INTO `operation_log` VALUES (259, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 13, 1, NULL, '2026-04-07 19:22:17');
INSERT INTO `operation_log` VALUES (260, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-07 19:22:18');
INSERT INTO `operation_log` VALUES (261, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 28, 1, NULL, '2026-04-07 19:22:19');
INSERT INTO `operation_log` VALUES (262, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 10, 1, NULL, '2026-04-07 19:22:20');
INSERT INTO `operation_log` VALUES (263, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-07 19:22:28');
INSERT INTO `operation_log` VALUES (264, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-07 19:22:28');
INSERT INTO `operation_log` VALUES (265, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 21, 1, NULL, '2026-04-07 19:31:24');
INSERT INTO `operation_log` VALUES (266, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-07 19:31:25');
INSERT INTO `operation_log` VALUES (267, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-07 19:31:25');
INSERT INTO `operation_log` VALUES (268, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 10, 1, NULL, '2026-04-07 19:31:28');
INSERT INTO `operation_log` VALUES (269, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-07 19:31:29');
INSERT INTO `operation_log` VALUES (270, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-07 19:31:30');
INSERT INTO `operation_log` VALUES (271, NULL, NULL, 'DiaryController', 'getById', 'DiaryController.getById', '/api/v1/diary/74', 'GET', '74', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-07 19:31:30');
INSERT INTO `operation_log` VALUES (272, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-07 19:31:35');
INSERT INTO `operation_log` VALUES (273, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜\n, listener, {}', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-07 19:31:42');
INSERT INTO `operation_log` VALUES (274, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=一朝, email=null, password=123456, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 628, 1, NULL, '2026-04-07 19:32:23');
INSERT INTO `operation_log` VALUES (275, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-07 19:32:25');
INSERT INTO `operation_log` VALUES (276, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 11, 1, NULL, '2026-04-07 19:32:25');
INSERT INTO `operation_log` VALUES (277, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-07 19:32:26');
INSERT INTO `operation_log` VALUES (278, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜玩玩, listener, {}', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-07 19:32:33');
INSERT INTO `operation_log` VALUES (279, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-07 19:33:05');
INSERT INTO `operation_log` VALUES (280, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-07 19:33:06');
INSERT INTO `operation_log` VALUES (281, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-07 19:33:07');
INSERT INTO `operation_log` VALUES (282, NULL, NULL, 'AuthController', 'logout', 'AuthController.logout', '/api/v1/auth/logout', 'POST', 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoxLCJ1c2VySWQiOjMyLCJ1c2VybmFtZSI6IuS4gOacnSIsInN1YiI6IuS4gOacnSIsImlhdCI6MTc3NTU2MTU0MiwiZXhwIjoxNzc1NjQ3OTQyfQ.H1NZyFr2ZAG_7CfQJl8plkd4ec0i0oxRFydAlC45XpU', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-07 19:33:11');
INSERT INTO `operation_log` VALUES (283, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=一朝, email=null, password=123456, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 121, 1, NULL, '2026-04-07 19:33:28');
INSERT INTO `operation_log` VALUES (284, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-07 19:33:31');
INSERT INTO `operation_log` VALUES (285, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜, listener, {}', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-07 19:33:40');
INSERT INTO `operation_log` VALUES (286, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜, listener, {}', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-07 19:35:44');
INSERT INTO `operation_log` VALUES (287, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜, listener, {}', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-07 19:36:48');
INSERT INTO `operation_log` VALUES (288, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜, listener, {}', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-07 19:37:11');
INSERT INTO `operation_log` VALUES (289, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜, listener, {}', NULL, '0:0:0:0:0:0:0:1', 2, 1, NULL, '2026-04-07 19:37:23');
INSERT INTO `operation_log` VALUES (290, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜, listener, {}', NULL, '0:0:0:0:0:0:0:1', 2, 1, NULL, '2026-04-07 19:40:32');
INSERT INTO `operation_log` VALUES (291, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-07 19:40:38');
INSERT INTO `operation_log` VALUES (292, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-07 19:40:38');
INSERT INTO `operation_log` VALUES (293, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', 'w呜呜呜, listener, {}', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-07 19:40:44');
INSERT INTO `operation_log` VALUES (294, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', 'www, listener, {}', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-07 19:42:49');
INSERT INTO `operation_log` VALUES (295, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-07 19:42:57');
INSERT INTO `operation_log` VALUES (296, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-07 19:42:57');
INSERT INTO `operation_log` VALUES (297, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '我一直在哭\n, listener, {}', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-07 19:43:04');
INSERT INTO `operation_log` VALUES (298, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-07 19:46:27');
INSERT INTO `operation_log` VALUES (299, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜, listener, {}', NULL, '0:0:0:0:0:0:0:1', 1, 1, NULL, '2026-04-07 19:46:30');
INSERT INTO `operation_log` VALUES (300, NULL, NULL, 'AuthController', 'login', 'AuthController.login', '/api/v1/auth/login', 'POST', 'LoginRequest(username=一朝, email=null, password=123456, loginType=null)', NULL, '0:0:0:0:0:0:0:1', 143, 1, NULL, '2026-04-07 19:50:38');
INSERT INTO `operation_log` VALUES (301, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-07 19:50:40');
INSERT INTO `operation_log` VALUES (302, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-07 19:50:40');
INSERT INTO `operation_log` VALUES (303, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜\n, listener, {}', NULL, '0:0:0:0:0:0:0:1', 2, 1, NULL, '2026-04-07 19:50:46');
INSERT INTO `operation_log` VALUES (304, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜, listener, {}', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-07 19:51:00');
INSERT INTO `operation_log` VALUES (305, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 1561, 1, NULL, '2026-04-07 20:09:18');
INSERT INTO `operation_log` VALUES (306, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 12, 1, NULL, '2026-04-07 20:09:18');
INSERT INTO `operation_log` VALUES (307, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜, listener, {}', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-07 20:09:22');
INSERT INTO `operation_log` VALUES (308, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 10, 1, NULL, '2026-04-07 20:10:34');
INSERT INTO `operation_log` VALUES (309, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 25, 1, NULL, '2026-04-07 20:10:34');
INSERT INTO `operation_log` VALUES (310, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', 'hello啊, listener, {}', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-07 20:12:29');
INSERT INTO `operation_log` VALUES (311, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '为什么, listener, {}', NULL, '0:0:0:0:0:0:0:1', 1, 1, NULL, '2026-04-07 20:12:42');
INSERT INTO `operation_log` VALUES (312, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 112, 1, NULL, '2026-04-07 20:13:01');
INSERT INTO `operation_log` VALUES (313, NULL, NULL, 'DiaryController', 'getById', 'DiaryController.getById', '/api/v1/diary/74', 'GET', '74', NULL, '0:0:0:0:0:0:0:1', 2, 1, NULL, '2026-04-07 20:13:03');
INSERT INTO `operation_log` VALUES (314, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '啊啊, listener, {}', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-07 20:13:56');
INSERT INTO `operation_log` VALUES (315, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '我一直在哭, listener, {}', NULL, '127.0.0.1', 0, 1, NULL, '2026-04-07 20:14:07');
INSERT INTO `operation_log` VALUES (316, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '啊, listener, {}', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-07 20:15:04');
INSERT INTO `operation_log` VALUES (317, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 106, 1, NULL, '2026-04-07 20:17:15');
INSERT INTO `operation_log` VALUES (318, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 10, 1, NULL, '2026-04-07 20:17:15');
INSERT INTO `operation_log` VALUES (319, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 19, 1, NULL, '2026-04-07 20:17:17');
INSERT INTO `operation_log` VALUES (320, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜, listener, {}', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-07 20:17:19');
INSERT INTO `operation_log` VALUES (321, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜, listener, {}', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-07 20:17:26');
INSERT INTO `operation_log` VALUES (322, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-04-07 20:18:53');
INSERT INTO `operation_log` VALUES (323, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 21, 1, NULL, '2026-04-07 20:18:53');
INSERT INTO `operation_log` VALUES (324, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '呜呜呜, listener, {}', NULL, '0:0:0:0:0:0:0:1', 13, 1, NULL, '2026-04-07 20:18:59');
INSERT INTO `operation_log` VALUES (325, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 1055, 1, NULL, '2026-04-08 08:20:38');
INSERT INTO `operation_log` VALUES (326, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 18, 1, NULL, '2026-04-08 08:20:39');
INSERT INTO `operation_log` VALUES (327, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 14, 1, NULL, '2026-04-08 08:20:39');
INSERT INTO `operation_log` VALUES (328, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 10, 1, NULL, '2026-04-08 08:20:39');
INSERT INTO `operation_log` VALUES (329, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-04-08 08:20:41');
INSERT INTO `operation_log` VALUES (330, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '我的室友万新喜欢狗叫\n, analyst, {}', NULL, '0:0:0:0:0:0:0:1', 2, 1, NULL, '2026-04-08 08:21:01');
INSERT INTO `operation_log` VALUES (331, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '我的室友万新喜欢狗叫, analyst, {}', NULL, '0:0:0:0:0:0:0:1', 0, 1, NULL, '2026-04-08 08:21:28');
INSERT INTO `operation_log` VALUES (332, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '是的是的, analyst, {}', NULL, '0:0:0:0:0:0:0:1', 1, 1, NULL, '2026-04-08 08:21:40');
INSERT INTO `operation_log` VALUES (333, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '我的室友万新喜欢狗叫, healer, {}', NULL, '0:0:0:0:0:0:0:1', 0, 1, NULL, '2026-04-08 08:22:00');
INSERT INTO `operation_log` VALUES (334, NULL, NULL, 'ChatController', 'stream', 'ChatController.stream', '/api/v1/chat/stream', 'POST', '他还是在狗叫, healer, {}', NULL, '0:0:0:0:0:0:0:1', 2, 1, NULL, '2026-04-08 08:22:11');
INSERT INTO `operation_log` VALUES (335, NULL, NULL, 'UserController', 'getProfile', 'UserController.getProfile', '/api/v1/user/profile', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 22, 1, NULL, '2026-04-08 08:22:20');
INSERT INTO `operation_log` VALUES (336, NULL, NULL, 'DiaryController', 'getEmotionReport', 'DiaryController.getEmotionReport', '/api/v1/diary/report', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-08 08:22:23');
INSERT INTO `operation_log` VALUES (337, NULL, NULL, 'DiaryController', 'getEmotionReport', 'DiaryController.getEmotionReport', '/api/v1/diary/report', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-08 08:22:30');
INSERT INTO `operation_log` VALUES (338, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 11, 1, NULL, '2026-04-08 08:22:31');
INSERT INTO `operation_log` VALUES (339, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-08 08:22:32');
INSERT INTO `operation_log` VALUES (340, NULL, NULL, 'DiaryController', 'getEmotionReport', 'DiaryController.getEmotionReport', '/api/v1/diary/report', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-08 08:22:45');
INSERT INTO `operation_log` VALUES (341, NULL, NULL, 'DiaryController', 'getEmotionReport', 'DiaryController.getEmotionReport', '/api/v1/diary/report', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-08 08:22:46');
INSERT INTO `operation_log` VALUES (342, NULL, NULL, 'AdminAuthController', 'login', 'AdminAuthController.login', '/admin/v1/auth/login', 'POST', '{username=admin, password=123456}', NULL, '0:0:0:0:0:0:0:1', 326, 1, NULL, '2026-04-08 08:24:40');
INSERT INTO `operation_log` VALUES (343, NULL, NULL, 'AdminStatsController', 'getUserStats', 'AdminStatsController.getUserStats', '/admin/v1/stats/users', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 79, 1, NULL, '2026-04-08 08:24:40');
INSERT INTO `operation_log` VALUES (344, NULL, NULL, 'AdminStatsController', 'getOverview', 'AdminStatsController.getOverview', '/admin/v1/stats/overview', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 174, 1, NULL, '2026-04-08 08:24:40');
INSERT INTO `operation_log` VALUES (345, NULL, NULL, 'AdminStatsController', 'getUserStats', 'AdminStatsController.getUserStats', '/admin/v1/stats/users', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-04-08 08:24:40');
INSERT INTO `operation_log` VALUES (346, NULL, NULL, 'AdminStatsController', 'getOverview', 'AdminStatsController.getOverview', '/admin/v1/stats/overview', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-08 08:24:40');
INSERT INTO `operation_log` VALUES (347, NULL, NULL, 'AdminUserController', 'list', 'AdminUserController.list', '/admin/v1/user/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 49, 1, NULL, '2026-04-08 08:24:42');
INSERT INTO `operation_log` VALUES (348, NULL, NULL, 'AdminUserController', 'list', 'AdminUserController.list', '/admin/v1/user/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 11, 1, NULL, '2026-04-08 08:24:42');
INSERT INTO `operation_log` VALUES (349, NULL, NULL, 'AdminStatsController', 'getUserStats', 'AdminStatsController.getUserStats', '/admin/v1/stats/users', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-04-08 08:24:43');
INSERT INTO `operation_log` VALUES (350, NULL, NULL, 'AdminStatsController', 'getOverview', 'AdminStatsController.getOverview', '/admin/v1/stats/overview', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-04-08 08:24:43');
INSERT INTO `operation_log` VALUES (351, NULL, NULL, 'AdminStatsController', 'getUserStats', 'AdminStatsController.getUserStats', '/admin/v1/stats/users', 'GET', '7', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-08 08:24:43');
INSERT INTO `operation_log` VALUES (352, NULL, NULL, 'AdminStatsController', 'getOverview', 'AdminStatsController.getOverview', '/admin/v1/stats/overview', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-04-08 08:24:44');
INSERT INTO `operation_log` VALUES (353, NULL, NULL, 'AdminKnowledgeController', 'list', 'AdminKnowledgeController.list', '/admin/v1/knowledge/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 910, 0, '\r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\r\n### The error may exist in com/mindrealm/common/mapper/KnowledgeMapper.java (best guess)\r\n### The error may involve defaultParameterMap\r\n### The error occurred while setting parameters\r\n### SQL: SELECT  id,title,content,category,source,tags,summary,created_at,updated_at,status,view_count  FROM knowledge_base           ORDER BY created_at DESC LIMIT ?\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\n; bad SQL grammar []', '2026-04-08 08:24:49');
INSERT INTO `operation_log` VALUES (354, NULL, NULL, 'AdminKnowledgeController', 'list', 'AdminKnowledgeController.list', '/admin/v1/knowledge/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 4, 0, '\r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\r\n### The error may exist in com/mindrealm/common/mapper/KnowledgeMapper.java (best guess)\r\n### The error may involve defaultParameterMap\r\n### The error occurred while setting parameters\r\n### SQL: SELECT  id,title,content,category,source,tags,summary,created_at,updated_at,status,view_count  FROM knowledge_base           ORDER BY created_at DESC LIMIT ?\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\n; bad SQL grammar []', '2026-04-08 08:24:49');
INSERT INTO `operation_log` VALUES (355, NULL, NULL, 'AdminWarningController', 'getHighRisk', 'AdminWarningController.getHighRisk', '/admin/v1/warning/high-risk', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 29, 1, NULL, '2026-04-08 08:24:52');
INSERT INTO `operation_log` VALUES (356, NULL, NULL, 'AdminWarningController', 'list', 'AdminWarningController.list', '/admin/v1/warning/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 46, 1, NULL, '2026-04-08 08:24:52');
INSERT INTO `operation_log` VALUES (357, NULL, NULL, 'AdminWarningController', 'getHighRisk', 'AdminWarningController.getHighRisk', '/admin/v1/warning/high-risk', 'GET', '', NULL, '0:0:0:0:0:0:0:1', 7, 1, NULL, '2026-04-08 08:24:52');
INSERT INTO `operation_log` VALUES (358, NULL, NULL, 'AdminWarningController', 'list', 'AdminWarningController.list', '/admin/v1/warning/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-04-08 08:24:52');
INSERT INTO `operation_log` VALUES (359, NULL, NULL, 'AdminKnowledgeController', 'list', 'AdminKnowledgeController.list', '/admin/v1/knowledge/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 9, 0, '\r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\r\n### The error may exist in com/mindrealm/common/mapper/KnowledgeMapper.java (best guess)\r\n### The error may involve defaultParameterMap\r\n### The error occurred while setting parameters\r\n### SQL: SELECT  id,title,content,category,source,tags,summary,created_at,updated_at,status,view_count  FROM knowledge_base           ORDER BY created_at DESC LIMIT ?\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\n; bad SQL grammar []', '2026-04-08 08:24:53');
INSERT INTO `operation_log` VALUES (360, NULL, NULL, 'AdminKnowledgeController', 'list', 'AdminKnowledgeController.list', '/admin/v1/knowledge/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 7, 0, '\r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\r\n### The error may exist in com/mindrealm/common/mapper/KnowledgeMapper.java (best guess)\r\n### The error may involve defaultParameterMap\r\n### The error occurred while setting parameters\r\n### SQL: SELECT  id,title,content,category,source,tags,summary,created_at,updated_at,status,view_count  FROM knowledge_base           ORDER BY created_at DESC LIMIT ?\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'status\' in \'field list\'\n; bad SQL grammar []', '2026-04-08 08:24:53');
INSERT INTO `operation_log` VALUES (361, NULL, NULL, 'DiaryController', 'list', 'DiaryController.list', '/api/v1/diary/list', 'GET', '1, 10', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-04-08 08:33:04');

-- ----------------------------
-- Table structure for sms_code
-- ----------------------------
DROP TABLE IF EXISTS `sms_code`;
CREATE TABLE `sms_code`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证码',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '验证码表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_code
-- ----------------------------

-- ----------------------------
-- Table structure for user_collect
-- ----------------------------
DROP TABLE IF EXISTS `user_collect`;
CREATE TABLE `user_collect`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收藏类型(diary/knowledge/knowledge)',
  `target_id` bigint NOT NULL COMMENT '收藏目标ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_type`(`user_id` ASC, `target_type` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_collect_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_collect
-- ----------------------------
INSERT INTO `user_collect` VALUES (1, 1, 'knowledge', 1, '2026-04-02 14:53:03');
INSERT INTO `user_collect` VALUES (2, 1, 'knowledge', 2, '2026-04-01 14:53:03');
INSERT INTO `user_collect` VALUES (3, 2, 'knowledge', 3, '2026-03-31 14:53:03');
INSERT INTO `user_collect` VALUES (4, 3, 'knowledge', 1, '2026-04-02 14:53:03');
INSERT INTO `user_collect` VALUES (5, 1, 'knowledge', 1, '2026-04-04 19:58:16');
INSERT INTO `user_collect` VALUES (6, 1, 'knowledge', 2, '2026-04-03 19:58:16');
INSERT INTO `user_collect` VALUES (7, 2, 'knowledge', 3, '2026-04-02 19:58:16');
INSERT INTO `user_collect` VALUES (8, 3, 'knowledge', 1, '2026-04-04 19:58:16');
INSERT INTO `user_collect` VALUES (9, 1, 'knowledge', 1, '2026-04-04 22:34:46');
INSERT INTO `user_collect` VALUES (10, 1, 'knowledge', 2, '2026-04-03 22:34:46');
INSERT INTO `user_collect` VALUES (11, 2, 'knowledge', 3, '2026-04-02 22:34:46');
INSERT INTO `user_collect` VALUES (12, 3, 'knowledge', 1, '2026-04-04 22:34:46');
INSERT INTO `user_collect` VALUES (13, 1, 'knowledge', 1, '2026-04-04 22:39:05');
INSERT INTO `user_collect` VALUES (14, 1, 'knowledge', 2, '2026-04-03 22:39:05');
INSERT INTO `user_collect` VALUES (15, 2, 'knowledge', 3, '2026-04-02 22:39:05');
INSERT INTO `user_collect` VALUES (16, 3, 'knowledge', 1, '2026-04-04 22:39:05');

-- ----------------------------
-- Table structure for user_feedback
-- ----------------------------
DROP TABLE IF EXISTS `user_feedback`;
CREATE TABLE `user_feedback`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '反馈类型 (bug/suggestion/complaint)',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '反馈标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '反馈内容',
  `contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 (0待处理1处理中2已解决)',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `handler_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `handled_at` datetime NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `handler_id`(`handler_id` ASC) USING BTREE,
  CONSTRAINT `user_feedback_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_feedback_ibfk_2` FOREIGN KEY (`handler_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_feedback
-- ----------------------------
INSERT INTO `user_feedback` VALUES (1, 1, 'suggestion', '希望增加音乐播放功能', '希望能添加一些舒缓的背景音乐，帮助放松心情', NULL, 2, NULL, NULL, '2026-04-02 19:58:23', NULL);
INSERT INTO `user_feedback` VALUES (2, 2, 'bug', '日记保存失败', '有时候日记写到一半会丢失，希望能够自动保存', NULL, 0, NULL, NULL, '2026-04-04 19:58:23', NULL);
INSERT INTO `user_feedback` VALUES (3, 1, 'suggestion', '希望增加音乐播放功能', '希望能添加一些舒缓的背景音乐，帮助放松心情', NULL, 2, NULL, NULL, '2026-04-02 22:34:47', NULL);
INSERT INTO `user_feedback` VALUES (4, 2, 'bug', '日记保存失败', '有时候日记写到一半会丢失，希望能够自动保存', NULL, 0, NULL, NULL, '2026-04-04 22:34:47', NULL);
INSERT INTO `user_feedback` VALUES (5, 1, 'suggestion', '希望增加音乐播放功能', '希望能添加一些舒缓的背景音乐，帮助放松心情', NULL, 2, NULL, NULL, '2026-04-02 22:39:06', NULL);
INSERT INTO `user_feedback` VALUES (6, 2, 'bug', '日记保存失败', '有时候日记写到一半会丢失，希望能够自动保存', NULL, 0, NULL, NULL, '2026-04-04 22:39:06', NULL);

-- ----------------------------
-- Table structure for user_profile
-- ----------------------------
DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE `user_profile`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `personality_tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '人格标签(JSON)',
  `emotional_pattern` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '情绪模式',
  `stress_triggers` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '压力触发点',
  `last_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '最后分析结果',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_profile_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户画像表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_profile
-- ----------------------------

-- ----------------------------
-- Table structure for user_settings
-- ----------------------------
DROP TABLE IF EXISTS `user_settings`;
CREATE TABLE `user_settings`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `diary_reminder` tinyint NULL DEFAULT 1 COMMENT '日记提醒',
  `ai_push` tinyint NULL DEFAULT 1 COMMENT 'AI推送',
  `weekly_report` tinyint NULL DEFAULT 1 COMMENT '周报推送',
  `warning_notify` tinyint NULL DEFAULT 1 COMMENT '预警通知',
  `reminder_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '20:00' COMMENT '提醒时间',
  `privacy_diary_password` tinyint NULL DEFAULT 0 COMMENT '日记加密',
  `privacy_show_mood` tinyint NULL DEFAULT 1 COMMENT '展示情绪',
  `privacy_anonymous_data` tinyint NULL DEFAULT 0 COMMENT '匿名数据',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_settings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_settings
-- ----------------------------
INSERT INTO `user_settings` VALUES (1, 1, 1, 1, 1, 1, '20:00', 0, 1, 0, '2026-04-03 14:53:02', '2026-04-03 14:53:02');
INSERT INTO `user_settings` VALUES (2, 2, 1, 1, 1, 1, '21:00', 0, 1, 0, '2026-04-03 14:53:02', '2026-04-03 14:53:02');
INSERT INTO `user_settings` VALUES (3, 3, 1, 1, 1, 1, '20:00', 0, 1, 0, '2026-04-03 14:53:02', '2026-04-03 14:53:02');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `gender` tinyint NULL DEFAULT 3 COMMENT '性别(1男2女3未知)',
  `role` tinyint NULL DEFAULT 1 COMMENT '角色(1用户2咨询师3管理员)',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(0禁用1正常)',
  `guardian_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '监护人电话',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'test_user', '13800138000', 'e10adc3949ba59abbe56e057f20f883e', '测试用户', NULL, 18, 1, 1, 1, NULL, '2026-04-03 14:42:03', '2026-04-03 14:42:03', NULL);
INSERT INTO `users` VALUES (2, 'xiaoming', '13800138001', 'e10adc3949ba59abbe56e057f20f883e', '小明', NULL, 16, 1, 1, 1, NULL, '2026-04-03 14:42:03', '2026-04-03 14:42:03', NULL);
INSERT INTO `users` VALUES (3, 'xiaohua', '13800138002', 'e10adc3949ba59abbe56e057f20f883e', '小花', NULL, 17, 2, 1, 1, NULL, '2026-04-03 14:42:03', '2026-04-03 14:42:03', NULL);
INSERT INTO `users` VALUES (4, 'counselor', '13900139000', 'e10adc3949ba59abbe56e057f20f883e', '心理咨询师', NULL, 30, 1, 2, 1, NULL, '2026-04-03 14:42:03', '2026-04-03 14:42:03', NULL);
INSERT INTO `users` VALUES (5, 'admin', '13900139001', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', NULL, 35, 1, 3, 1, NULL, '2026-04-03 14:42:03', '2026-04-03 14:42:03', NULL);
INSERT INTO `users` VALUES (32, '一朝', NULL, 'e10adc3949ba59abbe56e057f20f883e', '一朝', 'https://mind-heart.oss-cn-beijing.aliyuncs.com/images/d74a242745a74f1883b9f005af8cb45c.jpg', 18, 1, 1, 1, NULL, '2026-04-05 19:43:28', '2026-04-06 15:56:30', '2780059846@qq.com');

-- ----------------------------
-- Table structure for warning_record
-- ----------------------------
DROP TABLE IF EXISTS `warning_record`;
CREATE TABLE `warning_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `risk_level` tinyint NULL DEFAULT 1 COMMENT '风险等级(1低2中3高)',
  `trigger_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '触发类型(keyword/emotion/manual)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '触发内容',
  `handled` tinyint NULL DEFAULT 0 COMMENT '是否处理(0否1是)',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `handler_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `handled_at` datetime NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_risk_level`(`risk_level` ASC) USING BTREE,
  INDEX `idx_handled`(`handled` ASC) USING BTREE,
  INDEX `handler_id`(`handler_id` ASC) USING BTREE,
  CONSTRAINT `warning_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `warning_record_ibfk_2` FOREIGN KEY (`handler_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预警记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of warning_record
-- ----------------------------
INSERT INTO `warning_record` VALUES (1, 2, 2, 'keyword', '和父母争吵后表达了消极想法', 0, NULL, NULL, '2026-04-02 14:53:02', NULL);
INSERT INTO `warning_record` VALUES (2, 1, 1, 'emotion', '连续多日记录负面情绪', 0, NULL, NULL, '2026-04-01 14:53:02', NULL);
INSERT INTO `warning_record` VALUES (3, 2, 2, 'keyword', '和父母争吵后表达了消极想法', 0, NULL, NULL, '2026-04-02 15:01:22', NULL);
INSERT INTO `warning_record` VALUES (4, 1, 1, 'emotion', '连续多日记录负面情绪', 0, NULL, NULL, '2026-04-01 15:01:22', NULL);
INSERT INTO `warning_record` VALUES (5, 2, 2, 'keyword', '和父母争吵后表达了消极想法', 0, NULL, NULL, '2026-04-02 15:03:40', NULL);
INSERT INTO `warning_record` VALUES (6, 1, 1, 'emotion', '连续多日记录负面情绪', 0, NULL, NULL, '2026-04-01 15:03:40', NULL);
INSERT INTO `warning_record` VALUES (7, 2, 2, 'keyword', '和父母争吵后表达了消极想法', 0, NULL, NULL, '2026-04-04 18:46:10', NULL);
INSERT INTO `warning_record` VALUES (8, 1, 1, 'emotion', '连续多日记录负面情绪', 0, NULL, NULL, '2026-04-03 18:46:10', NULL);
INSERT INTO `warning_record` VALUES (9, 2, 2, 'keyword', '和父母争吵后表达了消极想法', 0, NULL, NULL, '2026-04-04 19:55:00', NULL);
INSERT INTO `warning_record` VALUES (10, 1, 1, 'emotion', '连续多日记录负面情绪', 0, NULL, NULL, '2026-04-03 19:55:00', NULL);
INSERT INTO `warning_record` VALUES (11, 2, 2, 'keyword', '和父母争吵后表达了消极想法', 0, NULL, NULL, '2026-04-04 19:58:15', NULL);
INSERT INTO `warning_record` VALUES (12, 1, 1, 'emotion', '连续多日记录负面情绪', 0, NULL, NULL, '2026-04-03 19:58:15', NULL);
INSERT INTO `warning_record` VALUES (13, 2, 2, 'keyword', '和父母争吵后表达了消极想法', 0, NULL, NULL, '2026-04-04 22:34:45', NULL);
INSERT INTO `warning_record` VALUES (14, 1, 1, 'emotion', '连续多日记录负面情绪', 0, NULL, NULL, '2026-04-03 22:34:45', NULL);
INSERT INTO `warning_record` VALUES (15, 2, 2, 'keyword', '和父母争吵后表达了消极想法', 0, NULL, NULL, '2026-04-04 22:39:04', NULL);
INSERT INTO `warning_record` VALUES (16, 1, 1, 'emotion', '连续多日记录负面情绪', 0, NULL, NULL, '2026-04-03 22:39:04', NULL);

SET FOREIGN_KEY_CHECKS = 1;
