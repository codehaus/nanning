/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.jelly;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.codehaus.nanning.AspectFactory;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.TagLibrary;

/**
 * TODO document AspectTagLibrary
 *
 * <!-- $Id: AspectTagLibrary.java,v 1.1 2003-07-04 10:53:57 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class AspectTagLibrary extends TagLibrary {
    public static final String TAG_LIBRARY_URI = "http://nanning.sf.net/jelly/taglib";

    public AspectTagLibrary() {
        registerTag("aspect-repository", AspectRepositoryTag.class);
        registerTag("class", AspectClassTag.class);
        registerTag("aspect", AspectTag.class);
        registerTag("interface", InterfaceTag.class);
        registerTag("interceptor", InterceptorTag.class);
        registerTag("target", TargetTag.class);
    }

    public static Collection findDefinedRepositories(JellyContext context) {
        Collection result = new LinkedList();
        Map variables = context.getVariables();
        for (Iterator iterator = variables.values().iterator(); iterator.hasNext();) {
            Object value = iterator.next();
            if (value instanceof AspectFactory) {
                result.add(value);
            }
        }
        return result;
    }
}
