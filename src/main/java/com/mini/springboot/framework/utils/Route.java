package com.mini.springboot.framework.utils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

public class Route {
    private Pattern pattern;
    private Method method;
    private List<String> pathVariables;
}
