/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.jelly;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * TODO document InterceptorTag
 *
 * <!-- $Id: InterceptorTag.java,v 1.1 2003-07-01 16:08:10 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class InterceptorTag extends TagSupport {
    public void doTag(XMLOutput xmlOutput) throws JellyTagException {
        try {
            Class interceptorClass =
                    Thread.currentThread().getContextClassLoader().loadClass(getBodyText().trim());
            ((AspectTag) getParent()).addInterceptor(interceptorClass);
        } catch (ClassNotFoundException e) {
            new JellyTagException(e);
        }
    }
}
