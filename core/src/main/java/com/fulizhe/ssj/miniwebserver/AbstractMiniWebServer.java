package com.fulizhe.ssj.miniwebserver;

import com.fulizhe.ssj.IMiniWebServer;
import com.fulizhe.ssj.IStaticLoadingPageFactory;
import com.fulizhe.ssj.SpiProvider;

public abstract class AbstractMiniWebServer implements IMiniWebServer {

    static final long startTime = System.currentTimeMillis();

    final MySharedLock mySharedLock = new MySharedLock();

    final IStaticLoadingPageFactory staticLoadingPageFactory;

    public AbstractMiniWebServer(IStaticLoadingPageFactory staticLoadingPageFactory){
        this.staticLoadingPageFactory = staticLoadingPageFactory;
    }

    public AbstractMiniWebServer(){        
        this(SpiProvider.getInstance().staticLoadingPageFactory());
    }
}
