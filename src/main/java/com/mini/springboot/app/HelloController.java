package com.mini.springboot.app;

import com.mini.springboot.framework.annotations.GetMapping;
import com.mini.springboot.framework.annotations.RestController;

@RestController
public class HelloController {
    @GetMapping(value = "/hello")
    public String sayHello(){
        return "Hello!!!";
    }
}
