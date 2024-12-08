package com.fulizhe.ssj.requestdealer;

import com.fulizhe.ssj.IRequestDealer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class TomcatRequestDealContext implements IRequestDealer.IRequestDealContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    private boolean dealed;

    public TomcatRequestDealContext(final HttpServletRequest request, final HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public String getUri() {
        return request.getRequestURI();
    }

    @Override
    public String getParameter(String key){
        return request.getParameter(key);
    }

    @Override
    public void addHeader(String headerName, String headerValue) {
        response.addHeader(headerName, headerValue);
    }

    @Override
    public void write(String body) {
        try {
            PrintWriter writer = response.getWriter();
            writer.write(body);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dealed = true;
    }

    @Override
    public boolean isDealed() {
        return dealed;
    }
}
