package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.part.classpath;

/**
 * @author Gregory Boissinot
 */
public class ProjectClassPathObj {

    private String ArtifactId;

    private String Version;

    public ProjectClassPathObj(String artifactId, String version) {
        ArtifactId = artifactId;
        Version = version;
    }

    public String getArtifactId() {
        return ArtifactId;
    }

    public String getVersion() {
        return Version;
    }

}
