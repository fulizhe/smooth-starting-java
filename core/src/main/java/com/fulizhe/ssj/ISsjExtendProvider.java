package com.fulizhe.ssj;

/**
 * 统一的扩展入口
 * 
 * @author LQ
 *
 */
public interface ISsjExtendProvider {
    default IRequestDealer requestDealer() {
        return IRequestDealer.Factory.get();
    }

    default IStaticLoadingPageFactory staticLoadingPageFactory() {
        return IStaticLoadingPageFactory.Instance.instance();
    }

    default IMiniWebServer miniWebServer() {
        return IMiniWebServer.Factory.get();
    }

    static class Default implements ISsjExtendProvider {
        private static final ISsjExtendProvider instance = new Default();

        public static final ISsjExtendProvider instance() {
            return instance;
        }
    }
}
