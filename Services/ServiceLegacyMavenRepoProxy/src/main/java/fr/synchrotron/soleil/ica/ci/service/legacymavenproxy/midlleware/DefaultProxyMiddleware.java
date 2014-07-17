package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.midlleware;

import com.github.ebx.core.MessagingTemplate;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.ServiceAddressRegistry;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pommetadata.POMCache;
import fr.synchrotron.soleil.ica.proxy.midlleware.*;
import fr.synchrotron.soleil.ica.proxy.midlleware.response.DefaultClientResponseHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.*;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

/**
 * @author Gregory Boissinot
 */
public class DefaultProxyMiddleware implements ProxyMiddleware {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultProxyMiddleware.class);

    @Override
    public void pull(MiddlewareContext context) {

        final ProxyRequestType requestType = context.getRequestType();
        final String clientRequestPath = context.getClientRequestPath();
        final HttpServerRequest request = context.getHttpServerRequest();

        if (ProxyRequestType.POMSHA1.equals(requestType)) {
            final Vertx vertx = context.getVertx();
            final POMCache pomCache = new POMCache(vertx);
            final String sha1 = pomCache.getSha1(clientRequestPath);
            if (sha1 != null) {
                request.response().setStatusCode(HttpResponseStatus.OK.code());
                request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(sha1.getBytes().length));
                request.response().end(sha1);
                return;
            }
        }
        makePullRequestAndRespond(context);
    }

    private void makePullRequestAndRespond(final MiddlewareContext context) {

        final ProxyRequestType requestType = context.getRequestType();
        final HttpServerRequest request = context.getHttpServerRequest();
        final ProxyService proxyService = new ProxyService();

        HttpClientRequest clientRequest;
        switch (requestType) {
            case POM:
                clientRequest = proxyService.getClientRequest(context, new POMResponseHandler(context).get());
                break;
            case ANY:
                clientRequest = proxyService.getClientRequest(context, new DefaultClientResponseHandler(context).get());
                break;
            default:
                throw new ProxyException("Wrong request type:" + requestType);
        }

        clientRequest.headers().set(request.headers());
        clientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                LOG.error("error", throwable);
                ProxyService proxyService = new ProxyService();
                proxyService.sendError(request, throwable);
                context.getHttpClient().close();
            }
        });
        clientRequest.end();
    }

    @Override
    public void push(MiddlewareContext context) {
        final ProxyRequestType requestType = context.getRequestType();
        switch (requestType) {
            case POM:
                uploadPom(context);
                break;
            case ANY:
                ProxyService proxyService = new ProxyService();
                proxyService.makePushRequestAndRespond(context);
                break;
            default:
                throw new ProxyException("Wrong request type:" + requestType);
        }
    }

    private void uploadPom(final MiddlewareContext context) {

        final HttpClient httpClient = context.getHttpClient();
        final String clientRequestPath = context.getClientRequestPath();
        final HttpServerRequest request = context.getHttpServerRequest();

        final Buffer pomContentBuffer = new Buffer();
        final HttpClientRequest vertxHttpClientRequest = httpClient.put(clientRequestPath, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                final int statusCode = clientResponse.statusCode();
                request.response().setStatusCode(statusCode);
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                ProxyService proxyService = new ProxyService();
                proxyService.fixWarningCookieDomain(context, clientResponse);
                clientResponse.endHandler(new Handler<Void>() {
                    public void handle(Void event) {
                        final AsyncResultHandler<Message<Void>> replyHandler = new AsyncResultHandler<Message<Void>>() {
                            @Override
                            public void handle(AsyncResult<Message<Void>> asyncResult) {
                                if (asyncResult.failed()) {
                                    asyncResult.cause().printStackTrace();
                                    LOG.error(asyncResult.cause().getMessage());
                                    request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                                    request.response().setStatusMessage(asyncResult.cause().getMessage());
                                }
                                request.response().end();
                                context.closeHttpClient();
                            }
                        };
                        final JsonObject jsonObject = new JsonObject();
                        jsonObject.putString("action", "store");
                        jsonObject.putString("content", pomContentBuffer.toString());
                        MessagingTemplate
                                .address(context.getVertx().eventBus(), ServiceAddressRegistry.EB_ADDRESS_POMMETADATA_SERVICE)
                                .action("store")
                                .content(jsonObject).send(replyHandler);
                    }
                });
            }
        });

        //vertxHttpClientRequest.setChunked(true);
        vertxHttpClientRequest.headers().set(request.headers());
        vertxHttpClientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                LOG.error("error", throwable);
                ProxyService proxyService = new ProxyService();
                proxyService.sendError(request, throwable);
                context.closeHttpClient();
            }
        });

        request.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer data) {
                pomContentBuffer.appendBuffer(data);
                vertxHttpClientRequest.write(data);
            }
        });

        request.endHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                vertxHttpClientRequest.end();
            }
        });
    }

}


