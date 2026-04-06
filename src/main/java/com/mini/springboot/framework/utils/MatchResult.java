package com.mini.springboot.framework.utils;

import java.lang.reflect.Method;
import java.util.Map;

public class MatchResult {
    private final Method method;
    private final Map<String, String> variables;


    public MatchResult(Method method, Map<String, String> variables) {
        this.method = method;
        this.variables = variables;
    }

    public Method getMethod() {
        return method;
    }

    public Map<String, String> getVariables() {
        return variables;
    }
}
