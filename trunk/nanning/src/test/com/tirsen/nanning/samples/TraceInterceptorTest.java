/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.samples;

import com.tirsen.nanning.definition.AspectClass;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO document TraceInterceptorTest
 *
 * <!-- $Id: TraceInterceptorTest.java,v 1.6 2003-01-24 13:29:30 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.6 $
 */
public class TraceInterceptorTest extends TestCase {
    private ClassLoader prevContextClassLoader;
    private String prevFactory;

    public static class MockLogFactory extends LogFactory {
        private MockLog mockLog = new MockLog();

        public Log getInstance(Class aClass) throws LogConfigurationException {
            return getMockLog();
        }

        public MockLog getMockLog() {
            return mockLog;
        }

        ///CLOVER:OFF
        public Object getAttribute(String s) {
            return null;
        }

        public String[] getAttributeNames() {
            return new String[0];
        }

        public Log getInstance(String s) throws LogConfigurationException {
            return null;
        }

        public void release() {
        }

        public void removeAttribute(String s) {
        }

        public void setAttribute(String s, Object o) {
        }
        ///CLOVER:ON
    }

    public static class MockLog implements Log {
        private List expectMessages = new ArrayList();
        private List actualMessages = new ArrayList();

        public void error(Object message, Throwable throwable) {
            String m = "ERROR " + message;
            actualMessages.add(m);
        }

        public void trace(Object message) {
            actualMessages.add(String.valueOf(message));
        }

        public void debug(Object message) {
            actualMessages.add(String.valueOf(message));
        }

        public void expectAddMessage(String messageToExpect) {
            expectMessages.add(messageToExpect);
        }

        public void verify() {
            Iterator actualIterator = actualMessages.iterator();
            Iterator expectIterator = expectMessages.iterator();
            while (expectIterator.hasNext()) {
                String expectedMessage = (String) expectIterator.next();
                assertTrue("log output not as expected", actualIterator.hasNext());
                String actualMessage = (String) actualIterator.next();
                assertTrue("log output does not match: " + expectedMessage, actualMessage.startsWith(expectedMessage));
            }
        }

        public void reset() {
            actualMessages.clear();
            expectMessages.clear();
        }

        ///CLOVER:OFF
        public void trace(Object o, Throwable throwable) {
        }

        public boolean isDebugEnabled() {
            return false;
        }

        public boolean isErrorEnabled() {
            return false;
        }

        public boolean isFatalEnabled() {
            return false;
        }

        public boolean isInfoEnabled() {
            return false;
        }

        public boolean isTraceEnabled() {
            return false;
        }

        public boolean isWarnEnabled() {
            return false;
        }

        public void debug(Object o, Throwable throwable) {
        }

        public void info(Object o) {
        }

        public void info(Object o, Throwable throwable) {
        }

        public void warn(Object o) {
        }

        public void warn(Object o, Throwable throwable) {
        }

        public void error(Object o) {
        }

        public void fatal(Object o) {
        }

        public void fatal(Object o, Throwable throwable) {
        }
        ///CLOVER:ON
    }

    protected void setUp() throws Exception {
        super.setUp();
        // hack into commons-logging to intercept logging for test
        prevContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[0]));
        prevFactory = System.getProperty(LogFactory.FACTORY_PROPERTY);
        System.setProperty(LogFactory.FACTORY_PROPERTY,
                "com.tirsen.nanning.samples.TraceInterceptorTest$MockLogFactory");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        // clean up hack for next tests
        Thread.currentThread().setContextClassLoader(prevContextClassLoader);
        if (prevFactory != null) {
            System.setProperty(LogFactory.FACTORY_PROPERTY, prevFactory);
        }
    }

    public static interface Intf {
        String call(String arg, String arg2);
    }

    public static class Impl implements Intf {
        public String call(String arg, String arg2) {
            return "hej tillbax!";
        }
    }

    public static class ErrorImpl implements Intf {
        public String call(String arg, String arg2) {
            throw new RuntimeException("ERROR");
        }
    }

    public void testLogInterceptor() throws InstantiationException, IllegalAccessException {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(TraceInterceptor.class);
        aspectClass.setTarget(Impl.class);

        assertTrue("failed to patch into commons-logging", LogFactory.getFactory() instanceof MockLogFactory);
        MockLog mockLog = ((MockLogFactory) LogFactory.getFactory()).getMockLog();
        mockLog.expectAddMessage(">>> call(hej, svej)");
        mockLog.expectAddMessage("<<< call(hej, svej), took");
        Intf intf = (Intf) aspectClass.newInstance();
        intf.call("hej", "svej");
        mockLog.verify();

        mockLog.reset();
        aspectClass.setTarget(ErrorImpl.class);
        mockLog.expectAddMessage(">>> call(hej, svej)");
        mockLog.expectAddMessage("ERROR <<< call(hej, svej) threw exception, took");
        intf = (Intf) aspectClass.newInstance();
        try {
            intf.call("hej", "svej");
            ///CLOVER:OFF
            fail("exception not thrown");
            ///CLOVER:ON
        } catch (Exception shouldHappen) {
        }
        mockLog.verify();
    }
}