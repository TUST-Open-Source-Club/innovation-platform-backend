package com.abajin.innovation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * CAS账号合并请求DTO
 */
@Data
public class CasMergeRequest {
    /**
     * CAS用户ID（学号/工号）
     */
    @NotBlank(message = "CAS用户ID不能为空")
    private String casUid;

    /**
     * 真实姓名（用于查找本地账号）
     */
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    /**
     * 本地账号密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
