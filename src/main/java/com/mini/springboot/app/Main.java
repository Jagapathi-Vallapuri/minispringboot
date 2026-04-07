package com.mini.springboot.app;

import com.mini.springboot.framework.context.ApplicationContext;
import com.mini.springboot.framework.server.WebServer;

public class Main {
    static void main() {
        ApplicationContext context = new ApplicationContext();
        context.refresh("com.mini.springboot.app");
        WebServer server = new WebServer(8080, context);

        try{
            server.start();
        }catch (Exception e){
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
