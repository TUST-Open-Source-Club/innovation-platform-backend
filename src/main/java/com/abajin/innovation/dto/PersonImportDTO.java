package com.abajin.innovation.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 人员库导入 Excel 行 DTO
 *
 * 支持通过表头文字匹配列，列顺序不限
 * 支持的表头名称：姓名、人员类型、性别、手机号、邮箱、职称、所属单位、职位、
 *                 研究方向、主要成就、个人简介、专业领域
 */
@Data
public class PersonImportDTO {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("人员类型")
    private String personType;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("职称")
    private String title;

    @ExcelProperty("所属单位")
    private String organization;

    @ExcelProperty("职位")
    private String position;

    @ExcelProperty("研究方向")
    private String researchDirection;

    @ExcelProperty("主要成就")
    private String achievements;

    @ExcelProperty("个人简介")
    private String introduction;

    @ExcelProperty("专业领域")
    private String expertiseAreas;
}
