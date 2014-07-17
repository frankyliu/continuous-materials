package fr.synchrotron.soleil.ica.ci.lib.workflow;

import java.util.Arrays;

/**
 * @author Gregory Boissinot
 */
public class DefaultWorkflow extends Workflow {

    private static final String DEFAULT_STATUS_BUILD = "BUILD";
    private static final String DEFAULT_STATUS_INTEGRATION = "INTEGRATION";
    private static final String DEFAULT_STATUS_RELEASE = "RELEASE";
    private static final String[] status = new String[]{DEFAULT_STATUS_BUILD, DEFAULT_STATUS_INTEGRATION, DEFAULT_STATUS_RELEASE};

    public DefaultWorkflow() {
        super("DEFAULT_WORKFLOW", Arrays.asList(status));
    }

    public StatusVersion extractStatusAndVersionFromMavenVersion(String version) {
        final String snapshotVersionSuffix = "-SNAPSHOT";
        StatusVersion statusVersion = new StatusVersion();
        if (version.endsWith(snapshotVersionSuffix)) {
            statusVersion.status = DEFAULT_STATUS_BUILD;
            statusVersion.version = version.substring(0, version.lastIndexOf(snapshotVersionSuffix));
            return statusVersion;
        }

        if (version.endsWith("." + DEFAULT_STATUS_INTEGRATION)) {
            statusVersion.status = DEFAULT_STATUS_INTEGRATION;
            statusVersion.version = version.substring(0, version.lastIndexOf("." + DEFAULT_STATUS_INTEGRATION));
            return statusVersion;
        }

        if (version.endsWith("." + DEFAULT_STATUS_RELEASE)) {
            statusVersion.status = DEFAULT_STATUS_RELEASE;
            statusVersion.version = version.substring(0, version.lastIndexOf("." + DEFAULT_STATUS_RELEASE));
            return statusVersion;
        }

        statusVersion.status = DEFAULT_STATUS_RELEASE;
        statusVersion.version = version;
        return statusVersion;
    }
}
