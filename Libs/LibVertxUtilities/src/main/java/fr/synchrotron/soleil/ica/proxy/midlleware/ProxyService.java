package fr.synchrotron.soleil.ica.proxy.midlleware;

import fr.synchrotron.soleil.ica.proxy.midlleware.response.DefaultClientResponseHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.*;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.core.streams.Pump;

/**
 * @author Gregory Boissinot
 */
public class ProxyService {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyService.class);

    public void sendError(HttpServerRequest request, Throwable throwable) {
        LOG.error("Severe error during request processing :", throwable);
        request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        throwable.printStackTrace();
        final String message = throwable.getMessage();
        if (message != null) {
            request.response().setStatusMessage(message);
        }
        request.response().end();
    }

    public HttpClientRequest getClientRequest(MiddlewareContext context, Handler<HttpClientResponse> clientResponseHandler) {

        final HttpServerRequest request = context.getHttpServerRequest();
        final HttpClient httpClient = context.getHttpClient();
        final String clientRequestPath = context.getClientRequestPath();

        HttpClientRequest clientRequest;
        switch (request.method()) {
            case "HEAD":
                final ProxyRequestType requestType = context.getRequestType();
                if (ProxyRequestType.PON.equals(requestType)) {
                    clientRequest = httpClient.get(clientRequestPath, clientResponseHandler);
                } else {
                    clientRequest = httpClient.head(clientRequestPath, clientResponseHandler);
                }
                break;
            case "GET":
                clientRequest = httpClient.get(clientRequestPath, clientResponseHandler);
                break;
            default:
                throw new ProxyException(request.method() + " is not supported.");
        }

        return clientRequest;
    }

    public void fixWarningCookieDomain(MiddlewareContext context, HttpClientResponse clientResponse) {
        final HttpServerRequest request = context.getHttpServerRequest();
        final String setCookie = clientResponse.headers().get(HttpHeaders.SET_COOKIE);
        if (setCookie != null) {
            request.response().headers().set(HttpHeaders.SET_COOKIE, getNewCookieContent(context, setCookie));
        }
    }

    private String getNewCookieContent(MiddlewareContext context, String cookie) {
        final HttpEndpointInfo httpEndpointInfo = context.getHttpEndpointInfo();
        String repoUri = httpEndpointInfo.getUri();
        int index = repoUri.indexOf("/", 1);
        if (index < 0)
            index = repoUri.length();
        return cookie.replace(repoUri.substring(0, index), context.getProxyPrefix());
    }

    public void makePushRequestAndRespond(MiddlewareContext context) {

        final HttpServerRequest request = context.getHttpServerRequest();
        final String clientRequestPath = context.getClientRequestPath();
        final HttpClient httpClient = context.getHttpClient();

        request.pause();

        final HttpClientRequest vertxHttpClientRequest = httpClient.put(clientRequestPath, new DefaultClientResponseHandler(context).get());
        vertxHttpClientRequest.headers().set(request.headers());
        vertxHttpClientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                ProxyService proxyService = new ProxyService();
                proxyService.sendError(request, throwable);
                httpClient.close();
            }
        });

        request.endHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                vertxHttpClientRequest.end();
            }
        });

        final Pump pump = Pump.createPump(request, vertxHttpClientRequest);
        pump.start();
        request.resume();

    }

}
