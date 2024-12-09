package com.fulizhe.ssj;

import com.fulizhe.ssj.miniwebserver.MiniJettyServer;
import com.fulizhe.ssj.miniwebserver.MiniTomcatServer;
import com.fulizhe.ssj.miniwebserver.MiniUndertowServer;

import cn.hutool.core.util.ClassLoaderUtil;

public interface IMiniWebServer {
    void start(int port);

    void stop();

    static class Factory {
        public static IMiniWebServer get() {
            final IRequestDealer iRequestDealer = SpiProvider.getInstance().requestDealer();
            if (ClassLoaderUtil.isPresent("io.undertow.Undertow")) {
                return new MiniUndertowServer(iRequestDealer);
            } else if(ClassLoaderUtil.isPresent("org.eclipse.jetty.server.Server")){
                return new MiniJettyServer(iRequestDealer);
            }

            return new MiniTomcatServer(iRequestDealer);
        }
    }
}