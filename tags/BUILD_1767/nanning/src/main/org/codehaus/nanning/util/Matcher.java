package org.codehaus.nanning.util;

import org.apache.oro.text.regex.*;

public class Matcher {
    private String text;
    private Pattern pattern;
    private Perl5Matcher perl5Matcher;

    public Matcher(String text, Pattern pattern) {
        this.text = text;
        this.pattern = pattern;
        perl5Matcher = new Perl5Matcher();
    }

    public String replaceAll(String s) {
        return Util.substitute(perl5Matcher, pattern, new StringSubstitution(s), text, Util.SUBSTITUTE_ALL);
    }

    public boolean matches() {
        return perl5Matcher.matches(text, pattern);
    }

    public String group(int i) {
        return perl5Matcher.getMatch().group(i);
    }
}
