package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.service;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.POMExportService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.POMImportService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.PomReaderService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.dictionary.NoDictionary;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Gregory Boissinot
 */
public class POMExportServiceTest {


    private static DB mongoDB;
    private static Jongo jongo;

    private static POMImportService pomImportService;
    private static POMExportService pomExportService;

    @BeforeClass
    public static void setupMongoDB() throws IOException {
        final Fongo fongo = new Fongo("testMongoServer");
        mongoDB = fongo.getDB("repo");
        jongo = new Jongo(mongoDB);
        final InMemoryMongoDBDataSource mongoDBDataSource = new InMemoryMongoDBDataSource();
        pomImportService = new POMImportService(new NoDictionary(), mongoDBDataSource);
        pomExportService = new POMExportService(mongoDBDataSource);
    }

    @After
    public void cleanupProjectsCollection() {
        MongoCollection projectsCollection = jongo.getCollection("projects");
        projectsCollection.remove();

        MongoCollection artifactsCollection = jongo.getCollection("artifacts");
        artifactsCollection.remove();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExport() throws IOException, URISyntaxException, SAXException {

        //TEST DATA
        StringWriter writer = new StringWriter();
        String org = "fr.synchrotron.soleil.ica.ci.lib";
        String name = "maven-versionresolver";
        String version = "1.0.0";
        String status = "RELEASE";
        File inputPomFile = new File(this.getClass().getResource("pom-1.xml").toURI());
        PomReaderService pomReaderService = new PomReaderService();
        FileReader inputPomFileReader = new FileReader(inputPomFile);
        Model inputPomFileModel = pomReaderService.getModel(inputPomFileReader);
        inputPomFileReader.close();


        pomImportService.importPomFile(inputPomFile);
        pomExportService.exportPomFile(writer, new ArtifactDocumentKey(org, name, version, status));

        final String outputPomContent = writer.toString();
        //  System.out.println(outputPomContent);
        assertNotNull(outputPomContent);
        final StringReader outputPomContentStringReader = new StringReader(outputPomContent);
        Model outputPomFileModel = pomReaderService.getModel(outputPomContentStringReader);
        outputPomContentStringReader.close();
        assertEquals(inputPomFileModel.getDescription(), outputPomFileModel.getDescription());
        //                      getModelVersion vraiment utile?
        //assertEquals(inputPomFileModel.getModelVersion(), outputPomFileModel.getModelVersion());
        assertEquals(inputPomFileModel.getGroupId(), outputPomFileModel.getGroupId());
        assertEquals(inputPomFileModel.getArtifactId(), outputPomFileModel.getArtifactId());
        assertEquals(inputPomFileModel.getVersion() + "." + status, outputPomFileModel.getVersion());
        assertEquals(inputPomFileModel.getPackaging(), outputPomFileModel.getPackaging());
        assertEquals(inputPomFileModel.getInceptionYear(), outputPomFileModel.getInceptionYear());
        assertTrue(EqualsBuilder.reflectionEquals(inputPomFileModel.getOrganization(), outputPomFileModel.getOrganization()));
        //TODO           assertEquals(inputPomFileModel.getParent(), outputPomFileModel.getParent());
        //TODO project classifier  ?
        List<License> licenses = inputPomFileModel.getLicenses();
        List<License> licensesOut = outputPomFileModel.getLicenses();
        assertEquals(licenses.size(), licensesOut.size());
        for (int i = 0; i < licenses.size(); i++) {
            License licenseIn = licenses.get(i);
            License licenseOut = licensesOut.get(i);
            assertTrue(EqualsBuilder.reflectionEquals(licenseIn, licenseOut));
//            assertEquals(licenseIn.getName(), licenseOut.getName());
//            assertEquals(licenseIn.getUrl(), licenseOut.getUrl());
//            assertEquals(licenseIn.getComments(), licenseOut.getComments());
//            assertEquals(licenseIn.getDistribution(), licenseOut.getDistribution());

        }

        // SCM - "scm:git:" removed by pomimporter
        assertEquals(inputPomFileModel.getScm().getConnection(), "scm:git:" + outputPomFileModel.getScm().getConnection());
        // dependencies
        List<Dependency> inputDependencies = inputPomFileModel.getDependencies();
        List<Dependency> outputDependencies = outputPomFileModel.getDependencies();
        assertEquals(inputDependencies.size(), outputDependencies.size());
        for (int i = 0; i < inputDependencies.size(); i++) {
            Dependency inputDependency = inputDependencies.get(i);
            Dependency outputDependency = outputDependencies.get(i);
            //      System.out.println("in: "+ToStringBuilder.reflectionToString(inputDependency));
            //    System.out.println("out: "+ToStringBuilder.reflectionToString(outputDependency));
            // exclude test version in not imported in mongodb
            assertTrue(EqualsBuilder.reflectionEquals(inputDependency, outputDependency, new String[]{"version", "exclusions"}));


            //     assertEquals(inputDependency.getGroupId(), outputDependency.getGroupId());
            //   assertEquals(inputDependency.getArtifactId(), outputDependency.getArtifactId());
            //  assertEquals(inputDependency.getScope(), outputDependency.getScope());
            //TODO? assertEquals(inputDependency.getSystemPath(), outputDependency.getSystemPath());
            //TODO? assertEquals(inputDependency.getType(), outputDependency.getType());
            //TODO? assertEquals(inputDependency.getClassifier(), outputDependency.getClassifier());
            // System.out.println(inputDependency.getExclusions());
            assertTrue(EqualsBuilder.reflectionEquals(inputDependency.getExclusions(), outputDependency.getExclusions()));
        }
        // developers info
        assertTrue(EqualsBuilder.reflectionEquals(inputPomFileModel.getDevelopers(), outputPomFileModel.getDevelopers()));
        //TODO contributors
        //   assertEquals(inputPomFileModel.getContributors(), outputPomFileModel.getContributors());

        assertEquals(inputPomFileModel.getModules(), outputPomFileModel.getModules());
        //TODO build?
        //TODO properties

        //TODO dependency management? transitive  exclusion can be done in dep man

    }

    static private class InMemoryMongoDBDataSource implements MongoDBDataSource {
        @Override
        public DB getMongoDB() {
            return mongoDB;
        }
    }

}
