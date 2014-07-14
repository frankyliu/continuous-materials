package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.dictionary.Dictionary;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.ArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import fr.synchrotron.soleil.ica.ci.lib.workflow.DefaultWorkflow;
import fr.synchrotron.soleil.ica.ci.lib.workflow.Workflow;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;

import java.io.*;

/**
 * @author Gregory Boissinot
 */
public class POMImportService {

    private Dictionary dictionary;
    private ArtifactRepository artifactRepository;

    public POMImportService(Dictionary dictionary, MongoDBDataSource mongoDBDataSource) {
        if (dictionary == null) {
            throw new NullPointerException("A Dictionary is required.");
        }
        if (mongoDBDataSource == null) {
            throw new NullPointerException("A MongoDB DataSource is required.");
        }
        this.dictionary = dictionary;
        this.artifactRepository = new ArtifactRepository(mongoDBDataSource);
    }

    public void importPomFile(String pomContent) {
        importPomFile(pomContent, new DefaultWorkflow());
    }

    public void importPomFile(String pomContent, Workflow workflow) {
        if (pomContent == null) {
            throw new NullPointerException("A POM File Content is required.");
        }
        StringReader stringReader = new StringReader(pomContent);
        importPomFile(stringReader, workflow);
        stringReader.close();
    }

    public void importPomFile(File pomFile) {
        importPomFile(pomFile, new DefaultWorkflow());
    }

    public void importPomFile(File pomFile, Workflow workflow) {
        if (pomFile == null) {
            throw new NullPointerException("An pomFile element is required.");
        }
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(pomFile);
            importPomFile(fileReader, workflow);
        } catch (FileNotFoundException fne) {
            throw new POMImporterException(fne);
        } finally {
            IOUtils.closeQuietly(fileReader);
        }
    }

    private void importPomFile(Reader pomReader, Workflow workflow) {
        PomReaderService pomReaderService = new PomReaderService();
        final Model pomModel = pomReaderService.getModel(pomReader);
        insertOrUpdateArtifactDocument(pomModel, workflow);
    }

    void insertOrUpdateArtifactDocument(Model pomModel, Workflow workflow) {
        ArtifactDocumentLoaderService artifactDocumentLoaderService = new ArtifactDocumentLoaderService(dictionary);
        final ArtifactDocument artifactDocument = artifactDocumentLoaderService.populateArtifactDocument(pomModel, workflow);

        if (artifactRepository.isArtifactDocumentAlreadyExists(artifactDocument.getKey())) {
            artifactRepository.updateArtifactDocument(artifactDocument);
        } else {
            artifactRepository.insertArtifactDocument(artifactDocument);
        }
    }

}
