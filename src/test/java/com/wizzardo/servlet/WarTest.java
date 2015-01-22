package com.wizzardo.servlet;

import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.*;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: wizzardo
 * Date: 06.12.14
 */
public abstract class WarTest extends ServerTest {

    @Override
    protected void init() throws IOException {
        contextPath = "/http";
        WarBuilder builder = new WarBuilder();
        customizeWar(builder);
        File war = builder.build("/tmp/http.war");
        war.deleteOnExit();

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/http");
        webapp.setWar(war.getAbsolutePath());

        jetty.setHandler(webapp);

        myServer.registerWar(war.getAbsolutePath());
    }

    protected abstract void customizeWar(WarBuilder warBuilder);
}
