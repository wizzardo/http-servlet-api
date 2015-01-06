package com.wizzardo.servlet;

import com.wizzardo.tools.io.FileTools;
import com.wizzardo.tools.io.ZipTools;
import com.wizzardo.tools.xml.Node;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by wizzardo on 04.01.15.
 */
public class WarBuilder {

    private File workDir;
    private Set<File> classes = new HashSet<>();
    private WebXmlBuilder webXmlBuilder = new WebXmlBuilder();

    public WarBuilder() {
        try {
            workDir = new File(WarBuilder.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (workDir.isFile())
                workDir = workDir.getParentFile();
        } catch (URISyntaxException ignore) {
        }
    }

    public WebXmlBuilder getWebXmlBuilder() {
        return webXmlBuilder;
    }

    public File build() throws IOException {
        return build(File.createTempFile("test", "war"));
    }

    public File build(String path) throws IOException {
        return build(new File(path));
    }

    public WarBuilder addClass(Class clazz) {
        if (clazz.getName().contains("$")) {
            String path = clazz.getName().substring(0, clazz.getName().indexOf("$"));
            String parentClass = path.substring(path.lastIndexOf('.') + 1);
            path = path.replace('.', '/');
            File dir = new File(workDir, path).getParentFile();
            for (File file : dir.listFiles((dir1, name) -> name.startsWith(parentClass + "$") || name.equals(parentClass + ".class"))) {
                classes.add(file);
            }
        } else {
            String path = clazz.getName().replace('.', '/') + ".class";
            classes.add(new File(workDir, path));
        }
        return this;
    }

    public File build(File warFile) throws IOException {
        ZipTools.ZipBuilder builder = new ZipTools.ZipBuilder();

        builder.append("/WEB-INF/web.xml", webXmlBuilder.build().getBytes());

        for (File clazz : classes) {
            String path = clazz.getAbsolutePath().substring(workDir.getAbsolutePath().length());
            builder.append("/WEB-INF/classes" + path, FileTools.bytes(clazz));
        }

        builder.zip(warFile);
        return warFile;
    }

    public static class WebXmlBuilder {
        private List<ServletMapping> servletMappings = new ArrayList<>();

        public WebXmlBuilder append(ServletMapping servletMapping) {
            servletMappings.add(servletMapping);
            return this;
        }

        public String build() {
            Node root = new Node("web-app");
            root.attr("version", "2.5")
                    .attr("xmlns", "http://java.sun.com/xml/ns/javaee")
                    .attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
                    .attr("xsi:schemaLocation", "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd");

            for (ServletMapping servletMapping : servletMappings) {
                Node servlet = new Node("servlet");
                root.add(servlet);
                servlet.add(new Node("servlet-name").addText(servletMapping.servletName));
                servlet.add(new Node("servlet-class").addText(servletMapping.servletClass));
                for (Map.Entry<String, String> parameter : servletMapping.params.entrySet()) {
                    Node param = new Node("init-param");
                    servlet.add(param);
                    param.add(new Node("param-name").addText(parameter.getKey()));
                    param.add(new Node("param-value").addText(parameter.getValue()));
                }

                Node mapping = new Node("servlet-mapping");
                root.add(mapping);
                for (String urlPattern : servletMapping.urlPatterns) {
                    mapping.add(new Node("servlet-name").addText(servletMapping.servletName));
                    mapping.add(new Node("url-pattern").addText(urlPattern));
                }
            }

            return root.toXML(true);
        }
    }

    public static class ServletMapping {
        private String servletClass;
        private String servletName;
        private Set<String> urlPatterns = new LinkedHashSet<>();
        private Map<String, String> params = new LinkedHashMap<>();

        public ServletMapping(Class servletClass, String servletName) {
            this.servletClass = servletClass.getName();
            this.servletName = servletName;
        }

        public ServletMapping(Class servletClass) {
            this(servletClass, servletClass.getSimpleName());
        }

        public ServletMapping appendUrlPattern(String pattern) {
            urlPatterns.add(pattern);
            return this;
        }

        public ServletMapping url(String pattern) {
            return appendUrlPattern(pattern);
        }

        public ServletMapping param(String key, String value) {
            params.put(key, value);
            return this;
        }
    }


}
