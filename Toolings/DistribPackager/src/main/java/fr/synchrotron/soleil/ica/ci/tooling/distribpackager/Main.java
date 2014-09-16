package fr.synchrotron.soleil.ica.ci.tooling.distribpackager;

import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.PackageConfig;

import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public class Main {

    public static void main(String[] args) throws IOException {

        String outputDirPath = args[0];
        String packageDescFilePath = args[1];
        String gradleInstallationDir = args[2];

        try {
            PackageConfig packageConfig = new PackageConfig(packageDescFilePath, outputDirPath, gradleInstallationDir);
            DistribService distribService = new DistribService(packageConfig);
            distribService.makePackage();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }


}
