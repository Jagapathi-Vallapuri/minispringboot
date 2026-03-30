package com.mini.springboot.framework.context;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BeanScanner {
    public List<Class<?>> scan(String packageName){
        List<Class<?>> classes = new ArrayList<>();

        String path = packageName.replace('.', '/');

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try{
            URL resource = loader.getResource(path);
            if(resource != null){
                File dir = new File(resource.getFile());
                if(dir.exists()){
                    scanDirectory(dir, packageName, classes);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return classes;
    }

    private void scanDirectory(File dir, String packageName, List<Class<?>> classes) {
        for(File f : Objects.requireNonNull(dir.listFiles())){
            if(f.isDirectory()){
                scanDirectory(f, packageName + "." + f.getName(), classes);
            }else if(f.getName().endsWith(".class")){
                String className = packageName + "." + f.getName().replace(".class", "");
                try{
                    classes.add(Class.forName(className));
                }catch (ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
