/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.jelly;

import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.TagSupport;
import com.tirsen.nanning.AspectClass;

/**
 * TODO document AspectClassTag
 *
 * <!-- $Id: AspectClassTag.java,v 1.2 2002-12-03 13:55:24 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
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
