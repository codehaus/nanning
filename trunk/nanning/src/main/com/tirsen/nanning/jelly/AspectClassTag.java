/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.jelly;

import org.apache.commons.jelly.XMLOutput;
import com.tirsen.nanning.AspectClass;

/**
 * TODO document AspectClassTag
 *
 * <!-- $Id: AspectClassTag.java,v 1.1 2002-11-03 19:05:32 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class AspectClassTag extends AspectTag
{
    public void doTag(XMLOutput xmlOutput) throws Exception
    {
        aspectDefinition = new AspectClass();
        invokeBody(xmlOutput);
        ((AspectRepositoryTag) getParent()).getAspectRepository().defineClass(getAspectClass());
    }

    public AspectClass getAspectClass()
    {
        return (AspectClass) getAspectDefinition();
    }
}
