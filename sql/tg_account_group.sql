-- 账号分组功能 数据库迁移
-- 1. 账号分组表
CREATE TABLE IF NOT EXISTS `tg_account_group` (
    `id`          INT             NOT NULL AUTO_INCREMENT COMMENT '组ID',
    `group_name`  VARCHAR(100)    NOT NULL                COMMENT '组名称',
    `enabled`     TINYINT(1)      NOT NULL DEFAULT 1      COMMENT '是否可用 1-可用 0-不可用',
    `create_time` DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME        DEFAULT NULL            COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='TG账号分组';

-- 2. 账号表增加分组ID
ALTER TABLE `tg_telethon_account`
    ADD COLUMN `group_id` INT DEFAULT NULL COMMENT '所属账号分组ID' AFTER `batch_no`;
ALTER TABLE `tg_telethon_account` ADD INDEX `idx_group_id` (`group_id`);

-- 3. 好友分配日志增加来源、分组信息
ALTER TABLE `tg_contact_assign_log`
    ADD COLUMN `source`     VARCHAR(20)  DEFAULT 'import' COMMENT '来源 import-账号导入 group-账号分组',
    ADD COLUMN `group_id`   INT          DEFAULT NULL     COMMENT '账号分组ID(source=group时)',
    ADD COLUMN `group_name` VARCHAR(100) DEFAULT NULL     COMMENT '账号分组名称(source=group时)';

-- 4. 菜单 & 权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2022, '账号分组', 2000, 6, 'accountGroup', 'tg/accountGroup/index', 1, 0, 'C', '0', '0', 'tg:accountGroup:list', 'peoples', 'admin', now(), '账号分组管理');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2023, '分组查询', 2022, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:accountGroup:query', '#', 'admin', now(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2024, '分组新增', 2022, 2, '#', '', 1, 0, 'F', '0', '0', 'tg:accountGroup:add', '#', 'admin', now(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2025, '分组修改', 2022, 3, '#', '', 1, 0, 'F', '0', '0', 'tg:accountGroup:edit', '#', 'admin', now(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2026, '分组添加好友', 2022, 4, '#', '', 1, 0, 'F', '0', '0', 'tg:accountGroup:assign', '#', 'admin', now(), '');

-- 授权 role 1,2,3
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id FROM (SELECT 1 role_id UNION SELECT 2 UNION SELECT 3) r
JOIN (SELECT 2022 menu_id UNION SELECT 2023 UNION SELECT 2024 UNION SELECT 2025 UNION SELECT 2026) m
WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);
