package com.mini.springboot.framework.resolvers;

import com.mini.springboot.framework.context.RequestContext;
import java.lang.reflect.Parameter;

public interface ArgumentResolver {
    boolean supports(Parameter parameter);
    Object resolve(Parameter parameter, RequestContext context);
}

