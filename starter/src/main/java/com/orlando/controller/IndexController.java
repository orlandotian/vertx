package com.orlando.controller;

import com.orlando.starter.Model;
import com.orlando.starter.annotation.Controller;
import com.orlando.starter.annotation.RequestMapping;
import io.vertx.core.http.HttpMethod;

@Controller
public class IndexController {

  @RequestMapping(value = "/tmp", method = HttpMethod.GET)
  public String getTemplate(Model model) {
    // we define a hardcoded title for our application
    model.put("welcome", "Hi there!");
    return "templates/index.html";
  }
}
