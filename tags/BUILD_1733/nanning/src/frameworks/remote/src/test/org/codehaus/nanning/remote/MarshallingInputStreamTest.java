package org.codehaus.nanning.remote;

import junit.framework.TestCase;
import org.codehaus.nanning.prevayler.Marshaller;
import org.codehaus.nanning.remote.MarshallingInputStream;

import java.io.*;

public class MarshallingInputStreamTest extends TestCase {
    private ByteArrayOutputStream bytes;
    private Marshaller marshaller;
    private ObjectOutputStream out;

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
                fail();
                return null;
            }

            public Object unmarshal(Object o) {
                if (o instanceof MarshalledObject) {
                    return new MarshalObject();
                } else {
                    return o;
                }
            }
        };

        out = new ObjectOutputStream(bytes);
    }

    public void testDontMarshalObject() throws IOException, ClassNotFoundException {
        out.writeObject(new DontMarshalObject());

        MarshallingInputStream in = new MarshallingInputStream(new ByteArrayInputStream(bytes.toByteArray()), marshaller);
        Object readObject = in.readObject();
        assertTrue(readObject instanceof DontMarshalObject);
    }

    public void testMarshalObject() throws IOException, ClassNotFoundException {
        out.writeObject(new MarshalledObject());

        MarshallingInputStream in = new MarshallingInputStream(new ByteArrayInputStream(bytes.toByteArray()), marshaller);
        Object readObject = in.readObject();
        assertTrue(readObject instanceof MarshalObject);
    }
}
