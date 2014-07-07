package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.*;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.core.streams.Pump;

import java.io.File;

/**
 * @author Gregory Boissinot
 */
public class PUTFileHandler implements Handler<HttpServerRequest> {
    private  final Logger logger = LoggerFactory.getLogger(PUTFileHandler.class);
    private final Vertx vertx;
    private final String fsRepositoryRootDir;
    private final String proxyPath;

    public PUTFileHandler(Vertx vertx, String fsRepositoryRootDir, String proxyPath) {
        this.vertx = vertx;
        this.fsRepositoryRootDir = fsRepositoryRootDir;
        this.proxyPath =proxyPath;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final String path = request.path();
        logger.debug("archiving "+ path);

        String artifactPath = path.substring(proxyPath.length() + 1);

        //TODO check
        final File uploadedDirectory = new File(fsRepositoryRootDir, artifactPath.substring(0, artifactPath.lastIndexOf("/")));
        final String filename = artifactPath.substring(artifactPath.lastIndexOf("/") + 1);
        final File uploadedFile = new File(uploadedDirectory, filename);


        //----When  upload a file
        //request.expectMultiPart(true);
//        request.uploadHandler(new Handler<HttpServerFileUpload>() {
//            @Override
//            public void handle(final HttpServerFileUpload upload) {
//
//                upload.exceptionHandler(new Handler<Throwable>() {
//                    @Override
//                    public void handle(Throwable throwable) {
//                        request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
//                        request.response().setStatusMessage(throwable.getMessage());
//                        request.response().end();
//                    }
//                });
//
//                upload.endHandler(new Handler<Void>() {
//                    @Override
//                    public void handle(Void event) {
//                        request.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code());
//                        request.response().end();
//                    }
//                });
//
//                vertx.fileSystem().mkdir(uploadedDirectory.getPath(), true, new AsyncResultHandler<Void>() {
//                    @Override
//                    public void handle(AsyncResult<Void> asyncResult) {
//                        if (asyncResult.failed()) {
//                            throw new RuntimeException(asyncResult.cause());
//                        }
//                    }
//                });
//
//                upload.streamToFileSystem(uploadedFile.getAbsolutePath());
//            }
//        });


        //application/x-www-form-urlencoded
        request.pause();
        vertx.fileSystem().mkdir(uploadedDirectory.getPath(), true, new AsyncResultHandler<Void>() {
            @Override
            public void handle(AsyncResult<Void> asyncResult) {
                if (asyncResult.failed()) {
                    throw new RuntimeException(asyncResult.cause());
                }
            }
        });
        vertx.fileSystem().open(uploadedFile.getPath(), new AsyncResultHandler<AsyncFile>() {
            @Override
            public void handle(final AsyncResult<AsyncFile> asyncFileAsyncResult) {

                if (asyncFileAsyncResult.succeeded()) {

                    final AsyncFile asyncFile = asyncFileAsyncResult.result();
                    final Pump pump = Pump.createPump(request, asyncFile);

                    request.exceptionHandler(new Handler<Throwable>() {
                        @Override
                        public void handle(Throwable throwable) {
                            request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                            request.response().end();
                        }
                    });

                    request.endHandler(new VoidHandler() {
                        @Override
                        protected void handle() {
                            asyncFile.close(new AsyncResultHandler<Void>() {
                                @Override
                                public void handle(AsyncResult<Void> asyncResult) {
                                    if (asyncResult.succeeded()) {
                                        logger.debug("archiving SUCCESS of "+ path);
                                        request.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code());
                                        request.response().end();
                                    } else {
                                        logger.error("archiving FAILED of "+ path);
                                        logger.error("archiving FAILED trace ", asyncResult.cause());
                                        asyncResult.cause().printStackTrace(System.err);
                                    }
                                }
                            });
                        }


                    });

                    pump.start();
                    request.resume();
                } else {
                    asyncFileAsyncResult.cause().printStackTrace(System.err);
                    request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                    request.response().end();
                }
            }
        });
    }
}
