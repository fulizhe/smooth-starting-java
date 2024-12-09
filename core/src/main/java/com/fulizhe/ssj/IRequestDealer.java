package com.fulizhe.ssj;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fulizhe.ssj.requestdealer.TomcatRequestDealContext;
import com.fulizhe.ssj.requestdealer.UndertowRequestDealContext;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import io.undertow.server.HttpServerExchange;

public interface IRequestDealer {
    void deal(IRequestDealContext dc);

    static class Factory {
        public static IRequestDealer get() {
            return new IRequestDealer() {
                @Override
                public void deal(IRequestDealContext dc) {
                    String requestURI = dc.getUri();
                    if (StrUtil.isNotEmpty(requestURI) && requestURI.contains("/inner/start")) {
                        Console.log("### 继续容器的启动流程");

                        dc.addHeader("Content-Type", "text/html");
                        dc.write("loading");
                    } else if ((StrUtil.isNotEmpty(requestURI) && requestURI.contains("/inner/config"))) {
                        Console.log("### 接收用户提交的配置项, 进行持久化");

                    }
                }
            };
        }
    }

    static interface IRequestDealContext {

        String getUri();

        String getParameter(String key);

        void addHeader(String headerName, String headerValue);

        void write(final String body);

        boolean isDealed();
    }

    static class IRequestDealContextFactory {
        public static IRequestDealContext getUndertowContext(HttpServerExchange exchange) {
            return new UndertowRequestDealContext(exchange);
        }

        public static IRequestDealContext getTomcatContext(HttpServletRequest request, HttpServletResponse response) {
            return new TomcatRequestDealContext(request, response);
        }
    }
}