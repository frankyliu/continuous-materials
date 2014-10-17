package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pommetadata;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.service.Sha1Getter;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;
import org.vertx.java.core.shareddata.SharedData;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Gregory Boissinot
 */
public class POMCache {

    private static final String KEY_CACHE_POM_CONTENT = "pomContent";
    private static final String KEY_CACHE_POM_SHA1 = "pomContentSha1";

    private final ConcurrentSharedMap<String, String> pomContentMap;
    private final ConcurrentSharedMap<String, String> pomSha1Map;

    private MessageDigest digester;

    public POMCache(Vertx vertx) {
        final SharedData sharedData = vertx.sharedData();
        pomContentMap = sharedData.getMap(KEY_CACHE_POM_CONTENT);
        pomSha1Map = sharedData.getMap(KEY_CACHE_POM_SHA1);

        try {
            digester = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSha1(String pomSha1Path) {
        return pomSha1Map.get(pomSha1Path);
    }

    public void putPomContent(String pomPath, String pomContent) {

        //--POM CONTENT
        pomContentMap.put(pomPath, pomContent);

        //--SHA1
        final String sha1Path = pomPath + ".sha1";
        digester.reset();
        final byte[] bytes = pomContent.getBytes();
        digester.update(bytes, 0, bytes.length);
        Sha1Getter sha1Getter = new Sha1Getter();
        final String sha1 = sha1Getter.getSha1(digester.digest());
        pomSha1Map.put(sha1Path, sha1);

    }

}
