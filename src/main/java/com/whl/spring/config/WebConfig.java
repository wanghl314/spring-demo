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
     * 通过 URL 路径段解析接口版本，例如：
     * <pre>
     *   /api/v1/user    -> VersionController#getUserV1
     *   /api/v1.2/user  -> VersionController#getUserV1_1Plus（基线 1.1+）
     *   /api/v2/user    -> VersionController#getUserV2
     * </pre>
     *
     * <p>两个关键点（缺一个就会出现 400）：
     * <ol>
     *   <li><b>受支持版本列表</b>：默认只把 mapping 里出现过的版本当作受支持版本，
     *       而基线 {@code version = "1.1+"} 只会登记 1.1，{@code "1"} 和 {@code "2"} 各登记 1.0 / 2.0，
     *       所以 /api/v1.2/user、/api/v1.3/user 会被判为 {@code InvalidApiVersionException} → 400。
     *       这里用 {@code addSupportedVersions(...)} 显式声明 1.2 / 1.3；</li>
     *   <li><b>路径谓词</b>：{@code usePathSegment(index)} 不带谓词时会把<b>所有</b>请求路径都当作“版本化路径”，
     *       于是 /spring/、/spring/demo/test 这类非版本化 URL 会把第 2 段（如 "test"）当版本解析 → 400。
     *       加上 {@code path -> path.pathWithinApplication().value().startsWith("/api/")} 后，
     *       只有 /api/** 才解析版本，其他接口恢复正常。</li>
     * </ol>
     *
     * <p>另外，版本段里的 "v" 前缀不影响解析：{@code SemanticApiVersionParser} 会跳过开头的非数字字符，
     * 所以 "v1.2" 等价于 "1.2"。
     */
    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer.usePathSegment(1, path -> path.pathWithinApplication().value().startsWith("/api/"))
                .setVersionRequired(false)
                .setDefaultVersion("1")
                .addSupportedVersions("1", "1.1", "1.2", "1.3", "2");
    }

}
