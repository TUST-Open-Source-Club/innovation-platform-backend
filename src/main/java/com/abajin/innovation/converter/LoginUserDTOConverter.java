package com.abajin.innovation.converter;

import com.abajin.innovation.dto.LoginUserDTO;
import com.abajin.innovation.entity.User;

/**
 * User 转换为 LoginUserDTO 的转换器
 */
public class LoginUserDTOConverter {

    /**
     * 将 User 转换为 LoginUserDTO
     *
     * @param user 用户实体
     * @return LoginUserDTO
     */
    public static LoginUserDTO convert(User user) {
        if (user == null) {
            return null;
        }
        LoginUserDTO dto = new LoginUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setCollegeId(user.getCollegeId());
        dto.setCollegeName(user.getCollegeName());
        dto.setStatus(user.getStatus());
        dto.setAuthType(user.getAuthType());
        dto.setCasUid(user.getCasUid());
        dto.setIsProfileComplete(user.getIsProfileComplete());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        return dto;
    }
}
