use innovation_platform;
-- 删除外键
ALTER TABLE space DROP FOREIGN KEY space_ibfk_1;

-- 删除不需要的列
ALTER TABLE space DROP COLUMN space_type_id;

-- 为space表 新增is_delete列
ALTER TABLE space ADD COLUMN is_deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除';

-- 为 space 表的 name 字段添加唯一索引
ALTER TABLE space ADD CONSTRAINT uk_space_name UNIQUE (name);

-- 为activity表 新增is_deleted列
ALTER TABLE activity ADD COLUMN is_deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除';