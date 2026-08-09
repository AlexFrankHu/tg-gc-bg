-- 补齐前端已存在但菜单缺失的页面: 5 个日志页 + 数据统计 + 系统配置
-- tg_menus.sql 里 2010 "操作日志" 指向的 tg/log/index 组件并不存在, 这里改成日志目录并挂 5 个真实日志页
-- 幂等: 可重复执行
SET NAMES utf8mb4;

DELETE FROM sys_role_menu WHERE menu_id IN (2010,2012,2013,2014,2015,2016,2017,2018);
DELETE FROM sys_menu WHERE menu_id IN (2010,2012,2013,2014,2015,2016,2017,2018);

-- 日志目录 (二级目录组件必须是 ParentView)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2010, '日志管理', 2000, 10, 'log', 'ParentView', 1, 0, 'M', '0', '0', '', 'log', 'admin', sysdate(), '日志目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(2012, '登录日志',     2010, 1, 'loginLog',         'tg/log/loginLog',         1, 0, 'C', '0', '0', 'tg:import:list', 'log', 'admin', sysdate(), ''),
(2013, '好友分配日志', 2010, 2, 'contactAssignLog', 'tg/log/contactAssignLog', 1, 0, 'C', '0', '0', 'tg:import:list', 'log', 'admin', sysdate(), ''),
(2014, '自动回复日志', 2010, 3, 'autoReplyLog',     'tg/log/autoReplyLog',     1, 0, 'C', '0', '0', 'tg:import:list', 'log', 'admin', sysdate(), ''),
(2015, '发送失败日志', 2010, 4, 'sendFailLog',      'tg/log/sendFailLog',      1, 0, 'C', '0', '0', 'tg:import:list', 'log', 'admin', sysdate(), ''),
(2016, '代理分配日志', 2010, 5, 'proxyAssignLog',   'tg/log/proxyAssignLog',   1, 0, 'C', '0', '0', 'tg:proxyIp:list', 'log', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(2017, '数据统计', 2000, 11, 'replyStat',    'tg/replyStat/index',    1, 0, 'C', '0', '0', 'tg:replyStat:list',    'chart',    'admin', sysdate(), '自动回复率统计'),
(2018, '系统配置', 2000, 12, 'systemConfig', 'tg/systemConfig/index', 1, 0, 'C', '0', '0', 'tg:systemConfig:list', 'system', 'admin', sysdate(), '');

-- 授权: 内置 admin 角色 + 全权限角色 superadmin
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r
JOIN sys_menu m ON m.menu_id IN (2010,2012,2013,2014,2015,2016,2017,2018)
WHERE r.role_key IN ('admin','superadmin');
