package com.abajin.innovation.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 登录返回的用户信息DTO
 */
@Data
public class LoginUserDTO {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String role;
    private Long collegeId;
    private String collegeName;
    private Integer status;
    private String authType; // 认证方式：LOCAL-本地密码, CAS-CAS统一认证, BOTH-双认证
    private String casUid; // CAS用户唯一标识（学号/工号）
    private Integer isProfileComplete; // 资料是否完善：0-未完善，1-已完善
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
