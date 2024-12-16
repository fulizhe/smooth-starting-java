package com.fulizhe.ssj.propedit;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.bind.annotation.RequestBody;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCustomPropertiesConfigObservabilityController {

  private final AbstractCustomPropertiesConfig config;

  public AbstractCustomPropertiesConfigObservabilityController(AbstractCustomPropertiesConfig config) {
    super();
    this.config = config;
  }

  void update(@RequestBody Map<String, String> body) {
    // 因为前端的限制, key中存在 . 有诸多限制
    // 所以我们作一个约定
    Map<String, String> finalBody = body.entrySet().stream()
        .collect(Collectors.toMap(entry -> entry.getKey().replace("_", "."), Map.Entry::getValue));

    log.warn("### custom config is [ {} ]", finalBody);

    config.modifyKVBatch(finalBody);
  }

  Properties list() {
    try {
      return PropertiesLoaderUtils.loadProperties(config.getCustomConfigFileResource());
    } catch (IOException e) {
      throw ExceptionUtil.wrapRuntime(e);
    }
  }
}
