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
 * <!-- $Id: TargetTag.java,v 1.6 2003-05-11 13:40:52 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.6 $
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
