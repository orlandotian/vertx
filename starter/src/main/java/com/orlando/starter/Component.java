package com.orlando.starter;

import java.lang.reflect.Method;
import java.util.HashMap;

public class Component {
  private static HashMap<String, Object> beans = new HashMap<>();
  private static HashMap<String, Method> requests = new HashMap<>();

  public static Object getComponent(String name) {
    return beans.get(name);
  }

  public static void putComponents(String name, Object bean) {
    beans.put(name, bean);
  }

  public static Method getMappingMethod(String path) {
    return requests.get(path);
  }

  public static void putRequests(String path,  Method method) {
    requests.put(path, method);
  }
}
