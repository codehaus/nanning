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
 * TODO document TargetTag
 *
 * <!-- $Id: TargetTag.java,v 1.4 2003-03-21 17:11:12 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.4 $
 */
public class TargetTag extends TagSupport {
    public void doTag(XMLOutput xmlOutput) throws JellyTagException {
        try {
            Class targetClass =
                    Thread.currentThread().getContextClassLoader().loadClass(getBodyText().trim());
            ((AspectTag) getParent()).setTarget(targetClass);
        } catch (ClassNotFoundException e) {
            throw new JellyTagException(e);
        }
    }
}
