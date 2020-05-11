package com.orlando.starter;

import com.orlando.starter.annotation.Controller;
import com.orlando.starter.annotation.RequestMapping;
import com.orlando.starter.annotation.RestController;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

public class VerticleStarter extends AbstractVerticle {
  private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());


  protected ThymeleafTemplateEngine engine;
  protected Router router;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    engine = ThymeleafTemplateEngine.create(vertx);
    router = Router.router(vertx);
  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    this.start();
  }

  @Override
  public void start() throws Exception {
    loadController();
    router.route().handler(BodyHandler.create());
    router.route().handler(StaticHandler.create().setCachingEnabled(false));
  }

  private void loadController() throws Exception {
    Reflections reflections = new Reflections("com.orlando", new SubTypesScanner(), new MethodAnnotationsScanner(), new TypeAnnotationsScanner());
    Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
    for (Class<?> controller : controllers) {
      Component.putComponents(controller.getName(), controller.newInstance());
    }
    controllers = reflections.getTypesAnnotatedWith(RestController.class);
    for (Class<?> controller : controllers) {
      Component.putComponents(controller.getName(), controller.newInstance());
    }

    Set<Method> requestMethods = reflections.getMethodsAnnotatedWith(RequestMapping.class);
    for (Method method : requestMethods) {
      if (method.getReturnType() == Void.TYPE) {
        throw new RuntimeException("Request controller return null type, Class: " + method.getDeclaringClass().getName() + "$" + method.getName());
      }
      if (method.getModifiers() != 1) {
        throw new RuntimeException("Request method not public, Class: " + method.getDeclaringClass().getName() + "$" + method.getName());
      }
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      //
      Component.putRequests(requestMapping.value(), method);
      // 加载路由
      for (HttpMethod requestMethod : requestMapping.method()) {
        router.route(requestMethod, requestMapping.value()).handler(this::callback);
      }
    }

  }

  private void callback(RoutingContext rct) {
    Method method = Component.getMappingMethod(rct.request().path());
    Object o = Component.getComponent(method.getDeclaringClass().getName());
    if (o == null) {
      rct.response().setStatusCode(503).end("Internal server error");
    }
    try {
      Class<?>[] parameters = method.getParameterTypes();
      Object[] args = new Object[parameters.length];
      Model model = null;

      for (int i = 0; i < parameters.length; i++) {
        if (parameters[i] == RoutingContext.class) {
          args[i] = rct;
        } else if (parameters[i] == HttpServerRequest.class) {
          args[i] = rct.request();
        } else if (parameters[i] == HttpServerResponse.class) {
          args[i] = rct.response();
        } else if (parameters[i] == ThymeleafTemplateEngine.class) {
          args[i] = engine;
        } else if (parameters[i] == Model.class) {
          model = new Model();
          args[i] = model;
        }
      }
      Object result = method.invoke(o, args);
      if (method.getDeclaringClass().getAnnotation(Controller.class) != null && method.getReturnType() == String.class) {
        engine.render(model, (String) result, res -> {
          if (res.succeeded()) {
            rct.response().end(res.result());
          } else {
            rct.fail(res.cause());
          }
        });
      } else if (method.getDeclaringClass().getAnnotation(RestController.class) != null) {
        rct.response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(result));
      } else {

      }

    } catch (Exception e) {
      logger.error(getClass().getName(), e.getMessage());
      rct.response().setStatusCode(503).end("Internal server error :" + e.getMessage());
    }
  }
}
