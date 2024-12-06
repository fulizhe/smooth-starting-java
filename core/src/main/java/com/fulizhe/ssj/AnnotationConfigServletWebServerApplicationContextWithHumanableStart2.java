package com.fulizhe.ssj;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.PropertyPlaceholderHelper;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author LQ
 *
 */
class AnnotationConfigServletWebServerApplicationContextWithHumanableStart2
        extends AnnotationConfigServletWebServerApplicationContext {

    Tomcat minimalUndertowserver;

    private final MySharedLock mySharedLock = new MySharedLock();

    private static final long startTime = System.currentTimeMillis();

    @Override
    protected void prepareRefresh() {
        String property = this.getEnvironment().getProperty("server.port");
        minimalUndertowserver = minimalUndertowserver(Convert.toInt(property), mySharedLock);
        // === main thread
        // 这里阻塞主线程, 此时minimal-Undertow-Server已经启动了, 所以可以正常响应外界请求——界面化修改配置项.
        // 释放锁在下方的minimal-Undertow-Server响应里
        // TODO 以下功能待启用
        // mySharedLock.waitForNotification();
        super.prepareRefresh();
    }

    @Override
    protected void finishRefresh() {
        // 关键时间线: AbstractApplicationContext.refresh()
        // 1. finishBeanFactoryInitialization() 比较耗时
        // 2. super.finishRefresh()中将触发 WebServerStartStopLifecycle.start()
        // 以启动webserver, 所以我们得在它之前将我们的轻量级webserver关闭掉
        if (minimalUndertowserver != null) {
            try {
                minimalUndertowserver.stop();
                minimalUndertowserver.destroy();
            } catch (LifecycleException e) {
                throw ExceptionUtil.wrapRuntime(e);
            }
        }
        super.finishRefresh();
    }

    static Tomcat minimalUndertowserver(int port, final MySharedLock parentMySharedLock) {
        // Start the minimal Undertow server

        Tomcat tomcat = new Tomcat();

        Connector connector = new Connector("HTTP/1.1");
        connector.setPort(port);
        tomcat.getService().addConnector(connector);

        Context context = tomcat.addContext("", new File(".").getAbsolutePath());

        Tomcat.addServlet(context, "minimalServlet", new MinimalServlet(parentMySharedLock));
        context.addServletMappingDecoded("/*", "minimalServlet");

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            throw ExceptionUtil.wrapRuntime(e);
        }

        return tomcat;
    }

    //
    private static class MinimalServlet extends HttpServlet {
        private final MySharedLock mySharedLock;

        public MinimalServlet(MySharedLock mySharedLock) {
            this.mySharedLock = mySharedLock;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setContentType("text/html; charset=UTF-8");

            String requestURI = req.getRequestURI();
            if (requestURI.contains("/inner/start")) {
                mySharedLock.notifyMainThread();
                PrintWriter writer = resp.getWriter();
                writer.write("loading");
                writer.flush();
            } else if (requestURI.contains("/inner/config")) {
                // Handle configuration changes
            } else {
                // Default response
                final String loadingHtml = ResourceUtil.readStr("static/loading.html", CharsetUtil.CHARSET_UTF_8);
                long currentTime = System.currentTimeMillis();
                long secondsPassed = (currentTime - startTime) / 1000;
                Console.log("### elapseTimeBySecond: [ {} ]s", secondsPassed);
                final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${",
                        "}");
                final Map<String, String> map = Collections.singletonMap("elapseTimeBySecond",
                        StrUtil.toString(secondsPassed));
                final String loadingHtmlFinal = propertyPlaceholderHelper.replacePlaceholders(loadingHtml,
                        map::get);
                PrintWriter writer = resp.getWriter();
                writer.write(loadingHtmlFinal);
                writer.flush();
            }
        }
    }

    // =====================
    public class MySharedLock {

        private final Object lock = new Object();

        // Method to be called by the main thread to wait
        public void waitForNotification() {
            // 默认暂停5分钟
            waitForNotification(1000 * 60 * 5);
        }

        public void waitForNotification(long timeout) {
            synchronized (lock) {
                try {
                    lock.wait(timeout);
                } catch (InterruptedException e) {
                    // SWALLOW
                }
            }
        }

        // Method to be called by the HTTP request thread to notify
        public void notifyMainThread() {
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    // ===========================
    static class Factory implements ApplicationContextFactory {

        @Override
        public ConfigurableApplicationContext create(WebApplicationType webApplicationType) {
            return new AnnotationConfigServletWebServerApplicationContextWithHumanableStart2();
        }

    }

}
