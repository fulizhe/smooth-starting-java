package com.fulizhe.ssj.requestdealer;

import com.fulizhe.ssj.IRequestDealer;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class UndertowRequestDealContext implements IRequestDealer.IRequestDealContext {

    private final HttpServerExchange exchange;

    private boolean dealed;

    public UndertowRequestDealContext(final HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public String getUri() {
        return exchange.getRequestURI();
    }

    @Override
    public String getParameter(String key) {
        throw new RuntimeException("未实现");
    }

    @Override
    public void addHeader(String headerName, String headerValue) {
        //exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
        exchange.getResponseHeaders().put(Headers.fromCache(headerName), headerValue);
    }

    @Override
    public void write(String body) {
        exchange.getResponseSender().send(body);

        dealed = true;
    }

    @Override
    public boolean isDealed() {
        return dealed;
    }
}
