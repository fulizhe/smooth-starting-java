package com.fulizhe.ssj.miniwebserver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.fulizhe.ssj.IMiniWebServer;
import com.fulizhe.ssj.IRequestDealer;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Console;

public class MiniJettyServer extends AbstractMiniWebServer implements IMiniWebServer {

    private Server jettyServer;
    private final IRequestDealer requestDealer;

    public MiniJettyServer(IRequestDealer requestDealer) {
        this.requestDealer = requestDealer;
    }

    @Override
    public void start(int port) {
        jettyServer = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        jettyServer.setHandler(context);

        context.addServlet(new ServletHolder(new MinimalServlet(mySharedLock)), "/*");

        try {
            jettyServer.start();
        } catch (Exception e) {
            throw ExceptionUtil.wrapRuntime(e);
        }

        // 这里阻塞主线程, 此时 Jetty Server 已经启动了, 所以可以正常响应外界请求
        // TODO 以下功能待启用
        // mySharedLock.waitForNotification();
    }

    @Override
    public void stop() {
        if (jettyServer != null) {
            try {
                jettyServer.stop();
                jettyServer.join();
            } catch (Exception e) {
                throw ExceptionUtil.wrapRuntime(e);
            }
        }
    }

    private class MinimalServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;
        private final MySharedLock mySharedLock;

        public MinimalServlet(MySharedLock mySharedLock) {
            this.mySharedLock = mySharedLock;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            final IRequestDealer.IRequestDealContext jettyContext = IRequestDealer.IRequestDealContextFactory
                    .getJettyContext(req, resp);
            requestDealer.deal(jettyContext);
            if (jettyContext.isDealed()) {
                return;
            }

            // Default response
            resp.setContentType("text/html; charset=UTF-8");
            long currentTime = System.currentTimeMillis();
            long secondsPassed = (currentTime - startTime) / 1000;
            Console.log("### elapseTimeBySecond: [ {} ]s, with [ {} ] url", secondsPassed, jettyContext.getUri());

            final String loadingHtml = MiniJettyServer.this.staticLoadingPageFactory.get(Convert.toInt(secondsPassed));
            PrintWriter writer = resp.getWriter();
            writer.write(loadingHtml);
            writer.flush();
        }
    }
}
