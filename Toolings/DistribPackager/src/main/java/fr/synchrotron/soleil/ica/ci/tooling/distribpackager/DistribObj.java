package fr.synchrotron.soleil.ica.ci.tooling.distribpackager;

import java.util.List;

/**
 * Created by Administrateur on 15/07/14.
 */
public class DistribObj {

    private List<DistribComponent> distrib;

    public DistribObj(){
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
