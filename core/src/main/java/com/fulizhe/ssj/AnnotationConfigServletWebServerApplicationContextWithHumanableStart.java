package com.fulizhe.ssj;

import java.util.Optional;

import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author LQ
 *
 */
public class AnnotationConfigServletWebServerApplicationContextWithHumanableStart
        extends AnnotationConfigServletWebServerApplicationContext {

    final IMiniWebServer miniWebServer;

    public AnnotationConfigServletWebServerApplicationContextWithHumanableStart(IMiniWebServer minimalUndertowserver) {
        super();
        this.miniWebServer = minimalUndertowserver;
    }

    public AnnotationConfigServletWebServerApplicationContextWithHumanableStart() {
        this(SpiProvider.getInstance().miniWebServer());
    }

    @Override
    protected void prepareRefresh() {
        String port = this.getEnvironment().getProperty("server.port");
        miniWebServer.start(Integer.parseInt(Optional.ofNullable(port).orElse("80")));

        super.prepareRefresh();
    }

    @Override
    protected void finishRefresh() {
        // 关键时间线: AbstractApplicationContext.refresh()
        // 1. finishBeanFactoryInitialization() 比较耗时
        // 2. super.finishRefresh()中将触发 WebServerStartStopLifecycle.start()
        // 以启动webserver, 所以我们得在它之前将我们的轻量级webserver关闭掉
        miniWebServer.stop();
        super.finishRefresh();
    }

    // =========================== SpringBoot2.4+
    static class Factory implements ApplicationContextFactory {

        @Override
        public ConfigurableApplicationContext create(WebApplicationType webApplicationType) {
            return (webApplicationType != WebApplicationType.SERVLET) ? null
                    : new AnnotationConfigServletWebServerApplicationContextWithHumanableStart(
                            SpiProvider.getInstance().miniWebServer());
        }

    }

}
