package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.mongodb.integration;

import fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.mongodb.MongoDBDocumentBuilderService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.maven.index.ArtifactInfo;
import org.apache.maven.index.context.IndexUtils;
import org.apache.maven.index.context.IndexingContext;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;

/**
 * @author Gregory Boissinot
 */
public class MongoDBIntegrationService {

    private final MessageChannel inputChannel;
    private final MongoDBDocumentBuilderService builderService;

    public MongoDBIntegrationService(MessageChannel inputChannel, MongoDBDocumentBuilderService builderService) {
        this.inputChannel = inputChannel;
        this.builderService = builderService;
    }

    public void insert(IndexingContext repoMavenContext, String repoURL) throws Exception {
        final IndexSearcher searcher = repoMavenContext.acquireIndexSearcher();
        final IndexReader ir = searcher.getIndexReader();
        for (int i = 0; i < ir.maxDoc(); i++) {
            if (!ir.isDeleted(i)) {
                final Document doc = ir.document(i);
                String metadata = doc.get("u");
                if (metadata != null) {
                    final ArtifactInfo ai = IndexUtils.constructArtifactInfo(doc, repoMavenContext);
                    final ArtifactDocument artifactObj = builderService.buildArtifactObj(ai);
                    System.out.println("Processing artifact with MongoDB and SI... " + artifactObj);
                    final Message<ArtifactDocument> artifactObjMessage =
                            MessageBuilder
                                    .withPayload(artifactObj)
                                    .setHeader("repo.url", repoURL)
                                    .build();
                    inputChannel.send(artifactObjMessage);
                }
            }
        }
    }
}
