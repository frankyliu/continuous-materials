package fr.synchrotron.soleil.ica.ci.service.multirepoproxy;

import fr.synchrotron.soleil.ica.proxy.midlleware.HttpEndpointInfo;
import fr.synchrotron.soleil.ica.proxy.midlleware.MiddlewareContext;
import fr.synchrotron.soleil.ica.proxy.midlleware.ProxyRequestType;
import fr.synchrotron.soleil.ica.proxy.midlleware.ProxyService;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class PUTRequestHandler implements Handler<HttpServerRequest> {

    private Vertx vertx;

    private String proxyPath;

    private HttpEndpointInfo httpEndpointInfo;

    public PUTRequestHandler(Vertx vertx, String proxyPath, HttpEndpointInfo httpEndpointInfo) {
        this.vertx = vertx;
        this.proxyPath = proxyPath;
        this.httpEndpointInfo = httpEndpointInfo;
    }

    @Override
    public void handle(HttpServerRequest request) {
        final HttpClient httpClient = vertx.createHttpClient()
                .setHost(httpEndpointInfo.getHost())
                .setPort(httpEndpointInfo.getPort())
                .setKeepAlive(false);
        ProxyService proxyService = new ProxyService();
        proxyService.makePushRequestAndRespond(new MiddlewareContext(vertx, proxyPath, request, httpClient, httpEndpointInfo, ProxyRequestType.ANY));
    }
}
