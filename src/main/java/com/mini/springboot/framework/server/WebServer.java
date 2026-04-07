package com.mini.springboot.framework.server;

import com.mini.springboot.framework.context.ApplicationContext;
import com.mini.springboot.framework.context.RequestContext;
import com.mini.springboot.framework.resolvers.ArgumentResolver;
import com.mini.springboot.framework.resolvers.PathParamResolver;
import com.mini.springboot.framework.resolvers.QueryParamResolver;
import com.mini.springboot.framework.resolvers.RequestBodyResolver;
import com.mini.springboot.framework.utils.MatchResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class WebServer {
    private final int port;
    private final ApplicationContext context;
    private final List<ArgumentResolver> resolvers = List.of(
            new QueryParamResolver(),
            new PathParamResolver(),
            new RequestBodyResolver()
    );




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
            String verb = exchange.getRequestMethod();

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

            MatchResult matchResult = context.getMethodForRoute(path, verb);

            if(matchResult == null){
                String notFound = "404 Not Found - No route mapped for " + path;
                exchange.sendResponseHeaders(404, notFound.length());
                exchange.getResponseBody().write(notFound.getBytes());
                exchange.getResponseBody().close();
                return;
            }
            Method targetMethod = matchResult.getMethod();

            if(targetMethod != null){
                try{
                    String body = "";
                    if(!verb.equalsIgnoreCase("GET")){
                        body = readRequestBody(exchange);
                    }

                    RequestContext requestContext = new RequestContext(exchange, matchResult, body);
                    Parameter[] parameters = targetMethod.getParameters();
                    Object[] args = new Object[parameters.length];

                    for (int i = 0; i < parameters.length; i++) {
                        Parameter param = parameters[i];
                        for(ArgumentResolver resolver : resolvers){
                            if(resolver.supports(param)){
                                args[i] = resolver.resolve(param, requestContext);
                                break;
                            }
                        }
                    }
                    Object controller = context.getBeanInstance(targetMethod.getDeclaringClass());
                    String res = (String) targetMethod.invoke(controller, args);

                    byte[] responseBytes = res.getBytes(StandardCharsets.UTF_8);

                    exchange.sendResponseHeaders(200, responseBytes.length);
                    exchange.getResponseBody().write(responseBytes);
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
