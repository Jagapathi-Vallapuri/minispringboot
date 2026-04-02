package com.mini.springboot.app;

import com.mini.springboot.framework.annotations.Autowired;
import com.mini.springboot.framework.annotations.Service;

@Service
public class GreetingService {
    @Autowired
    private HelloController controller;
    public String getGreeting(){
        return "Hello from service";
    }
}
