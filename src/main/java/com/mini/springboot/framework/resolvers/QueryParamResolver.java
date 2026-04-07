package com.mini.springboot.framework.resolvers;

import com.mini.springboot.framework.annotations.RequestParam;
import com.mini.springboot.framework.context.RequestContext;

import java.lang.reflect.Parameter;

public class QueryParamResolver implements ArgumentResolver{
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestParam.class);
    }

    @Override
    public Object resolve(Parameter parameter, RequestContext requestContext) {
        String key = parameter.getAnnotation(RequestParam.class).value();
        return requestContext.getQueryParams().get(key);
    }
}