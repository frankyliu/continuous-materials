package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pommetadata;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.mongodb.MongoDBArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.POMImportService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.dictionary.SoleilDictionary;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import fr.synchrotron.soleil.ica.ci.lib.workflow.DefaultWorkflow;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.ServiceAddressRegistry;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class POMMetadataWorkerVerticle extends BusModBase {

    private static final String ACTION_FIXWRONGVALUE = "fixWrongValue";
    private static final String ACTION_STORE = "store";
    private static final String ACTION_CACHE = "cache";

    @Override
    public void start() {

        super.start();
        final String mongoHost = getMandatoryStringConfig("mongoHost");
        final Integer mongoPort = getMandatoryIntConfig("mongoPort");
        final String mongoDbName = getMandatoryStringConfig("mongoDbName");

        final BasicMongoDBDataSource mongoDBDatasource = new BasicMongoDBDataSource(mongoHost, mongoPort, mongoDbName);

        eb.registerHandler(ServiceAddressRegistry.EB_ADDRESS_POMMETADATA_SERVICE, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> message) {
                String action = message.body().getString("action");
                switch (action) {
                    case ACTION_FIXWRONGVALUE:
                        fixPom(message, mongoDBDatasource);
                        break;
                    case ACTION_STORE:
                        importPom(message, mongoDBDatasource);
                        break;
                    case ACTION_CACHE:
                        putPomInCache(vertx, message);
                        break;
                    default:
                        message.reply("Wrong Verticle Action in POMMetadataWorkerVerticle.");
                }
            }
        });
    }

    private void putPomInCache(Vertx verx, Message<JsonObject> message) {
        POMCache pomCache = new POMCache(vertx);
        String pomContent = message.body().getString("content");
        String pomPath = message.body().getString("requestPath");
        pomCache.putPomContent(pomPath, pomContent);
        message.reply(pomContent);
    }

    private void fixPom(Message<JsonObject> message, BasicMongoDBDataSource mongoDBDatasource) {
        try {
            String pomContent = message.body().getString("content");

            final MavenPomFixer mavenPomFixer =
                    new MavenPomFixer(new MongoDBArtifactRepository(
                            mongoDBDatasource));
            final Model resolvedPomModel = mavenPomFixer.getModelWithResolvedParent(pomContent);

            //POM for consumers
            //Remove dependency with scope test
            List<Dependency> keeypedDependencies = new ArrayList<>();
            List dependencies = resolvedPomModel.getDependencies();
            for (Object dependencyObject : dependencies) {
                Dependency dependency = (Dependency) dependencyObject;
                if (!"test".equals(dependency.getScope())) {
                    keeypedDependencies.add(dependency);
                }
            }
            resolvedPomModel.setDependencies(keeypedDependencies);


            StringWriter stringWriter = new StringWriter();
            new MavenXpp3Writer().write(stringWriter, resolvedPomModel);
            message.reply(stringWriter.toString());

        } catch (Throwable e) {
            e.printStackTrace();
            message.fail(-1, e.getMessage());
        }
    }

    private void importPom(Message<JsonObject> message, BasicMongoDBDataSource mongoDBDataSource) {
        try {
            String pomContent = message.body().getString("content");
            final POMImportService pomImportService = new POMImportService(
                    new SoleilDictionary(), mongoDBDataSource);
            pomImportService.importPomFile(pomContent, new DefaultWorkflow());
            message.reply();
        } catch (Throwable e) {
            e.printStackTrace();
            message.fail(-1, e.getMessage());
        }
    }

}
