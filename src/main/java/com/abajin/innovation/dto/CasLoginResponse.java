package com.abajin.innovation.dto;

import lombok.Data;

/**
 * CAS登录响应DTO
 */
@Data
public class CasLoginResponse {
    /**
     * JWT token
     */
    private String token;

    /**
     * 用户信息
     */
    private LoginUserDTO user;

    /**
     * 是否需要完善资料
     */
    private Boolean needCompleteProfile;

    /**
     * 是否需要合并账号
     */
    private Boolean needMerge;

    /**
     * 同名本地账号信息（用于合并提示）
     */
    private LoginUserDTO duplicateAccount;

    /**
     * CAS用户信息（用于合并）
     */
    private String casUid;
    private String casName;
}
