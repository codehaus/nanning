package com.tirsen.nanning.prevayler;

import com.tirsen.nanning.attribute.Attributes;

import java.io.Serializable;

/**
 * TODO document Identity
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsen</a>
 * @version $Revision: 1.1 $
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
}
