package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.*;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.streams.Pump;

import java.io.File;

/**
 * @author Gregory Boissinot
 */
public class GETFileHandler implements Handler<HttpServerRequest> {

    private final Vertx vertx;
    private final String fsRepositoryRootDir;
    private final String proxyPath;

    public GETFileHandler(Vertx vertx, String fsRepositoryRootDir, String proxyPath) {
        this.vertx = vertx;
        this.fsRepositoryRootDir = fsRepositoryRootDir;
        this.proxyPath = proxyPath;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final String path = request.path();
        final String method = request.method();
        System.out.println(method + " " + path);

        String artifactPath = path.substring(proxyPath.length() + 1);

        //TODO check
        final File getterDirectory = new File(fsRepositoryRootDir, artifactPath.substring(0, artifactPath.lastIndexOf("/")));
        final String filename = artifactPath.substring(artifactPath.lastIndexOf("/") + 1);
        final File getterFile = new File(getterDirectory, filename);

        vertx.fileSystem().exists(getterFile.getPath(), new AsyncResultHandler<Boolean>() {
            @Override
            public void handle(AsyncResult<Boolean> asyncResult) {
                if (asyncResult.result().booleanValue()) {
                    vertx.fileSystem().open(getterFile.getPath(), new AsyncResultHandler<AsyncFile>() {
                        public void handle(AsyncResult<AsyncFile> ar) {
                            if (ar.succeeded()) {
                                AsyncFile asyncFile = ar.result();
                                request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(getterFile.length()));
                                if ("GET".equals(method)) {
                                    request.response().setChunked(true);
                                    Pump.createPump(asyncFile, request.response()).start();
                                }
                                asyncFile.endHandler(new VoidHandler() {
                                    public void handle() {
                                        request.response().end();
                                    }
                                });
                            } else {
                                request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
                                request.response().end();
                            }
                        }
                    });
                } else {
                    request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
                    request.response().end();
                }

            }
        });


    }
}
