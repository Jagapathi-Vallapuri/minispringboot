package com.mini.springboot.framework.context;

import com.mini.springboot.framework.annotations.GetMapping;
import com.mini.springboot.framework.annotations.RestController;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContext {
    private Map<String, Method> routeMap = new HashMap<>();
    private Map<Class<?>, Object> beanMap = new HashMap<>();

    private final BeanScanner scanner = new BeanScanner();

    public void init(String packageName){

    }

    public Method getMethodForRoute(String path){
        return routeMap.get(path);
    }

    public Object getBeanInstance(Class<?> clas){
        return beanMap.get(clas);
    }

    public void refresh(String rootPackage){
        List<Class<?>> candidates = scanner.scan(rootPackage);

        for(Class<?> clas : candidates){
            if(clas.isAnnotationPresent(RestController.class)){
                try{
                    Object instance = clas.getDeclaredConstructor().newInstance();
                    beanMap.put(clas, instance);
                    processMethods(clas);
                    System.out.println("Registered Bean: " + clas);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void processMethods(Class<?> clas){
        for(Method method : clas.getDeclaredMethods()){
            if(method.isAnnotationPresent(GetMapping.class)){
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                String url = mapping.value();
                routeMap.put(url ,method);
                System.out.println("Mapped URL [" + url + "] to method [" + method.getName() + "]");
            }
        }
    }
}
