package org.codehaus.nanning.util;

import java.util.Collection;
import java.util.ArrayList;

import org.apache.oro.text.perl.Perl5Util;

public class OroUtils {
    public static String[] split(String s, String pattern) {
        Collection results = new ArrayList();
        new Perl5Util().split(results, "/" + pattern + "/", s);
        return (String[]) results.toArray(new String[0]);
    }

    public static boolean match(String s, String pattern) {
        return new Perl5Util().match("/" + pattern + "/", s);
    }
}
