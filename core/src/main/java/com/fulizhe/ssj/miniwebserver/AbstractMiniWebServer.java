package com.fulizhe.ssj.miniwebserver;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.fulizhe.ssj.IMiniWebServer;
import com.fulizhe.ssj.IStaticLoadingPageFactory;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Collections;
import java.util.Map;

public abstract class AbstractMiniWebServer implements IMiniWebServer {

    static final long startTime = System.currentTimeMillis();

    final MySharedLock mySharedLock = new MySharedLock();

    final IStaticLoadingPageFactory staticLoadingPageFactory;

    public AbstractMiniWebServer(IStaticLoadingPageFactory staticLoadingPageFactory){
        this.staticLoadingPageFactory = staticLoadingPageFactory;
    }

    public AbstractMiniWebServer(){
        this(IStaticLoadingPageFactory.Instance.instance());
    }
}
