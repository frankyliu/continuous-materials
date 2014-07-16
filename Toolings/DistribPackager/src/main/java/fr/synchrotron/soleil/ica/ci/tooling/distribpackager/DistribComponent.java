package fr.synchrotron.soleil.ica.ci.tooling.distribpackager;

import java.util.List;
import java.util.Map;

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

    private String mainClasspath;
    private String name;
    private List<PlatformObj> platforms;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Map<String, Object> extraOptions;

    public DistribComponent(){

    }

    public List<PlatformObj> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<PlatformObj> platforms) {
        this.platforms = platforms;
    }

    public String getMainClasspath() {
        return mainClasspath;
    }

    public void setMainClasspath(String mainClasspath) {
        this.mainClasspath = mainClasspath;
    }

     public Map<String, Object> getExtraOptions() {
        return extraOptions;
    }

    public void setExtraOptions(Map<String, Object> extraOptions) {
        this.extraOptions = extraOptions;
    }

    @Override
    public String toString() {
        return "DistribComponent{" +
                "mainClasspath='" + mainClasspath + '\'' +
                ", platforms=" + platforms +
                ", extraOptions=" + extraOptions +
                '}';
    }
}
