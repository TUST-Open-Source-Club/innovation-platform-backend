package com.abajin.innovation.controller;

import com.abajin.innovation.common.Result;
import com.abajin.innovation.config.CasConfig;
import com.abajin.innovation.dto.CasLoginResponse;
import com.abajin.innovation.dto.CasMergeRequest;
import com.abajin.innovation.dto.CompleteProfileDTO;
import com.abajin.innovation.dto.LoginUserDTO;
import com.abajin.innovation.service.CasService;
import com.abajin.innovation.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * CAS统一身份认证Controller
 */
@RestController
@RequestMapping("/auth/cas")
@Slf4j
public class CasAuthController {

    @Autowired
    private CasConfig casConfig;

    @Autowired
    private CasService casService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取CAS功能状态
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> getStatus() {
        Map<String, Object> data = new HashMap<>();
        boolean enabled = casService.isCasEnabled();
        data.put("enabled", enabled);
        if (enabled) {
            data.put("loginUrl", casConfig.getServerLoginUrl());
            data.put("mockMode", casConfig.getMockMode());
        }
        return Result.success(data);
    }

    /**
     * 发起CAS登录
     * 重定向到CAS服务器登录页面
     */
    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        if (!casService.isCasEnabled()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CAS功能未启用");
            return;
        }

        // Mock模式：直接重定向到前端回调页面，带上mock ticket
        if (casConfig.getMockMode()) {
            // 使用拼音避免中文编码问题
            String mockTicket = "MOCK-2021001-ZhangSan";
            String redirectUrl = casConfig.getClientHostUrl().replace("/api", "") + 
                "/cas-callback?ticket=" + mockTicket;
            log.info("[CAS Mock] 重定向到回调页面: {}", redirectUrl);
            response.sendRedirect(redirectUrl);
            return;
        }

        // 构造回调地址
        String serviceUrl = casConfig.getClientHostUrl() + "/auth/cas/validate";
        String encodedServiceUrl = URLEncoder.encode(serviceUrl, StandardCharsets.UTF_8);

        // 重定向到CAS登录页面
        String casLoginUrl = casConfig.getServerLoginUrl() + "?service=" + encodedServiceUrl;
        log.info("[CAS] 重定向到CAS登录页面: {}", casLoginUrl);
        response.sendRedirect(casLoginUrl);
    }

    /**
     * CAS回调验证ticket
     * CAS服务器会重定向到这个地址，并携带ticket参数
     */
    @GetMapping("/validate")
    public Result<CasLoginResponse> validate(@RequestParam("ticket") String ticket) {
        log.info("[CAS] 收到ticket验证请求");
        try {
            if (!casService.isCasEnabled()) {
                log.warn("[CAS] 功能未启用");
                return Result.error("CAS功能未启用");
            }

            // 构造service URL（必须与login时的一致）
            String serviceUrl = casConfig.getClientHostUrl() + "/auth/cas/validate";

            // 验证ticket并处理登录
            CasLoginResponse response = casService.validateTicketAndLogin(ticket, serviceUrl);
            
            if (response.getNeedMerge() != null && response.getNeedMerge()) {
                log.info("[CAS] 检测到同名账号，需要合并: casUid={}, realName={}", 
                    response.getCasUid(), response.getCasName());
            } else if (response.getNeedCompleteProfile() != null && response.getNeedCompleteProfile()) {
                log.info("[CAS] 新用户登录，需要完善资料: userId={}", 
                    response.getUser() != null ? response.getUser().getId() : "unknown");
            } else {
                log.info("[CAS] 用户登录成功: userId={}", 
                    response.getUser() != null ? response.getUser().getId() : "unknown");
            }

            return Result.success(response);
        } catch (Exception e) {
            log.error("[CAS] 登录失败: {}", e.getMessage(), e);
            return Result.error("CAS登录失败: " + e.getMessage());
        }
    }

    /**
     * 合并本地账号
     * 当检测到同名本地账号时，用户可以选择合并
     */
    @PostMapping("/merge")
    public Result<CasLoginResponse> mergeAccount(@Valid @RequestBody CasMergeRequest request) {
        log.info("[CAS] 账号合并请求: casUid={}, realName={}", request.getCasUid(), request.getRealName());
        try {
            if (!casService.isCasEnabled()) {
                return Result.error("CAS功能未启用");
            }

            CasLoginResponse response = casService.mergeAccountWithRealName(
                request.getCasUid(), 
                request.getRealName(), 
                request.getPassword()
            );
            
            log.info("[CAS] 账号合并成功: userId={}", 
                response.getUser() != null ? response.getUser().getId() : "unknown");
            return Result.success(response);

        } catch (Exception e) {
            log.error("[CAS] 账号合并失败: {}", e.getMessage());
            return Result.error("账号合并失败: " + e.getMessage());
        }
    }

    /**
     * 创建新账号（跳过合并）
     * 当用户选择不合并同名账号时，创建一个新的CAS账号
     */
    @PostMapping("/create-new")
    public Result<CasLoginResponse> createNewAccount(
            @RequestParam("casUid") String casUid,
            @RequestParam("realName") String realName) {
        log.info("[CAS] 创建新账号请求: casUid={}, realName={}", casUid, realName);
        try {
            if (!casService.isCasEnabled()) {
                return Result.error("CAS功能未启用");
            }

            CasLoginResponse response = casService.createNewAccountWithoutMerge(casUid, realName);
            
            log.info("[CAS] 新账号创建成功: userId={}", 
                response.getUser() != null ? response.getUser().getId() : "unknown");
            return Result.success(response);

        } catch (Exception e) {
            log.error("[CAS] 创建账号失败: {}", e.getMessage());
            return Result.error("创建账号失败: " + e.getMessage());
        }
    }

    /**
     * 完善用户资料
     * CAS新用户首次登录后需要完善邮箱、手机号、学院等信息
     */
    @PostMapping("/complete-profile")
    public Result<Map<String, Object>> completeProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CompleteProfileDTO dto) {
        log.info("[CAS] 完善资料请求");
        try {
            if (!casService.isCasEnabled()) {
                return Result.error("CAS功能未启用");
            }

            // 从token中获取用户ID
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.error("无效的认证头");
            }
            
            String token = authHeader.substring(7); // 移除 "Bearer "
            Long userId = jwtUtil.getUserIdFromToken(token);

            if (userId == null) {
                return Result.error("无效的token");
            }

            casService.completeProfile(userId, dto);
            
            log.info("[CAS] 资料完善成功: userId={}", userId);
            
            // 返回更新后的用户信息
            Map<String, Object> result = new HashMap<>();
            result.put("message", "资料完善成功");
            result.put("profileComplete", true);
            
            return Result.success(result);

        } catch (Exception e) {
            log.error("[CAS] 资料完善失败: {}", e.getMessage());
            return Result.error("资料完善失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否需要完善资料
     */
    @GetMapping("/check-profile")
    public Result<Map<String, Object>> checkProfile(
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.error("无效的认证头");
            }
            
            String token = authHeader.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);

            if (userId == null) {
                return Result.error("无效的token");
            }

            // 获取用户信息检查是否需要完善资料
            Map<String, Object> result = new HashMap<>();
            // 这里可以通过UserService查询用户信息
            result.put("needComplete", false); // 默认值，实际需要查询数据库
            
            return Result.success(result);

        } catch (Exception e) {
            log.error("[CAS] 检查资料状态失败: {}", e.getMessage());
            return Result.error("检查失败: " + e.getMessage());
        }
    }

    /**
     * CAS单点登出回调
     * CAS服务器在用户登出时会调用此接口
     */
    @PostMapping("/logout")
    public Result<Void> casLogout(@RequestBody(required = false) Map<String, String> body) {
        log.info("[CAS] 收到单点登出请求");
        try {
            // 这里可以实现token黑名单等登出逻辑
            // 实际实现中可能需要将token加入黑名单
            return Result.success("登出成功", null);
        } catch (Exception e) {
            log.error("[CAS] 登出处理失败: {}", e.getMessage());
            return Result.error("登出失败: " + e.getMessage());
        }
    }
}
