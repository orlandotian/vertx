package com.orlando.starter;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

public interface IRepository {
  void find(JsonObject where, Handler<AsyncResult<List<JsonObject>>> var);

  void findOne(JsonObject where, Handler<AsyncResult<JsonObject>> var);

  void count(JsonObject object, Handler<AsyncResult<Long>> var);

  void remove(JsonObject where, Handler<AsyncResult<Void>> var);

  void save(JsonObject object, Handler<AsyncResult<String>> var);

  void insert(JsonObject object, Handler<AsyncResult<String>> var);

  void update(JsonObject where, JsonObject object, Handler<AsyncResult<Void>> var);

  void replace(JsonObject where, JsonObject object, Handler<AsyncResult<Void>> var);
}
