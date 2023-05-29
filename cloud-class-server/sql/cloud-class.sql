/*
 Navicat Premium Data Transfer

 Source Server         : local_mysql_8.0
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : localhost:3306
 Source Schema         : cloud-class

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 02/05/2023 00:39:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_course
-- ----------------------------
DROP TABLE IF EXISTS `t_course`;
CREATE TABLE `t_course` (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '课程名称',
  `course_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '课程编码',
  `intro` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '课程简介',
  `course_type` tinyint(1) DEFAULT '1' COMMENT '课程类型,CLASS_COURSE(1, "班级课"),PRIVATE_COURSE(2, "私教课"),PUBLIC_COURSE(3, "公开课");',
  `cover` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '课程封面',
  `teacher_id` int DEFAULT NULL COMMENT '外键, 教师id',
  `session` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '开课年度',
  `start_time` datetime DEFAULT NULL COMMENT '课程开始日期',
  `end_time` datetime DEFAULT NULL COMMENT '课程结束日期',
  `join_end_time` datetime DEFAULT NULL COMMENT '加入课程的截止时间',
  `week_arrange` varchar(50) DEFAULT NULL COMMENT '课程每周安排',
  `user_num` int DEFAULT '50' COMMENT '课程上限人数',
  `need_review` tinyint unsigned DEFAULT '0' COMMENT '加入课程是否需要审核, 默认0需要',
  `need_open` tinyint(1) DEFAULT '0' COMMENT '是否公开，默认公开0',
  `status` tinyint(1) DEFAULT '2' COMMENT '状态枚举, 1:未审核, 2:通过, 3:驳回',
  `creator_id` int DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` int DEFAULT NULL COMMENT '修改人id',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '1:已删除，0:正常',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `t_course_ibfk_1` (`teacher_id`),
  CONSTRAINT `t_course_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `t_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


-- ----------------------------
-- Table structure for t_course_file
-- ----------------------------
DROP TABLE IF EXISTS `t_course_file`;
CREATE TABLE `t_course_file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_id` int DEFAULT NULL COMMENT '外键, 课程id',
  `upload_user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `file_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件名',
  `cover` varchar(255) DEFAULT NULL,
  `size` varchar(255) DEFAULT NULL,
  `file_intro` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件简介',
  `upload_time` datetime DEFAULT NULL COMMENT '上传时间',
  `file_link` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件下载地址',
  `creator_id` int DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` int DEFAULT NULL COMMENT '修改人id',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '1:已删除，0:正常',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `course_id` (`course_id`) USING BTREE,
  CONSTRAINT `t_course_file_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


-- ----------------------------
-- Table structure for t_course_job
-- ----------------------------
DROP TABLE IF EXISTS `t_course_job`;
CREATE TABLE `t_course_job` (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_id` int DEFAULT NULL COMMENT '外键, 课程id',
  `job_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '作业名',
  `job_intro` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '作业简介',
  `content` longtext COMMENT '作业详情',
  `ai_review` tinyint(1) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL COMMENT '作业发布日期',
  `end_time` datetime DEFAULT NULL COMMENT '作业结束日期',
  `job_file_link` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '作业附件下载地址',
  `creator_id` int DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` int DEFAULT NULL COMMENT '修改人id',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '1:已删除，0:正常',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `course_id` (`course_id`) USING BTREE,
  CONSTRAINT `t_course_job_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


-- ----------------------------
-- Table structure for t_course_job_commit
-- ----------------------------
DROP TABLE IF EXISTS `t_course_job_commit`;
CREATE TABLE `t_course_job_commit` (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_job_id` int DEFAULT NULL COMMENT '外键, 作业id',
  `student_id` int DEFAULT NULL COMMENT '外键, 学生id',
  `content` longtext COMMENT '提交内容',
  `commit_time` datetime DEFAULT NULL COMMENT '提交日期',
  `commit_file_link` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '提交附件下载地址',
  `summary_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '汇总地址',
  `summary_link` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '汇总链接',
  `ai_check` tinyint(1) DEFAULT NULL COMMENT '是否ai批改',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态枚举, 1:未批改, 2:已批改, 3:驳回',
  `score` int DEFAULT NULL,
  `comment` longtext CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '批改后内容',
  `creator_id` int DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` int DEFAULT NULL COMMENT '修改人id',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '1:已删除，0:正常',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `course_job_id` (`course_job_id`) USING BTREE,
  KEY `student_id` (`student_id`) USING BTREE,
  CONSTRAINT `t_course_job_commit_ibfk_1` FOREIGN KEY (`course_job_id`) REFERENCES `t_course_job` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_course_job_commit_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `t_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


-- ----------------------------
-- Table structure for t_course_playback
-- ----------------------------
DROP TABLE IF EXISTS `t_course_playback`;
CREATE TABLE `t_course_playback` (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_id` int DEFAULT NULL COMMENT '外键, 课程id',
  `upload_user_id` int DEFAULT NULL,
  `playback_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '回放名',
  `cover` varchar(255) DEFAULT NULL,
  `upload_time` datetime DEFAULT NULL COMMENT '发布日期',
  `playback_file_link` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '回放文件地址',
  `creator_id` int DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` int DEFAULT NULL COMMENT '修改人id',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '1:已删除，0:正常',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `course_id` (`course_id`) USING BTREE,
  CONSTRAINT `t_course_playback_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


-- ----------------------------
-- Table structure for t_course_user
-- ----------------------------
DROP TABLE IF EXISTS `t_course_user`;
CREATE TABLE `t_course_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_id` int DEFAULT NULL COMMENT '外键, 课程id',
  `student_id` int DEFAULT NULL COMMENT '外键, 学生id',
  `join_type` tinyint DEFAULT NULL COMMENT '加入类型，1:老师邀请，2:用户申请',
  `selected_time` datetime DEFAULT NULL COMMENT '选课时间',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态枚举, 1:未审核, 2:通过, 3:驳回',
  `creator_id` int DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` int DEFAULT NULL COMMENT '修改人id',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '1:已删除，0:正常',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `course_id` (`course_id`) USING BTREE,
  KEY `student_id` (`student_id`) USING BTREE,
  CONSTRAINT `t_course_user_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_course_user_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `t_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


-- ----------------------------
-- Table structure for t_live
-- ----------------------------
DROP TABLE IF EXISTS `t_live`;
CREATE TABLE `t_live` (
  `id` int NOT NULL AUTO_INCREMENT,
  `room_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '房间号',
  `user_video_publish` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '摄像头publish地址',
  `user_video_play` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '摄像头live地址',
  `screen_video_publish` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '屏幕publish地址',
  `screen_video_play` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '屏幕live地址',
  `course_id` int DEFAULT NULL COMMENT '外键, 课程id',
  `creator_id` int DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` int DEFAULT NULL COMMENT '修改人id',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '1:已删除，0:正常',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `course_id` (`course_id`) USING BTREE,
  CONSTRAINT `t_live_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `msg_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '信息名称',
  `msg_type` tinyint DEFAULT NULL COMMENT '信息类型',
  `msg_read` tinyint(1) DEFAULT '0' COMMENT '信息状态, 是否已读',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `send_id` int DEFAULT NULL COMMENT '外键, 用户id',
  `receive_id` int DEFAULT NULL COMMENT '外键, 用户id',
  `msg_content` longtext COMMENT '信息详情',
  `need_reply` tinyint(1) DEFAULT '0' COMMENT '是否需要回复, 默认不需要',
  `confirm_url` varchar(255) DEFAULT NULL COMMENT '确认地址',
  `refuse_url` varchar(255) DEFAULT NULL COMMENT '取消地址',
  `creator_id` int DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` int DEFAULT NULL COMMENT '修改人id',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '1:已删除，0:正常',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `t_message_ibfk_1` (`send_id`),
  KEY `t_message_ibfk_2` (`receive_id`),
  CONSTRAINT `t_message_ibfk_1` FOREIGN KEY (`send_id`) REFERENCES `t_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_message_ibfk_2` FOREIGN KEY (`receive_id`) REFERENCES `t_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=185 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '密码',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '头像',
  `real_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '真实姓名',
  `age` int DEFAULT NULL COMMENT '年龄',
  `sex` tinyint(1) DEFAULT '1' COMMENT '性别, NO(1,”未知”),MAN(2,”男”),WOMAN(3,”女”);',
  `tel` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '电话',
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '邮箱',
  `user_type` tinyint(1) DEFAULT '3' COMMENT '角色类型, ADMIN(1,”系统管理员"),TEA(2,”教师"),STU(3,”学生");',
  `status` tinyint(1) DEFAULT '1' COMMENT ' 0:停用, 1:正常',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后一次登录时间',
  `creator_id` int DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` int DEFAULT NULL COMMENT '修改人id',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '1:已删除，0:正常',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_unique_num` (`user_name`) USING BTREE COMMENT '账号索引'
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


-- ----------------------------
-- Table structure for t_video
-- ----------------------------
DROP TABLE IF EXISTS `t_video`;
CREATE TABLE `t_video` (
  `id` int NOT NULL AUTO_INCREMENT,
  `record_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `user_id` int DEFAULT NULL COMMENT '外键, 用户id',
  `video_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '视频名',
  `video_intro` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '视频简介',
  `video_file_link` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '视频文件地址',
  `video_cover_link` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '视频封面地址',
  `record_start` datetime DEFAULT NULL COMMENT '录制开始时间',
  `record_end` datetime DEFAULT NULL COMMENT '录制结束时间',
  `open` tinyint(1) DEFAULT NULL COMMENT '是否公开',
  `watch_count` int DEFAULT NULL COMMENT '观看次数',
  `creator_id` int DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` int DEFAULT NULL COMMENT '修改人id',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '1:已删除，0:正常',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  CONSTRAINT `t_video_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
