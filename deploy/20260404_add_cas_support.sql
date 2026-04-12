-- ============================================
-- 数据库迁移脚本：添加CAS统一身份认证支持
-- 日期：2026-04-04
-- 说明：添加认证方式、CAS用户ID、资料完善标记字段
-- 执行方式：在MySQL中执行 source /path/to/20260404_add_cas_support.sql
-- ============================================

-- 设置数据库（请根据实际情况修改数据库名）
-- USE innovation_platform;

-- ============================================
-- 1. 添加认证方式字段
-- ============================================
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_name = 'user' AND column_name = 'auth_type');
SET @sql := IF(@exist = 0, 
               'ALTER TABLE `user` ADD COLUMN `auth_type` VARCHAR(20) DEFAULT "LOCAL" COMMENT "认证方式：LOCAL-本地密码, CAS-CAS统一认证, BOTH-双认证" AFTER `status`',
               'SELECT "Column auth_type already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 2. 添加CAS用户ID字段（学号/工号）
-- ============================================
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_name = 'user' AND column_name = 'cas_uid');
SET @sql := IF(@exist = 0, 
               'ALTER TABLE `user` ADD COLUMN `cas_uid` VARCHAR(50) NULL COMMENT "CAS用户唯一标识（学号/工号）" AFTER `auth_type`',
               'SELECT "Column cas_uid already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 3. 添加资料完善标记字段
-- ============================================
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_name = 'user' AND column_name = 'is_profile_complete');
SET @sql := IF(@exist = 0, 
               'ALTER TABLE `user` ADD COLUMN `is_profile_complete` TINYINT(1) DEFAULT 1 COMMENT "资料是否完善：0-未完善，1-已完善" AFTER `cas_uid`',
               'SELECT "Column is_profile_complete already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 4. 创建索引（如果不存在）
-- ============================================

-- cas_uid唯一索引（用于CAS用户快速查询和去重）
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_name = 'user' AND index_name = 'idx_cas_uid');
SET @sql := IF(@exist = 0, 
               'ALTER TABLE `user` ADD UNIQUE INDEX `idx_cas_uid` (`cas_uid`)',
               'SELECT "Index idx_cas_uid already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- auth_type索引（用于按认证方式筛选）
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_name = 'user' AND index_name = 'idx_auth_type');
SET @sql := IF(@exist = 0, 
               'CREATE INDEX `idx_auth_type` ON `user`(`auth_type`)',
               'SELECT "Index idx_auth_type already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- real_name索引（用于同名账号检测）
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_name = 'user' AND index_name = 'idx_real_name');
SET @sql := IF(@exist = 0, 
               'CREATE INDEX `idx_real_name` ON `user`(`real_name`)',
               'SELECT "Index idx_real_name already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 组合索引（real_name + auth_type，用于检测同名本地账号）
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_name = 'user' AND index_name = 'idx_real_name_auth_type');
SET @sql := IF(@exist = 0, 
               'CREATE INDEX `idx_real_name_auth_type` ON `user`(`real_name`, `auth_type`)',
               'SELECT "Index idx_real_name_auth_type already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 5. 更新现有用户数据（迁移逻辑）
-- ============================================

-- 5.1 将所有现有用户的认证方式设为LOCAL（如果不为空则保持原值）
UPDATE `user` SET `auth_type` = 'LOCAL' WHERE `auth_type` IS NULL OR `auth_type` = '';

-- 5.2 将所有现有用户的资料完善标记设为1（已完善）
UPDATE `user` SET `is_profile_complete` = 1 WHERE `is_profile_complete` IS NULL;

-- ============================================
-- 6. 验证迁移结果
-- ============================================
SELECT 
  '迁移完成' as status,
  COUNT(*) as total_users,
  SUM(CASE WHEN auth_type = 'LOCAL' THEN 1 ELSE 0 END) as local_users,
  SUM(CASE WHEN auth_type = 'CAS' THEN 1 ELSE 0 END) as cas_users,
  SUM(CASE WHEN auth_type = 'BOTH' THEN 1 ELSE 0 END) as both_users,
  SUM(CASE WHEN cas_uid IS NOT NULL THEN 1 ELSE 0 END) as cas_bound_users
FROM `user`;

-- ============================================
-- 可选：验证脚本（手动执行检查）
-- ============================================
-- 检查表结构
-- DESC user;

-- 检查索引
-- SHOW INDEX FROM user;

-- 检查CAS用户列表
-- SELECT id, username, real_name, cas_uid, auth_type, is_profile_complete, create_time
-- FROM user WHERE auth_type IN ('CAS', 'BOTH') ORDER BY create_time DESC;
