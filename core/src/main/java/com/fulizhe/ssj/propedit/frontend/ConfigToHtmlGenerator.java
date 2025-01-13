package com.fulizhe.ssj.propedit.frontend;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;

public class ConfigToHtmlGenerator {
    private final ObjectMapper mapper;
    private final TemplateEngine templateEngine;
    private String defaultTemplate;

    public ConfigToHtmlGenerator() {
        this.mapper = new ObjectMapper();
        this.templateEngine = new TemplateEngine();
        
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine.setTemplateResolver(resolver);
        
        try {
            Resource resource = new ClassPathResource("META-INF/propedit/template.html");
            this.defaultTemplate = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("无法加载默认模板", e);
        }
    }

    public void generate(String jsonConfigPath, String outputPath) throws IOException {
        generate(jsonConfigPath, outputPath, null);
    }

    public void generate(String jsonConfigPath, String outputPath, String customTemplatePath) throws IOException {
        // 1. 读取并解析JSON配置
        JsonNode rootNode = mapper.readTree(new File(jsonConfigPath));
        
        // 2. 准备模板数据
        Map<String, Object> templateData = new HashMap<>();
        List<Map<String, Object>> steps = new ArrayList<>();
        
        JsonNode stepsNode = rootNode.get("steps");
        for (JsonNode stepNode : stepsNode) {
            Map<String, Object> step = new HashMap<>();
            step.put("title", stepNode.get("title").asText());
            
            List<Map<String, Object>> fields = new ArrayList<>();
            for (JsonNode fieldNode : stepNode.get("fields")) {
                Map<String, Object> field = new HashMap<>();
                field.put("type", fieldNode.get("type").asText());
                field.put("id", fieldNode.get("id").asText());
                field.put("name", fieldNode.get("name").asText());
                
                if (fieldNode.has("default")) {
                    field.put("default", fieldNode.get("default").asText());
                }
                
                if (fieldNode.has("options")) {
                    List<String> options = new ArrayList<>();
                    fieldNode.get("options").forEach(option -> options.add(option.asText()));
                    field.put("options", options);
                }
                
                fields.add(field);
            }
            step.put("fields", fields);
            steps.add(step);
        }
        templateData.put("steps", steps);
        
        // 3. 获取模板内容
        String templateContent;
        if (customTemplatePath != null) {
            templateContent = FileUtil.readString(customTemplatePath, CharsetUtil.CHARSET_UTF_8);
        } else {
            templateContent = defaultTemplate;
        }
        
        // 4. 渲染模板
        Context context = new Context();
        context.setVariables(templateData);
        String renderedHtml = templateEngine.process(templateContent, context);
        
        // 5. 确保输出目录存在
        Path outputDir = Paths.get(outputPath).getParent();
        if (outputDir != null) {
            Files.createDirectories(outputDir);
        }
        
        // 6. 写入生成的HTML
        FileUtil.writeString(renderedHtml,outputPath, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws IOException {
        ConfigToHtmlGenerator generator = new ConfigToHtmlGenerator();
        
        // 使用默认模板生成
        generator.generate(
            "core/src/main/resources/META-INF/propedit/props.json",
            "core/src/main/resources/META-INF/propedit2/config.html"
        );
    }
}