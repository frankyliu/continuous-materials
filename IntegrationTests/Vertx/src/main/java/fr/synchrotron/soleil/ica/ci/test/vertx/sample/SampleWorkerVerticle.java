package fr.synchrotron.soleil.ica.ci.test.vertx.sample;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */
public class SampleWorkerVerticle extends Verticle {

    private static final Logger LOG = LoggerFactory.getLogger(SampleWorkerVerticle.class);

    @Override
    public void start() {
        super.start();
        vertx.eventBus().registerHandler("sample.address", new Handler<Message>() {
            @Override
            public void handle(Message message) {
                long starttime = System.currentTimeMillis();
                for (int i = 0; i < 1000; i++) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(i);
                    }
                }
                long endTime = System.currentTimeMillis();
                LOG.info("\n");
                LOG.info("Time:" + (endTime - starttime));
                message.reply(true);
            }
        });

    }
}
