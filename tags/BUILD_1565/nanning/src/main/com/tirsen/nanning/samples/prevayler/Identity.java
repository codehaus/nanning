package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.attribute.Attributes;

import java.io.Serializable;

/**
 * TODO document Identity
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsen</a>
 * @version $Revision: 1.19 $
 */
public class Identity implements Serializable {
    static final long serialVersionUID = 716500751463534855L;

    private Class objectClass;
    private Object identifier;

    public Identity(Class objectClass, Object identifier) {
        this.objectClass = objectClass;
        this.identifier = identifier;
    }

    public Class getObjectClass() {
        return objectClass;
    }

    public Object getIdentifier() {
        return identifier;
    }

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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identity)) return false;

        final Identity identity = (Identity) o;

        if (!identifier.equals(identity.identifier)) return false;
        if (!objectClass.equals(identity.objectClass)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = objectClass.hashCode();
        result = 29 * result + identifier.hashCode();
        return result;
    }

    public String toString() {
        return this + "[objectClass=" + objectClass + ",identifier=" + identifier + "]";
    }

    public static boolean isService(Class objectClass) {
        return isStatelessService(objectClass) || isStatefulService(objectClass);
    }
}