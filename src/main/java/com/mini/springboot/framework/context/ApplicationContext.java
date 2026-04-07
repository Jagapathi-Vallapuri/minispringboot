package com.mini.springboot.framework.context;

import com.mini.springboot.framework.annotations.*;
import com.mini.springboot.framework.utils.MatchResult;
import com.mini.springboot.framework.utils.TrieNode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContext {

    private final TrieNode root = new TrieNode();

    private Map<Class<?>, Object> beanMap = new HashMap<>();

    private final BeanScanner scanner = new BeanScanner();

    public void init(String packageName){

    }
    public MatchResult getMethodForRoute(String path, String verb){
        return search(path, verb);
    }

    public Object getBeanInstance(Class<?> clas){
        return beanMap.get(clas);
    }

    public void refresh(String rootPackage){
        List<Class<?>> candidates = scanner.scan(rootPackage);

        for(Class<?> clas : candidates){
            if(clas.isAnnotationPresent(RestController.class) || clas.isAnnotationPresent(Service.class)){
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

        for(Object bean : beanMap.values()){
            for(Field field : bean.getClass().getDeclaredFields()){
                if(field.isAnnotationPresent(Autowired.class)){
                    Class<?> fieldType = field.getType();
                    Object dependency = beanMap.get(fieldType);

                    if(dependency != null){
                        try{
                            field.setAccessible(true);
                            field.set(bean, dependency);
                            System.out.println("Injected " + fieldType.getSimpleName() + " into " + bean.getClass().getSimpleName());
                        }catch(IllegalAccessException e){
                            e.printStackTrace();
                        }
                    }else{
                        throw new RuntimeException("ERROR: Context Initialization Failed. " +
                                "Bean of type [" + field.getDeclaringClass().getSimpleName() +"] " +
                                "requires a bean of type[" + fieldType.getSimpleName() + "] " +
                                "that could not be found."
                        );
                    }
                }
            }
        }
    }

    public void insert(String path, String verb, Method method){
        if (path.endsWith("/") && path.length() > 1) path = path.substring(0, path.length() - 1);
        String[] segments = path.split("/");
        TrieNode current = root;

        for(String segment: segments){
            if(segment.isEmpty()) continue;
            if(segment.startsWith("{") && segment.endsWith("}")){
                String varName = segment.substring(1, segment.length() - 1);

                if(current.getWildcardChild() == null){
                    current.setWildcard(varName, new TrieNode());
                }
                current = current.getWildcardChild();
            }else{
                current = current.getChildren()
                        .computeIfAbsent(segment, k -> new TrieNode());
            }
        }
        current.setMethod(verb, method);
    }


    public MatchResult search(String path, String verb){
        if(path.endsWith("/") && path.length() > 1){
            path = path.substring(0, path.length()-1);
        }
        String[] segments = path.split("/");
        TrieNode current = root;
        Map<String, String> pathVariables = new HashMap<>();

        for(String segment: segments){
            if(segment.isEmpty()) continue;

            TrieNode next = current.getChildren().get(segment);
            if(next != null){
                current = next;
            }
            else if(current.getWildcardChild() != null){
                String varName = current.getVariableName();
                pathVariables.put(varName, segment);
                current = current.getWildcardChild();
            } else{
                return null;
            }
        }
        if(current.getMethod(verb) == null){
            return null;
        }

        return new MatchResult(current.getMethod(verb), pathVariables);
    }

    private void processMethods(Class<?> clas){
        for(Method method : clas.getDeclaredMethods()){
            if(method.isAnnotationPresent(GetMapping.class)){
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                String url = mapping.value();
                insert(url, "GET", method);
                System.out.println("Mapped GET URL [" + url + "] to method [" + method.getName() + "]");
            } else if(method.isAnnotationPresent(PostMapping.class)){
                PostMapping mapping = method.getAnnotation(PostMapping.class);
                String url = mapping.value();
                insert(url, "POST", method);
                System.out.println("Mapped POST URL [" + url + "] to method [" + method.getName() + "]");
            } else if(method.isAnnotationPresent(PutMapping.class)){
                PutMapping mapping = method.getAnnotation(PutMapping.class);
                String url = mapping.value();
                insert(url, "PUT", method);
                System.out.println("Mapped PUT URL [" + url + "] to method [" + method.getName() + "]");
            } else if(method.isAnnotationPresent(DeleteMapping.class)){
                DeleteMapping mapping = method.getAnnotation(DeleteMapping.class);
                String url = mapping.value();
                insert(url, "DELETE", method);
                System.out.println("Mapped DELETE URL [" + url + "] to method [" + method.getName() + "]");
            }
        }
    }
}
