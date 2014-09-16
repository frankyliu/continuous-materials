package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.plugin

import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.DistribService
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.PackageConfig
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.GradleBuild
import org.gradle.api.tasks.TaskAction

class PackagerPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.extensions.create("packageConfig", PackagingPluginExtension)

        project.task('cleanDistrib', type: Delete, description:'Clean the distribution output.') {
            delete "${project.packageConfig.outputDirPath}"
        }

//        project.task('clean').dependsOn('cleanPackage')

        project.task('buildDistrib', type: PackagingTask, description:'Build the distribution.')

        project.task('distrib', type: GradleBuild, dependsOn: 'buildDistrib') {
            buildFile = 'build/build.gradle'
            tasks << 'distrib'
        }

    }
}

class PackagingPluginExtension {
    def String description = 'Packaging with Gradle tooling...'
    def String outputDirPath = 'build'
    def packageDescriptorFilePath = 'package.json'
}


class PackagingTask extends DefaultTask {

    @TaskAction
    doMyAction() {
        print "RUNNING\n";
        try {
            PackageConfig packageConfig = new PackageConfig(
                    "${project.packageConfig.packageDescriptorFilePath}",
                    "${project.packageConfig.outputDirPath}",
                    "${project.gradle.gradleHomeDir}");

            DistribService distribService = new DistribService(packageConfig);
            distribService.makePackage();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}



