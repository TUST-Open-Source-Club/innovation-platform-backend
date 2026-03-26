package com.abajin.innovation.controller;

import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.PageResult;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.converter.LoginUserDTOConverter;
import com.abajin.innovation.dto.CreateUserDTO;
import com.abajin.innovation.dto.LoginUserDTO;
import com.abajin.innovation.dto.UserQueryDTO;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 用户管理控制器
 * 包含当前用户相关接口和管理员用户管理接口
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户信息
     * GET /api/users/me
     * 用于页面刷新后恢复用户信息和角色
     */
    @GetMapping("/me")
    public Result<LoginUserDTO> getCurrentUser(@RequestAttribute("userId") Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(LoginUserDTOConverter.convert(user));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改当前用户密码
     * PUT /api/users/me/password
     * 请求体：{ "oldPassword": "原密码", "newPassword": "新密码" }
     */
    @PutMapping("/me/password")
    public Result<Void> changePassword(
            @RequestBody Map<String, String> body,
            @RequestAttribute("userId") Long userId) {
        try {
            String oldPassword = body.get("oldPassword");
            String newPassword = body.get("newPassword");
            userService.changePassword(userId, oldPassword, newPassword);
            return Result.success("密码修改成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户列表（管理员功能）
     * GET /api/users
     */
    @GetMapping
    public Result<PageResult<User>> getUserList(UserQueryDTO queryDTO) {
        try {
            PageResult<User> result = userService.getUserList(queryDTO);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员创建用户
     * POST /api/users
     */
    @PostMapping
    public Result<User> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        try {
            User user = userService.createUser(createUserDTO);
            return Result.success("创建用户成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户详情
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            userService.updateUser(id, user);
            return Result.success("更新用户成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户状态
     * PUT /api/users/{id}/status
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        try {
            Integer status = body.get("status");
            if (status == null || (status != 0 && status != 1)) {
                return Result.error("状态值无效，只能是0或1");
            }
            userService.updateUserStatus(id, status);
            return Result.success("状态更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 重置用户密码
     * PUT /api/users/{id}/password/reset
     */
    @PutMapping("/{id}/password/reset")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String newPassword = body.get("newPassword");
            userService.resetPassword(id, newPassword);
            return Result.success("密码重置成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success("删除用户成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传 Excel 批量导入用户（学院/学校管理员）
     * POST /api/users/import
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Integer> importUsersExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("请选择 Excel 文件");
            }
            String name = file.getOriginalFilename();
            if (name == null || (!name.endsWith(".xlsx") && !name.endsWith(".xls"))) {
                return Result.error("请上传 .xlsx 或 .xls 格式的 Excel 文件");
            }
            int count = userService.importUsersFromExcel(file.getInputStream());
            return Result.success("成功导入 " + count + " 个用户", count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
