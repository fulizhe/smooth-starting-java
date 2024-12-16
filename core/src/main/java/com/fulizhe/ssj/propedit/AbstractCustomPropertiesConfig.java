package com.fulizhe.ssj.propedit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 各模块自定义配置文件配置项加载配置
 * <p>
 * 这里存放的是“用户自定义的配置项”
 * 
 * <p>
 * 思路:
 * <p>
 * 对于系统中提供的配置项, 如果使用者有额外的调整需求, 有一个比较好的实践是不进行原地修改，而是提供一个诸如custom.yaml的约定配置文件,
 * 用其中的配置项设置覆盖原始的.
 * <p>
 * 这样的思路遵循了古老的"开闭原则", 在诸如grafana等成熟开源软件中得到广泛应用.
 * <p>
 * 如此好的实践我们没道理不进行学习借鉴.
 **/
@Slf4j
public abstract class AbstractCustomPropertiesConfig {        

    private final String defaultFileRelativePath;

    public static Class<?> mainClass;

    public AbstractCustomPropertiesConfig(String defaultFileRelativePath) {
        super();
        this.defaultFileRelativePath = defaultFileRelativePath;
    }

    String getConfigFile() {
        String dataPath = getCustomBasePath();
        return StrUtil.format("file:{}/ssj-customconfig.properties", dataPath);
    }

    // ========================================================================
    String getCustomBasePath() {
        final String currentDir = currentDir(mainClass);
        log.warn("### current CustomBasePath is [ {} ]", currentDir);
        return currentDir;
    }

    static String currentDir(Class<?> c) {
        ApplicationHome home = new ApplicationHome(c);
        // 单元测试等情况下, 可能为null
        return Optional.ofNullable(home.getSource())//
                .flatMap(s -> Optional.ofNullable(s.getParentFile()))//
                .map(t -> t.getAbsolutePath())//
                .orElse("");
    }

    /**
     * @return
     * 
     * @see {@code ZuulGroovyConfiguration}
     */
    public final Resource getCustomConfigFileResource() {
        String customConfigFile = getConfigFile();

        try {
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(customConfigFile);
            makesureFileExist(resource.getFile());
            return resource;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //
    //
    // public final Resource getCustomConfigFileResource() {
    // String customConfigFile = getConfigFile();
    //
    // try {
    // DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
    // Resource resource = resourceLoader.getResource(customConfigFile);
    // makesureFileExist(resource.getFile());
    // return resource;
    // } catch (IOException e) {
    // throw new RuntimeException(e);
    // }
    // }

    void makesureFileExist(File fileMayoutExist) {
        if (FileUtil.exist(fileMayoutExist)) {
            return;
        }

        //
        String content = StrUtil.EMPTY;
        try {
            content = ResourceUtil.readStr(defaultFileRelativePath, CharsetUtil.CHARSET_UTF_8);
        } catch (NoResourceException e) {
            log.warn("### not found [ {} ]", defaultFileRelativePath);
            log.warn(StrUtil.format("file [ {} ] do not exist. default content will be empty", defaultFileRelativePath),
                    e);
            content = StrUtil.EMPTY;
        }

        FileUtil.writeUtf8String(content, fileMayoutExist);
    }

    // =================================================================================

    /**
     * @return 自定义配置文件的基础存放目录
     */
    public File getConfigFolderPath() {
        return FileUtil.getParent(getConfigFileFullPath(), 1);
    }

    public File getConfigFileFullPath() {
        try {
            return getCustomConfigFileResource().getFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void modifyKV(final String key, final String val) {
        modifyKVBatch(Collections.singletonMap(key, val));
    }

    /**
     * @param key
     *            该key默认已经存在, 只是被注释
     * @param val
     */
    public void modifyKVBatch(final Map<String, String> keyValues) {
        // 相关: zheng中的PropertiesFileUtil.java
        final File customConfigFileFullPath = getConfigFileFullPath();
        doModifyKVBatch(keyValues, customConfigFileFullPath);
    }

    void doModifyKVBatch(final Map<String, String> keyValues, final File customConfigFileFullPath) {
        // 备份
        final String destFilename = StrUtil.format("{}-{}", customConfigFileFullPath.getName(),
                DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN));
        FileUtil.copyContent(customConfigFileFullPath, FileUtil.file(StrUtil
                .replace(customConfigFileFullPath.getAbsolutePath(), customConfigFileFullPath.getName(), destFilename)),
                true);

        final List<String> utf8Lines = FileUtil.readUtf8Lines(customConfigFileFullPath);

        final List<String> finalFileContents = CollUtil.newArrayList();

        final Map<String, String> temp_hasPlaceIntoConfigFile = MapUtil.newHashMap(); // 已經完成写入的配置项
        for (String line : utf8Lines) {
            // Map是否包含该行，如果包含，则替换该行配置，否则直接尾部新增
            final Optional<Entry<String, String>> matchedKeyVal = contain(keyValues, line);

            if (matchedKeyVal.isPresent()) {
                // 該配置項已經存在于當前配置文件中, 故替換掉現有行
                final String key = matchedKeyVal.get().getKey();
                final String val = matchedKeyVal.get().getValue();
                final String newLineContent = StrUtil.format("{}={}", key, val);

                finalFileContents.add(newLineContent);
                //
                temp_hasPlaceIntoConfigFile.put(key, val);
            } else {
                // 原有行
                finalFileContents.add(line);
            }
        }

        // 当前配置文件中不包含的配置项
        finalFileContents.add("");
        Map<String, String> notExistInConfigFile = keyValues.entrySet().stream()
                .filter(entry -> !temp_hasPlaceIntoConfigFile.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        notExistInConfigFile.forEach((k, v) -> {
            final String newLineContent = StrUtil.format("{}={}", k, v);
            finalFileContents.add(newLineContent);
        });

        FileUtil.writeUtf8Lines(finalFileContents, customConfigFileFullPath);
    }

    private Optional<Entry<String, String>> contain(final Map<String, String> currentMap, final String line) {
        return currentMap.entrySet().stream().filter(entry -> {
            final String key = entry.getKey();
            return StrUtil.startWith(line, key + "=") || // 已存在的配置项
            (StrUtil.startWith(line, "#") && StrUtil.contains(line, key + "=")); // 已存在但被注释的配置项
        }).findFirst();
    }

    /**
     * @return 备份文件数量, 其实也就是针对配置文件的操作次数
     */
    public List<String> getFileBackupFilenames() {
        final File customConfigFolderPath = getConfigFolderPath();
        return FileUtil.listFileNames(customConfigFolderPath.getAbsolutePath()).stream()
                .filter(fileName -> FileUtil.extName(fileName).contains("properties-")).collect(Collectors.toList());
    }
}
