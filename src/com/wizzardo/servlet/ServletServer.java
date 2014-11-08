package com.wizzardo.servlet;

import com.wizzardo.http.HttpServer;
import com.wizzardo.http.response.Status;

import javax.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * @author: wizzardo
 * Date: 03.11.14
 */
public class ServletServer<T extends ServletHttpConnection> extends HttpServer<T> {
    private UrlMapping<HttpServlet> servletsMapping = new UrlMapping<>();
    private Context context;

    public ServletServer(Context context) {
        super(context.getHost(), context.getPort());
        this.context = context;
    }

    @Override
    protected T createConnection(int fd, int ip, int port) {
        return (T) new ServletHttpConnection(fd, ip, port);
    }

    protected void handle(T connection) {
        try {
            HttpRequest httpRequest = connection.getRequest();
            HttpResponse httpResponse = connection.getResponse();
            httpRequest.setContext(context);

            String path = httpRequest.path();
            path = path.substring(context.getContextPath().length());

            HttpServlet servlet = servletsMapping.get(path);
            if (servlet == null) {
                httpResponse.setStatus(Status._404);
            } else {
                servlet.service(httpRequest, httpResponse);
                if (httpResponse.hasWriter())
                    httpResponse.setBody(httpResponse.getData());
            }
            finishHandling(connection);
        } catch (Exception t) {
            t.printStackTrace();
            //TODO render error page
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ServletServer append(String path, HttpServlet servlet) {
        servletsMapping.append(path, servlet);
        return this;
    }
}
