package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.template;

import java.util.List;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class DistribComponent {

    private String mainClasspath;

    private String name;

    private List<PlatformObj> platforms;

    public DistribComponent() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Map<String, Object> extraOptions;


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
