package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDependency;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDependencyExclusion;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.BuildContext;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.BuildTool;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.maven.MavenProjectInfo;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.dictionary.Dictionary;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Gregory Boissinot
 */
class ArtifactDocumentLoaderService {

    private Dictionary dictionary;

    ArtifactDocumentLoaderService(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @SuppressWarnings("unchecked")
    ArtifactDocument populateArtifactDocument(Model model) {

        if (model == null) {
            throw new NullPointerException("A Maven Model is required.");
        }

        BuildContext buildContext = new BuildContext();

        StatusVersion statusVersion = extractStatusFromVersion(model.getVersion());
        ArtifactDocument artifactDocument =
                new ArtifactDocument(
                        model.getGroupId(),
                        model.getArtifactId(),
                        statusVersion.version,
                        statusVersion.status);
        artifactDocument.setModules(model.getModules());
        final List<Dependency> dependencies = model.getDependencies();
        if (dependencies != null) {
            List<ArtifactDependency> artifactDependencies = new ArrayList<>();
            for (Dependency dependency : dependencies) {
                ArtifactDependency artifactDependency = getArtifactDependency(dependency, model.getProperties());
                List<Exclusion> exclusions = dependency.getExclusions();
                List<ArtifactDependencyExclusion> artifactDependencyExclusions = new ArrayList<ArtifactDependencyExclusion>();
                for (Exclusion exclusion : exclusions) {
                    artifactDependencyExclusions.add(new ArtifactDependencyExclusion(exclusion.getGroupId(), exclusion.getArtifactId()));
                }
                artifactDependency.setExclusions(artifactDependencyExclusions);
                artifactDependencies.add(artifactDependency);
            }
            //artifactDocument.setDependencies(artifactDependencies);
            buildContext.setRuntimeDependencies(artifactDependencies);
        }


        MavenProjectInfo mavenProjectInfo = new MavenProjectInfo();
        final String packaging = model.getPackaging();
        mavenProjectInfo.setPackaging(packaging != null ? packaging : "jar");

        final BuildTool buildTool = new BuildTool();
        buildTool.setMaven(mavenProjectInfo);
        buildContext.setBuildTool(buildTool);

        ProjectDocumentLoaderService projectDocumentLoaderService = new ProjectDocumentLoaderService(dictionary);
        final ProjectDocument projectDocument = projectDocumentLoaderService.populateProjectDocument(model);
        buildContext.setProjectInfo(projectDocument);

        artifactDocument.setBuildContext(buildContext);

        return artifactDocument;
    }

    private ArtifactDependency getArtifactDependency(Dependency dependency, Properties properties) {
        final String version = dependency.getVersion();

        String resolvedVersion;
        if (version == null) {
            resolvedVersion = version;
        } else if (version.startsWith("$")) {
            String property = version.substring(version.indexOf("${") + 2, version.lastIndexOf("}"));
            resolvedVersion = properties.getProperty(property);
        } else {
            resolvedVersion = version;
        }

        return new ArtifactDependency(
                dependency.getGroupId(),
                dependency.getArtifactId(),
                resolvedVersion,
                dependency.getScope());
    }

    private StatusVersion extractStatusFromVersion(String version) {
        StatusVersion statusVersion = new StatusVersion();
        if (version.endsWith("-SNAPSHOT")) {
            statusVersion.status = "INTEGRATION";
            statusVersion.version = version.substring(0, version.lastIndexOf("-SNAPSHOT"));
            return statusVersion;
        }

        if (version.endsWith(".RELEASE")) {
            statusVersion.status = "RELEASE";
            statusVersion.version = version.substring(0, version.lastIndexOf(".RELEASE"));
            return statusVersion;
        }

        statusVersion.status = "RELEASE";
        statusVersion.version = version;
        return statusVersion;
    }

    private class StatusVersion {
        String version;
        String status;
    }
}
