package com.tirsen.nanning.samples.prevayler;

import junit.framework.TestCase;
import com.tirsen.nanning.samples.prevayler.ObjectGraphVisitor;

public class ObjectGraphVisitorTest extends TestCase {
    public static class A {
        B[] array = { new B(), new B() };
        B b = new B();
        Object nullField = null;

        public String toString() {
            return "A";
        }
    }
    public static class B {
        A circularity;
        public String toString() {
            return "B";
        }
    }


    public void test() {
        final StringBuffer log = new StringBuffer();
        A a = new A();
        a.b.circularity = a;
        ObjectGraphVisitor.visit(a, new ObjectGraphVisitor() {
            public void visit(Object o) {
                log.append(o);
                super.visit(o);
            }
        });
        assertEquals("ABBB", log.toString());
    }
}
