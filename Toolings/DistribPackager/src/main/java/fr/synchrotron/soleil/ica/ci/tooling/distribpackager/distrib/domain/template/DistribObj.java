package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.template;

import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class DistribObj {

    private List<DistribComponent> distrib;

    public DistribObj() {
    }

    public List<DistribComponent> getDistrib() {
        return distrib;
    }

    public void setDistrib(List<DistribComponent> distrib) {
        this.distrib = distrib;
    }

    @Override
    public String toString() {
        return "DistribObj{" +
                "distrib=" + distrib +
                '}';
    }
}
