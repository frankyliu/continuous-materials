package fr.synchrotron.soleil.ica.ci.test.vertx.sample;

import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */
public class DeployerVerticle extends Verticle {

    @Override
    public void start() {
        super.start();

        container.deployVerticle(SampleVerticle.class.getCanonicalName(), container.config(), 2);

        container.deployWorkerVerticle(SampleWorkerVerticle.class.getCanonicalName(), container.config(), 20);
    }
}
