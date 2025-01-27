package com.fulizhe.ssj.miniwebserver;

import com.fulizhe.ssj.IMiniWebServer;
import com.fulizhe.ssj.IStaticLoadingPageFactory;
import com.fulizhe.ssj.IRequestDealer;
import com.fulizhe.ssj.SpiProvider;

public abstract class AbstractMiniWebServer implements IMiniWebServer {

    static final long startTime = System.currentTimeMillis();

    final MySharedLock mySharedLock = new MySharedLock();

    final IStaticLoadingPageFactory staticLoadingPageFactory;
    protected final IRequestDealer requestDealer;

    public AbstractMiniWebServer(IStaticLoadingPageFactory staticLoadingPageFactory, IRequestDealer requestDealer) {
        this.staticLoadingPageFactory = staticLoadingPageFactory;
        this.requestDealer = requestDealer;
    }

    public AbstractMiniWebServer(IRequestDealer requestDealer) {
        this(SpiProvider.getInstance().staticLoadingPageFactory(), requestDealer);
    }
}
