-- CAS统一身份认证相关数据库修改
use innovation_platform;

-- 为user表添加CAS认证相关字段（如果尚不存在）
-- 注意：这些字段可能已经存在，执行前请先确认

-- 添加认证方式字段
ALTER TABLE user ADD COLUMN IF NOT EXISTS auth_type VARCHAR(20) DEFAULT 'LOCAL' COMMENT '认证方式：LOCAL-本地密码, CAS-CAS统一认证, BOTH-双认证';

-- 添加CAS用户唯一标识字段
ALTER TABLE user ADD COLUMN IF NOT EXISTS cas_uid VARCHAR(50) NULL COMMENT 'CAS用户唯一标识（学号/工号）';

-- 添加资料是否完善字段
ALTER TABLE user ADD COLUMN IF NOT EXISTS is_profile_complete TINYINT(1) DEFAULT 1 COMMENT '资料是否完善：0-未完善，1-已完善';

-- 为cas_uid添加索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_cas_uid ON user(cas_uid);

-- 为auth_type添加索引
CREATE INDEX IF NOT EXISTS idx_auth_type ON user(auth_type);

-- 为real_name添加索引（用于同名账号检测）
CREATE INDEX IF NOT EXISTS idx_real_name ON user(real_name);

-- 添加组合索引（用于检测同名本地账号）
CREATE INDEX IF NOT EXISTS idx_real_name_auth_type ON user(real_name, auth_type);
