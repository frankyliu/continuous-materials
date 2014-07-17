package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.part.classpath;

import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ClasspathObj {

    private ProjectClassPathObj project;

    private List<DependencyClasspathObj> dependencies;

    public ClasspathObj(ProjectClassPathObj project, List<DependencyClasspathObj> dependencies) {
        this.project = project;
        this.dependencies = dependencies;
    }

    public ProjectClassPathObj getProject() {
        return project;
    }

    public List<DependencyClasspathObj> getDependencies() {
        return dependencies;
    }
}
