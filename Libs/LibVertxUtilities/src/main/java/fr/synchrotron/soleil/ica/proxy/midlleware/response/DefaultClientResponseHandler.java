package fr.synchrotron.soleil.ica.proxy.midlleware.response;

import fr.synchrotron.soleil.ica.proxy.midlleware.MiddlewareContext;
import fr.synchrotron.soleil.ica.proxy.midlleware.ProxyService;
import io.netty.handler.codec.http.HttpMethod;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.core.streams.Pump;

/**
 * @author Gregory Boissinot
 */
public class DefaultClientResponseHandler implements ClientResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultClientResponseHandler.class);

    protected MiddlewareContext context;

    public DefaultClientResponseHandler(MiddlewareContext context) {
        this.context = context;
    }

    public Handler<HttpClientResponse> get() {

        final HttpServerRequest request = context.getHttpServerRequest();
        return new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                clientResponse.pause();
                final int statusCode = clientResponse.statusCode();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("Returned endpoint status code: %s", statusCode));
                }
                request.response().setStatusCode(statusCode);
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                ProxyService proxyService = new ProxyService();
                proxyService.fixWarningCookieDomain(context, clientResponse);
                clientResponse.endHandler(new Handler<Void>() {
                    public void handle(Void event) {
                        endRequest();
                    }
                });
                if (HttpMethod.GET.name().equals(request.method())) {
                    Pump.createPump(clientResponse, request.response()).start();
                }
                clientResponse.resume();
            }
        };
    }

    protected void endRequest() {
        final HttpServerRequest request = context.getHttpServerRequest();
        final HttpClient httpClient = context.getHttpClient();
        request.response().end();
        if (httpClient != null) {
            httpClient.close();
        }
    }


}
