package com.tirsen.nanning.samples.prevayler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.attribute.Attributes;

/**
 * TODO document Identity
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.8 $
 */
public class Identity implements Serializable {
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

    public Object resolve() {
        if (InputStream.class.isAssignableFrom(objectClass)) {
            return new ByteArrayInputStream((byte[]) identifier);
        }
        if (isService(objectClass)) {
            return Aspects.getCurrentAspectFactory().newInstance(identifier);
        }
        if (isEntity(objectClass)) {
            return CurrentPrevayler.getSystem().getObjectWithID(((Long) identifier).longValue());
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
        } else if (o instanceof List) {
            return true;
        } else {
            return false;
        }
    }
}
