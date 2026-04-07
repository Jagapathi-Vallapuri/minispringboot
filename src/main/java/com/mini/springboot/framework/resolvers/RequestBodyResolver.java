package com.mini.springboot.framework.resolvers;

import com.mini.springboot.framework.annotations.RequestBody;
import com.mini.springboot.framework.context.RequestContext;
import java.lang.reflect.Parameter;
import java.util.Map;

public class RequestBodyResolver implements ArgumentResolver{
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public Object resolve(Parameter parameter, RequestContext requestContext) {
        if(Map.class.isAssignableFrom(parameter.getType())){
            return requestContext.getParsedBody();
        }
        return requestContext.getRawBody();
    }
}
