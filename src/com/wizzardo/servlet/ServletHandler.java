package com.wizzardo.servlet;

import com.wizzardo.http.Handler;
import com.wizzardo.http.request.Request;
import com.wizzardo.http.response.Response;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * @author: wizzardo
 * Date: 26.10.14
 */
public class ServletHandler implements Handler {
    private HttpServlet servlet;

    public ServletHandler(HttpServlet servlet) {
        this.servlet = servlet;
    }

    @Override
    public Response handle(Request request, Response response) throws IOException {
        HttpRequest httpRequest = new HttpRequest(request);
        HttpResponse httpResponse = new HttpResponse(response);
        try {
            servlet.service(httpRequest, httpResponse);
        } catch (ServletException e) {
            e.printStackTrace();
        }
        return response;
    }
}
