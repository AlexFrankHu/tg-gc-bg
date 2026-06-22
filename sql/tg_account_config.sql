-- ----------------------------
-- Telegram账号配置表
-- 该表由 tg-client-server 工程使用，tg-client-bg 管理后台共用同一数据库
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tg_account_config` (
  `id`               INT           NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `tg_user_id`       BIGINT        DEFAULT NULL             COMMENT 'Telegram用户ID',
  `custom_username`  VARCHAR(128)  DEFAULT NULL             COMMENT '自定义用户名',
  `notice_flag`      INT           DEFAULT NULL             COMMENT '通知标识',
  `phone_num`        VARCHAR(32)   NOT NULL                 COMMENT '手机号（含国际区号）',
  `login_status`     INT           NOT NULL DEFAULT -1      COMMENT '登录状态（1=已登录 0=等待登录 -1=未登录 -999=已注销）',
  `api_id`           INT           NOT NULL                 COMMENT 'Telegram API ID',
  `api_hash`         VARCHAR(64)   NOT NULL                 COMMENT 'Telegram API Hash',
  `device_model`     VARCHAR(64)   DEFAULT NULL             COMMENT '设备型号（如 Samsung Galaxy S24, iPhone 15 Pro）',
  `system_version`   VARCHAR(32)   DEFAULT NULL             COMMENT '系统版本（如 Android 14, iOS 17.4）',
  `app_version`      VARCHAR(32)   DEFAULT NULL             COMMENT '应用版本（如 10.8.3）',
  `system_language_code` VARCHAR(16) DEFAULT NULL           COMMENT '系统语言代码（如 zh-Hans, en-US）',
  `two_fa_password`  VARCHAR(128)  DEFAULT NULL             COMMENT '二级密码（两步验证密码）',
  `last_online_time` DATETIME      DEFAULT NULL             COMMENT '最后在线时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'Telegram账号配置表';

-- ----------------------------
-- Telegram账号管理菜单
-- ----------------------------
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('Telegram管理', 0, 5, 'tg', NULL, 1, 0, 'M', '0', '0', '', 'message', 'admin', sysdate(), '', NULL, 'Telegram管理目录');

SET @parentId = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('账号管理', @parentId, 1, 'account', 'tg/account/index', 1, 0, 'C', '0', '0', 'tg:account:list', 'peoples', 'admin', sysdate(), '', NULL, 'Telegram账号管理菜单');

SET @menuId = LAST_INSERT_ID();

-- 按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('账号查询', @menuId, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:account:query', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('账号新增', @menuId, 2, '#', '', 1, 0, 'F', '0', '0', 'tg:account:add', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('账号修改', @menuId, 3, '#', '', 1, 0, 'F', '0', '0', 'tg:account:edit', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('账号删除', @menuId, 4, '#', '', 1, 0, 'F', '0', '0', 'tg:account:remove', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('账号导出', @menuId, 5, '#', '', 1, 0, 'F', '0', '0', 'tg:account:export', '#', 'admin', sysdate(), '', NULL, '');
