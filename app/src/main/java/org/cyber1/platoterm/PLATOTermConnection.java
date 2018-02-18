/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

public class PLATOTermConnection {

    private String host;
    private String port;

    public PLATOTermConnection(String host, String port) {
        this.host = host;
        this.port = port;
    }

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

    public static class Options {

        public TerminalType terminalType;

        private enum TerminalType {
            MODE_CLASSIC, MODE_ASCII
        }

    }

}
