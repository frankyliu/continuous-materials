package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDependency;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;

import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class BuildContext {

    private BuildTool buildTool;

    private List<ArtifactDependency> buildDependencies;

    private List<ArtifactDependency> runtimeDependencies;

    private ProjectDocument projectInfo;

    private JenkinsRecord jenkinsRecord;

    public BuildTool getBuildTool() {
        return buildTool;
    }

    public void setBuildTool(BuildTool buildTool) {
        this.buildTool = buildTool;
    }


    public List<ArtifactDependency> getBuildDependencies() {
        return buildDependencies;
    }

    public void setBuildDependencies(List<ArtifactDependency> buildDependencies) {
        this.buildDependencies = buildDependencies;
    }

    public List<ArtifactDependency> getRuntimeDependencies() {
        return runtimeDependencies;
    }

    public void setRuntimeDependencies(List<ArtifactDependency> runtimeDependencies) {
        this.runtimeDependencies = runtimeDependencies;
    }

    public ProjectDocument getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(ProjectDocument projectInfo) {
        this.projectInfo = projectInfo;
    }

    public JenkinsRecord getJenkinsRecord() {
        return jenkinsRecord;
    }

    public void setJenkinsRecord(JenkinsRecord jenkinsRecord) {
        this.jenkinsRecord = jenkinsRecord;
    }

    @Override
    public String toString() {
        return "BuildContext{" +
                "buildTool=" + buildTool +
                ", buildDependencies=" + buildDependencies +
                ", runtimeDependencies=" + runtimeDependencies +
                ", projectInfo=" + projectInfo +
                ", jenkinsRecord=" + jenkinsRecord +
                '}';
    }
}
