import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Zip

public class PackagePlugin implements Plugin<Project> {

    private static final String CONFIGURATION_MERGECONFIG_NAME = "mergeConfig"

    public void apply(Project project) {

        def scriptGeneration = new ScriptGeneration(project)

        if ((!project.getPlugins().hasPlugin('base'))
                || (!project.getPlugins().hasPlugin('java'))) {
            project.apply(plugin: 'base')
        }

        //--Create a private classpath generator task
        Task makeClassPathTask = project.task(TaskNames.TASK_BUILD_CLASSPATH)
        makeClassPathTask.description = 'Hook task for generating a classpath of each configuration.'

        //--Create a generate script file task
        Task generateScriptFileTask = project.task(TaskNames.TASK_APPLY_TEMPLATE)
        generateScriptFileTask.description = 'Hook task for applying a template file.'
        generateScriptFileTask.getTaskDependencies().add(makeClassPathTask)
        generateScriptFileTask.ext.generationDir = scriptGeneration.getTemplateFilePath();

        //--Create a public distribution task
        Task distributionTask = project.task(TaskNames.TASK_SOLEIL_DISTRIBUTION, type: Zip)
        distributionTask.description = 'Make a Soleil Distribution.'
        distributionTask.getTaskDependencies().add(generateScriptFileTask)


        project.ext.map = [:]
        project.task(TaskNames.TASK_INTERNAL_STORE_LATEST_VERSION) << {
            //Closure evaluated when the task is executed
            project.configurations.getByName(CONFIGURATION_MERGECONFIG_NAME).resolvedConfiguration.resolvedArtifacts.each { artifact ->
                String key = "${artifact.moduleVersion.id.group}-${artifact.moduleVersion.id.name}"
                project.ext.map.put(key, "${artifact.moduleVersion.id.version}")
            }
        }
        //--Create a private copyDependencies task off the shelf for using with zip tasks
        Task copyDependenciesTask = project.task(TaskNames.TASK_FETCHED_DEPENDENCIES, type: Copy)
        copyDependenciesTask.description = 'A copy spec with all the dependencies of a fusion configuration.'

        project.ext.nbGeneration = 0


        project.extensions.add(ScriptGeneration.CONFIGURATION_NAME, scriptGeneration)

        //-- Create a fusion configuration
        Configuration mergeConfig = project.configurations.create(CONFIGURATION_MERGECONFIG_NAME)

        project.afterEvaluate {

            //Set changing to true for all dependencies of all configurations
            project.configurations*.allDependencies*.each { dependency ->
                if (dependency instanceof ExternalModuleDependency) {
                    ((ExternalModuleDependency) dependency).changing = true
                }
            }

            ///Invalid cache for resolution strategy
            project.configurations.all {
                resolutionStrategy {
                    // don't cache changing modules at all
                    cacheChangingModulesFor 0, 'seconds'
                }
            }

            //Make mergeConfiguration extends all other (expect self)
            Set<Configuration> configurationSet = new HashSet()
            project.configurations.all.each { configuration ->
                if (!configuration.name.equals(CONFIGURATION_MERGECONFIG_NAME)) {
                    configurationSet.add(configuration);
                }

            }
            mergeConfig.setExtendsFrom(configurationSet)

            //--Create a classpath generator task for each configuration
            //-- Each classpath generator configuration will depends on makeClassPathTask
            project.configurations.each { conf ->
                String makeClassPathConfName = TaskNames.TASK_BUILD_CLASSPATH_STARTER_NAME + "${conf.name}"
                project.task("${makeClassPathConfName}", dependsOn: TaskNames.TASK_INTERNAL_STORE_LATEST_VERSION) << {
                    conf.resolvedConfiguration.resolvedArtifacts.each { artifact ->
                        String key = "${artifact.moduleVersion.id.group}-${artifact.moduleVersion.id.name}"
                        String classifier = artifact.classifier
                        def line;
                        if (classifier == null) {
                            line = key + "-" + project.ext.map.get(key) + ".${artifact.extension}"
                        } else {
                            line = key + "-" + project.ext.map.get(key) + "-${classifier}.${artifact.extension}"
                        }

                        project.file(scriptGeneration.getGeneratedDirPath() + "/classpath${conf.name}").mkdirs()
                        project.file(scriptGeneration.getGeneratedDirPath() + "/classpath${conf.name}/classpath.txt") << line
                        project.file(scriptGeneration.getGeneratedDirPath() + "/classpath${conf.name}/classpath.txt") << "\n"
                    }
                }
                makeClassPathTask.getTaskDependencies().add(project.tasks["${makeClassPathConfName}"])
            }

            //-- Implement the copyDependencies task with the fusion configuration created above
            //-- Use a preparation task for avoid to evaluate during the initialization phase
            Task configureCopyDependenciesTask = project.task(TaskNames.TASK_PREPARE_FETCHED_DEPENDENCIES);
            configureCopyDependenciesTask << {
                copyDependenciesTask.into "${project.buildDir}/deps"
                project.configurations.getByName(CONFIGURATION_MERGECONFIG_NAME).resolvedConfiguration.resolvedArtifacts.each {
                    artifact ->
                        copyDependenciesTask.from(artifact.file.path) {
                            String classifier = artifact.classifier
                            if (classifier == null) {
                                rename '(.*)', "${artifact.moduleVersion.id.group}-${artifact.moduleVersion.id.name}-${artifact.moduleVersion.id.version}.${artifact.extension}"
                            } else {
                                rename '(.*)', "${artifact.moduleVersion.id.group}-${artifact.moduleVersion.id.name}-${artifact.moduleVersion.id.version}-${classifier}.${artifact.extension}"
                            }
                        }
                }
            }
            copyDependenciesTask.dependsOn configureCopyDependenciesTask

        }

    }


}