package com.tirsen.nanning.prevayler;

import com.tirsen.nanning.attribute.Attributes;

public class PrevaylerUtils {
    static boolean isEntity(Class objectClass) {
        return Attributes.hasInheritedAttribute(objectClass, "entity");
    }

    public static boolean isStatelessService(Class objectClass) {
        return Attributes.hasInheritedAttribute(objectClass, "service");
    }

    public static boolean isStatefulService(Class objectClass) {
        return Attributes.hasInheritedAttribute(objectClass, "stateful-service");
    }

    public static boolean isPrimitive(Object o) {
        if (o == null) {
            return true;
        } else if (o instanceof Number) {
            return true;
        } else if (o instanceof String) {
            return true;
        } else if (o instanceof Character) {
            return true;
        } else if (o instanceof Class) {
            return true;
        } else if (o instanceof Boolean) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isService(Class objectClass) {
        return isStatelessService(objectClass) || isStatefulService(objectClass);
    }

    public static boolean isPersistent(Class objectClass) {
        return isService(objectClass) || isEntity(objectClass);
    }
}
