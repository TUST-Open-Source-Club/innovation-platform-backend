package com.abajin.innovation.dto;

import com.abajin.innovation.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageRequest {
    private String username; // 用户名（模糊查询）
    private String realName; // 真实姓名（模糊查询）
    private String casUid; // 学号/工号（CAS用户ID，精确查询）
    private String role; // 角色
    private Long collegeId; // 学院ID
    private Integer status; // 状态
}
