package com.tirsen.nanning.samples.prevayler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.prevayler.PrevalentSystem;

import java.io.Serializable;
import java.lang.reflect.Method;

public class MyInvokeCommand extends MarshallingCommand {
    private static final Log logger = LogFactory.getLog(MyInvokeCommand.class);

    protected Identity identify(Object object) {
        if(object instanceof MyObject) {
            return new Identity(MyObject.class, new Integer(PrevaylerTest.getMySystem().getOID(object)));
        }
        throw new IllegalArgumentException("Can't identify " + object);
    }

    protected Object resolve(Identity identity) {
        if(MyObject.class.isAssignableFrom(identity.getObjectClass())) {
            return PrevaylerTest.getMySystem().getObject(((Integer) identity.getIdentifier()).intValue());
        }
        throw new IllegalArgumentException("Can't resolve " + identity.getObjectClass());
    }

    protected Serializable execute(
            PrevalentSystem system, Method method, Object unmarshalledTarget, Object[] unmarshalledArgs) {
        PrevaylerTest.getPrevaylerInterceptor().enterTransaction();
        try {
            try {
                return (Serializable) method.invoke(unmarshalledTarget, unmarshalledArgs);
            } catch (Exception e) {
                logger.fatal("Failed to execute command.", e);
                throw new RuntimeException(e);
            }
        } finally {
            PrevaylerTest.getPrevaylerInterceptor().exitTransaction();
        }
    }
}
