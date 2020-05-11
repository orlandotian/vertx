package com.orlando.controller;

import com.orlando.starter.annotation.RequestMapping;
import com.orlando.starter.annotation.RestController;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;

import java.util.HashMap;

@RestController
public class ApiController {
  @RequestMapping("/json")
  public HashMap getAll(HttpServerRequest request) {
    HashMap<String, String> result = new HashMap<>();
    result.put("method", request.method().name());
    return result;
  }

  @RequestMapping(value = "/", method = HttpMethod.GET)
  public String getIndex() {
    return "<h1>Hello from my first Vert.x 3 application</h1>";
  }
}
