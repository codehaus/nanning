package org.codehaus.nanning;

import java.lang.reflect.Method;
import java.util.*;

public class MethodInterceptorMapping {
    private Mapping[] mappings = new Mapping[0];

    public void add(Method method, Interceptor interceptor) {
        getOrCreateMapping(method).add(interceptor);
    }

    public List get(Method method) {
        Mapping mapping = getMapping(method);
        if (mapping == null) {
            return Collections.EMPTY_LIST;
        }
        return mapping.getInterceptors();
    }

    public Set getAllInterceptors() {
        Set interceptors = new HashSet();
        for (int i = 0; i < mappings.length; i++) {
            interceptors.addAll(mappings[i].getInterceptors());
        }
        return interceptors;
    }

    private Mapping getOrCreateMapping(final Method method) {
        Mapping mapping = getMapping(method);
        if (mapping != null) {
            return mapping;
        }

        Mapping[] newMappings = new Mapping[mappings.length + 1];
        System.arraycopy(mappings, 0, newMappings, 0, mappings.length);
        newMappings[newMappings.length - 1] = new Mapping(method);
        mappings = newMappings;
        return mappings[mappings.length - 1];
    }

    private Mapping getMapping(final Method method) {
        for (int i = 0; i < mappings.length; i++) {
            Mapping mapping = mappings[i];
            if (mapping.getMethod().equals(method)) {
                return mapping;
            }
        }
        return null;
    }

    private static class Mapping {
        private Method method;
        private Interceptor[] interceptors = new Interceptor[0];

        public Mapping(Method method) {
            this.method = method;
        }

        public void add(Interceptor interceptor) {
            Interceptor[] newInterceptors = new Interceptor[interceptors.length + 1];
            System.arraycopy(interceptors, 0, newInterceptors, 0, interceptors.length);
            interceptors = newInterceptors;
            interceptors[interceptors.length - 1] = interceptor;
        }

        public Method getMethod() {
            return method;
        }

        public List getInterceptors() {
            return Arrays.asList(interceptors);
        }
    }
}
