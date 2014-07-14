package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact;

import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ArtifactDependency {

    private String org;

    private String name;

    private String version;

    private String scope;

    private List<ArtifactDependencyExclusion> exclusions;

    public ArtifactDependency() {
    }

    public ArtifactDependency(String org, String name, String version, String scope) {
        this.org = org;
        this.name = name;
        this.version = version;
        this.scope = scope;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<ArtifactDependencyExclusion> getExclusions() {
        return exclusions;
    }

    public void setExclusions(List<ArtifactDependencyExclusion> exclusions) {
        this.exclusions = exclusions;
    }
}
