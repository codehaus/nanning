package com.tirsen.nanning.remote;

import java.io.Serializable;

public class NamingLookup implements Serializable {
    static final long serialVersionUID = -4574390669425800337L;

    private String name;

    public NamingLookup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
