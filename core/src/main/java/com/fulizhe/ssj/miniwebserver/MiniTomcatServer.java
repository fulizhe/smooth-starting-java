package com.fulizhe.ssj.miniwebserver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.convert.Convert;
import com.fulizhe.ssj.IRequestDealer;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.util.PropertyPlaceholderHelper;

import com.fulizhe.ssj.IMiniWebServer;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;

public class MiniTomcatServer extends AbstractMiniWebServer implements IMiniWebServer {

    private Tomcat minimalTomcatServer;

    private final IRequestDealer requestDealer;

    public MiniTomcatServer(IRequestDealer requestDealer) {
        this.requestDealer = requestDealer;
    }

    @Override
    public void start(int port) {
        minimalTomcatServer = new Tomcat();

        Connector connector = new Connector("HTTP/1.1");
        connector.setPort(port);
        minimalTomcatServer.getService().addConnector(connector);

        Context context = minimalTomcatServer.addContext("", new File(".").getAbsolutePath());

        Tomcat.addServlet(context, "minimalServlet", new MinimalServlet(mySharedLock));
        context.addServletMappingDecoded("/*", "minimalServlet");

        try {
            minimalTomcatServer.start();
        } catch (LifecycleException e) {
            throw ExceptionUtil.wrapRuntime(e);
        }

        // === main thread
        // 这里阻塞主线程, 此时minimal-Undertow-Server已经启动了, 所以可以正常响应外界请求——界面化修改配置项.
        // 释放锁在下方的minimal-Undertow-Server响应里
        // TODO 以下功能待启用
        // mySharedLock.waitForNotification();
    }

    @Override
    public void stop() {
        if (minimalTomcatServer != null) {
            try {
                minimalTomcatServer.stop();
                minimalTomcatServer.destroy();
            } catch (LifecycleException e) {
                throw ExceptionUtil.wrapRuntime(e);
            }
        }

    }

    //
    private class MinimalServlet extends HttpServlet {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private final MySharedLock mySharedLock;

        public MinimalServlet(MySharedLock mySharedLock) {
            this.mySharedLock = mySharedLock;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            final IRequestDealer.IRequestDealContext tomcatContext = IRequestDealer.IRequestDealContextFactory.getTomcatContext(req, resp);
            requestDealer.deal(tomcatContext);
            if (tomcatContext.isDealed()) {
                return;
            }

/*
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
 */
            // Default response
            long currentTime = System.currentTimeMillis();
            long secondsPassed = (currentTime - startTime) / 1000;
            Console.log("### elapseTimeBySecond: [ {} ]s", secondsPassed);

            final String loadingHtml = MiniTomcatServer.this.staticLoadingPageFactory.get(Convert.toInt(secondsPassed));
            PrintWriter writer = resp.getWriter();
            writer.write(loadingHtml);
            writer.flush();
        }
    }

}
