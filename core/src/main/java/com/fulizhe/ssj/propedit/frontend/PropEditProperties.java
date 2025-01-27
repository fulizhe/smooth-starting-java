package com.fulizhe.ssj.propedit.frontend;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "ssj.propedit")
public class PropEditProperties {
    
    /**
     * 是否启用配置页面生成功能
     */
    private boolean enabled = true;

    /**
     * JSON配置文件路径，相对于classpath
     */
    private String jsonConfigPath = "META-INF/propedit/template.json";

    /**
     * 自定义模板路径，相对于classpath
     */
    private String customTemplatePath;

    /**
     * 生成的HTML页面访问路径
     */
    private String pageUrlPath = "/propedit/config";

    /**
     * 生成的HTML文件存放路径，相对于classpath
     */
    private String outputPath = "META-INF/propedit/propedit.html";
} 