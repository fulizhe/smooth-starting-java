package com.fulizhe.ssj;

import java.util.Optional;

import cn.hutool.core.util.ServiceLoaderUtil;

public class SpiProvider {

    private static final ISsjExtendProvider ssjExtendProvider;

    static {
        final ISsjExtendProvider loadFirstAvailable = ServiceLoaderUtil.loadFirstAvailable(ISsjExtendProvider.class);

        ssjExtendProvider = Optional.ofNullable(loadFirstAvailable) //
                .orElse(ISsjExtendProvider.Default.instance());
    }

    public static final ISsjExtendProvider getInstance() {
        return ssjExtendProvider;
    }
}
