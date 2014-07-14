package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Gregory Boissinot
 */
public class PomReaderService {

    public Model getModel(Reader pomReader) {

        if (pomReader == null) {
            throw new NullPointerException("A pom content as reader object is required.");
        }

        try {
            final MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
            return mavenXpp3Reader.read(pomReader);
        } catch (XmlPullParserException xe) {
            throw new POMImporterException("Can't read Maven Model", xe);
        } catch (IOException ioe) {
            throw new POMImporterException("Can't read Maven Model", ioe);
        }
    }
}
