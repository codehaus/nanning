/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.trace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.codehaus.nanning.config.AspectSystem;
import org.codehaus.nanning.config.InterceptorAspect;
import org.codehaus.nanning.config.MixinAspect;

/**
 * TODO document TraceInterceptorTest
 *
 * <!-- $Id: TraceInterceptorTest.java,v 1.3 2003-09-09 11:10:28 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.3 $
 */
public class TraceInterceptorTest extends TestCase {
    public static class MockLogger implements Log {
        private List expectMessages = new ArrayList();
        private List actualMessages = new ArrayList();

        public void error(Object message, Throwable throwable) {
            actualMessages.add("ERROR " + message);
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
        public boolean isDebugEnabled() {
            return true;
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

        public void trace(Object o) {
        }

        public void trace(Object o, Throwable throwable) {
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
        AspectSystem system = new AspectSystem();
        MockLogger mockLogger = new MockLogger();
        system.addAspect(new InterceptorAspect(new TraceInterceptor(mockLogger)));
        system.addAspect(new MixinAspect(Intf.class, Impl.class));

        mockLogger.expectAddMessage(">>> call(hej, svej)");
        mockLogger.expectAddMessage("<<< call(hej, svej), took");
        Intf intf = (Intf) system.newInstance(Intf.class);
        intf.call("hej", "svej");
        mockLogger.verify();
    }
}
