package com.tirsen.nanning.samples.rmi;

import com.tirsen.nanning.samples.prevayler.Identity;

public class RemoteIdentity extends Identity {
    private String hostname;
    private int port;

    public RemoteIdentity(Class objectClass, Object identifier, String hostname, int port) {
        super(objectClass, identifier);
        this.hostname = hostname;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteIdentity)) return false;
        if (!super.equals(o)) return false;

        final RemoteIdentity remoteIdentity = (RemoteIdentity) o;

        if (port != remoteIdentity.port) return false;
        if (hostname != null ? !hostname.equals(remoteIdentity.hostname) : remoteIdentity.hostname != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (hostname != null ? hostname.hashCode() : 0);
        result = 29 * result + port;
        return result;
    }
}
