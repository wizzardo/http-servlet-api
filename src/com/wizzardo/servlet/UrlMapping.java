package com.wizzardo.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author: wizzardo
 * Date: 31.10.14
 */
public class UrlMapping<T> {

    protected HashMap<String, T> mapping = new HashMap<>();
    protected LinkedHashMap<Pattern, T> regexpMapping = new LinkedHashMap<>();

    public T get(String path) throws IOException {
        T handler = mapping.get(path);
        if (handler != null)
            return handler;

        for (Map.Entry<Pattern, T> entry : regexpMapping.entrySet()) {
            if (entry.getKey().matcher(path).matches())
                return entry.getValue();
        }

        return null;
    }

    public UrlMapping append(String url, T handler) {
        if (url.contains("*")) {
            regexpMapping.put(Pattern.compile(url.replace("*", ".*")), handler);
        } else {
            mapping.put(url, handler);
        }

        return this;
    }
}