package com.fulizhe.ssj.sample;

import java.util.Collections;
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

                if (ArrayUtil.contains(
                        new String[] { "/simple-page.css", "/loading.css", "/logo.svg", "/loading.js" },
                        uri)) {
                    if (uri.contains(".svg")) {
                        dc.addHeader("Content-Type", "image/svg+xml");
                    }else if (uri.contains(".js")){
                        dc.addHeader("Content-Type", "application/javascript");
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
                final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${",
                        "}");
                final Map<String, String> map = Collections.singletonMap("elapseTimeBySecond",
                        StrUtil.toString(startedTimeBySecond));
                return propertyPlaceholderHelper.replacePlaceholders(loadingHtml,
                        map::get);
            }
        };
    }
}
