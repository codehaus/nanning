package com.tirsen.nanning.samples.prevayler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import com.tirsen.nanning.AspectFactory;
import com.tirsen.nanning.attribute.Attributes;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * TODO document Identity
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.12 $
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

    public Object resolve(AspectFactory aspectFactory, IdentifyingSystem system) {
        if (InputStream.class.isAssignableFrom(objectClass)) {
            return new ByteArrayInputStream((byte[]) identifier);
        }
        if (isService(objectClass)) {
            return aspectFactory.newInstance(identifier);
        }
        if (isEntity(objectClass)) {
            return system.getObjectWithID(((Long) identifier).longValue());
        }
        throw new IllegalArgumentException("Can't resolve objects of " + objectClass);
    }

    static boolean isEntity(Class objectClass) {
        return Attributes.hasInheritedAttribute(objectClass, "entity");
    }

    public static boolean isService(Class objectClass) {
        return Attributes.hasInheritedAttribute(objectClass, "service");
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
        return new ToStringBuilder(this).append("objectClass", objectClass).append("identifier", identifier).toString();
    }
}
