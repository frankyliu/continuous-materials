package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class GETMetadataHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {

        final String path = request.path();
        final String method = request.method();
        System.out.println(method + " " + path);

        request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
        request.response().end();

    }
}
