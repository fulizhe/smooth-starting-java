package com.fulizhe.ssj.miniwebserver;

import java.util.Collections;
import java.util.Map;

import org.springframework.util.PropertyPlaceholderHelper;

import com.fulizhe.ssj.IMiniWebServer;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class MiniUndertowServer implements IMiniWebServer {

    Undertow minimalUndertowserver;

    private static final long startTime = System.currentTimeMillis();

    private final MySharedLock mySharedLock = new MySharedLock();

    @Override
    public void start(int port) {
        final String loadingHtml = ResourceUtil.readStr("static/loading.html", CharsetUtil.CHARSET_UTF_8);
        // Start the minimal Undertow server
        minimalUndertowserver = Undertow.builder().addHttpListener(port, "0.0.0.0").setHandler(new HttpHandler() {

            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                // === XNIO-1-I/O thread
                //
                String requestURI = exchange.getRequestURI();
                String requestURL = exchange.getRequestURL();
                Console.log("### ========== \n requestURI: {} \n requestURL: {} ", requestURI, requestURL);

                //
                if (StrUtil.isNotEmpty(requestURI) && requestURI.contains("/inner/start")) {
                    Console.log("### 继续undertow容器的启动流程");
                    mySharedLock.notifyMainThread();

                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                    exchange.getResponseSender().send("loading");
                } else if ((StrUtil.isNotEmpty(requestURI) && requestURI.contains("/inner/config"))) {
                    Console.log("### 接收用户提交的配置项, 进行持久化");

                } else {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");

                    long currentTime = System.currentTimeMillis();
                    long secondsPassed = (currentTime - startTime) / 1000;
                    Console.log("### elapseTimeBySecond: [ {} ]s", secondsPassed);
                    final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${",
                            "}");
                    final Map<String, String> map = Collections.singletonMap("elapseTimeBySecond",
                            StrUtil.toString(secondsPassed));
                    final String loadingHtmlFinal = propertyPlaceholderHelper.replacePlaceholders(loadingHtml,
                            map::get);
                    exchange.getResponseSender().send(loadingHtmlFinal);
                }

            }
        }).build();

        minimalUndertowserver.start();

        // === main thread
        // 这里阻塞主线程, 此时minimal-Undertow-Server已经启动了, 所以可以正常响应外界请求——界面化修改配置项.
        // 释放锁在下方的minimal-Undertow-Server响应里
        // TODO 以下功能待启用
        // mySharedLock.waitForNotification();
    }

    @Override
    public void stop() {
        // 关键时间线: AbstractApplicationContext.refresh()
        // 1. finishBeanFactoryInitialization() 比较耗时
        // 2. super.finishRefresh()中将触发 WebServerStartStopLifecycle.start()
        // 以启动webserver, 所以我们得在它之前将我们的轻量级webserver关闭掉
        minimalUndertowserver.stop();
    }
}
