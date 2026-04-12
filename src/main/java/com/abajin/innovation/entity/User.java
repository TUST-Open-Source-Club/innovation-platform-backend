package com.abajin.innovation.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    private Long id;
    private String username; // 用户名
    private String password; // 密码
    private String realName; // 真实姓名
    private String email; // 邮箱
    private String phone; // 手机号
    private String role; // 角色：STUDENT, TEACHER, COLLEGE_ADMIN, SCHOOL_ADMIN
    private Long collegeId; // 所属学院ID
    private String collegeName; // 学院名称
    private Integer status; // 状态：0-禁用，1-启用
    private String authType; // 认证方式：LOCAL-本地密码, CAS-CAS统一认证, BOTH-双认证
    private String casUid; // CAS用户唯一标识（学号/工号）
    private Integer isProfileComplete; // 资料是否完善：0-未完善，1-已完善
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
