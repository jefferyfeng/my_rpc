package com.fdh.rpc.protocol;

import java.io.Serializable;

/**
 * 封装host和端口
 *
 * @author Jeffery
 */
public class HostAndPort implements Serializable{
    private String host;
    private int port;

    public HostAndPort() {
    }

    public HostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
