package com.abajin.innovation.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 活动导入 Excel 行 DTO
 *
 * 支持通过表头文字匹配列，列顺序不限
 * 支持的表头名称：活动标题、活动类型、活动系列、开始时间、结束时间、
 *                 活动地点、活动描述、活动内容、最大参与人数、报名链接
 */
@Data
public class ActivityImportDTO {

    @ExcelProperty("活动标题")
    private String title;

    @ExcelProperty("活动类型")
    private String activityType;

    @ExcelProperty("活动系列")
    private String activitySeries;

    @ExcelProperty("开始时间")
    private String startTime;

    @ExcelProperty("结束时间")
    private String endTime;

    @ExcelProperty("活动地点")
    private String location;

    @ExcelProperty("活动描述")
    private String description;

    @ExcelProperty("活动内容")
    private String content;

    @ExcelProperty("最大参与人数")
    private Integer maxParticipants;

    @ExcelProperty("报名链接")
    private String registrationLink;
}
