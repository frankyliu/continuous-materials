package fr.synchrotron.soleil.ica.ci.service.multirepoproxy;


import fr.synchrotron.soleil.ica.proxy.midlleware.HttpEndpointInfo;
import fr.synchrotron.soleil.ica.proxy.midlleware.MiddlewareContext;
import fr.synchrotron.soleil.ica.proxy.midlleware.ProxyRequestType;
import fr.synchrotron.soleil.ica.proxy.midlleware.ProxyService;
import fr.synchrotron.soleil.ica.proxy.midlleware.response.DefaultClientResponseHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

import java.nio.channels.UnresolvedAddressException;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class MutltiGETHandler implements Handler<HttpServerRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(MutltiGETHandler.class);

    private final Vertx vertx;
    private final RepositoryScanner repositoryScanner;
    private final String proxyPath;

    public MutltiGETHandler(Vertx vertx, String proxyPath, List<HttpEndpointInfo> repos) {
        this.vertx = vertx;

        if (repos == null || repos.size() == 0) {
            throw new IllegalArgumentException("repos");
        }
        this.repositoryScanner = new RepositoryScanner(repos);
        this.proxyPath = proxyPath;
    }

    @Override
    public void handle(final HttpServerRequest request) {
        processRepository(request, 0);
    }

    private void processRepository(final HttpServerRequest request,
                                   final int repoIndex) {

        if (repositoryScanner.isLastRepo(repoIndex)) {
            request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
            request.response().setStatusMessage("Artifact NOT FOUND");
            request.response().end();
            return;
        }

        LOG.info("Trying to download " + request.path() + "from " + repositoryScanner.getRepoFromIndex(repoIndex));

        final HttpEndpointInfo httpEndpointInfo = repositoryScanner.getRepoFromIndex(repoIndex);

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

        final MiddlewareContext context = new MiddlewareContext(vertx, proxyPath, request, httpClient, httpEndpointInfo, ProxyRequestType.ANY);

        HttpClientRequest clientRequest = httpClient.head(context.getClientRequestPath(), new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {

                switch (clientResponse.statusCode()) {
                    case 200:
                        if ("HEAD".equals(request.method())) {
                            request.response().setStatusCode(clientResponse.statusCode());
                            request.response().headers().set(clientResponse.headers());
                            clientResponse.endHandler(new Handler<Void>() {
                                public void handle(Void event) {
                                    request.response().end();
                                    httpClient.close();
                                }
                            });
                        } else {
                            ProxyService proxyService = new ProxyService();
                            HttpClientRequest getClientRequest = proxyService.getClientRequest(context, new DefaultClientResponseHandler(context).get());
                            getClientRequest.headers().set(request.headers());
                            getClientRequest.exceptionHandler(exceptionHandler);
                            getClientRequest.end();
                            httpClient.close();
                        }
                        break;
                    case 301:
                    case 404:
                        httpClient.close();
                        processRepository(request, repositoryScanner.getNextIndex(repoIndex));
                        break;
                    default:
                        request.response().setStatusCode(clientResponse.statusCode());
                        request.response().setStatusMessage(clientResponse.statusMessage());
                        request.response().end();
                        httpClient.close();
                        break;
                }
            }
        });

        clientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                if (isUnreasolvedHost(throwable)) {
                    processRepository(request, repositoryScanner.getNextIndex(repoIndex));
                } else {
                    ProxyService proxyService = new ProxyService();
                    proxyService.sendError(request, throwable);
                }
            }

            private boolean isUnreasolvedHost(Throwable throwable) {
                return throwable.getClass().equals(UnresolvedAddressException.class);
            }
        });

        clientRequest.end();
    }

}
