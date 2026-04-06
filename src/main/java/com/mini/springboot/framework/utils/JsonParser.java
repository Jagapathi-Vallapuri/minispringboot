package com.mini.springboot.framework.utils;

import java.util.HashMap;
import java.util.Map;

public class JsonParser {

    public static Map<String, Object> parse(String json){
        Map<String, Object> map = new HashMap<>();
        if(json == null || json.trim().isEmpty()) return map;

        StringBuilder buffer = new StringBuilder();
        String currentKey = null;
        boolean insideQuotes = false;

        for(char c : json.toCharArray()){
            if (c == '"'){
                insideQuotes = !insideQuotes;
            }else if(c == ':' && !insideQuotes){
                currentKey = buffer.toString().trim();
                buffer.setLength(0);
            }else if((c == ',' || c == '}') && !insideQuotes){
                if(currentKey != null){
                    String value = buffer.toString().trim();
                    map.put(currentKey, convertToType(value));
                    currentKey = null;
                }
                buffer.setLength(0);
            }else if(c != '{' && !Character.isWhitespace(c) || insideQuotes){
                buffer.append(c);
            }
        }
        return map;
    }


    private static Object convertToType(String value){
        if(value.equalsIgnoreCase("true")) return true;
        if(value.equalsIgnoreCase("false")) return false;
        if(value.equalsIgnoreCase("null")) return null;

        if(value.startsWith("\"") && value.endsWith(("\""))){
            return value.substring(1, value.length()-1);
        }

        try{
            if(value.contains(".")){
                return Double.parseDouble(value);
            }else{
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e){
            return value;
        }
    }
}
