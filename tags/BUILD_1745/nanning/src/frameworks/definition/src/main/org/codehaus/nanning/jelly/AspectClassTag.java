/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.jelly;

import org.codehaus.nanning.definition.AspectClass;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * TODO document AspectClassTag
 *
 * <!-- $Id: AspectClassTag.java,v 1.1 2003-07-04 10:53:57 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class AspectClassTag extends AspectTag {
    private AspectClass aspectClass;

    public void doTag(XMLOutput xmlOutput) throws JellyTagException {
        aspectClass = new AspectClass();
        invokeBody(xmlOutput);
        ((AspectRepositoryTag) getParent()).getAspectRepository().defineClass(getAspectClass());
    }

    public AspectClass getAspectClass() {
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
