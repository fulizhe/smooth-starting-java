package com.fulizhe.ssj.propedit;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import cn.hutool.core.exceptions.ExceptionUtil;

/**
 * <p>
 * 本扩展的目的是因为有些配置项(例如 {@code logging.level.root})不会生效
 * <p>
 * 最佳实践应该是将配置项进行拆分, 将以上无法生效的配置放置到专门的配置文件中, 然后放置在SpringBoot的默认配置加载规则约定的位置
 * <p>
 * 在 {@link http://{ip}:{port}/kqgis/mnt/env} 中可以看到
 * 
 * @author LQ
 *
 */
public class OverridePropertiesPropertySourceEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

  private int order = SystemEnvironmentPropertySourceEnvironmentPostProcessor.DEFAULT_ORDER - 1;

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
      application.getMainApplicationClass();
    AbstractCustomPropertiesConfig.mainClass = application.getMainApplicationClass();
      
    final Resource customOverrideSpringBooConfigFileResource = OverridePropertiesConfig
        .getOverrideSpringBootPropertiesConfigFileResource();
    Map<String, ?> properties = loadProperties(customOverrideSpringBooConfigFileResource);
    MapPropertySource mapPropertySource = new MapPropertySource("customOvrrideProperties",
        Collections.unmodifiableMap(properties));
    environment.getPropertySources().addFirst(mapPropertySource);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private Map<String, ?> loadProperties(Resource resource) {
    try {
      return (Map) PropertiesLoaderUtils.loadProperties(resource);
    } catch (IOException e) {
      throw ExceptionUtil.wrapRuntime(e);
    }
  }

  @Override
  public int getOrder() {
    return this.order;
  }

  public void setOrder(int order) {
    this.order = order;
  }
}
