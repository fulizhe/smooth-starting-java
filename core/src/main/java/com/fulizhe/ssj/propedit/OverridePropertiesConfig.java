package com.fulizhe.ssj.propedit;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;

/**
 * <p>
 * 这里存放的是“系统内置配置项”. 这里的修改将覆盖既有配置项
 * 
 * <p> 原理参见：
 * {@link http://oa.kqgeo.com:1890/pages/viewpage.action?pageId=61383194&focusedCommentId=61383257#comment-61383257}
 **/
@Component
public class OverridePropertiesConfig extends AbstractCustomPropertiesConfig {

  public OverridePropertiesConfig() {
    super("META-INF/ssj-application-override.properties");
  }

  @Override
  String getConfigFile() {      
    String dataPath = getCustomBasePath();
    return StrUtil.format("file:{}/ssj-application-override.properties", dataPath);

  }

//  public static File getOverrideSpringBootPropertiesFileFullPath() {
//    final OverridePropertiesConfig customPropertiesConfig = cn.hutool.extra.spring.SpringUtil
//        .getBean(OverridePropertiesConfig.class);
//    return customPropertiesConfig.getConfigFileFullPath();
//  }

  public static Resource getOverrideSpringBootPropertiesConfigFileResource() {
    // final OverridePropertiesConfig customPropertiesConfig =
    // cn.hutool.extra.spring.SpringUtil
    // .getBean(OverridePropertiesConfig.class);
    // 這裏調用時機太早 
    return new OverridePropertiesConfig().getCustomConfigFileResource();
  }
  //
  // public static void modifyKVInOverrideConfig(final String key, final String
  // val) {
  // modifyKVBatchInOverrideConfig(Collections.singletonMap(key, val));
  // }
  //
  // public static void modifyKVBatchInOverrideConfig(final Map<String, String>
  // keyValues) {
  // CustomPropertiesConfig.doModifyKVBatch(keyValues,
  // getOverrideSpringBootPropertiesFileFullPath());
  // }
}
