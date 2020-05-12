package com.orlando.starter;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

public abstract class BaseRepository implements IRepository {

  protected MongoClient mongo;
  protected String collection;

  public BaseRepository(MongoClient mongo, String collection) {
    this.mongo = mongo;
    this.collection = collection;
  }

  @Override
  public void find(JsonObject where, Handler<AsyncResult<List<JsonObject>>> var) {
    mongo.find(collection, where, var);
  }

  @Override
  public void findOne(JsonObject where, Handler<AsyncResult<JsonObject>> var) {
    mongo.findOne(collection, where, null, var);
  }

  @Override
  public void count(JsonObject object, Handler<AsyncResult<Long>> var) {
    mongo.count(collection, object, var);
  }

  @Override
  public void remove(JsonObject where, Handler<AsyncResult<Void>> var) {
    mongo.remove(collection, where, var);
  }

  @Override
  public void save(JsonObject object, Handler<AsyncResult<String>> var) {
    mongo.save(collection, object, var);
  }

  @Override
  public void insert(JsonObject object, Handler<AsyncResult<String>> var) {
    mongo.insert(collection, object, var);
  }

  @Override
  public void update(JsonObject where, JsonObject object, Handler<AsyncResult<Void>> var) {
    mongo.update(collection, where, object, var);
  }

  @Override
  public void replace(JsonObject where, JsonObject object, Handler<AsyncResult<Void>> var) {
    mongo.replace(collection, where, object, var);
  }
}
