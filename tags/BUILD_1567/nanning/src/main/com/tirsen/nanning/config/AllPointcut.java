package com.tirsen.nanning.config;

import java.lang.reflect.Method;

public class AllPointcut extends AbstractPointcut {
    protected boolean adviseMethod(Method method) {
        return true;
    }
}
