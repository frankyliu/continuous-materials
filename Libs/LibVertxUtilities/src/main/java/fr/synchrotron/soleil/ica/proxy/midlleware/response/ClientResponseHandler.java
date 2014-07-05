package fr.synchrotron.soleil.ica.proxy.midlleware.response;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClientResponse;

/**
 * @author Gregory Boissinot
 */
public interface ClientResponseHandler {

    public Handler<HttpClientResponse> get();
}
