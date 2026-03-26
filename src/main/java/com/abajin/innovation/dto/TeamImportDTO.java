package com.abajin.innovation.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 团队导入 Excel 行 DTO
 *
 * 支持通过表头文字匹配列，列顺序不限
 * 支持的表头名称：团队名称、队伍类型、团队简介、负责人姓名、负责人ID、学院名、
 *                 指导老师、成员数量、是否招募、是否公开、招募要求、历史荣誉、项目标签、创建时间
 */
@Data
public class TeamImportDTO {

    @ExcelProperty("团队名称")
    private String name;

    @ExcelProperty("队伍类型")
    private String teamType;

    @ExcelProperty("团队简介")
    private String description;

    @ExcelProperty("负责人姓名")
    private String leaderRealName;

    @ExcelProperty("负责人ID")
    private String leaderStudentId;

    @ExcelProperty("学院名")
    private String collegeName;

    @ExcelProperty("指导老师")
    private String instructorName;

    @ExcelProperty("成员数量")
    private String memberCount;

    @ExcelProperty("是否招募")
    private String recruiting;

    @ExcelProperty("是否公开")
    private String isPublic;

    @ExcelProperty("招募要求")
    private String recruitmentRequirement;

    @ExcelProperty("历史荣誉")
    private String honors;

    @ExcelProperty("项目标签")
    private String tags;

    @ExcelProperty("创建时间")
    private String createTime;
}
