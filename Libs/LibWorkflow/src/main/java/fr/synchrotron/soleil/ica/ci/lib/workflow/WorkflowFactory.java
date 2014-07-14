package fr.synchrotron.soleil.ica.ci.lib.workflow;

import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class WorkflowFactory {

    public static Workflow createWorkFlow(final String name, final List<String> statusList) {
        return new Workflow(name, statusList) {
            @Override
            public StatusVersion extractStatusAndVersionFromMavenVersion(String version) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
