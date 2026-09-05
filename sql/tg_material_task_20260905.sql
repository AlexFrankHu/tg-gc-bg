-- 强制使用 utf8mb4 导入, 否则客户端默认字符集会把中文二次编码成乱码
SET NAMES utf8mb4;

-- 昵称/头像素材库 + 账号任务(修改昵称/头像/2FA) 数据库迁移

-- 1. 账号表: 昵称默认空字符串, 新增 2FA 密码字段
UPDATE `tg_telethon_account` SET `nickname` = '' WHERE `nickname` IS NULL;
ALTER TABLE `tg_telethon_account`
    MODIFY COLUMN `nickname` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '昵称(firstName + lastName)',
    ADD COLUMN `twofa_password` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '2FA密码(两步验证)' AFTER `nickname`;

-- 2. 昵称素材库
CREATE TABLE IF NOT EXISTS `tg_nickname_material` (
    `id`          INT          NOT NULL AUTO_INCREMENT,
    `nickname`    VARCHAR(128) NOT NULL COMMENT '昵称',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='昵称素材库';

-- 3. 头像素材库 (文件存放在后台 profile/tg_avatar 目录, file_path 为 /profile/tg_avatar/xxx.jpg)
CREATE TABLE IF NOT EXISTS `tg_avatar_material` (
    `id`          INT          NOT NULL AUTO_INCREMENT,
    `file_name`   VARCHAR(255) NOT NULL COMMENT '原文件名',
    `file_path`   VARCHAR(500) NOT NULL COMMENT '访问路径(/profile/...)',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='头像素材库';

-- 4. 账号任务表: 后台下发, 节点轮询执行
CREATE TABLE IF NOT EXISTS `tg_account_task` (
    `id`           INT          NOT NULL AUTO_INCREMENT,
    `account_id`   INT          NOT NULL COMMENT '账号ID',
    `phone`        VARCHAR(32)  NOT NULL COMMENT '手机号',
    `node_id`      VARCHAR(32)  DEFAULT NULL COMMENT '执行节点ID(账号所属节点)',
    `task_type`    VARCHAR(20)  NOT NULL COMMENT '任务类型 nickname/avatar/twofa',
    `param`        TEXT         COMMENT '任务参数JSON',
    `status`       VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT 'pending/success/failed',
    `error_reason` VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
    `source`       VARCHAR(20)  DEFAULT NULL COMMENT '来源 single/batch/group',
    `source_ref`   VARCHAR(64)  DEFAULT NULL COMMENT '来源引用(批次号/分组ID)',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_node_status` (`node_id`, `status`),
    KEY `idx_account_id` (`account_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号任务(修改昵称/头像/2FA)';

-- 5. 菜单 & 权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(2230, '昵称素材', 2000, 30, 'nicknameMaterial', 'tg/nicknameMaterial/index', 1, 0, 'C', '0', '0', 'tg:nicknameMaterial:list', 'edit',    'admin', now(), '昵称素材库'),
(2231, '头像素材', 2000, 31, 'avatarMaterial',   'tg/avatarMaterial/index',   1, 0, 'C', '0', '0', 'tg:avatarMaterial:list',   'user',    'admin', now(), '头像素材库'),
(2232, '账号任务', 2000, 32, 'accountTask',      'tg/accountTask/index',      1, 0, 'C', '0', '0', 'tg:accountTask:list',      'list',    'admin', now(), '修改昵称/头像/2FA任务记录'),
(2233, '昵称素材导入', 2230, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:nicknameMaterial:add',    '#', 'admin', now(), ''),
(2234, '昵称素材删除', 2230, 2, '#', '', 1, 0, 'F', '0', '0', 'tg:nicknameMaterial:remove', '#', 'admin', now(), ''),
(2235, '头像素材导入', 2231, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:avatarMaterial:add',      '#', 'admin', now(), ''),
(2236, '头像素材删除', 2231, 2, '#', '', 1, 0, 'F', '0', '0', 'tg:avatarMaterial:remove',   '#', 'admin', now(), ''),
(2237, '账号任务删除', 2232, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:accountTask:remove',      '#', 'admin', now(), '');

-- 已拥有 TG 根目录(2000)的角色自动获得新菜单; 已拥有父菜单的角色自动获得按钮权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT rm.role_id, m.menu_id
FROM sys_menu m
JOIN sys_role_menu rm ON rm.menu_id = m.parent_id
WHERE m.menu_id BETWEEN 2230 AND 2232
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu x WHERE x.role_id = rm.role_id AND x.menu_id = m.menu_id);
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT rm.role_id, m.menu_id
FROM sys_menu m
JOIN sys_role_menu rm ON rm.menu_id = m.parent_id
WHERE m.menu_id BETWEEN 2233 AND 2237
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu x WHERE x.role_id = rm.role_id AND x.menu_id = m.menu_id);
