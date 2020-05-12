package com.orlando.starter;

import io.vertx.core.json.JsonObject;

import java.util.List;

public interface RCTCallback {
  void done(JsonObject result);

  void done(List<JsonObject> result);

  void done(JsonObject result, String template);

  void done(String content);
}
