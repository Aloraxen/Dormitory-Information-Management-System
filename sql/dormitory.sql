-- =============================================================
-- 宿舍信息管理系统（Dormitory Information Management System）
-- 数据库脚本  —  适用于 Navicat 直接执行
-- 数据库名：Dormitory   字符集：utf8mb3（或 utf8mb4）  排序规则：utf8mb3_unicode_ci
-- 适用 MySQL 5.7 / 8.0
--
-- 使用方式：
--   1. 先在 Navicat 中新建数据库 Dormitory（字符集 utf8mb4，排序规则 utf8mb4_unicode_ci）；
--   2. 双击打开该数据库，在查询窗口中执行本文件全部语句；
--   3. 表结构 + 初始演示数据会自动建好；本文件可重复执行（每次执行会先删除全部表，
--      再重建并写入初始数据，即重复执行会重置为初始演示数据）。
--
-- 初始演示账号（密码均为明文示例，已使用 BCrypt 加密存储）：
--   管理员    admin     / admin123      角色：系统管理员
--   宿管员    manager   / manager123    角色：宿舍管理员
--   学生      student01 / student123    角色：学生（王小明）
--   学生      student02 / stu02@123     角色：学生（李思思）
--   学生      student03 / stu03@123     角色：学生（赵宇航）
-- =============================================================

-- 若尚未创建数据库，可先执行下面这行（已创建过则无需执行）
-- CREATE DATABASE IF NOT EXISTS `Dormitory` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `Dormitory`;

SET NAMES utf8mb4;

-- -------------------------------------------------------------
-- 0. 清理旧表（保证脚本可重复执行：先删除全部表，再重建）
--    重复执行本脚本会重置为初始演示数据
-- -------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `t_backup`;
DROP TABLE IF EXISTS `t_apply`;
DROP TABLE IF EXISTS `t_stay`;
DROP TABLE IF EXISTS `t_room`;
DROP TABLE IF EXISTS `t_room_type`;
DROP TABLE IF EXISTS `t_building`;
DROP TABLE IF EXISTS `t_student`;
DROP TABLE IF EXISTS `t_user`;
SET FOREIGN_KEY_CHECKS = 1;

-- -------------------------------------------------------------
-- 1. 用户账户表（登录账号，角色区分权限）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`    VARCHAR(50)  NOT NULL COMMENT '登录账号',
  `password`    VARCHAR(100) NOT NULL COMMENT '登录密码（BCrypt加密）',
  `name`        VARCHAR(50)  NOT NULL COMMENT '姓名',
  `role`        VARCHAR(20)  NOT NULL DEFAULT 'ROLE_STUDENT' COMMENT '角色：ROLE_ADMIN-管理员 / ROLE_MANAGER-宿管员 / ROLE_STUDENT-学生',
  `enabled`     TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用 0-禁用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`)
) ENGINE=InnoDB COMMENT='用户账户表';

-- -------------------------------------------------------------
-- 2. 学生信息表
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_student` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`     BIGINT       NULL COMMENT '关联登录账户（可为空，仅由管理员维护信息的学生）',
  `student_no`  VARCHAR(30)  NOT NULL COMMENT '学号',
  `name`        VARCHAR(50)  NOT NULL COMMENT '姓名',
  `gender`      VARCHAR(4)   DEFAULT '男' COMMENT '性别：男/女',
  `college`     VARCHAR(100) DEFAULT NULL COMMENT '学院',
  `major`       VARCHAR(100) DEFAULT NULL COMMENT '专业',
  `class_name`  VARCHAR(50)  DEFAULT NULL COMMENT '班级',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `email`       VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_no` (`student_no`),
  UNIQUE KEY `uk_student_user` (`user_id`),
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB COMMENT='学生信息表';

-- -------------------------------------------------------------
-- 3. 宿舍楼栋表
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_building` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`        VARCHAR(50) NOT NULL COMMENT '楼栋名称，如：一号公寓',
  `gender_limit` VARCHAR(4) DEFAULT '男' COMMENT '入住性别：男/女',
  `floors`      INT         DEFAULT 6 COMMENT '层数',
  `manager`     VARCHAR(50) DEFAULT NULL COMMENT '负责人',
  `remark`      VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='宿舍楼栋表';

-- -------------------------------------------------------------
-- 4. 宿舍房型表
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_room_type` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`        VARCHAR(50)  NOT NULL COMMENT '房型名称，如：四人间',
  `bed_count`   INT          NOT NULL DEFAULT 4 COMMENT '床位数',
  `price`       DECIMAL(8,2) DEFAULT 0.00 COMMENT '收费标准（元/学期）',
  `remark`      VARCHAR(255) DEFAULT NULL COMMENT '配置说明',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='宿舍房型表';

-- -------------------------------------------------------------
-- 5. 宿舍房间表
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_room` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
  `building_id` BIGINT      NOT NULL COMMENT '所属楼栋',
  `room_type_id` BIGINT     NOT NULL COMMENT '房型',
  `room_no`     VARCHAR(20) NOT NULL COMMENT '房间号，如：101',
  `floor`       INT         DEFAULT 1 COMMENT '所在楼层',
  `status`      VARCHAR(10) DEFAULT '正常' COMMENT '状态：正常/维修中',
  `remark`      VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_building_room` (`building_id`, `room_no`),
  KEY `idx_room_building` (`building_id`),
  CONSTRAINT `fk_room_building` FOREIGN KEY (`building_id`) REFERENCES `t_building` (`id`),
  CONSTRAINT `fk_room_type` FOREIGN KEY (`room_type_id`) REFERENCES `t_room_type` (`id`)
) ENGINE=InnoDB COMMENT='宿舍房间表';

-- -------------------------------------------------------------
-- 6. 住宿安排表（学生入住/退宿记录，含床位）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_stay` (
  `id`             BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id`     BIGINT   NOT NULL COMMENT '学生',
  `room_id`        BIGINT   NOT NULL COMMENT '房间',
  `bed_no`         VARCHAR(10) DEFAULT NULL COMMENT '床位号，如：1号床',
  `check_in_date`  DATE     DEFAULT NULL COMMENT '入住日期',
  `check_out_date` DATE     DEFAULT NULL COMMENT '退宿日期',
  `status`         VARCHAR(10) DEFAULT '在住' COMMENT '状态：在住/已退宿',
  `remark`         VARCHAR(255) DEFAULT NULL COMMENT '备注（调整原因等）',
  `create_time`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_stay_student` (`student_id`),
  KEY `idx_stay_room` (`room_id`),
  CONSTRAINT `fk_stay_student` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`),
  CONSTRAINT `fk_stay_room` FOREIGN KEY (`room_id`) REFERENCES `t_room` (`id`)
) ENGINE=InnoDB COMMENT='住宿安排表';

-- -------------------------------------------------------------
-- 7. 学生申请表（宿舍类型申请 / 调宿申请 / 宿舍报修）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_apply` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id`  BIGINT   NOT NULL COMMENT '申请人',
  `type`        VARCHAR(30) NOT NULL COMMENT '申请类型：宿舍类型申请/调宿申请/宿舍报修',
  `title`       VARCHAR(100) DEFAULT NULL COMMENT '标题',
  `content`     TEXT     COMMENT '申请内容',
  `status`      VARCHAR(10) DEFAULT '待处理' COMMENT '状态：待处理/已通过/已驳回',
  `reply`       VARCHAR(255) DEFAULT NULL COMMENT '处理回复',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`),
  KEY `idx_apply_student` (`student_id`),
  KEY `idx_apply_status` (`status`),
  CONSTRAINT `fk_apply_student` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`)
) ENGINE=InnoDB COMMENT='学生申请表';

-- -------------------------------------------------------------
-- 8. 数据备份记录表
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_backup` (
  `id`          BIGINT    NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_name`   VARCHAR(100) NOT NULL COMMENT '备份文件名',
  `file_path`   VARCHAR(255) DEFAULT NULL COMMENT '备份文件路径',
  `size`        BIGINT    DEFAULT 0 COMMENT '文件大小（字节）',
  `operator`    VARCHAR(50) DEFAULT NULL COMMENT '操作人',
  `create_time` DATETIME  DEFAULT CURRENT_TIMESTAMP COMMENT '备份时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='数据备份记录表';

-- =============================================================
-- 初始演示数据（密码均为 BCrypt 加密串，明文依次为 admin123 / manager123 / student123 / stu02@123 / stu03@123）
-- =============================================================

-- 用户账户
INSERT INTO `t_user` (`id`, `username`, `password`, `name`, `role`, `enabled`) VALUES
(1, 'admin',     '$2a$10$KICrBolDUhG.IvfzMsZMg.1Pe/fJMV6ZH0tDe15E4zXDu7b9Xprbm', '张伟',   'ROLE_ADMIN',    1),
(2, 'manager',   '$2a$10$EnR3sAONhX/LEsmsJpNOHuOggyE1NQa8fLrjdf5NLrEi2FV7L6Ff.', '李静',   'ROLE_MANAGER',  1),
(3, 'student01', '$2a$10$EI00uTT78lVOv/ntMg3Es.4gshU8tM9TVcG2pnGny7CGa5UTuxNBu', '王小明', 'ROLE_STUDENT',  1),
(4, 'student02', '$2a$10$7syArW3QP75e2DKd1dF2J.EWBlxHZt/YJIAliXZugtxKJQHbhiWyW', '李思思', 'ROLE_STUDENT',  1),
(5, 'student03', '$2a$10$S19QYugmKIyPPL0LTL6mDufJ34lCOlCxrLyN8Z6gqd6dw1cRB7FN6', '赵宇航', 'ROLE_STUDENT',  1)
ON DUPLICATE KEY UPDATE `username` = VALUES(`username`);

-- 学生信息
INSERT INTO `t_student` (`id`, `user_id`, `student_no`, `name`, `gender`, `college`, `major`, `class_name`, `phone`, `email`) VALUES
(1, 3, '2023010100', '王小明', '男', '计算机学院',   '软件工程', '软工2301', '13800000001', 'wangxm@qq.com'),
(2, 4, '2023010101', '李思思', '女', '外国语学院',   '商务英语', '商英2301', '13800000002', 'liss@qq.com'),
(3, 5, '2023010102', '赵宇航', '男', '计算机学院',   '软件工程', '软工2301', '13800000003', 'zhaoyh@qq.com'),
(4, NULL, '2023010103', '陈思颖', '女', '经济管理学院', '会计学',   '会计2301', '13800000004', 'chensy@qq.com'),
(5, NULL, '2023010104', '刘一鸣', '男', '计算机学院',   '网络工程', '网工2302', '13800000005', 'liuym@qq.com'),
(6, NULL, '2023010105', '孙雨桐', '女', '外国语学院',   '英语',     '英语2302', '13800000006', 'sunyt@qq.com');

-- 宿舍楼栋
INSERT INTO `t_building` (`id`, `name`, `gender_limit`, `floors`, `manager`, `remark`) VALUES
(1, '一号公寓', '男', 6, '李静', '男生宿舍楼'),
(2, '二号公寓', '女', 6, '李静', '女生宿舍楼');

-- 宿舍房型
INSERT INTO `t_room_type` (`id`, `name`, `bed_count`, `price`, `remark`) VALUES
(1, '四人间', 4, 1200.00, '上床下桌，独立卫浴，空调'),
(2, '六人间', 6, 800.00,  '上下铺，公共卫浴，空调'),
(3, '八人间', 8, 600.00,  '上下铺，公共卫浴');

-- 房间
INSERT INTO `t_room` (`id`, `building_id`, `room_type_id`, `room_no`, `floor`, `status`) VALUES
(1, 1, 1, '101', 1, '正常'),
(2, 1, 1, '102', 1, '正常'),
(3, 1, 1, '103', 1, '正常'),
(4, 1, 1, '201', 2, '正常'),
(5, 1, 1, '202', 2, '正常'),
(6, 1, 1, '203', 2, '正常'),
(7, 1, 2, '204', 2, '正常'),
(8, 2, 1, '101', 1, '正常'),
(9, 2, 1, '102', 1, '正常'),
(10, 2, 2, '201', 2, '正常'),
(11, 2, 2, '202', 2, '正常');

-- 住宿安排
INSERT INTO `t_stay` (`id`, `student_id`, `room_id`, `bed_no`, `check_in_date`, `check_out_date`, `status`, `remark`) VALUES
(1, 1, 1, '1号床', '2025-09-01', NULL,       '在住', NULL),
(2, 5, 1, '2号床', '2025-09-01', '2026-07-01', '已退宿', '调至六人间204'),
(3, 5, 7, '1号床', '2026-09-02', NULL,       '在住', NULL),
(4, 3, 3, '3号床', '2026-09-02', NULL,       '在住', NULL),
(5, 2, 8, '1号床', '2026-09-02', NULL,       '在住', NULL),
(6, 4, 10, '2号床', '2026-09-02', NULL,      '在住', NULL),
(7, 6, 8, '3号床', '2026-09-02', NULL,       '在住', NULL);

-- 学生申请
INSERT INTO `t_apply` (`id`, `student_id`, `type`, `title`, `content`, `status`, `reply`, `create_time`, `handle_time`) VALUES
(1, 1, '宿舍类型申请', '申请调整到四人间', '本人现住六人间，想申请调至本楼四人间，望批准。', '待处理', NULL, '2026-09-26 10:20:00', NULL),
(2, 2, '调宿申请',   '申请从101调至102', '本人与室友作息时间差异较大，申请调整到102宿舍。', '待处理', NULL, '2026-09-27 09:15:00', NULL),
(3, 3, '宿舍报修',   '卫生间漏水报修',   '宿舍卫生间水龙头漏水，希望尽快安排维修。', '已通过', '已联系后勤处，本周五上门维修。', '2026-09-20 14:00:00', '2026-09-22 09:30:00');

-- 数据备份记录
INSERT INTO `t_backup` (`id`, `file_name`, `file_path`, `size`, `operator`, `create_time`) VALUES
(1, 'dormitory_20260925_120000.sql', 'backups/dormitory_20260925_120000.sql', 102400, 'admin', '2026-09-25 12:00:00');

-- =============================================================
-- 脚本结束
-- =============================================================
