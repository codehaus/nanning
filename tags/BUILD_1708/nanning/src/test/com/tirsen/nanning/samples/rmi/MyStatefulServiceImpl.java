package com.tirsen.nanning.samples.rmi;

public class MyStatefulServiceImpl implements MyStatefulService {
    private String value;

    public void modify(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
