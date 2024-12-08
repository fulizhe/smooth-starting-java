package com.fulizhe.ssj;

import com.fulizhe.ssj.miniwebserver.MiniTomcatServer;
import com.fulizhe.ssj.miniwebserver.MiniUndertowServer;

import cn.hutool.core.util.ClassLoaderUtil;

public interface IMiniWebServer {
    void start(int port);

    void stop();

    static class Factory {
        static IMiniWebServer get() {
            final IRequestDealer iRequestDealer = IRequestDealer.Factory.get();
            if (ClassLoaderUtil.isPresent("io.undertow.Undertow")) {
                return new MiniUndertowServer(iRequestDealer);
            }

            return new MiniTomcatServer(iRequestDealer);
        }
    }
}