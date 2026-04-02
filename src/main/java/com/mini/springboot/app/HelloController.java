package com.mini.springboot.app;

import com.mini.springboot.framework.annotations.Autowired;
import com.mini.springboot.framework.annotations.GetMapping;
import com.mini.springboot.framework.annotations.RequestParam;
import com.mini.springboot.framework.annotations.RestController;

@RestController
public class HelloController {

    @Autowired
    private GreetingService greetingService;

    @GetMapping(value = "/hello")
    public String sayHello(){
        return "Hello!!!";
    }

    @GetMapping(value = "/greet")
    public String greet(){
        return greetingService.getGreeting();
    }

    @GetMapping("/greetMe")
    public String greetMe(@RequestParam("name") String name){
        return "Hello! " + name;
    }
}
