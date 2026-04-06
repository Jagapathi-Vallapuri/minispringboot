package com.mini.springboot.framework.server;

import com.mini.springboot.framework.annotations.PathVariable;
import com.mini.springboot.framework.annotations.RequestBody;
import com.mini.springboot.framework.annotations.RequestParam;
import com.mini.springboot.framework.context.ApplicationContext;
import com.mini.springboot.framework.utils.JsonParser;
import com.mini.springboot.framework.utils.MatchResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WebServer {
    private final int port;
    private final ApplicationContext context;


    private Map<String, String> parseQueryParams(String query){
        Map<String, String> queryParams = new HashMap<>();
        if(query == null || query.isEmpty()) return queryParams;

        String[] pairs = query.split("&");
        for(String pair : pairs){
            String[] keyValue = pair.split("=");
            if(keyValue.length == 2){
                String decoded  = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                queryParams.put(keyValue[0], decoded);
            }
        }
        return queryParams;
    }

    public WebServer(int port, ApplicationContext context){
        this.port = port;
        this.context = context;
    }

    private String readRequestBody(HttpExchange exchange) throws IOException{
        try(InputStream is  = exchange.getRequestBody()){
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();

            if(path.equals("/favicon.ico")){
                try(InputStream is = getClass().getResourceAsStream("/favicon.ico")){
                    if(is != null){
                        byte[] iconBytes = is.readAllBytes();
                        exchange.getResponseHeaders().set("Content-Type", "image/x-icon");
                        exchange.sendResponseHeaders(200, iconBytes.length);
                        exchange.getResponseBody().write(iconBytes);
                    } else{
                        exchange.sendResponseHeaders(404, -1);
                    }
                }catch(Exception e){
                    exchange.sendResponseHeaders(500,-1);
                }
                exchange.getResponseBody().close();
                return;
            }


            System.out.println("Received Request for: " + path);

            Method targetMethod = context.getMethodForRoute(path).getMethod();
            MatchResult match = context.getMethodForRoute(path);

            if(targetMethod != null){
                try{
                    Parameter[] parameters = targetMethod.getParameters();
                    Object[] args = new Object[parameters.length];

                    String body = readRequestBody(exchange);
                    Map<String, String> pathVars = match.getVariables();
                    Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

                    for (int i = 0; i < parameters.length; i++) {
                        if (parameters[i].isAnnotationPresent(RequestBody.class)) {
                            Class<?> paramType = parameters[i].getType();
                            if (Map.class.isAssignableFrom(paramType)) {
                                args[i] = JsonParser.parse(body);
                            } else {
                                args[i] = body;
                            }
                        }
                        else if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                            RequestParam ann = parameters[i].getAnnotation(RequestParam.class);
                            args[i] = queryParams.get(ann.value());
                        }
                        else if (parameters[i].isAnnotationPresent(PathVariable.class)) {
                            PathVariable pv = parameters[i].getAnnotation(PathVariable.class);
                            args[i] = pathVars.get(pv.value());
                        }
                    }
                    Object controller = context.getBeanInstance(targetMethod.getDeclaringClass());
                    String res = (String) targetMethod.invoke(controller, args);

                    exchange.sendResponseHeaders(200, res.length());
                    exchange.getResponseBody().write(res.getBytes());
                }catch (Exception e){
                    String error = "500 Internal Server Error: " + e.getMessage();
                    exchange.sendResponseHeaders(500 ,error.length());
                    exchange.getResponseBody().write(error.getBytes());
                }
            }
            else{
                String notFound = "404 Not Found - Method not mapped for " + path;
                exchange.sendResponseHeaders(404, notFound.length());
                exchange.getResponseBody().write(notFound.getBytes());
            }
            exchange.getResponseBody().close();
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }
}
