package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pommetadata.POMMetadataWorkerVerticle;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */
public class DeployerVerticle extends Verticle {

    @Override
    public void start() {

        final JsonObject config = container.config();
        container.deployVerticle(ProxyEndpointVerticle.class.getCanonicalName(), config, 8);

        final JsonObject mongo = config.getObject("mongo");
        container.deployWorkerVerticle(POMMetadataWorkerVerticle.class.getCanonicalName(), mongo, 20);
    }
}
