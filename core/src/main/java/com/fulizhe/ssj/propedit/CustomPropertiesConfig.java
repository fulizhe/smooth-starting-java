/*
 * Copyright (C) 2019 KQ GEO Technologies Co., Ltd.
 * All rights reserved.
 */
package com.fulizhe.ssj.propedit;

import cn.hutool.core.util.StrUtil;

/**
 * @author LQ
 * 
 * <p> 原理参见：
 * {@link http://oa.kqgeo.com:1890/pages/viewpage.action?pageId=61383194&focusedCommentId=61383257#comment-61383257}
 */
public class CustomPropertiesConfig extends AbstractCustomPropertiesConfig {

  public CustomPropertiesConfig() {
    super("META-INF/ssj-customconfig.properties");
  }

  @Override
  String getConfigFile() {
    String dataPath = getCustomBasePath();
    return StrUtil.format("file:{}/ssj-customconfig.properties", dataPath);
  }

}
