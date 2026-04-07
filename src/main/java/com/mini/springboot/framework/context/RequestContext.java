package com.mini.springboot.framework.context;

import com.mini.springboot.framework.utils.JsonParser;
import com.mini.springboot.framework.utils.MatchResult;
import com.mini.springboot.framework.utils.RequestUtils;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.Map;

public class RequestContext {
    private final HttpExchange exchange;
    private final String method;
    private final Map<String, Object> queryParams;
    private final Map<String, Object> pathParams;
    private final String rawBody;
    private final Map<String, Object> parsedBody;

    public RequestContext(HttpExchange exchange, MatchResult result, String body){
        this.exchange = exchange;
        this.method = exchange.getRequestMethod();
        this.rawBody = body;
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> rawPathVars = (result != null) ? result.getVariables() : new HashMap<>();
        this.queryParams = RequestUtils.parseQueryParams(query == null ? "" : query);
        this.pathParams = RequestUtils.parsePathVariables(rawPathVars);
        this.parsedBody = (rawBody == null || rawBody.isEmpty()) ? new HashMap<>() : JsonParser.parse(rawBody);
    }

    public HttpExchange getExchange() {
        return exchange;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    public Map<String, Object> getPathParams() {
        return pathParams;
    }

    public String getRawBody() {
        return rawBody;
    }

    public Map<String, Object> getParsedBody() {
        return parsedBody;
    }
}
