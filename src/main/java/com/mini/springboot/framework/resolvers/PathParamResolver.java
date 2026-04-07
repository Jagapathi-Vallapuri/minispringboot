package com.mini.springboot.framework.resolvers;

import com.mini.springboot.framework.annotations.PathVariable;
import com.mini.springboot.framework.context.RequestContext;
import java.lang.reflect.Parameter;

public class PathParamResolver implements ArgumentResolver{
    @Override
    public Object resolve(Parameter parameter, RequestContext requestContext) {
        String key = parameter.getAnnotation(PathVariable.class).value();
        return requestContext.getPathParams().get(key);
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(PathVariable.class);
    }
}
