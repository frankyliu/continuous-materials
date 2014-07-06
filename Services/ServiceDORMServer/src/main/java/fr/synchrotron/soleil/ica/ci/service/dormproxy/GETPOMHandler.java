package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import com.github.ebx.core.MessagingTemplate;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.eventbus.ReplyException;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Gregory Boissinot
 */
public class GETPOMHandler implements Handler<HttpServerRequest> {

    private Vertx vertx;

    private String proxyPrefix;

    public GETPOMHandler(Vertx vertx, String proxyPrefix) {
        this.vertx = vertx;
        this.proxyPrefix = proxyPrefix;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final String path = request.path();
        System.out.println("GET" + path);

        QueryObjectService queryObjectService = new QueryObjectService();
        final String queryPath = extractPomPath(path);
        JsonObject pomQuery = queryObjectService.getMavenQueryObject(queryPath);
        MessagingTemplate.address(vertx.eventBus(), ServiceAddressRegistry.EB_ADDRESS_POMIMPORT_SERVICE)
                .content(pomQuery)
                .action("export").send(new AsyncResultHandler<Message<String>>() {
            @Override
            public void handle(AsyncResult<Message<String>> asyncResult) {
                if (asyncResult.succeeded()) {
                    String pomContent = asyncResult.result().body();
                    request.response().setStatusCode(HttpResponseStatus.OK.code());
                    request.response().end(pomContent);
                } else {

                    if (asyncResult.cause() instanceof ReplyException) {
                        ReplyException replyException = (ReplyException) asyncResult.cause();
                        final int failureCode = replyException.failureCode();
                        if (failureCode == 0) {
                            request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
                            request.response().end();
                        }
                    } else {
                        request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                        final Throwable cause = asyncResult.cause();
                        if (cause != null && cause.getMessage() != null) {
                            request.response().setStatusMessage(cause.getMessage());
                        }
                        request.response().end();
                    }
                }
            }
        });
    }

    private String extractPomPath(String path) {
        if (proxyPrefix.endsWith("/")) {
            return path.substring(proxyPrefix.length());
        } else {
            return path.substring((proxyPrefix + "/").length());
        }
    }

}
