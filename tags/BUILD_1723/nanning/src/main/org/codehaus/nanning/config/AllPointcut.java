package org.codehaus.nanning.config;

import java.lang.reflect.Method;

public class AllPointcut extends AbstractPointcut {
    public boolean adviseMethod(Method method) {
        return true;
    }
}
