package com.orlando.starter;

import io.vertx.ext.mongo.MongoClient;
import io.vertx.pgclient.PgPool;

import java.lang.reflect.Method;
import java.util.HashMap;

public class Component {
  private static HashMap<String, Object> beans = new HashMap<>();
  private static HashMap<String, Method> requests = new HashMap<>();

  private static MongoClient mongoClient;
  private static PgPool pgClient;

  public static MongoClient getMongoClient() {
    return mongoClient;
  }

  public static void setMongoClient(MongoClient mongoClient) {
    Component.mongoClient = mongoClient;
  }

  public static void setPgClient(PgPool pgClient) {
    Component.pgClient = pgClient;
  }

  public static PgPool getPgClient() {
    return pgClient;
  }

  public static Object getComponent(String name) {
    return beans.get(name);
  }

  public static void putComponents(String name, Object bean) {
    beans.put(name, bean);
  }

  public static Method getMappingMethod(String path) {
    return requests.get(path);
  }

  public static void putRequests(String path, Method method) {
    requests.put(path, method);
  }
}
