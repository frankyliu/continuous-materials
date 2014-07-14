package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.domain.maven;

import java.util.Date;

/**
 * @author Gregory Boissinot
 */
public class MavenArtifactDocument {

    private String organisation;

    private String name;

    private String version;

    private String status;

    private String type;

    private Date creationDate;

    private String sha1;

    private String md5;

    private String description;

    private String fileExtension;

    private long fileSize;

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MavenArtifactDocument that = (MavenArtifactDocument) o;

        if (fileSize != that.fileSize) return false;
        if (creationDate != null ? !creationDate.equals(that.creationDate) : that.creationDate != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (fileExtension != null ? !fileExtension.equals(that.fileExtension) : that.fileExtension != null)
            return false;
        if (md5 != null ? !md5.equals(that.md5) : that.md5 != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (organisation != null ? !organisation.equals(that.organisation) : that.organisation != null) return false;
        if (sha1 != null ? !sha1.equals(that.sha1) : that.sha1 != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = organisation != null ? organisation.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (sha1 != null ? sha1.hashCode() : 0);
        result = 31 * result + (md5 != null ? md5.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (fileExtension != null ? fileExtension.hashCode() : 0);
        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        return result;
    }
}
