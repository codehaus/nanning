package org.codehaus.nanning.remote;

import org.codehaus.nanning.remote.MyStatefulService;

public class MyStatefulServiceImpl implements MyStatefulService {
    private String value;

    public void modify(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
