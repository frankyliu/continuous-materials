package fr.synchrotron.soleil.ica.ci.tooling.distribpackager;

import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.DistribObj;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.service.DistribService;

import java.io.File;
import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public class Main {

    public static void main(String[] args) throws IOException {

        //TODO check
        String outputDirPath = args[0];
        String packageDescFilePath = args[1];

        try {
            DistribService distribService = new DistribService(new File(outputDirPath));
            final File packageDescriptorFile = new File(packageDescFilePath);
            final DistribObj distribObj = distribService.loadDescriptorFile(packageDescriptorFile);
            distribService.process(distribObj);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }


}
