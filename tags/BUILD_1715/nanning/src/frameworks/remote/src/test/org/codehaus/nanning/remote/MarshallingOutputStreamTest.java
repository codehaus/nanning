package org.codehaus.nanning.remote;

import junit.framework.TestCase;
import org.codehaus.nanning.prevayler.Marshaller;
import org.codehaus.nanning.remote.MarshallingOutputStream;

import java.io.*;

public class MarshallingOutputStreamTest extends TestCase {
    private ByteArrayOutputStream bytes;
    private Marshaller marshaller;
    private MarshallingOutputStream out;

    public static class DontMarshalObject implements Serializable {
    }

    public static class MarshalObject implements Serializable {
    }

    public static class MarshalledObject implements Serializable {
    }

    protected void setUp() throws Exception {
        super.setUp();

        bytes = new ByteArrayOutputStream();
        marshaller = new Marshaller() {
            public Object marshal(Object o) {
                if (o instanceof MarshalObject) {
                    return new MarshalledObject();
                } else {
                    return o;
                }
            }

            public Object unmarshal(Object o) {
                fail();
                return null;
            }
        };

        out = new MarshallingOutputStream(bytes, marshaller);
    }

    public void testWriteDontMarshalObject() throws IOException, ClassNotFoundException {
        out.writeObject(new DontMarshalObject());
        out.flush();

        Object readObject = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())).readObject();
        assertTrue(readObject instanceof DontMarshalObject);
    }

    public void testWriteMarshalObject() throws IOException, ClassNotFoundException {
        out.writeObject(new MarshalObject());
        out.flush();

        Object readObject = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())).readObject();
        assertTrue(readObject instanceof MarshalledObject);
    }
}
