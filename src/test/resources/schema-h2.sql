-- H2 Test Database Schema

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    college_id BIGINT,
    college_name VARCHAR(100),
    status INT DEFAULT 1,
    auth_type VARCHAR(20) DEFAULT 'LOCAL',
    cas_uid VARCHAR(50),
    is_profile_complete INT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 学院表
CREATE TABLE IF NOT EXISTS college (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20),
    description VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_cas_uid ON user(cas_uid);
CREATE INDEX IF NOT EXISTS idx_auth_type ON user(auth_type);
CREATE INDEX IF NOT EXISTS idx_real_name ON user(real_name);
CREATE INDEX IF NOT EXISTS idx_real_name_auth_type ON user(real_name, auth_type);
