package com.fulizhe.ssj.propedit.frontend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.springframework.util.StringUtils;

public class ConfigurationSaveHandler {
    private final String configFilePath;
    private final Properties properties;

    public ConfigurationSaveHandler(String configFilePath) {
        this.configFilePath = configFilePath;
        this.properties = new Properties();
    }

    public void saveConfiguration(Map<String, Object> formData) throws IOException {
        // 将表单数据转换为Properties格式
        formData.forEach((key, value) -> {
            if (value != null) {
                properties.setProperty(key, value.toString());
            }
        });

        // 保存到配置文件
        try (FileOutputStream out = new FileOutputStream(new File(configFilePath))) {
            properties.store(out, "Configuration updated by ConfigToHtmlGenerator");
        }
    }

    public void loadConfiguration() throws IOException {
        File configFile = new File(configFilePath);
        if (configFile.exists()) {
            properties.load(new java.io.FileInputStream(configFile));
        }
    }

    public String getPropertyValue(String key) {
        return properties.getProperty(key);
    }

    public void setPropertyValue(String key, String value) {
        if (StringUtils.hasText(value)) {
            properties.setProperty(key, value);
        } else {
            properties.remove(key);
        }
    }
} 