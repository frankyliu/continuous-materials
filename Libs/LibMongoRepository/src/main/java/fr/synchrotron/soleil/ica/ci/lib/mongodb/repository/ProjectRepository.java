package fr.synchrotron.soleil.ica.ci.lib.mongodb.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.WriteConcern;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.exception.NoDocumentException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.exception.NonUniqueDocumentException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBException;
import org.jongo.MongoCollection;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ProjectRepository extends AbstractRepository {

    public ProjectRepository(MongoDBDataSource mongoDBDataSource) {
        super(mongoDBDataSource);
    }

    public List<ProjectDocument> getAllProjectDocument() {
        List<ProjectDocument> result = new ArrayList<ProjectDocument>();
        MongoCollection projects = jongo.getCollection(ProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        final Iterable<ProjectDocument> projectDocuments = projects.find().as(ProjectDocument.class);
        for (ProjectDocument projectDocument : projectDocuments) {
            result.add(projectDocument);
        }
        return result;
    }

    public ProjectDocument findProjectDocument(String org, String name) {

        MongoCollection projects = jongo.getCollection(ProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        ProjectDocumentKey queryObject = new ProjectDocumentKey(org, name);

        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        try {
            objectMapper.writeValue(stringWriter, queryObject);
        } catch (IOException ioe) {
            throw new MongoDBException(ioe);
        }
        final Iterable<ProjectDocument> projectDocuments = projects.find(stringWriter.toString()).as(ProjectDocument.class);
        final Iterator<ProjectDocument> projectDocumentIterator = projectDocuments.iterator();
        if (!projectDocumentIterator.hasNext()) {
            throw new NoDocumentException("One Project document must match criteria.");
        }

        final ProjectDocument projectDocument = projectDocumentIterator.next();
        if (projectDocumentIterator.hasNext()) {
            throw new NonUniqueDocumentException("Only one Project document must be returned.");
        }

        return projectDocument;
    }

    public boolean isProjectDocumentAlreadyExists(ProjectDocumentKey projectDocumentKey) {
        MongoCollection projects = jongo.getCollection(ProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        return projects.count(getStringQuery(projectDocumentKey)) != 0;
    }

    public void updateProjectDocument(ProjectDocument projectDocument) {
        MongoCollection projects = jongo.getCollection(ProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        projects.withWriteConcern(WriteConcern.SAFE);
        projects.update(getStringQuery(projectDocument.getKey())).with(projectDocument);
    }

    public void insertProjectDocument(ProjectDocument projectDocument) {
        MongoCollection projects = jongo.getCollection(ProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        projects.insert(projectDocument);
    }

    public void deleteProjectsCollection() {
        MongoCollection projectsCollection = jongo.getCollection(ProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        projectsCollection.remove();
    }

}
