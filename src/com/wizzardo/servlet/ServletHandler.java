package com.wizzardo.servlet;

import com.wizzardo.http.Handler;
import com.wizzardo.http.request.Request;
import com.wizzardo.http.response.Response;
import com.wizzardo.http.response.Status;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * @author: wizzardo
 * Date: 26.10.14
 */
public class ServletHandler implements Handler {
    private UrlMapping<HttpServlet> servletsMapping = new UrlMapping<>();
    private Context context;

    public ServletHandler(Context context) {
        this.context = context;
    }

    @Override
    public Response handle(Request request, Response response) throws IOException {
        HttpRequest httpRequest = new HttpRequest(context, request);
        HttpResponse httpResponse = new HttpResponse(response);
        String path = request.path();
        path = path.substring(context.getContextPath().length());

        try {
            HttpServlet servlet = servletsMapping.get(path);
            if (servlet == null)
                return response.setStatus(Status._404);

            servlet.service(httpRequest, httpResponse);
            if (httpResponse.hasWriter())
                response.setBody(httpResponse.getData());
        } catch (ServletException e) {
            e.printStackTrace();
        }
        return response;
    }

    public ServletHandler append(String path, HttpServlet servlet) {
        servletsMapping.append(path, servlet);
        return this;
    }
}
