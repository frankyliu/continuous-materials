package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.midlleware;

import com.github.ebx.core.MessageFilterService;
import com.github.ebx.core.MessagingTemplate;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.ServiceAddressRegistry;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.service.Sha1Getter;
import fr.synchrotron.soleil.ica.proxy.midlleware.MiddlewareContext;
import fr.synchrotron.soleil.ica.proxy.midlleware.ProxyService;
import fr.synchrotron.soleil.ica.proxy.midlleware.response.DefaultClientResponseHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.*;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class POMResponseHandler extends DefaultClientResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(POMResponseHandler.class);

    public POMResponseHandler(MiddlewareContext context) {
        super(context);
    }

    @Override
    public Handler<HttpClientResponse> get() {

        final String clientRequestPath = context.getClientRequestPath();
        final List<MessageFilterService> bodyClientResponseFilters = new ArrayList<>();
        bodyClientResponseFilters.add(new MessageFilterService(ServiceAddressRegistry.EB_ADDRESS_POMMETADATA_SERVICE, "fixWrongValue"));
        bodyClientResponseFilters.add(new MessageFilterService(ServiceAddressRegistry.EB_ADDRESS_POMMETADATA_SERVICE, "cache"));
        return new Handler<HttpClientResponse>() {
            @Override
            public void handle(final HttpClientResponse clientResponse) {

                final int statusCode = clientResponse.statusCode();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("Returned endpoint status code: %s", statusCode));
                }

                switch (statusCode) {
                    case 200:
                        processPomContent(clientResponse);
                        break;
                    case 404:
                        sendResponseWithoutTransferEncoding(clientResponse);
                        break;
                    default:
                        sendPassThroughResponseWithoutContent(clientResponse);
                        break;
                }
            }

            private void processPomContent(final HttpClientResponse clientResponse) {
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
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.putString("requestPath", clientRequestPath);
                        jsonObject.putString("content", clientRepsonseBody.toString());
                        applyClientResponseFiltersAndRespond(clientResponse, bodyClientResponseFilters, jsonObject);
                    }
                });
            }

            private void sendResponseWithoutTransferEncoding(final HttpClientResponse clientResponse) {
                final HttpServerRequest request = context.getHttpServerRequest();
                request.response().setStatusCode(clientResponse.statusCode());
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                request.response().headers().remove(HttpHeaders.TRANSFER_ENCODING);
                ProxyService proxyService = new ProxyService();
                proxyService.fixWarningCookieDomain(context, clientResponse);
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

            private void sendPassThroughResponseWithoutContent(final HttpClientResponse clientResponse) {
                final HttpServerRequest request = context.getHttpServerRequest();
                request.response().setStatusCode(clientResponse.statusCode());
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                ProxyService proxyService = new ProxyService();
                proxyService.fixWarningCookieDomain(context, clientResponse);
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

        };
    }

    private void applyClientResponseFiltersAndRespond(
            final HttpClientResponse clientResponse,
            final List<MessageFilterService> messageFilterServiceList,
            final JsonObject jsonObjectMessage) {

        final HttpServerRequest request = context.getHttpServerRequest();
        final Vertx vertx = context.getVertx();
        final String messagePayload = jsonObjectMessage.getString("content");

        if (messageFilterServiceList.size() == 0) {
            request.response().setStatusCode(clientResponse.statusCode());
            request.response().setStatusMessage(clientResponse.statusMessage());
            request.response().headers().set(clientResponse.headers());
            Sha1Getter sha1Getter = new Sha1Getter();
            request.response().putHeader(HttpHeaders.ETAG, String.valueOf(messagePayload.hashCode()));
            request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(messagePayload.getBytes().length));
            ProxyService proxyService = new ProxyService();
            proxyService.fixWarningCookieDomain(context, clientResponse);

            if (!"HEAD".equals(request.method())) {
                request.response().write(messagePayload);
            }

            endRequest();
            return;
        }

        final MessageFilterService messageFilterService = messageFilterServiceList.get(0);
        AsyncResultHandler<Message<String>> responseHandler = new AsyncResultHandler<Message<String>>() {
            @Override
            public void handle(AsyncResult<Message<String>> asyncResult) {
                if (asyncResult.succeeded()) {
                    messageFilterServiceList.remove(0);
                    jsonObjectMessage.putString("content", asyncResult.result().body());
                    applyClientResponseFiltersAndRespond(clientResponse, messageFilterServiceList, jsonObjectMessage);
                } else {
                    final Throwable throwable = asyncResult.cause();
                    if (throwable != null) {
                        LOG.error("error", throwable);
                        throwable.printStackTrace();
                        request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                        request.response().setStatusMessage(throwable.getMessage());
                        request.response().headers().set(clientResponse.headers());
                        ProxyService proxyService = new ProxyService();
                        proxyService.fixWarningCookieDomain(context, clientResponse);
                        request.response().end();
                    }
                }
            }
        };

        MessagingTemplate
                .address(vertx.eventBus(), messageFilterService.getAddress())
                .action(messageFilterService.getAction())
                .content(jsonObjectMessage).send(responseHandler);
    }

}
