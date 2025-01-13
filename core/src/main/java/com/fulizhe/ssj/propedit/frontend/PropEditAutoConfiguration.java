package com.fulizhe.ssj.propedit.frontend;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(PropEditProperties.class)
@ConditionalOnProperty(prefix = "ssj.propedit", name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class PropEditAutoConfiguration {

    private final PropEditProperties properties;

    public PropEditAutoConfiguration(PropEditProperties properties) {
        this.properties = properties;
    }

    @Bean
    public WebMvcConfigurer propEditWebMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler(properties.getPageUrlPath() + "/**")
                        .addResourceLocations("classpath:/META-INF/propedit2/");
            }
        };
    }

    @Bean
    public ConfigToHtmlGenerator configToHtmlGenerator() throws IOException {
        ConfigToHtmlGenerator configToHtmlGenerator = new ConfigToHtmlGenerator();

        log.info("初始化配置页面生成器...");
        generateConfigPage(configToHtmlGenerator);

        return configToHtmlGenerator;
    }

    @Bean
    public ConfigurationController configurationController() {
        return new ConfigurationController(properties.getOutputPath());
    }

    private void generateConfigPage(ConfigToHtmlGenerator generator) throws IOException {
        // 获取JSON配置文件路径
        ClassPathResource jsonResource = new ClassPathResource(properties.getJsonConfigPath());
        String jsonPath = jsonResource.getFile().getAbsolutePath();

        // 确保输出目录存在
        String outputDir = new File(properties.getOutputPath()).getParent();
        if (outputDir != null) {
            new File(outputDir).mkdirs();
        }

        // 生成配置页面
        if (properties.getCustomTemplatePath() != null) {
            ClassPathResource templateResource = new ClassPathResource(properties.getCustomTemplatePath());
            generator.generate(jsonPath, properties.getOutputPath(), templateResource.getFile().getAbsolutePath());
        } else {
            generator.generate(jsonPath, properties.getOutputPath());
        }

        log.info("配置页面已生成，访问路径: {}", properties.getPageUrlPath());
    }
}