package com.whl.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC / Web 配置。
 *
 * <p>原项目用 springmvc.xml 中的 {@code <mvc:annotation-driven/>} 驱动 MVC，
 * 但 XML 命名空间不会回调 {@link WebMvcConfigurer}（Spring 7 的 XML MVC 命名空间已废弃，
 * 也不会跟随 Java 配置模型演进），因此 {@code configureApiVersioning} 永远不会被调用，
 * {@code RequestMappingHandlerMapping} 拿不到 {@code ApiVersionStrategy}，
 * 扫描到 {@code @GetMapping(version = "...")} 时会直接抛：
 * {@code IllegalStateException: API version specified, but no ApiVersionStrategy configured}。
 *
 * <p>修复方式：
 * <ul>
 *   <li>{@code @EnableWebMvc}：引入 {@code DelegatingWebMvcConfiguration}，才会回调本类的
 *       {@link #configureApiVersioning(ApiVersionConfigurer)}；</li>
 *   <li>{@code @ImportResource("classpath:springmvc.xml")}：保留原有 XML 中的 controller 扫描、
 *       静态资源、视图解析器、文件上传等配置。</li>
 * </ul>
 *
 * <p>注意：{@code DispatcherServlet} 通过
 * {@code AnnotationConfigWebApplicationContext} 加载本类（见 WEB-INF/web.xml），
 * 且 {@code applicationContext.xml}（root 容器）已排除 {@code @Configuration}，
 * 避免 MVC 基础设施被重复注册到错误的容器里。
 */
@Configuration
@EnableWebMvc
@ImportResource("classpath:springmvc.xml")
public class WebConfig implements WebMvcConfigurer {

    /**
     * 通过请求头 X-API-Version 解析接口版本，例如：
     * <pre>
     *   X-API-Version: 1    -> VersionController#getUserV1
     *   X-API-Version: 1.2  -> VersionController#getUserV1_1Plus（基线 1.1+）
     *   X-API-Version: 2    -> VersionController#getUserV2
     * </pre>
     *
     * <p>说明：
     * <ul>
     *   <li>{@code setVersionRequired(false)} 很重要：API versioning 一旦启用，默认版本是“全局必填”的，
     *       连 /demo、/ 这些未版本化的接口也会因为缺少 X-API-Version 而 400；设为 false 后
     *       未带版本的请求不再报错；</li>
     *   <li>{@code setDefaultVersion("1")}：未带 X-API-Version 时按版本 1 处理（/api/user 默认命中 v1）；</li>
     *   <li>“支持的版本”默认从 mapping 中的 version 属性收集，但基线 "1.1+" 只会登记 1.1，
     *       所以 1.2 / 1.3 需要显式加入，否则会被判为 400（InvalidApiVersionException）；</li>
     *   <li>如果改用路径段版本（如 /api/user/{version}），把 useRequestHeader 换成
     *       {@code usePathSegment(2)}，并让 mapping 路径包含 {version} 即可。</li>
     * </ul>
     */
    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer.usePathSegment(1)
                .setVersionRequired(false)
                .setDefaultVersion("1");
    }

}
