package fr.synchrotron.soleil.ica.proxy.midlleware.response;

import fr.synchrotron.soleil.ica.proxy.midlleware.MiddlewareContext;
import fr.synchrotron.soleil.ica.proxy.midlleware.ProxyService;
import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpHeaders;
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

                final int statusCode = clientResponse.statusCode();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("Returned endpoint status code: %s", statusCode));
                }

                sendPassThroughResponse(clientResponse);
//                switch (statusCode) {
//                    case 404:
//                        sendWithoutTransferEncodingResponse(clientResponse);
//                        break;
//                    default:
//                        sendPassThroughResponse(clientResponse);
//                        break;
//                }
            }

            private void sendWithoutTransferEncodingResponse(final HttpClientResponse clientResponse) {
                final HttpServerRequest request = context.getHttpServerRequest();
                request.response().setStatusCode(clientResponse.statusCode());
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                request.response().headers().remove(HttpHeaders.TRANSFER_ENCODING);
                ProxyService proxyService = new ProxyService();
                proxyService.fixWarningCookieDomain(context, clientResponse);
                //endRequest();
                final Buffer clientRepsonseBody = new Buffer();
                clientResponse.dataHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(final Buffer data) {
                        clientRepsonseBody.appendBuffer(data);
                    }
                });
                clientResponse.endHandler(new VoidHandler() {
                    @Override
                    protected void handle() {
                        request.response().write(clientRepsonseBody);
                        endRequest();
                    }
                });
            }

            private void sendPassThroughResponse(final HttpClientResponse clientResponse) {
                clientResponse.pause();
                request.response().setStatusCode(clientResponse.statusCode());
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                ProxyService proxyService = new ProxyService();
                proxyService.fixWarningCookieDomain(context, clientResponse);
                clientResponse.endHandler(new Handler<Void>() {
                    public void handle(Void event) {
                        endRequest();
                    }
                });

//                if (HttpMethod.GET.name().equals(request.method())) {
                request.response().setChunked(true);
                Pump.createPump(clientResponse, request.response()).start();
//                }

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
