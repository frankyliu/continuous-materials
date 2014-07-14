package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pommetadata;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.LatestArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service.ArtifactVersionResolverService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service.MavenVersionResolverService;
import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Gregory Boissinot
 */
public class MavenPomFixer {

    private final LatestArtifactRepository artifactRepository;

    public MavenPomFixer(LatestArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    public Model getModelWithResolvedParent(String originalPomContent) {

        StringReader pomReader = new StringReader(originalPomContent);
        final MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
        Model model;
        try {
            model = mavenXpp3Reader.read(pomReader);
        } catch (IOException xe) {
            throw new RuntimeException(xe);
        } catch (XmlPullParserException xe) {
            throw new RuntimeException(xe);
        }
        pomReader.close();

        final Parent parent = model.getParent();
        if (parent != null && "RELEASE".equals(parent.getVersion())) {
            MavenVersionResolverService versionResolverService
                    = new MavenVersionResolverService(new ArtifactVersionResolverService(artifactRepository));
            final String resolvedParentVersion = versionResolverService.getLatestArtifact(parent.getGroupId(), parent.getArtifactId());
            parent.setVersion(resolvedParentVersion);
        }

        List profiles = model.getProfiles();
        if (profiles != null) {
            for (Object curProfile : profiles) {
                Profile profile = (Profile) curProfile;
                if (profile != null) {
                    String profilName = profile.getId();
                    if ("release".equals(profilName) || ("stable".equals(profilName))) {
                        Properties properties = profile.getProperties();
                        if (properties != null) {
                            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                                model.addProperty((String) entry.getKey(), (String) entry.getValue());
                            }
                        }
                    }
                }
            }
        }

        DependencyManagement dependencyManagement = model.getDependencyManagement();
        if (dependencyManagement != null) {
            List dependencies = dependencyManagement.getDependencies();
            if (dependencies != null) {
                for (int k = 0; k < dependencies.size(); k++) {
                    Dependency dependency = (Dependency) dependencies.get(k);
                    String artifactId = dependency.getArtifactId();
                    if ("C-CPP-Devices".equals(artifactId)) {
                        dependencies.remove(k);
                    }
                }
            }
        }


        return model;
    }
}
