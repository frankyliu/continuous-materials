package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;

/**
 * @author Gregory Boissinot
 */
public class DORMProxyEndpointVerticle extends BusModBase {

   // public static final String PROXY_PATH = "/dormservice";

    @Override
    public void start() {

        super.start();

        final int port = getMandatoryIntConfig("httpPort");
        final String proxyPath = getMandatoryStringConfig("proxyPath");
        final String fsRepositoryRootDir = getMandatoryStringConfig("fs.repository.rootdir");

        final HttpServer httpServer = vertx.createHttpServer();
        RouteMatcher routeMatcher = new RouteMatcher();

        //=========================
        //=============  MAVEN
        //=========================
        routeMatcher.putWithRegEx(proxyPath + "/.*.pom", new PUTPOMHandler(vertx));
        routeMatcher.putWithRegEx(proxyPath + "/.*/maven-metadata.xml", new PUTNoActionHandler());
        routeMatcher.putWithRegEx(proxyPath + "/.*/maven-metadata.xml.sha1", new PUTNoActionHandler());
        routeMatcher.putWithRegEx(proxyPath + "/.*/maven-metadata.xml.md5", new PUTNoActionHandler());
        routeMatcher.putWithRegEx(proxyPath + "/.*.pom.sha1", new PUTNoActionHandler());
        routeMatcher.putWithRegEx(proxyPath + "/.*.pom.md5", new PUTNoActionHandler());
        routeMatcher.putWithRegEx(proxyPath + "/.*", new PUTFileHandler(vertx, fsRepositoryRootDir, proxyPath));

        routeMatcher.getWithRegEx(proxyPath + "/.*/maven-metadata.xml", new GETMetadataHandler());
        routeMatcher.getWithRegEx(proxyPath + "/.*", new GETFileHandler(vertx, fsRepositoryRootDir, proxyPath));
        //TODO
        routeMatcher.getWithRegEx(proxyPath + "/.*.pom", new GETPOMHandler(vertx));


        routeMatcher.allWithRegEx(proxyPath + "/.*", new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest request) {
                request.response().setStatusCode(HttpResponseStatus.METHOD_NOT_ALLOWED.code());
                request.response().end();
            }
        });
        routeMatcher.noMatch(new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest request) {
                request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
                request.response().end();
            }
        });

        httpServer.requestHandler(routeMatcher);
        httpServer.listen(port);

        container.logger().info("Webserver proxy started, listening on port:" + port);

    }
}
