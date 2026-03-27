package com.abajin.innovation.service;

import com.abajin.innovation.dto.PersonImportDTO;
import com.abajin.innovation.entity.PersonLibrary;
import com.abajin.innovation.entity.PersonType;
import com.abajin.innovation.mapper.PersonLibraryMapper;
import com.abajin.innovation.mapper.PersonTypeMapper;
import com.alibaba.excel.EasyExcel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 人员库服务测试
 */
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonLibraryMapper personLibraryMapper;

    @Mock
    private PersonTypeMapper personTypeMapper;

    @InjectMocks
    private PersonService personService;

    // ==================== Excel导入测试 ====================

    @Test
    void importPersonsFromExcel_withEmptyList_returnsZero() throws Exception {
        // Arrange
        when(personTypeMapper.selectAll()).thenReturn(Collections.emptyList());

        // 创建空Excel
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, PersonImportDTO.class).sheet("人员").doWrite(Collections.emptyList());
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Act
        int count = personService.importPersonsFromExcel(inputStream);

        // Assert
        assertEquals(0, count);
        verify(personLibraryMapper, never()).insert(any(PersonLibrary.class));
    }

    @Test
    void importPersonsFromExcel_withValidData_importsSuccessfully() throws Exception {
        // Arrange
        when(personTypeMapper.selectAll()).thenReturn(Collections.emptyList());
        when(personLibraryMapper.insert(any(PersonLibrary.class))).thenReturn(1);

        // 创建测试数据
        PersonImportDTO dto = new PersonImportDTO();
        dto.setName("张三");
        dto.setGender("男");
        dto.setPhone("13800138000");
        dto.setEmail("zhangsan@example.com");
        dto.setTitle("教授");
        dto.setOrganization("天津科技大学");
        dto.setPosition("教师");
        dto.setResearchDirection("人工智能");
        dto.setAchievements("获得国家级奖项");
        dto.setIntroduction("资深教授");
        dto.setExpertiseAreas("AI,机器学习");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, PersonImportDTO.class).sheet("人员").doWrite(Collections.singletonList(dto));
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Act
        int count = personService.importPersonsFromExcel(inputStream);

        // Assert
        assertEquals(1, count);
        verify(personLibraryMapper, times(1)).insert(any(PersonLibrary.class));
    }

    @Test
    void importPersonsFromExcel_withMultipleRows_importsAll() throws Exception {
        // Arrange
        when(personTypeMapper.selectAll()).thenReturn(Collections.emptyList());
        when(personLibraryMapper.insert(any(PersonLibrary.class))).thenReturn(1);

        // 创建多个测试数据
        PersonImportDTO dto1 = new PersonImportDTO();
        dto1.setName("张三");
        dto1.setGender("男");
        dto1.setPhone("13800138000");

        PersonImportDTO dto2 = new PersonImportDTO();
        dto2.setName("李四");
        dto2.setGender("女");
        dto2.setPhone("13900139000");

        PersonImportDTO dto3 = new PersonImportDTO();
        dto3.setName("王五");
        dto3.setGender("MALE");
        dto3.setEmail("wangwu@example.com");

        List<PersonImportDTO> list = new ArrayList<>();
        list.add(dto1);
        list.add(dto2);
        list.add(dto3);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, PersonImportDTO.class).sheet("人员").doWrite(list);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Act
        int count = personService.importPersonsFromExcel(inputStream);

        // Assert
        assertEquals(3, count);
        verify(personLibraryMapper, times(3)).insert(any(PersonLibrary.class));
    }

    @Test
    void importPersonsFromExcel_withPersonType_mappingWorks() throws Exception {
        // Arrange
        PersonType type = new PersonType();
        type.setId(1L);
        type.setName("校内导师");

        when(personTypeMapper.selectAll()).thenReturn(Collections.singletonList(type));
        when(personLibraryMapper.insert(any(PersonLibrary.class))).thenReturn(1);

        PersonImportDTO dto = new PersonImportDTO();
        dto.setName("导师姓名");
        dto.setPersonType("校内导师");
        dto.setGender("男");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, PersonImportDTO.class).sheet("人员").doWrite(Collections.singletonList(dto));
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Act
        int count = personService.importPersonsFromExcel(inputStream);

        // Assert
        assertEquals(1, count);
        verify(personLibraryMapper).insert(argThat(person -> 
            person.getPersonTypeId() != null && person.getPersonTypeId().equals(1L) &&
            "校内导师".equals(person.getPersonTypeName())
        ));
    }

    @Test
    void importPersonsFromExcel_withGenderVariations_parsesCorrectly() throws Exception {
        // Arrange
        when(personTypeMapper.selectAll()).thenReturn(Collections.emptyList());
        when(personLibraryMapper.insert(any(PersonLibrary.class))).thenReturn(1);

        // 测试各种性别格式
        PersonImportDTO dto1 = new PersonImportDTO();
        dto1.setName("人员1");
        dto1.setGender("男");

        PersonImportDTO dto2 = new PersonImportDTO();
        dto2.setName("人员2");
        dto2.setGender("女");

        PersonImportDTO dto3 = new PersonImportDTO();
        dto3.setName("人员3");
        dto3.setGender("MALE");

        PersonImportDTO dto4 = new PersonImportDTO();
        dto4.setName("人员4");
        dto4.setGender("FEMALE");

        PersonImportDTO dto5 = new PersonImportDTO();
        dto5.setName("人员5");
        dto5.setGender("M");

        PersonImportDTO dto6 = new PersonImportDTO();
        dto6.setName("人员6");
        dto6.setGender("F");

        List<PersonImportDTO> list = new ArrayList<>();
        list.add(dto1);
        list.add(dto2);
        list.add(dto3);
        list.add(dto4);
        list.add(dto5);
        list.add(dto6);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, PersonImportDTO.class).sheet("人员").doWrite(list);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Act
        int count = personService.importPersonsFromExcel(inputStream);

        // Assert
        assertEquals(6, count);
    }

    @Test
    void importPersonsFromExcel_setsDefaultStatusAndApprovalStatus() throws Exception {
        // Arrange
        when(personTypeMapper.selectAll()).thenReturn(Collections.emptyList());
        when(personLibraryMapper.insert(any(PersonLibrary.class))).thenReturn(1);

        PersonImportDTO dto = new PersonImportDTO();
        dto.setName("测试人员");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, PersonImportDTO.class).sheet("人员").doWrite(Collections.singletonList(dto));
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Act
        personService.importPersonsFromExcel(inputStream);

        // Assert
        verify(personLibraryMapper).insert(argThat(person -> 
            "ACTIVE".equals(person.getStatus()) && "APPROVED".equals(person.getApprovalStatus())
        ));
    }
}
