package com.abajin.innovation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CAS配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "cas")
public class CasConfig {
    /**
     * 是否启用CAS功能
     */
    private Boolean enabled = false;

    /**
     * CAS服务器地址前缀
     */
    private String serverUrlPrefix;

    /**
     * CAS服务器登录地址
     */
    private String serverLoginUrl;

    /**
     * 本应用地址
     */
    private String clientHostUrl;

    /**
     * Mock模式（用于本地测试，无需真实CAS服务器）
     */
    private Boolean mockMode = false;
}
