package com.mini.springboot.framework.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestUtils {
    public static Map<String, Object> parseQueryParams(String query){
        Map<String, Object> queryParams = new HashMap<>();
        for(String pair : query.split("&")){
            String[] kV = pair.split("=", 2);
            if(kV.length == 2){
                String value = URLDecoder.decode(kV[1], StandardCharsets.UTF_8);
                queryParams.put(kV[0], autoDetectType(value));
            }
        }
        return queryParams;
    }

    public static Map<String, Object> parsePathVariables(Map<String, String> rawVars) {
        Map<String, Object> pathVars = new HashMap<>();
        if (rawVars == null) return pathVars;

        for (Map.Entry<String, String> entry : rawVars.entrySet()) {
            pathVars.put(entry.getKey(), autoDetectType(entry.getValue()));
        }
        return pathVars;
    }

    private static Object autoDetectType(String value) {
        if (value == null) return null;
        if (value.equalsIgnoreCase("true")) return true;
        if (value.equalsIgnoreCase("false")) return false;

        try {
            if (value.contains(".")) return Double.parseDouble(value);
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return value;
        }
    }
}
