package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact;


import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.ArtifactDocumentForC;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.ArtifactDocumentForJava;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.BuildContext;

import java.util.Date;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ArtifactDocument {

    public static final String MONGO_ARTIFACTS_COLLECTION_NAME = "artifacts";

    private String org;

    private String name;

    private String version;

    private String status;

    private String type;
    private boolean thirdParty;
    private Date creationDate;
    private Date publicationDate;
    private String sha1;
    private String md5;
    private String description;
    private String fileExtension;
    private Long fileSize;
    private boolean force;
    private ArtifactDocumentForJava javaLanguage;
    private ArtifactDocumentForC cLanguage;

    private BuildContext buildContext;
    private List<String> modules;

    public ArtifactDocument() {
    }

    public ArtifactDocument(String org, String name, String version, String status) {
        this.org = org;
        this.name = name;
        this.version = version;
        this.status = status;
    }

    public ArtifactDocumentKey getKey() {
        return new ArtifactDocumentKey(org, name, version, status);
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(boolean thirdParty) {
        this.thirdParty = thirdParty;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public ArtifactDocumentForJava getJavaLanguage() {
        return javaLanguage;
    }

    public void setJavaLanguage(ArtifactDocumentForJava javaLanguage) {
        this.javaLanguage = javaLanguage;
    }

    public ArtifactDocumentForC getcLanguage() {
        return cLanguage;
    }

    public void setcLanguage(ArtifactDocumentForC cLanguage) {
        this.cLanguage = cLanguage;
    }

    public BuildContext getBuildContext() {
        return buildContext;
    }

    public void setBuildContext(BuildContext buildContext) {
        this.buildContext = buildContext;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    @Override
    public String toString() {
        return "ArtifactDocument{" +
                "org='" + org + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", thirdParty=" + thirdParty +
                ", creationDate=" + creationDate +
                ", publicationDate=" + publicationDate +
                ", sha1='" + sha1 + '\'' +
                ", md5='" + md5 + '\'' +
                ", description='" + description + '\'' +
                ", fileExtension='" + fileExtension + '\'' +
                ", fileSize=" + fileSize +
                ", isForce=" + force +
                ", javaLanguage=" + javaLanguage +
                ", cLanguage=" + cLanguage +
                ", buildContext=" + buildContext +
                ", modules=" + modules +
                '}';
    }
}
