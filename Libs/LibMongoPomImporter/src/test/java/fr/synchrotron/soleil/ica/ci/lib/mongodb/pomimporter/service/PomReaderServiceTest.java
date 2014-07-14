package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import org.apache.maven.model.Model;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static junit.framework.Assert.assertNotNull;

/**
 * @author Gregory Boissinot
 */
public class PomReaderServiceTest {

    private PomReaderService pomReaderService = new PomReaderService();

    private InputStream resourceAsStream;

    @After
    public void cleanup() {
        if (resourceAsStream != null) {
            try {
                resourceAsStream.close();
            } catch (IOException e) {
                //Ignore
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void nullReader() {
        pomReaderService.getModel(null);
    }

    @Test
    public void readPom() throws IOException {
        resourceAsStream = this.getClass().getResourceAsStream("pom-1.xml");
        InputStreamReader pomAsInputStreamReader = new InputStreamReader(resourceAsStream);
        Model model = pomReaderService.getModel(pomAsInputStreamReader);
        assertNotNull(model);
        pomAsInputStreamReader.close();
    }

    @Test(expected = POMImporterException.class)
    public void wrongPom() throws IOException {
        resourceAsStream = this.getClass().getResourceAsStream("wrongpom.xml");
        InputStreamReader pomAsInputStreamReader = new InputStreamReader(resourceAsStream);
        Model model = pomReaderService.getModel(pomAsInputStreamReader);
        assertNotNull(model);
        pomAsInputStreamReader.close();
    }


}
