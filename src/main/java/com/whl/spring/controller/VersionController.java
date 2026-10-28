package com.whl.spring.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.accept.ApiVersionStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/{version}/user")
public class VersionController {

    /**
     * MVC 配置（@EnableWebMvc）会注册名为 mvcApiVersionStrategy 的 ApiVersionStrategy bean，
     * 这里用构造器注入；原来的裸字段没有任何注解，永远不会被注入（请求时会 NPE）。
     */
    private final ApiVersionStrategy apiVersionStrategy;

    public VersionController(ApiVersionStrategy apiVersionStrategy) {
        this.apiVersionStrategy = apiVersionStrategy;
    }

    /**
     * 精确匹配版本 1
     * Header: X-API-Version:1
     */
    @GetMapping(version = "1")
    public Map<String, Object> getUserV1(HttpServletRequest request) {
        return Map.of(
                "id", 1001,
                "name", "张三",
                "version", currentVersion(request)
        );
    }

    /**
     * 基线版本 1.1+：1.1、1.2、1.3 都会命中这个方法
     * 适合向后兼容，新增字段不破坏老调用方
     */
    @GetMapping(version = "1.1+")
    public Map<String, Object> getUserV1_1Plus(HttpServletRequest request) {
        return Map.of(
                "id", 1001,
                "name", "张三",
                "email", "zhangsan@demo.com",
                "version", currentVersion(request)
        );
    }

    /**
     * 精确匹配版本2，不兼容改动
     */
    @GetMapping(version = "2")
    public Map<String, Object> getUserV2(HttpServletRequest request) {
        return Map.of(
                "userId", 1001,
                "displayName", "张三",
                "phone", "138xxxx0000",
                "version", currentVersion(request)
        );
    }

    /**
     * 返回本次请求实际生效的版本。resolveVersion 只返回请求头里的原始值，
     * 没带请求头时返回 null（此时生效的是 WebConfig 里配置的 defaultVersion），
     * 所以这里做一个兜底，避免 Map.of 因为 null 抛 NPE。
     */
    private String currentVersion(HttpServletRequest request) {
        String version = this.apiVersionStrategy.resolveVersion(request);
        if (version != null) {
            return version;
        }
        Comparable<?> defaultVersion = this.apiVersionStrategy.getDefaultVersion();
        return (defaultVersion != null ? defaultVersion.toString() : null);
    }

}
