package com.orlando.controller;

import com.orlando.bean.Player;
import com.orlando.repository.IPlayerRepository;
import com.orlando.starter.RCTCallback;
import com.orlando.starter.annotation.Autowired;
import com.orlando.starter.annotation.Controller;
import com.orlando.starter.annotation.RequestMapping;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

@Controller
public class ApiController {

  @Autowired
  public IPlayerRepository playerRepository;

  @RequestMapping("/json")
  public void getAll(HttpServerRequest request, RCTCallback callback) {
    JsonObject result = new JsonObject();
    result.put("method", request.method().name());
    callback.done(result);
  }

  @RequestMapping(value = "/", method = HttpMethod.GET)
  public void getIndex(RCTCallback callback) {
    Player player = new Player();
    playerRepository.save(JsonObject.mapFrom(player), result -> {
      if (result.succeeded()) {
        playerRepository.find(new JsonObject(), rt -> {
          callback.done(rt.result());
        });
      }
    });

  }

  @RequestMapping(value = "/check/:id", method = HttpMethod.GET)
  public void checkInde(HttpServerRequest request, RCTCallback callback) {
    String id = request.getParam("id");
        playerRepository.find(new JsonObject().put("_id", id), rt -> {
          callback.done(rt.result());
        });
  }
}
