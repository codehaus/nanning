package org.codehaus.nanning.util;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;


public class RegexpPattern {
    private Pattern pattern;

    public static RegexpPattern compile(String pattern) {
        try {
            return new RegexpPattern(new Perl5Compiler().compile(pattern));
        } catch (MalformedPatternException e) {
            throw new WrappedException(e);
        }
    }

    public RegexpPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Matcher matcher(String text) {
        return new Matcher(text, pattern);
    }

    public static boolean matches(String s, String s1) {
        return RegexpPattern.compile(s).matcher(s1).matches();
    }
}
