package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDependency;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.BuildContext;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.DeveloperDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.dictionary.NoDictionary;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.ArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Gregory Boissinot
 */
public class POMImportServiceTest {

    private static DB mongoDB;
    private static POMImportService pomImportService;
    private static ArtifactRepository artifactRepository;

    @BeforeClass
    public static void setupMongoDB() throws IOException {

        Fongo fongo = new Fongo("testMongoServer");
        mongoDB = fongo.getDB("repo");
        MongoDBDataSource mongoDBDataSource = new InMemoryMongoDBDataSource();
        artifactRepository = new ArtifactRepository(mongoDBDataSource);

        pomImportService = new POMImportService(new NoDictionary(), mongoDBDataSource);
    }

    @After
    public void cleanupProjectsCollection() {
        artifactRepository.deleteArtifactsCollection();
    }

    @Test
    public void artifactDocumentInsertion() throws Exception {

        URL resource = this.getClass().getResource("pom-1.xml");
        File pomFile = new File(resource.toURI());
        FileReader pomFileReader = new FileReader(pomFile);
        PomReaderService pomReaderService = new PomReaderService();
        pomImportService.insertOrUpdateArtifactDocument(pomReaderService.getModel(pomFileReader));
        pomFileReader.close();

        ArtifactDocument artifactDocument = artifactRepository.findArtifactDocument(
                new ArtifactDocumentKey(
                        "fr.synchrotron.soleil.ica.ci.lib",
                        "maven-versionresolver",
                        "1.0.1",
                        "INTEGRATION")
        );
        assertNotNull(artifactDocument);

        final ArtifactDocumentKey artifactDocumentKey = artifactDocument.getKey();
        assertEquals("fr.synchrotron.soleil.ica.ci.lib", artifactDocumentKey.getOrg());
        assertEquals("maven-versionresolver", artifactDocumentKey.getName());

        assertEquals("1.0.1", artifactDocumentKey.getVersion());
        assertEquals("INTEGRATION", artifactDocumentKey.getStatus());

        assertNotNull(artifactDocument.getBuildContext());
        final List<ArtifactDependency> dependencies = artifactDocument.getBuildContext().getRuntimeDependencies();
        assertNotNull(dependencies);
        assertNotEquals(0, dependencies.size());

        boolean depMongoDbDriverPresent = false;
        for (ArtifactDependency dependency : dependencies) {
            final String org = dependency.getOrg();
            final String name = dependency.getName();
            if ("org.mongodb".equals(org) && "mongo-java-driver".equals(name)) {
                assertNotNull(dependency.getVersion());
                depMongoDbDriverPresent = true;
            }
        }
        assertTrue(depMongoDbDriverPresent);

        final BuildContext buildContext = artifactDocument.getBuildContext();
        assertNotNull(buildContext);

        final ProjectDocument projectDocument = buildContext.getProjectInfo();
        assertNotNull(projectDocument);

        final ProjectDocumentKey projectDocumentKey = projectDocument.getKey();
        assertEquals("fr.synchrotron.soleil.ica.ci.lib", projectDocumentKey.getOrg());
        assertEquals("maven-versionresolver", projectDocumentKey.getName());
        assertEquals("Maven Version Resolver", projectDocument.getDescription());
//        assertEquals("https://github.com/synchrotron-soleil-ica/maven-versionresolver.git", projectDocument.getScmConnection());

        final List<DeveloperDocument> developers = projectDocument.getDevelopers();
        assertEquals(1, developers.size());
        DeveloperDocument developerDocument = developers.get(0);
        assertEquals("gbois", developerDocument.getId());
        assertEquals("Gregory Boissinot", developerDocument.getName());
        assertEquals("gregory.boissinot@gmail.com", developerDocument.getEmail());
        assertEquals(2, developerDocument.getRoles().size());

    }

    @Test
    public void projectDocumentInsertionOrUpdate() throws Exception {

        URL resource1 = this.getClass().getResource("pom-1.xml");
        File pomFile1 = new File(resource1.toURI());
        URL resource2 = this.getClass().getResource("pom-2.xml");
        File pomFile2 = new File(resource2.toURI());


        PomReaderService pomReaderService = new PomReaderService();
        FileReader pomFileReader1 = new FileReader(pomFile1);
        pomImportService.insertOrUpdateArtifactDocument(pomReaderService.getModel(pomFileReader1));
        pomFileReader1.close();

        //Insert the same project with some modification
        FileReader pomFileReader2 = new FileReader(pomFile2);
        pomImportService.insertOrUpdateArtifactDocument(pomReaderService.getModel(pomFileReader2));
        pomFileReader2.close();

        ArtifactDocument artifactDocument = artifactRepository.findArtifactDocument(
                new ArtifactDocumentKey(
                        "fr.synchrotron.soleil.ica.ci.lib",
                        "maven-versionresolver",
                        "1.0.1",
                        "INTEGRATION")
        );
        assertNotNull(artifactDocument);

        final BuildContext buildContext = artifactDocument.getBuildContext();
        assertNotNull(buildContext);

        final ProjectDocument projectDocument = buildContext.getProjectInfo();
        assertNotNull(projectDocument);

        final ProjectDocumentKey projectDocumentKey = projectDocument.getKey();
        assertEquals("fr.synchrotron.soleil.ica.ci.lib", projectDocumentKey.getOrg());
        assertEquals("maven-versionresolver", projectDocumentKey.getName());
        assertEquals("Maven Version Resolver", projectDocument.getDescription());
        assertEquals("https://github.com/synchrotron-soleil-ica/maven-versionresolver.git", projectDocument.getScmConnection());

        final List<DeveloperDocument> developers = projectDocument.getDevelopers();
        assertEquals(1, developers.size());
        DeveloperDocument developerDocument = developers.get(0);
        assertEquals("gbois2", developerDocument.getId());
        assertEquals("Gregory Boissinot", developerDocument.getName());
        assertEquals("gregory.boissinot@gmail.com", developerDocument.getEmail());
        assertEquals(2, developerDocument.getRoles().size());

    }

    static private class InMemoryMongoDBDataSource implements MongoDBDataSource {
        @Override
        public DB getMongoDB() {
            return mongoDB;
        }
    }
}
