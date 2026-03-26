package com.abajin.innovation.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 用户导入 Excel 行 DTO
 *
 * 支持通过表头文字匹配列，列顺序不限
 * 支持的表头名称：用户名、密码、真实姓名、邮箱、手机号、角色、学院ID、学院名称、状态
 */
@Data
public class UserImportDTO {

    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("密码")
    private String password;

    @ExcelProperty("真实姓名")
    private String realName;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("角色")
    private String role;

    @ExcelProperty("学院ID")
    private Long collegeId;

    @ExcelProperty("学院名称")
    private String collegeName;

    @ExcelProperty("状态")
    private Integer status;
}
