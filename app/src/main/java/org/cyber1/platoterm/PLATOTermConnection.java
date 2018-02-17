package org.cyber1.platoterm;

/**
 * Created by thomc on 2/17/2018.
 */

public class PLATOTermConnection {
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    private String host;
    private String port;

    public PLATOTermConnection(String host, String port) {
        this.host = host;
        this.port = port;
    }


}
