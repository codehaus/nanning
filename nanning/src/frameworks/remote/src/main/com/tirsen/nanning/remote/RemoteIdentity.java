package com.tirsen.nanning.remote;

import com.tirsen.nanning.prevayler.Identity;

public class RemoteIdentity extends Identity {
    static final long serialVersionUID = -7294340439273542677L;

    private ServerConnectionManager connectionManager;

    public RemoteIdentity(Class objectClass, Object identifier, ServerConnectionManager connectionManager) {
        super(objectClass, identifier);
        this.connectionManager = connectionManager;
    }

    public ServerConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteIdentity)) return false;
        if (!super.equals(o)) return false;

        final RemoteIdentity remoteIdentity = (RemoteIdentity) o;

        if (connectionManager != null ? !connectionManager.equals(remoteIdentity.connectionManager) : remoteIdentity.connectionManager != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (connectionManager != null ? connectionManager.hashCode() : 0);
        return result;
    }
}
