package com.fulizhe.ssj.propedit;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

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
@Configuration
class CustomPropertiesConfiguration {

  @Bean
  public CustomPropertiesConfig customPropertiesConfig(){
      return new CustomPropertiesConfig();
  }
    
  /**
   * <p>
   * 这里注入的配置只能使用{@code @Value(${})} 读取到 加载属性配置
   *
   * <p>
   * 1. WARN: 自定义配置项放在这里没有问题, 但是如果是SpringBoot内置的配置项放置在这里将无法生效 2. WARN:
   * {@code @ConfigurationProperties}方式可以读取到自定义配置项. 范例：
   * {@code WebSocketProxyProperties}
   *
   * @param environment
   * 环境属性
   * 
   * @description 加载属性配置
   */
  @Bean
  public PropertySourcesPlaceholderConfigurer customPropertySourcesPlaceholderConfig(Environment environment,
      CustomPropertiesConfig customPropertiesConfig) throws IOException {
    final PropertySourcesPlaceholderConfigurer config = new PropertySourcesPlaceholderConfigurer();
    final Resource customConfigFileResource = customPropertiesConfig.getCustomConfigFileResource();
    log.warn("### the customconfig file path in module [ ssj ] is [ {} ]", 
        customPropertiesConfig.getConfigFileFullPath());
    config.setLocations(new Resource[]{customConfigFileResource});
    config.setLocalOverride(true);
    return config;
  }

}
