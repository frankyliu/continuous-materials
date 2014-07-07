package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.midlleware.DefaultProxyMiddleware;
import fr.synchrotron.soleil.ica.proxy.midlleware.*;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class ProxyRequestHandler implements Handler<HttpServerRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyRequestHandler.class);

    private final Vertx vertx;

    private String proxyPrefix;

    private final HttpEndpointInfo httpEndpointInfo;

    private ProxyRequestType requestType;

    public ProxyRequestHandler(Vertx vertx, String proxyPrefix, HttpEndpointInfo httpEndpointInfo, ProxyRequestType requestType) {
        this.vertx = vertx;
        this.proxyPrefix = proxyPrefix;
        this.httpEndpointInfo = httpEndpointInfo;
        this.requestType = requestType;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Incoming client request : " + request.method() + " " + request.uri());
        }

        //We have here a particular situation (reverse proxy).
        //We create an HttpClient object for each server request
        //It is supposed to be lightweight
        final HttpClient httpClient = vertx.createHttpClient()
                .setHost(httpEndpointInfo.getHost())
                .setPort(httpEndpointInfo.getPort())
                .setKeepAlive(false);

        final Handler<Throwable> exceptionHandler = new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                ProxyService proxyService = new ProxyService();
                proxyService.sendError(request, throwable);
                if (httpClient != null) {
                    httpClient.close();
                }
            }
        };

        request.exceptionHandler(exceptionHandler); //Sample use case: timeout on client request
        httpClient.exceptionHandler(exceptionHandler);
        cleanRequestHttpHeaders(request);

        MiddlewareContext middlewareContext =
                new MiddlewareContext(vertx, proxyPrefix, request, httpClient, httpEndpointInfo, requestType);
        DefaultProxyMiddleware proxyMiddleware = new DefaultProxyMiddleware();

        final String method = request.method();
        switch (method) {
            case "HEAD":
            case "GET":
                proxyMiddleware.pull(middlewareContext);
                break;
            case "PUT":
                proxyMiddleware.push(middlewareContext);
                break;
            default:
                throw new ProxyException(method + " is not supported.");
        }
    }

    private void cleanRequestHttpHeaders(HttpServerRequest request) {
        final MultiMap headers = request.headers();
        for (Map.Entry<String, String> header : headers) {
            String headerValue = header.getValue();
            if (headerValue == null) {
                headers.remove(header.getKey());
            }
        }
        headers.remove(HttpHeaders.KEEP_ALIVE);
        headers.remove(HttpHeaders.CONNECTION);  //not necessary with keepAlive to false from clients
    }
}
