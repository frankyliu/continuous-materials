package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.LatestArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.workflow.Workflow;

import java.util.logging.Logger;

/**
 * @author Gregory Boissinot
 */
public class ArtifactVersionResolverService {

    private static final Logger LOGGER = Logger.getLogger(MavenVersionResolverService.class.getName());

    private final LatestArtifactRepository latestArtifactRepository;

    private final Workflow workflow;

    public ArtifactVersionResolverService(LatestArtifactRepository latestArtifactRepository) {
        if (latestArtifactRepository == null) {
            throw new NullPointerException("An ArtifactRepository is required.");
        }
        this.latestArtifactRepository = latestArtifactRepository;
        this.workflow = Workflow.DEFAULT_WORKFLOW_STATUS;
    }

    public ArtifactVersionResolverService(LatestArtifactRepository latestArtifactRepository, Workflow workflow) {

        if (latestArtifactRepository == null) {
            throw new NullPointerException("An ArtifactRepository is required.");
        }
        this.latestArtifactRepository = latestArtifactRepository;

        if (workflow == null) {
            throw new NullPointerException("A Workflow is required.");
        }
        this.workflow = workflow;
    }

    /**
     * Gets the latest version
     *
     * @param org  the requested artifact organization
     * @param name the requested artifact name
     * @return the latest version for an existing artifact with its status, null otherwise
     */
    public String getLatestVersion(String org, String name) {

        if (org == null) {
            throw new NullPointerException("A requested organization is required.");
        }

        if (name == null) {
            throw new NullPointerException("A requested name is required.");
        }

        return getLatestVersion(org, name, workflow.getLatestPromotedStatus());
    }

    /**
     * Gets the latest version
     *
     * @param org    the requested artifact organization
     * @param name   the requested artifact name
     * @param status the requested artifact status
     * @return the latest version for an existing artifact with its status, null otherwise
     */
    public String getLatestVersion(String org, String name, String status) {

        if (org == null) {
            throw new NullPointerException("A requested organization is required.");
        }

        if (name == null) {
            throw new NullPointerException("A requested name is required.");
        }

        if (status == null) {
            throw new NullPointerException("A requested status is required.");
        }

        LOGGER.info(String.format("Using '%s worklow.", workflow.getName()));
        LOGGER.info(String.format("Resolving latest artifact with '(%s,%s,%s)'.", org, name, status));

        String latestVersion = null;
        String curStatus = workflow.getNormalizedStatus(status);
        while (latestVersion == null && curStatus != null) {
            latestVersion = latestArtifactRepository.getLatestVersion(org, name, "binary", curStatus);
            curStatus = workflow.getNextStatusLabel(curStatus);
        }

        return latestVersion;
    }
}
