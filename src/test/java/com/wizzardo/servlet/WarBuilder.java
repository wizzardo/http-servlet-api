package com.wizzardo.servlet;

import com.wizzardo.tools.io.FileTools;
import com.wizzardo.tools.io.ZipTools;
import com.wizzardo.tools.xml.Node;

import javax.servlet.Filter;
import javax.servlet.Servlet;
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
        private List<FilterMapping> filterMappings = new ArrayList<>();
        private Map<String, String> contextParams = new LinkedHashMap<>();

        public WebXmlBuilder append(ServletMapping servletMapping) {
            servletMappings.add(servletMapping);
            return this;
        }

        public WebXmlBuilder append(FilterMapping filterMapping) {
            filterMappings.add(filterMapping);
            return this;
        }

        public String build() {
            Node root = new Node("web-app");
            root.attr("version", "2.5")
                    .attr("xmlns", "http://java.sun.com/xml/ns/javaee")
                    .attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
                    .attr("xsi:schemaLocation", "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd");

            for (Map.Entry<String, String> param : contextParams.entrySet()) {
                Node contextParam = new Node("context-param");
                root.add(contextParam);
                contextParam.add(new Node("param-name").addText(param.getKey()));
                contextParam.add(new Node("param-value").addText(param.getValue()));
            }

            for (ServletMapping servletMapping : servletMappings) {
                Node servlet = new Node("servlet");
                root.add(servlet);
                servlet.add(new Node("servlet-name").addText(servletMapping.servletName));
                servlet.add(new Node("servlet-class").addText(servletMapping.servletClass));
                if (servletMapping.order >= 0)
                    servlet.add(new Node("load-on-startup").addText(String.valueOf(servletMapping.order)));

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

            for (FilterMapping filterMapping : filterMappings) {
                Node filter = new Node("filter");
                root.add(filter);
                filter.add(new Node("filter-name").addText(filterMapping.filterName));
                filter.add(new Node("filter-class").addText(filterMapping.filterClass));
                for (Map.Entry<String, String> parameter : filterMapping.params.entrySet()) {
                    Node param = new Node("init-param");
                    filter.add(param);
                    param.add(new Node("param-name").addText(parameter.getKey()));
                    param.add(new Node("param-value").addText(parameter.getValue()));
                }

                Node mapping = new Node("filter-mapping");
                root.add(mapping);
                for (String urlPattern : filterMapping.urlPatterns) {
                    mapping.add(new Node("filter-name").addText(filterMapping.filterName));
                    mapping.add(new Node("url-pattern").addText(urlPattern));
                }
            }

            return root.toXML(true);
        }

        public WebXmlBuilder param(String key, String value) {
            contextParams.put(key, value);
            return this;
        }
    }

    public static class ServletMapping {
        private String servletClass;
        private String servletName;
        private Set<String> urlPatterns = new LinkedHashSet<>();
        private Map<String, String> params = new LinkedHashMap<>();
        private int order = -1;

        public ServletMapping(Class<? extends Servlet> servletClass, String servletName) {
            this.servletClass = servletClass.getName();
            this.servletName = servletName;
        }

        public ServletMapping(Class<? extends Servlet> servletClass) {
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

        public ServletMapping loadOnStartup(int i) {
            order = 1;
            return this;
        }
    }

    public static class FilterMapping {
        private String filterClass;
        private String filterName;
        private Set<String> urlPatterns = new LinkedHashSet<>();
        private Map<String, String> params = new LinkedHashMap<>();

        public FilterMapping(Class<? extends Filter> filterClass, String filterName) {
            this.filterClass = filterClass.getName();
            this.filterName = filterName;
        }

        public FilterMapping(Class<? extends Filter> filterClass) {
            this(filterClass, filterClass.getSimpleName());
        }

        public FilterMapping appendUrlPattern(String pattern) {
            urlPatterns.add(pattern);
            return this;
        }

        public FilterMapping url(String pattern) {
            return appendUrlPattern(pattern);
        }

        public FilterMapping param(String key, String value) {
            params.put(key, value);
            return this;
        }
    }


}
