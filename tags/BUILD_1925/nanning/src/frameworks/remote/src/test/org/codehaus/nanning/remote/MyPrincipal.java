package org.codehaus.nanning.remote;

import java.security.Principal;
import java.io.Serializable;

public class MyPrincipal implements Principal, Serializable {
    private String name;

    public MyPrincipal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
