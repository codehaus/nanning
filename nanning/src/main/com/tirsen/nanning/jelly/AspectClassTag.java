/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.jelly;

import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.TagSupport;
import com.tirsen.nanning.definition.AspectClass;

/**
 * TODO document AspectClassTag
 *
 * <!-- $Id: AspectClassTag.java,v 1.3 2003-01-12 13:25:40 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.3 $
 */
public class AspectClassTag extends AspectTag
{
    private AspectClass aspectClass;

    public void doTag(XMLOutput xmlOutput) throws Exception
    {
        aspectClass = new AspectClass();
        invokeBody(xmlOutput);
        ((AspectRepositoryTag) getParent()).getAspectRepository().defineClass(getAspectClass());
    }

    public AspectClass getAspectClass()
    {
        return aspectClass;
    }

    public void setAspectInterface(Class interfaceClass) {
        aspectClass.setInterface(interfaceClass);
    }

    public void setTarget(Class targetClass) {
        aspectClass.setTarget(targetClass);
    }

    public void addInterceptor(Class interceptorClass) {
        aspectClass.addInterceptor(interceptorClass);
    }
}
