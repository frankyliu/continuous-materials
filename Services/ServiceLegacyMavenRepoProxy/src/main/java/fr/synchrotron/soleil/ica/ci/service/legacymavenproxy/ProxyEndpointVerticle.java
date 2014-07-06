package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import fr.synchrotron.soleil.ica.proxy.midlleware.HttpEndpointInfo;
import fr.synchrotron.soleil.ica.proxy.midlleware.ProxyRequestType;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Gregory Boissinot
 */

public class ProxyEndpointVerticle extends BusModBase {

    @Override
    public void start() {

        super.start();

        final int port = getMandatoryIntConfig("proxyPort");
        final String proxyPath = getMandatoryStringConfig("proxyPath");
        HttpServer httpServer = null;
        try {
            RouteMatcher routeMatcher = new RouteMatcher();

            //--GET or HEAD
            headOrGetRequests(routeMatcher, proxyPath);

            //--PUT
            putRequests(routeMatcher, proxyPath);

            //-- NO MATCH
            routeMatcher.noMatch(new Handler<HttpServerRequest>() {
                @Override
                public void handle(HttpServerRequest request) {
                    request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
                    request.response().end();
                }
            });

            httpServer = vertx.createHttpServer();
            httpServer.requestHandler(routeMatcher);
            httpServer.listen(port);

            container.logger().info("Webserver proxy started, listening on port:" + port);

        } catch (Throwable e) {
            container.logger().error(e);
            if (httpServer != null) {
                httpServer.close();
            }
        }
    }

    private void headOrGetRequests(RouteMatcher routeMatcher, String proxyPath) {
        final JsonObject getJsonObject = config.getObject("repo.get");
        final String repoHostGET = getJsonObject.getString("host");
        final int repoPortGET = getJsonObject.getInteger("port");
        final String repoURIPathGET = getJsonObject.getString("uri");

        final HttpEndpointInfo httpEndpointInfo = new HttpEndpointInfo(repoHostGET, repoPortGET, repoURIPathGET);

        final ProxyRequestHandler pomProxyRequestHandler = new ProxyRequestHandler(vertx, proxyPath, httpEndpointInfo, ProxyRequestType.PON);
        final ProxyRequestHandler pomSha1ProxyRequestHandler = new ProxyRequestHandler(vertx, proxyPath, httpEndpointInfo, ProxyRequestType.POMSHA1);
        final ProxyRequestHandler anyProxyRequestHandler = new ProxyRequestHandler(vertx, proxyPath, httpEndpointInfo, ProxyRequestType.ANY);

        //--HEAD
        routeMatcher
                .headWithRegEx(proxyPath + "/.*.pom", pomProxyRequestHandler)
                .headWithRegEx(proxyPath + "/.*.pom.sha1", pomSha1ProxyRequestHandler)
                .headWithRegEx(proxyPath + "/.*", anyProxyRequestHandler);

        //--GET
        routeMatcher
                .getWithRegEx(proxyPath + "/.*.pom", pomProxyRequestHandler)
                .getWithRegEx(proxyPath + "/.*.pom.sha1", pomSha1ProxyRequestHandler)
                .getWithRegEx(proxyPath + "/.*", anyProxyRequestHandler);
    }

    private void putRequests(RouteMatcher routeMatcher, String proxyPath) {
        final JsonObject putJsonObject = config.getObject("repo.put");
        final String repoHostPUT = putJsonObject.getString("host");
        final int repoPortPUT = putJsonObject.getInteger("port");
        final String repoURIPathPUT = putJsonObject.getString("uri");

        final HttpEndpointInfo httpEndpointInfo = new HttpEndpointInfo(repoHostPUT, repoPortPUT, repoURIPathPUT);
        routeMatcher
                .putWithRegEx(proxyPath + "/.*.pom", new ProxyRequestHandler(vertx, proxyPath, httpEndpointInfo, ProxyRequestType.PON))
                .putWithRegEx(proxyPath + "/.*", new ProxyRequestHandler(vertx, proxyPath, httpEndpointInfo, ProxyRequestType.ANY));
    }

}