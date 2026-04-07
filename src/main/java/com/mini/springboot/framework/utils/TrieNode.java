package com.mini.springboot.framework.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    private final Map<String, TrieNode> children = new HashMap<>();

    private TrieNode wildcardChild = null;
    private String variableName = null;

    private final Map<String, Method> methodMap = new HashMap<>();

    public void setMethod(String verb, Method method){
        this.methodMap.put(verb.toUpperCase(), method) ;
    }

    public Method getMethod(String verb){
        return this.methodMap.get(verb.toUpperCase());
    }

    public Map<String, TrieNode> getChildren(){
        return children;
    }

    public void setWildcard(String variableName, TrieNode node){
        this.wildcardChild = node;
        this.variableName = variableName;
    }

    public TrieNode getWildcardChild(){
        return wildcardChild;
    }

    public String getVariableName(){
        return variableName;
    }
}
