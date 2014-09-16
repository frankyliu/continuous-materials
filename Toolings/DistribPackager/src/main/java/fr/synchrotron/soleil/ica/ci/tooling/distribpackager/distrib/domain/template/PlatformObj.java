package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.template;

/**
 * @author Gregory Boissinot
 */
public class PlatformObj {

    private String os;
    private String templateFilePath;
    private String fileName;
    private String outputDirectory;

    public PlatformObj() {
    }

    public String getOs() {
        return os;
    }

    public String getTemplateFilePath() {
        return templateFilePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setTemplateFilePath(String templateFilePath) {
        this.templateFilePath = templateFilePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public String toString() {
        return "PlatformObj{" +
                "os='" + os + '\'' +
                ", templateFilePath='" + templateFilePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", outputDirectory='" + outputDirectory + '\'' +
                '}';
    }
}
