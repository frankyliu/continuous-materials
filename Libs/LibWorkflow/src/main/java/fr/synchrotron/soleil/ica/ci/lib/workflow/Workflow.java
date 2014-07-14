package fr.synchrotron.soleil.ica.ci.lib.workflow;

import java.util.List;

/**
 * @author Gregory Boissinot
 */
public abstract class Workflow {

    private String name;
    private WorkflowEngine workflowEngine;

    public Workflow(String name, List<String> statusList) {
        if (name == null) {
            throw new NullPointerException("A workflow name is required.");
        }
        if (statusList == null) {
            throw new NullPointerException("A status list is required.");
        }
        this.name = name;
        this.workflowEngine = new WorkflowEngine(statusList);
    }

    public abstract StatusVersion extractStatusAndVersionFromMavenVersion(String version);

    public String getName() {
        return name;
    }

    public String getLatestPromotedStatus() {
        return workflowEngine.getLatestPromotedStatus();
    }

    public String getNormalizedStatus(String label) {
        return workflowEngine.getNormalizedStatus(label);
    }

    public String getNextStatusLabel(String statusLabel) {
        return workflowEngine.getNextStatusLabel(statusLabel);
    }
}
