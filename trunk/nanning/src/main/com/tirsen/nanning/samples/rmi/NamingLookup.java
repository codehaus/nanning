package com.tirsen.nanning.samples.rmi;

import java.io.Serializable;

public class NamingLookup implements Serializable {
    private String name;

    public NamingLookup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
