package com.fulizhe.ssj.sample;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.PropertyPlaceholderHelper;

import com.fulizhe.ssj.IRequestDealer;
import com.fulizhe.ssj.ISsjExtendProvider;
import com.fulizhe.ssj.IStaticLoadingPageFactory;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

public class ISsjExtendProviderImpl implements ISsjExtendProvider {
    @Override
    public IRequestDealer requestDealer() {
        return new IRequestDealer() {

            @Override
            public void deal(IRequestDealContext dc) {
                final String uri = dc.getUri();

                Console.log("### current uri is [ {} ]", uri);

                if (ArrayUtil.contains(new String[] { "/simple-page.css", "/loading.css", "/snail-loading.svg", "/logo.svg", "/loading.js" },
                        uri)) {
                    if (uri.contains(".svg")) {
                        dc.addHeader("Content-Type", "image/svg+xml");
                    }
                    dc.write(ResourceUtil.readUtf8Str("static" + uri));
                } else if (uri.contains("/inner/lq")) {
                    dc.write("hello-fulizhe-18");
                }
            }

        };
    }

    @Override
    public IStaticLoadingPageFactory staticLoadingPageFactory() {
        return new IStaticLoadingPageFactory() {

            @Override
            public String get(int startedTimeBySecond) {
                String loadingHtml = ResourceUtil.readUtf8Str("static/loading2.html");
                final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");
                Map<String, String> map = new HashMap<>();
                map.put("elapseTimeBySecond", StrUtil.toString(startedTimeBySecond));
                map.put("os", System.getProperty("os.name"));
                map.put("memory", Runtime.getRuntime().totalMemory() / 1024 / 1024 + "MB");
                map.put("arch", System.getProperty("os.arch")); 
                map.put("cpuCores", Runtime.getRuntime().availableProcessors() + "");
                map.put("jdkVersion", System.getProperty("java.version"));
                map.put("timezone", System.getProperty("user.timezone"));
                map.put("jdkVendor", System.getProperty("java.vendor"));
                map.put("encoding", System.getProperty("file.encoding"));
                map.put("workDir", System.getProperty("user.dir"));
                map.put("username", System.getProperty("user.name"));
                map.put("status", "正在启动中...");
                return propertyPlaceholderHelper.replacePlaceholders(loadingHtml, map::get);
            }
        };
    }
}
