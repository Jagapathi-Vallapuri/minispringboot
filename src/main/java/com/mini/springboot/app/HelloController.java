package com.mini.springboot.app;

import com.mini.springboot.framework.annotations.*;

import java.util.Map;

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

    @PostMapping("/test")
    public String updateData(@RequestBody Map<String, Object> json){
        String name = (String) json.get("name");
        int price = (int) json.get("price");
        boolean is = (boolean) json.get("is");
        double d = (double) json.get("d");
        return "Name: " + name + "\t Price: " + price + "\t is: " + is + " d: " + d;
    }

    @GetMapping("/greet/{name}")
    public String specialGreet(@PathVariable("name") String name){
        return "Special greeting for: " + name;
    }
}
