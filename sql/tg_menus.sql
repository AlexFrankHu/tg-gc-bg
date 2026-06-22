SET NAMES utf8mb4;

-- Delete old TG menus if they exist (by path pattern)
DELETE FROM sys_role_menu WHERE menu_id IN (SELECT menu_id FROM sys_menu WHERE path IN ('tg','account','contact','chatMessage','import','contactImport','greeting','opening','proxyGroup','proxyIp','log','webclient') AND parent_id != 0);
DELETE FROM sys_role_menu WHERE menu_id IN (SELECT menu_id FROM sys_menu WHERE path = 'tg' AND parent_id = 0);
DELETE FROM sys_menu WHERE parent_id IN (SELECT t.menu_id FROM (SELECT menu_id FROM sys_menu WHERE path IN ('account','contact','chatMessage','import','contactImport','greeting','opening','proxyGroup','proxyIp','log','webclient')) t);
DELETE FROM sys_menu WHERE path IN ('account','contact','chatMessage','import','contactImport','greeting','opening','proxyGroup','proxyIp','log','webclient') AND parent_id != 0;
DELETE FROM sys_menu WHERE path = 'tg' AND parent_id = 0;

-- 1. Top-level TG management directory
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2000, 'Telegram管理', 0, 5, 'tg', NULL, 1, 0, 'M', '0', '0', '', 'message', 'admin', sysdate(), 'Telegram管理目录');

-- 2. Sub-menus (C = page menu)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2001, '账号管理', 2000, 1, 'account', 'tg/account/index', 1, 0, 'C', '0', '0', 'tg:account:list', 'peoples', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2002, '好友列表', 2000, 2, 'contact', 'tg/contact/index', 1, 0, 'C', '0', '0', 'tg:contact:list', 'list', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2003, '消息记录', 2000, 3, 'chatMessage', 'tg/chatMessage/index', 1, 0, 'C', '0', '0', 'tg:chatMessage:list', 'message', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2004, '账号导入', 2000, 4, 'import', 'tg/import/index', 1, 0, 'C', '0', '0', 'tg:import:list', 'upload', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2005, '联系人导入', 2000, 5, 'contactImport', 'tg/contactImport/index', 1, 0, 'C', '0', '0', 'tg:contactImport:list', 'upload', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2006, '广告问候语', 2000, 6, 'greeting', 'tg/greeting/index', 1, 0, 'C', '0', '0', 'tg:greeting:list', 'edit', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2007, '主动开场白', 2000, 7, 'opening', 'tg/opening/index', 1, 0, 'C', '0', '0', 'tg:opening:list', 'documentation', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2008, '代理分组', 2000, 8, 'proxyGroup', 'tg/proxyGroup/index', 1, 0, 'C', '0', '0', 'tg:proxyGroup:list', 'component', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2009, '代理IP', 2000, 9, 'proxyIp', 'tg/proxyIp/index', 1, 0, 'C', '0', '0', 'tg:proxyIp:list', 'server', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2010, '操作日志', 2000, 10, 'log', 'tg/log/index', 1, 0, 'C', '0', '0', 'tg:log:list', 'log', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2011, '节点信息', 2000, 0, 'node', 'tg/node/index', 1, 0, 'C', '0', '0', 'tg:node:list', 'monitor', 'admin', sysdate(), '集群节点信息');

-- 3. Button permissions for account page
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2101, '账号查询', 2001, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:account:query', '#', 'admin', sysdate(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2102, '账号新增', 2001, 2, '#', '', 1, 0, 'F', '0', '0', 'tg:account:add', '#', 'admin', sysdate(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2103, '账号修改', 2001, 3, '#', '', 1, 0, 'F', '0', '0', 'tg:account:edit', '#', 'admin', sysdate(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2104, '账号删除', 2001, 4, '#', '', 1, 0, 'F', '0', '0', 'tg:account:remove', '#', 'admin', sysdate(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2105, '账号导出', 2001, 5, '#', '', 1, 0, 'F', '0', '0', 'tg:account:export', '#', 'admin', sysdate(), '');

-- Button permissions for node page
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2111, '节点查询', 2011, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:node:query', '#', 'admin', sysdate(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2112, '节点修改', 2011, 2, '#', '', 1, 0, 'F', '0', '0', 'tg:node:edit', '#', 'admin', sysdate(), '');

-- 4. Assign all TG menus to admin role (role_id=1)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2000);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2001);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2002);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2003);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2004);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2005);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2006);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2007);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2008);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2009);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2010);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2101);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2102);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2103);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2104);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2105);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2011);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2111);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2112);
