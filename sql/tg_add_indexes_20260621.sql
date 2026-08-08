-- 强制使用 utf8mb4 导入, 否则客户端默认字符集会把中文二次编码成乱码
SET NAMES utf8mb4;

-- 为所有查询条件字段补充索引 (2026-06-21)
-- MySQL 5.7 在线加索引，不锁表: ALGORITHM=INPLACE, LOCK=NONE
-- 仅对高区分度/范围/排序字段加索引；前导通配符 LIKE '%x%' 及低区分度布尔枚举列不加。

ALTER TABLE tg_chat_message       ADD INDEX idx_sender_user_id (sender_user_id),   ALGORITHM=INPLACE, LOCK=NONE;

ALTER TABLE tg_contact            ADD INDEX idx_create_time (create_time),         ALGORITHM=INPLACE, LOCK=NONE;
ALTER TABLE tg_contact            ADD INDEX idx_update_time (update_time),         ALGORITHM=INPLACE, LOCK=NONE;
ALTER TABLE tg_contact            ADD INDEX idx_user_id (user_id),                 ALGORITHM=INPLACE, LOCK=NONE;

ALTER TABLE tg_contact_assign_log ADD INDEX idx_account_batch_no (account_batch_no), ALGORITHM=INPLACE, LOCK=NONE;
ALTER TABLE tg_contact_assign_log ADD INDEX idx_contact_batch_no (contact_batch_no), ALGORITHM=INPLACE, LOCK=NONE;
ALTER TABLE tg_contact_assign_log ADD INDEX idx_group_id (group_id),               ALGORITHM=INPLACE, LOCK=NONE;
ALTER TABLE tg_contact_assign_log ADD INDEX idx_create_time (create_time),         ALGORITHM=INPLACE, LOCK=NONE;
ALTER TABLE tg_contact_assign_log ADD INDEX idx_node_status (node_id, status),     ALGORITHM=INPLACE, LOCK=NONE;

ALTER TABLE tg_send_fail_log      ADD INDEX idx_send_time (send_time),             ALGORITHM=INPLACE, LOCK=NONE;

ALTER TABLE tg_proxy_ip           ADD INDEX idx_group_status (group_no, status),   ALGORITHM=INPLACE, LOCK=NONE;

ALTER TABLE tg_proxy_assign_log   ADD INDEX idx_account_batch_no (account_batch_no), ALGORITHM=INPLACE, LOCK=NONE;
ALTER TABLE tg_proxy_assign_log   ADD INDEX idx_proxy_group_no (proxy_group_no),   ALGORITHM=INPLACE, LOCK=NONE;
ALTER TABLE tg_proxy_assign_log   ADD INDEX idx_account_phone (account_phone),     ALGORITHM=INPLACE, LOCK=NONE;

ALTER TABLE tg_telethon_account   ADD INDEX idx_tg_user_id (tg_user_id),           ALGORITHM=INPLACE, LOCK=NONE;
