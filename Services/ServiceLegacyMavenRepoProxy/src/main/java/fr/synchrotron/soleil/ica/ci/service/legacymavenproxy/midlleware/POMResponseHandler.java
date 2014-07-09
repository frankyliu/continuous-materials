package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.midlleware;

import com.github.ebx.core.MessageFilterService;
import com.github.ebx.core.MessagingTemplate;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.ServiceAddressRegistry;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class POMResponseHandler extends DefaultClientResponseHandler {

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
            request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(messagePayload.getBytes().length));
            ProxyService proxyService = new ProxyService();
            proxyService.fixWarningCookieDomain(context, clientResponse);

            if (!"HEAD".equals(request.method())) {
                request.response().write(messagePayload);
            }
            request.response().end();
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
                        throwable.printStackTrace();
                        request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                        request.response().setStatusMessage(throwable.getMessage());
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
