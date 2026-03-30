package com.mini.springboot.framework.server;

import com.mini.springboot.framework.context.ApplicationContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public class WebServer {
    private final int port;
    private final ApplicationContext context;

    public WebServer(int port, ApplicationContext context){
        this.port = port;
        this.context = context;
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

            Method targetMethod = context.getMethodForRoute(path);

            if(targetMethod != null){
                try{
                    Object controller = context.getBeanInstance(targetMethod.getDeclaringClass());
                    String res = (String) targetMethod.invoke(controller);

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
