package fr.synchrotron.soleil.ica.proxy.midlleware;

/**
 * @author Gregory Boissinot
 */
public class HttpEndpointInfo {

    private final String host;
    private final int port;
    private final String uri;

    public HttpEndpointInfo(String host, int port, String uri) {
        this.host = host;
        this.port = port;
        this.uri = uri;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "HttpEndpointInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", uri='" + uri + '\'' +
                '}';
    }
}

