package com.tirsen.nanning.remote;

import com.tirsen.nanning.remote.MyStatefulService;

public class MyStatefulServiceImpl implements MyStatefulService {
    private String value;

    public void modify(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
