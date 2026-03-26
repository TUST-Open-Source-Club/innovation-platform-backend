package com.abajin.innovation.controller;

import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.PageResult;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.entity.PersonLibrary;
import com.abajin.innovation.mapper.PersonLibraryMapper;
import com.abajin.innovation.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 人员库控制器
 */
@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    private PersonLibraryMapper personLibraryMapper;

    @Autowired
    private PersonService personService;

    /**
     * 分页查询人员列表
     * GET /api/persons?pageNum=1&pageSize=10&personTypeId=1&keyword=xxx
     */
    @GetMapping
    public Result<PageResult<PersonLibrary>> getPersons(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) Long personTypeId,
            @RequestParam(required = false) String keyword) {
        try {
            int offset = (pageNum - 1) * pageSize;
            var list = personLibraryMapper.selectPage(offset, pageSize, personTypeId, keyword);
            long total = personLibraryMapper.count(personTypeId, keyword);
            var pageResult = PageResult.of(pageNum, pageSize, total, list);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询人员详情
     * GET /api/persons/{id}
     */
    @GetMapping("/{id}")
    public Result<PersonLibrary> getPersonById(@PathVariable Long id) {
        try {
            PersonLibrary person = personLibraryMapper.selectById(id);
            if (person == null) {
                return Result.error(404, "人员不存在");
            }
            return Result.success(person);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 添加人员（仅学院管理员、学校管理员）
     * POST /api/persons
     */
    @PostMapping
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN})
    public Result<PersonLibrary> createPerson(@RequestBody PersonLibrary person) {
        try {
            if (person.getPersonTypeId() == null || person.getName() == null || person.getName().isBlank()) {
                return Result.error("人员类型和姓名为必填项");
            }
            if (person.getApprovalStatus() == null || person.getApprovalStatus().isEmpty()) {
                person.setApprovalStatus("APPROVED");
            }
            if (person.getStatus() == null || person.getStatus().isEmpty()) {
                person.setStatus("ACTIVE");
            }
            personLibraryMapper.insert(person);
            return Result.success("添加成功", person);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新人员（仅学院管理员、学校管理员）
     * PUT /api/persons/{id}
     */
    @PutMapping("/{id}")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN})
    public Result<PersonLibrary> updatePerson(@PathVariable Long id, @RequestBody PersonLibrary person) {
        try {
            PersonLibrary existing = personLibraryMapper.selectById(id);
            if (existing == null) {
                return Result.error(404, "人员不存在");
            }
            person.setId(id);
            personLibraryMapper.update(person);
            PersonLibrary updated = personLibraryMapper.selectById(id);
            return Result.success("更新成功", updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除人员（仅学院管理员、学校管理员）
     * DELETE /api/persons/{id}
     */
    @DeleteMapping("/{id}")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN})
    public Result<Void> deletePerson(@PathVariable Long id) {
        try {
            PersonLibrary existing = personLibraryMapper.selectById(id);
            if (existing == null) {
                return Result.error(404, "人员不存在");
            }
            personLibraryMapper.deleteById(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传 Excel 批量导入人员（学院/学校管理员）
     * POST /api/persons/import
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Integer> importPersonsExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("请选择 Excel 文件");
            }
            String name = file.getOriginalFilename();
            if (name == null || (!name.endsWith(".xlsx") && !name.endsWith(".xls"))) {
                return Result.error("请上传 .xlsx 或 .xls 格式的 Excel 文件");
            }
            int count = personService.importPersonsFromExcel(file.getInputStream());
            return Result.success("成功导入 " + count + " 个人员", count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
