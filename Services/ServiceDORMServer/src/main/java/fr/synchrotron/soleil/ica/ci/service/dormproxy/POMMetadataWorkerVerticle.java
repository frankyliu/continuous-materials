package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.POMExportService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.POMImportService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.dictionary.SoleilDictionary;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.exception.NoDocumentException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

import java.io.StringWriter;

/**
 * @author Gregory Boissinot
 */
public class POMMetadataWorkerVerticle extends BusModBase {
    private static final String ACTION_IMPORT = "import";
    private static final String ACTION_EXPORT = "export";
    private final Logger logger = LoggerFactory.getLogger(POMMetadataWorkerVerticle.class);

    @Override
    public void start() {

        super.start();
        final String mongoHost = getMandatoryStringConfig("mongoHost");
        final Integer mongoPort = getMandatoryIntConfig("mongoPort");
        final String mongoDbName = getMandatoryStringConfig("mongoDbName");

        eb.registerHandler(ServiceAddressRegistry.EB_ADDRESS_POMIMPORT_SERVICE, new Handler<Message<JsonObject>>() {
                    @Override
                    public void handle(Message<JsonObject> message) {
                        String action = message.body().getString("action");
                        switch (action) {
                            case ACTION_IMPORT:
                                importPom(message, mongoHost, mongoPort, mongoDbName);
                                break;
                            case ACTION_EXPORT:
                                exportPom(message, mongoHost, mongoPort, mongoDbName);
                                break;
                            default:
                                message.reply("Wrong Verticle Action in POMMetadataWorkerVerticle.");
                        }
                    }
                }
        );
    }

    private void importPom(Message<JsonObject> message, String mongoHost, int mongoPort, String mongoDbName) {
        try {
            String pomContent = message.body().getString("content");

            final POMImportService pomImportService = new POMImportService(
                    new SoleilDictionary(),
                    new BasicMongoDBDataSource(mongoHost, mongoPort, mongoDbName));
            pomImportService.importPomFile(pomContent);
            message.reply(true);
        } catch (Throwable t) {
            logger.error("error", t);
            //  t.printStackTrace();
            message.fail(-1, t.getMessage());
        }
    }

    private void exportPom(Message<JsonObject> message,
                           String mongoHost, int mongoPort, String mongoDbName) {
        try {
            //TODO use check
            final JsonObject body = message.body();
            String org = body.getString("org");
            String name = body.getString("name");
            String version = body.getString("version");
            String status = body.getString("status");

            ArtifactDocumentKey artifactDocumentKey = new ArtifactDocumentKey(org, name, version, status);
            final POMExportService
                    pomExportService = new POMExportService(
                    new BasicMongoDBDataSource(mongoHost, mongoPort, mongoDbName));
            StringWriter stringWriter = new StringWriter();
            pomExportService.exportPomFile(stringWriter, artifactDocumentKey);
            message.reply(stringWriter.toString());
        } catch (NoDocumentException nse) {
            message.fail(0, "Artifact doesn't exist.");
        } catch (Throwable t) {
            logger.error("error", t);
            message.fail(-1, t.getMessage());
        }
    }

}
