package com.orlando.starter;

import com.orlando.starter.annotation.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerticleStarter extends AbstractVerticle {
  private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());


  protected ThymeleafTemplateEngine engine;
  protected Router router;
  protected MongoClient mongo;


  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    engine = ThymeleafTemplateEngine.create(vertx);
    router = Router.router(vertx);
    mongo = MongoClient.createShared(vertx, new JsonObject().put("db_name", "kongfu").put("connection_string", "mongodb://kongfu:zaq1xsw2@127.0.0.1:27017/kongfu?authSource=admin"));
  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    this.start();
  }

  @Override
  public void start() throws Exception {
    loadComponent();
    router.route().handler(BodyHandler.create());
    router.route().handler(StaticHandler.create().setCachingEnabled(false));
  }

  private void loadComponent() throws Exception {
    Component.setMongoClient(mongo);
    Reflections reflections = new Reflections("com.orlando",
      new SubTypesScanner(), new MethodAnnotationsScanner(),
      new TypeAnnotationsScanner(), new FieldAnnotationsScanner());
    loadController(reflections);
    loadRepository(reflections);
    loadAutowired(reflections);
  }

  private void loadController(Reflections reflections) throws Exception {
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
      if (method.getReturnType() != Void.TYPE) {
        throw new RuntimeException("Request controller must return null type, Class: " + method.getDeclaringClass().getName() + "$" + method.getName());
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

  private void loadRepository(Reflections reflections) throws Exception {
    Set<Class<?>> repositories = reflections.getTypesAnnotatedWith(MongoRepository.class);
    for (Class<?> repository : repositories) {
      Class<?> repositoryProxyClass = Proxy.getProxyClass(repository.getClassLoader(), repository);
      Constructor<?> cons = repositoryProxyClass.getConstructor(InvocationHandler.class);
      String collection = repository.getAnnotation(MongoRepository.class).value();
      if (collection == null || collection.isEmpty()) {
        String classname = repository.getSimpleName();
        classname = Util.coverHump(classname);
        Pattern p = Pattern.compile("^i_(.+)_repository");
        Matcher m = p.matcher(classname);
        if (m.find()) {
          collection = m.group(1);
        }
      }
      BaseRepository target = new BaseRepository(mongo, collection) {

      };
      IRepository impRepository = (IRepository) cons.newInstance(new MongoRepositoryHandler(target));
      Component.putComponents(repository.getName(), impRepository);
    }
  }

  private void loadAutowired(Reflections reflections) throws Exception {
    Set<Field> fields = reflections.getFieldsAnnotatedWith(Autowired.class);
    for (Field field : fields) {
      Object o = Component.getComponent(field.getDeclaringClass().getName());
      field.set(o, Component.getComponent(field.getType().getName()));
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
          args[i] = new Model();
        } else if (parameters[i] == RCTCallback.class) {
          args[i] = new RCTCallback() {
            @Override
            public void done(JsonObject result) {
              rct.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .end(Json.encodePrettily(result));
            }

            public void done(List<JsonObject> result) {
              rct.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .end(Json.encodePrettily(result));
            }

            @Override
            public void done(JsonObject result, String template) {
              engine.render(result, template, res -> {
                if (res.succeeded()) {
                  rct.response().end(res.result());
                } else {
                  rct.fail(res.cause());
                }
              });
            }

            @Override
            public void done(String content) {
              rct.response().end(content);
            }
          };
        }
      }
      System.out.println("Before: " + method.getDeclaringClass().getName() + "$" + method.getName());
      method.invoke(o, args);
      System.out.println("After: " + method.getDeclaringClass().getName() + "$" + method.getName());
    } catch (Exception e) {
      logger.error(getClass().getName(), e.getMessage());
      rct.response().setStatusCode(503).end("Internal server error :" + e.getMessage());
    }
  }

  class MongoRepositoryHandler implements InvocationHandler {

    private IRepository target;

    public MongoRepositoryHandler(IRepository target) {
      this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      Object ret = null;
      System.out.println("Before: " + method.getDeclaringClass().getName() + "$" + method.getName());
      ret = method.invoke(target, args);
      System.out.println("After: " + method.getDeclaringClass().getName() + "$" + method.getName());
      return ret;
    }
  }
}
