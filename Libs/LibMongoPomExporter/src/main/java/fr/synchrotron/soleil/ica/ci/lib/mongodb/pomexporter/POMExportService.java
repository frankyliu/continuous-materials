package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDependency;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.BuildContext;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.BuildTool;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.DeveloperDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.maven.MavenProjectInfo;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.LicenseDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.OrganisationDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.mongodb.MongoDBArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service.ArtifactVersionResolverService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.ArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class POMExportService {

    private final ArtifactRepository artifactRepository;
    private final MavenDependencyBuilder dependencyBuilder;

    public POMExportService(MongoDBDataSource mongoDBDataSource) {
        if (mongoDBDataSource == null) {
            throw new NullPointerException("A mongoDB dataSource is required.");
        }
        this.artifactRepository = new ArtifactRepository(mongoDBDataSource);
        this.dependencyBuilder = new MavenDependencyBuilder(new ArtifactVersionResolverService(new MongoDBArtifactRepository(mongoDBDataSource)));
    }

    public void exportPomFile(Writer writer, ArtifactDocumentKey artifactDocumentKey) throws POMExporterException {

        if (writer == null) {
            throw new NullPointerException("An writer element is required.");
        }

        if (artifactDocumentKey == null) {
            throw new NullPointerException("A key artifact is required.");
        }

        if (!artifactDocumentKey.isValid()) {
            throw new NullPointerException("All key artifact document elements must be set.");
        }

        final MavenXpp3Writer mavenXpp3Writer = new MavenXpp3Writer();
        Model pomModel = getMavenModel(
                artifactDocumentKey.getOrg(),
                artifactDocumentKey.getName(),
                artifactDocumentKey.getVersion(),
                artifactDocumentKey.getStatus());
        try {
            mavenXpp3Writer.write(writer, pomModel);
        } catch (IOException ioe) {
            throw new POMExporterException(ioe);
        }
    }

    private Model getMavenModel(String org, String name, String version, String status) {

        Model pomModel = new Model();

        final ArtifactDocument artifactDocument = artifactRepository.findArtifactDocument(new ArtifactDocumentKey(org, name, version, status));

        //-- Populate form ArtifactDocument
        pomModel.setGroupId(artifactDocument.getKey().getOrg());
        pomModel.setArtifactId(artifactDocument.getKey().getName());
        pomModel.setVersion(artifactDocument.getKey().getVersion() + "." + artifactDocument.getKey().getStatus());
        pomModel.setModules(artifactDocument.getModules());


        final BuildContext buildContext = artifactDocument.getBuildContext();
        if (buildContext == null) {
            return pomModel;
        }


        final List<ArtifactDependency> dependencies = buildContext.getRuntimeDependencies();
        if (dependencies != null) {
            for (ArtifactDependency artifactDependency : dependencies) {
                pomModel.addDependency(dependencyBuilder.getDependency(artifactDependency));
            }
        }

        //Extract Maven specifities
        final BuildTool buildTool = buildContext.getBuildTool();
        if (buildTool != null) {
            final MavenProjectInfo mavenProjectInfo = buildTool.getMaven();
            if (mavenProjectInfo != null) {
                pomModel.setPackaging(mavenProjectInfo.getPackaging());
            }
        }

        //-- Populate from ProjectDocument
        final ProjectDocument projectDocument = buildContext.getProjectInfo();
        if (projectDocument != null) {
            pomModel.setDescription(projectDocument.getDescription());
            pomModel.setInceptionYear(projectDocument.getInceptionYear());

            OrganisationDocument organisationDocument = projectDocument.getOrganisation();
            if (organisationDocument != null) {
                Organization organization = new Organization();
                organization.setUrl(organisationDocument.getUrl());
                organization.setName(organisationDocument.getName());
                pomModel.setOrganization(organization);
            }


            final List<DeveloperDocument> developers = projectDocument.getDevelopers();
            List<Developer> developersMaven = new ArrayList<Developer>();
            if (developers != null) {
                for (DeveloperDocument developerDocument : developers) {
                    Developer developer = new Developer();
                    developer.setId(developerDocument.getId());
                    developer.setName(developerDocument.getName());
                    developer.setEmail(developerDocument.getEmail());
                    developer.setUrl(developerDocument.getUrl());
                    developer.setRoles(developerDocument.getRoles());
                    developer.setOrganization(developerDocument.getOrganization());
                    developer.setOrganizationUrl(developerDocument.getOrganizationUrl());
                    developer.setTimezone(developerDocument.getTimezone());
                    //TODO Missing properties
                    developersMaven.add(developer);
                }
            }
            pomModel.setDevelopers(developersMaven);

            // Licence
            List<LicenseDocument> licenses = projectDocument.getLicences();
            List<License> licencesMaven = new ArrayList<License>();
            if (licenses != null) {
                for (LicenseDocument license : licenses) {
                    License licenseMaven = new License();
                    licenseMaven.setName(license.getName());
                    licenseMaven.setUrl(license.getUrl());
                    licenseMaven.setDistribution(license.getDistribution());
                    licenseMaven.setComments(license.getComments());
                    licencesMaven.add(licenseMaven);
                }
            }
            pomModel.setLicenses(licencesMaven);

            final String scmConnection = projectDocument.getScmConnection();
            if (scmConnection != null) {
                final Scm scm = new Scm();
                scm.setConnection(scmConnection);
                pomModel.setScm(scm);
            }
        }


        return pomModel;
    }
}
