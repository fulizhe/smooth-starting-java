package com.fulizhe.ssj;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.StrUtil;
import com.fulizhe.ssj.miniwebserver.MiniTomcatServer;
import com.fulizhe.ssj.miniwebserver.MiniUndertowServer;
import org.springframework.core.io.Resource;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Collections;
import java.util.Map;

public interface IStaticLoadingPageFactory {
    /**
     * @param startedTimeBySecond 已启动耗时
     * @return loading页面内容
     */
    String get(int startedTimeBySecond);

    static class Instance implements  IStaticLoadingPageFactory {

        @Override
        public String get(int startedTimeBySecond) {
           String loadingHtml = ResourceUtil.readStr("static/loading.html", CharsetUtil.CHARSET_UTF_8);
            final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${",
                    "}");
            final Map<String, String> map = Collections.singletonMap("elapseTimeBySecond",
                    StrUtil.toString(startedTimeBySecond));
            return propertyPlaceholderHelper.replacePlaceholders(loadingHtml,
                    map::get);
        }

        private static final IStaticLoadingPageFactory instanceInner = new Instance();

        public static IStaticLoadingPageFactory instance(){
          return  instanceInner;
        }
    }
}