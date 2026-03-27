package com.abajin.innovation.listener;

import com.abajin.innovation.dto.UserImportDTO;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户导入 Excel 读取监听，收集所有行
 */
public class UserImportListener implements ReadListener<UserImportDTO> {

    private final List<UserImportDTO> list = new ArrayList<>();

    @Override
    public void invoke(UserImportDTO data, AnalysisContext context) {
        if (data.getUsername() != null && !data.getUsername().trim().isEmpty()) {
            list.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}

    public List<UserImportDTO> getList() {
        return list;
    }
}
