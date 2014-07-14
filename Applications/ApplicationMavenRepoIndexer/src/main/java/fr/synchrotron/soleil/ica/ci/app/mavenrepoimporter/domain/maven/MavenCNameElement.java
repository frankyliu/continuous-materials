package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.domain.maven;

/**
 * @author Gregory Boissinot
 */
public class MavenCNameElement {

    private String name;

    private String archi;

    private String platform;

    private String compiler;

    private String typeDep;

    private String mod;

    private String version;

    private String classifier;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArchi() {
        return archi;
    }

    public void setArchi(String archi) {
        this.archi = archi;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCompiler() {
        return compiler;
    }

    public void setCompiler(String compiler) {
        this.compiler = compiler;
    }

    public String getTypeDep() {
        return typeDep;
    }

    public void setTypeDep(String typeDep) {
        this.typeDep = typeDep;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }
}
