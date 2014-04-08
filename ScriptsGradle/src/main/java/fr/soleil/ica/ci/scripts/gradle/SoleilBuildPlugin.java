package fr.soleil.ica.ci.scripts.gradle;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenInputArtifact;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.mongodb.MongoDBArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service.MavenVersionResolverService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.*;
import org.gradle.api.invocation.Gradle;


/**
 * Created by ABEILLE on 08/04/2014.
 */
public class SoleilBuildPlugin implements Plugin<Gradle> {

    private static String NEXUSMAVEN_REPOSITORY_URL = "http://172.16.5.7:8090/maven";

    @Override
    public void apply(Gradle gradle) {

        gradle.allprojects(new Action<Project>() {
            @Override
            public void execute(Project project) {
                ConfigurationContainer configurations = project.getConfigurations();
                for (Configuration configuration : configurations) {
                    ResolutionStrategy resolutionStrategy = configuration.getResolutionStrategy();
                    resolutionStrategy.eachDependency(new Action<DependencyResolveDetails>() {
                        @Override
                        public void execute(DependencyResolveDetails dependencyResolveDetails) {
                            ModuleVersionSelector requested = dependencyResolveDetails.getRequested();
                            String version = resolveMavenVersion(requested.getGroup(), requested.getName(), requested.getVersion());
                            dependencyResolveDetails.useVersion(version);
                        }
                    });
                }
            }
        });


    }

    private String resolveMavenVersion(String group, String name, String version) {
        MavenVersionResolverService resolverService = new MavenVersionResolverService(new MongoDBArtifactRepository(new BasicMongoDBDataSource("172.16.5.7", 27001, "repo")));
        MavenInputArtifact mavenInputArtifact = new MavenInputArtifact(group, name, version);
        String resolvedVersion = resolverService.getLatestArtifact(mavenInputArtifact.getGroupId(), mavenInputArtifact.getArtifactId());

        System.out.println("Fetching dependency $group:$name:$resolvedVersion");
        return resolvedVersion;
    }
}
