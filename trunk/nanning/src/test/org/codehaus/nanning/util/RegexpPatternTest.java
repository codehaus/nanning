package org.codehaus.nanning.util;

import junit.framework.TestCase;

import org.codehaus.nanning.util.RegexpPattern;
import org.codehaus.nanning.util.Matcher;

public class RegexpPatternTest extends TestCase {

    public void testSimplePatterns() throws Exception {
        RegexpPattern regexpPattern = RegexpPattern.compile("(.*a)(.*)");
        assertFalse(regexpPattern.matcher("ertry").matches());
        Matcher matcher = regexpPattern.matcher("erary");
        assertTrue(matcher.matches());
        assertEquals("era", matcher.group(1));
        assertEquals("ry", matcher.group(2));
    }

    public void testComplexPatterns() throws Exception {
        RegexpPattern regexpPattern = RegexpPattern.compile("uid=(.*?)(,.*)");
        Matcher matcher = regexpPattern.matcher("uid=jimmy,ou=TesT-uid,dc=lecando,dc=com");
        assertTrue(matcher.matches());
        assertEquals("jimmy", matcher.group(1));
        assertEquals(",ou=TesT-uid,dc=lecando,dc=com", matcher.group(2));
    }

    public void testSplit() throws Exception {
        String[] strings = OroUtils.split("hej.hopp", "\\.");
        assertEquals("hej", strings[0]);
        assertEquals("hopp", strings[1]);
    }
}
