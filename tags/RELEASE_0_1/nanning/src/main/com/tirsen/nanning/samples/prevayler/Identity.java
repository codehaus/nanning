package com.tirsen.nanning.samples.prevayler;

import java.io.Serializable;

/**
 * TODO document Identity
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.2 $
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
}
