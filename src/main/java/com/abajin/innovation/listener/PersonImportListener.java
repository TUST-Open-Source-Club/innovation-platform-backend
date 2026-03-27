package com.abajin.innovation.listener;

import com.abajin.innovation.dto.PersonImportDTO;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 人员库导入 Excel 读取监听，收集所有行
 */
public class PersonImportListener implements ReadListener<PersonImportDTO> {

    private final List<PersonImportDTO> list = new ArrayList<>();

    @Override
    public void invoke(PersonImportDTO data, AnalysisContext context) {
        if (data.getName() != null && !data.getName().trim().isEmpty()) {
            list.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}

    public List<PersonImportDTO> getList() {
        return list;
    }
}
