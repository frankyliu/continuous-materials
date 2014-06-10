package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pull.FixLegacyPOMWorkerVerticle;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.push.TrackMetadataWorkerVerticle;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */
public class DeployerVerticle extends Verticle {

    @Override
    public void start() {

        final JsonObject config = container.config();
        container.deployVerticle(HttpArtifactProxyEndpointVerticle.class.getCanonicalName(), config);

        final JsonObject mongo = config.getObject("mongo");
        container.deployWorkerVerticle(FixLegacyPOMWorkerVerticle.class.getCanonicalName(), mongo, 3);
        container.deployWorkerVerticle(TrackMetadataWorkerVerticle.class.getCanonicalName(), mongo, 3);

    }
}
