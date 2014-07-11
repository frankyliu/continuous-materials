package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import com.github.ebx.core.MessagingTemplate;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.eventbus.ReplyException;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

/**
 * @author Gregory Boissinot
 */
public class GETPOMHandler implements Handler<HttpServerRequest> {
    private  final Logger logger = LoggerFactory.getLogger(GETPOMHandler.class);
    private Vertx vertx;

    private String proxyPrefix;

    public GETPOMHandler(Vertx vertx, String proxyPrefix) {
        this.vertx = vertx;
        this.proxyPrefix = proxyPrefix;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final String path = request.path();
        final String method = request.method();
     //   System.out.println(method + " " + path);
        logger.debug(method + " " + path);

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
                    request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(pomContent.getBytes().length));
                    if ("HEAD".equals(method)) {
                        request.response().end();
                    } else {
                        request.response().end(pomContent);
                    }
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
                            logger.error("error", cause);
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
