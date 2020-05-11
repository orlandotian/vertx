package com.orlando.starter.annotation;

import io.vertx.core.http.HttpMethod;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
  String name() default "";

  String value() default "/";

//  String[] path() default {};

  HttpMethod[] method() default {HttpMethod.GET};

  String[] params() default {};

  String[] headers() default {};

  String[] consumes() default {};

  String[] produces() default {};
}
