package com.fulizhe.ssj.propedit.frontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigurationController {

    private final ConfigurationSaveHandler saveHandler;
    private final ConfigToHtmlGenerator htmlGenerator;

    public ConfigurationController(
            @Value("${config.file.path:META-INF/propedit2/config.properties}") String configFilePath) {
        this.saveHandler = new ConfigurationSaveHandler(configFilePath);
        this.htmlGenerator = new ConfigToHtmlGenerator();
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveConfiguration(@RequestBody Map<String, Object> formData) {
        try {
            saveHandler.saveConfiguration(formData);
            return ResponseEntity.ok("配置已成功保存");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("保存配置时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generateConfigurationPage(
            @RequestParam String jsonConfigPath,
            @RequestParam String outputPath,
            @RequestParam(required = false) String customTemplatePath) {
        try {
            htmlGenerator.generate(jsonConfigPath, outputPath, customTemplatePath);
            return ResponseEntity.ok("配置页面已生成");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("生成配置页面时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/load")
    public ResponseEntity<?> loadConfiguration() {
        try {
            saveHandler.loadConfiguration();
            return ResponseEntity.ok("配置已加载");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("加载配置时发生错误: " + e.getMessage());
        }
    }
} 