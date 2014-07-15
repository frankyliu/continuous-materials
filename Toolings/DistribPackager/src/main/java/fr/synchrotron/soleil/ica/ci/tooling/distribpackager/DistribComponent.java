package fr.synchrotron.soleil.ica.ci.tooling.distribpackager;

/**
 * Created by Administrateur on 15/07/14.
 */
public class DistribComponent {

    /*
    name": "fr.soleil.gui:Zouk:latest",
            "mainClasspath": "Scienta.ScientaPanel",
            "templateFilePath": "src/main/template/linux/zouk.vm",
            "fileName": "zouk", //optional
            "outputDirectory": "bin/linux"
     */

    private String name;
    private String mainClasspath;
    private String templateFilePath;
    private String fileName;
    private String outputDirectory;

    public DistribComponent(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainClasspath() {
        return mainClasspath;
    }

    public void setMainClasspath(String mainClasspath) {
        this.mainClasspath = mainClasspath;
    }

    public String getTemplateFilePath() {
        return templateFilePath;
    }

    public void setTemplateFilePath(String templateFilePath) {
        this.templateFilePath = templateFilePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public String toString() {
        return "DistribComponent{" +
                "name='" + name + '\'' +
                ", mainClasspath='" + mainClasspath + '\'' +
                ", templateFilePath='" + templateFilePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", outputDirectory='" + outputDirectory + '\'' +
                '}';
    }
}
