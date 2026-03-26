package com.abajin.innovation.service;

import com.abajin.innovation.dto.PersonImportDTO;
import com.abajin.innovation.entity.PersonLibrary;
import com.abajin.innovation.entity.PersonType;
import com.abajin.innovation.listener.PersonImportListener;
import com.abajin.innovation.mapper.PersonLibraryMapper;
import com.abajin.innovation.mapper.PersonTypeMapper;
import com.alibaba.excel.EasyExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 人员库服务类
 */
@Service
public class PersonService {

    @Autowired
    private PersonLibraryMapper personLibraryMapper;

    @Autowired
    private PersonTypeMapper personTypeMapper;

    /**
     * 从Excel导入人员
     * @param inputStream Excel文件输入流
     * @return 导入成功的人员数量
     */
    @Transactional
    public int importPersonsFromExcel(InputStream inputStream) {
        PersonImportListener listener = new PersonImportListener();
        EasyExcel.read(inputStream, PersonImportDTO.class, listener).sheet().doRead();
        List<PersonImportDTO> list = listener.getList();

        // 获取所有人员类型，用于根据名称查找类型ID
        List<PersonType> personTypes = personTypeMapper.selectAll();
        Map<String, PersonType> personTypeMap = personTypes.stream()
                .collect(Collectors.toMap(
                        pt -> pt.getName().trim(),
                        pt -> pt,
                        (pt1, pt2) -> pt1
                ));

        int count = 0;
        for (PersonImportDTO row : list) {
            // 检查必填字段
            if (row.getName() == null || row.getName().trim().isEmpty()) {
                throw new RuntimeException("第" + (count + 1) + "行：姓名不能为空");
            }

            // 确定人员类型ID
            Long personTypeId = null;
            String personTypeName = null;
            if (row.getPersonType() != null && !row.getPersonType().trim().isEmpty()) {
                PersonType pt = personTypeMap.get(row.getPersonType().trim());
                if (pt != null) {
                    personTypeId = pt.getId();
                    personTypeName = pt.getName();
                }
            }

            PersonLibrary person = new PersonLibrary();
            person.setName(row.getName().trim());
            person.setPersonTypeId(personTypeId);
            person.setPersonTypeName(personTypeName);
            person.setGender(parseGender(row.getGender()));
            person.setPhone(row.getPhone() != null && !row.getPhone().trim().isEmpty() ? row.getPhone().trim() : null);
            person.setEmail(row.getEmail() != null && !row.getEmail().trim().isEmpty() ? row.getEmail().trim() : null);
            person.setTitle(row.getTitle() != null && !row.getTitle().trim().isEmpty() ? row.getTitle().trim() : null);
            person.setOrganization(row.getOrganization() != null && !row.getOrganization().trim().isEmpty() ? row.getOrganization().trim() : null);
            person.setPosition(row.getPosition() != null && !row.getPosition().trim().isEmpty() ? row.getPosition().trim() : null);
            person.setResearchDirection(row.getResearchDirection() != null && !row.getResearchDirection().trim().isEmpty() ? row.getResearchDirection().trim() : null);
            person.setAchievements(row.getAchievements() != null && !row.getAchievements().trim().isEmpty() ? row.getAchievements().trim() : null);
            person.setIntroduction(row.getIntroduction() != null && !row.getIntroduction().trim().isEmpty() ? row.getIntroduction().trim() : null);
            person.setExpertiseAreas(row.getExpertiseAreas() != null && !row.getExpertiseAreas().trim().isEmpty() ? row.getExpertiseAreas().trim() : null);
            person.setStatus("ACTIVE");
            person.setApprovalStatus("APPROVED");
            person.setCreateTime(LocalDateTime.now());
            person.setUpdateTime(LocalDateTime.now());

            personLibraryMapper.insert(person);
            count++;
        }
        return count;
    }

    /**
     * 解析性别
     */
    private String parseGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            return null;
        }
        String g = gender.trim();
        if ("男".equals(g) || "MALE".equalsIgnoreCase(g) || "M".equalsIgnoreCase(g)) {
            return "MALE";
        }
        if ("女".equals(g) || "FEMALE".equalsIgnoreCase(g) || "F".equalsIgnoreCase(g)) {
            return "FEMALE";
        }
        return null;
    }
}
