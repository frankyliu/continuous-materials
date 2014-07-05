package fr.synchrotron.soleil.ica.proxy.midlleware;


import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class MiddlewareContext {

    private Vertx vertx;

    private String proxyPrefix;

    private HttpServerRequest httpServerRequest;

    private HttpClient httpClient;

    private HttpEndpointInfo httpEndpointInfo;

    private ProxyRequestType requestType;

    public MiddlewareContext(Vertx vertx, String proxyPrefix, HttpServerRequest httpServerRequest, HttpClient httpClient, HttpEndpointInfo httpEndpointInfo, ProxyRequestType requestType) {
        this.vertx = vertx;
        this.proxyPrefix = proxyPrefix;
        this.httpServerRequest = httpServerRequest;
        this.httpClient = httpClient;
        this.httpEndpointInfo = httpEndpointInfo;
        this.requestType = requestType;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public String getProxyPrefix() {
        return proxyPrefix;
    }

    public HttpServerRequest getHttpServerRequest() {
        return httpServerRequest;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public HttpEndpointInfo getHttpEndpointInfo() {
        return httpEndpointInfo;
    }

    public ProxyRequestType getRequestType() {
        return requestType;
    }

    public String getClientRequestPath() {
        String artifactPath = httpServerRequest.path().substring(proxyPrefix.length() + 1);
        String repoUri = httpEndpointInfo.getUri();
        return repoUri.endsWith("/") ? (repoUri + artifactPath) : (repoUri + "/" + artifactPath);
    }
}
