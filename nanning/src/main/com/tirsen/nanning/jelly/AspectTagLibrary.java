/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.jelly;

import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyContext;

import java.util.Collection;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedList;

import com.tirsen.nanning.definition.AspectDefinition;
import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.AspectFactory;

/**
 * TODO document AspectTagLibrary
 *
 * <!-- $Id: AspectTagLibrary.java,v 1.4 2003-01-12 13:25:40 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.4 $
 */
public class AspectTagLibrary extends TagLibrary
{
    public static final String TAG_LIBRARY_URI = "http://nanning.sf.net/jelly/taglib";

    public AspectTagLibrary()
    {
        registerTag("aspect-repository", AspectRepositoryTag.class);
        registerTag("class", AspectClassTag.class);
        registerTag("aspect", AspectTag.class);
        registerTag("interface", InterfaceTag.class);
        registerTag("interceptor", InterceptorTag.class);
        registerTag("target", TargetTag.class);
    }

    public static Collection findDefinedRepositories(JellyContext context)
    {
        Collection result = new LinkedList();
        Map variables = context.getVariables();
        for (Iterator iterator = variables.values().iterator(); iterator.hasNext();)
        {
            Object value = iterator.next();
            if(value instanceof AspectFactory)
            {
                result.add(value);
            }
        }
        return result;
    }
}
