package fr.synchrotron.soleil.ica.ci.test.vertx.sample;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */
public class SampleVerticle extends Verticle {

    private static final Logger LOG = LoggerFactory.getLogger(SampleVerticle.class);

    @Override
    public void start() {
        HttpServer server = this.vertx.createHttpServer();
        server.requestHandler(new Handler<HttpServerRequest>() {
            @Override
            public void handle(final HttpServerRequest request) {
                LOG.info(".");
                vertx.eventBus().send("sample.address", new JsonObject(), new Handler<Message<Boolean>>() {
                    @Override
                    public void handle(Message<Boolean> message) {
                        request.response().putHeader("content-type", "text/plain");
                        request.response().setStatusCode(HttpResponseStatus.OK.code());
                        request.response().end();
                    }
                });
            }
        });
        server.setAcceptBacklog(10000);
        server.setSendBufferSize(4 * 1024);
        server.setReceiveBufferSize(4 * 1024);
        server.listen(8080);
    }

}
