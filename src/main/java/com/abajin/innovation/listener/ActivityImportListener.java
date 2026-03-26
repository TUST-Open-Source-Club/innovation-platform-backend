package com.abajin.innovation.listener;

import com.abajin.innovation.dto.ActivityImportDTO;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动导入 Excel 读取监听，收集所有行
 */
public class ActivityImportListener implements ReadListener<ActivityImportDTO> {

    private final List<ActivityImportDTO> list = new ArrayList<>();

    @Override
    public void invoke(ActivityImportDTO data, AnalysisContext context) {
        if (data.getTitle() != null && !data.getTitle().trim().isEmpty()) {
            list.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}

    public List<ActivityImportDTO> getList() {
        return list;
    }
}
