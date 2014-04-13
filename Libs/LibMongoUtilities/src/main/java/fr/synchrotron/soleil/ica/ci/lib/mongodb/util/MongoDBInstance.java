package fr.synchrotron.soleil.ica.ci.lib.mongodb.util;

/**
 * @author Gregory Boissinot
 */
public class MongoDBInstance {

    private String host;

    private int port;

    public MongoDBInstance() {
    }

    public MongoDBInstance(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
