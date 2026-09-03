-- 强制使用 utf8mb4 导入, 否则客户端默认字符集会把中文二次编码成乱码
SET NAMES utf8mb4;

-- 补齐 TG 模块接口 @PreAuthorize 用到但 sys_menu 中不存在的按钮权限 (2026-09-01)
-- 之前这些权限没有菜单项, 除内置 admin 外任何角色都无法被授权, 导致「没有权限」
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(2201, '好友查询',     2002, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:contact:query',        '#', 'admin', now(), ''),
(2202, '好友修改',     2002, 2, '#', '', 1, 0, 'F', '0', '0', 'tg:contact:edit',         '#', 'admin', now(), ''),
(2203, '好友删除',     2002, 3, '#', '', 1, 0, 'F', '0', '0', 'tg:contact:remove',       '#', 'admin', now(), ''),
(2204, '消息查询',     2003, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:chatMessage:query',    '#', 'admin', now(), ''),
(2205, '消息删除',     2003, 2, '#', '', 1, 0, 'F', '0', '0', 'tg:chatMessage:remove',   '#', 'admin', now(), ''),
(2206, '账号导入新增', 2004, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:import:add',           '#', 'admin', now(), ''),
(2207, '账号导入修改', 2004, 2, '#', '', 1, 0, 'F', '0', '0', 'tg:import:edit',          '#', 'admin', now(), ''),
(2208, '好友导入新增', 2005, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:contactImport:add',    '#', 'admin', now(), ''),
(2209, '好友导入修改', 2005, 2, '#', '', 1, 0, 'F', '0', '0', 'tg:contactImport:edit',   '#', 'admin', now(), ''),
(2210, '好友导入删除', 2005, 3, '#', '', 1, 0, 'F', '0', '0', 'tg:contactImport:remove', '#', 'admin', now(), ''),
(2211, '问候语新增',   2006, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:greeting:add',         '#', 'admin', now(), ''),
(2212, '问候语修改',   2006, 2, '#', '', 1, 0, 'F', '0', '0', 'tg:greeting:edit',        '#', 'admin', now(), ''),
(2213, '问候语删除',   2006, 3, '#', '', 1, 0, 'F', '0', '0', 'tg:greeting:remove',      '#', 'admin', now(), ''),
(2214, '开场白查询',   2007, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:opening:query',        '#', 'admin', now(), ''),
(2215, '开场白新增',   2007, 2, '#', '', 1, 0, 'F', '0', '0', 'tg:opening:add',          '#', 'admin', now(), ''),
(2216, '开场白修改',   2007, 3, '#', '', 1, 0, 'F', '0', '0', 'tg:opening:edit',         '#', 'admin', now(), ''),
(2217, '开场白删除',   2007, 4, '#', '', 1, 0, 'F', '0', '0', 'tg:opening:remove',       '#', 'admin', now(), ''),
(2218, '代理查询',     2008, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:proxy:list',           '#', 'admin', now(), ''),
(2219, '代理导入',     2008, 2, '#', '', 1, 0, 'F', '0', '0', 'tg:proxy:add',            '#', 'admin', now(), ''),
(2220, '代理修改',     2008, 3, '#', '', 1, 0, 'F', '0', '0', 'tg:proxy:edit',           '#', 'admin', now(), ''),
(2221, '代理删除',     2008, 4, '#', '', 1, 0, 'F', '0', '0', 'tg:proxy:remove',         '#', 'admin', now(), ''),
(2222, '系统配置修改', 2018, 1, '#', '', 1, 0, 'F', '0', '0', 'tg:systemConfig:edit',    '#', 'admin', now(), '');

-- 已拥有父菜单的角色自动获得对应按钮权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT rm.role_id, m.menu_id
FROM sys_menu m
JOIN sys_role_menu rm ON rm.menu_id = m.parent_id
WHERE m.menu_id BETWEEN 2201 AND 2222
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu x WHERE x.role_id = rm.role_id AND x.menu_id = m.menu_id);
