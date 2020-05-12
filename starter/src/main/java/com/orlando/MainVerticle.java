package com.orlando;

import com.orlando.starter.VerticleStarter;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends VerticleStarter {

  private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());


  @Override
  public void start(Future<Void> startFuture) throws Exception {
    super.start(startFuture);
    vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        startFuture.complete();
        logger.info("HTTP server started on port 8888");
      } else {
        startFuture.fail(http.cause());
      }
    });
  }
}
