/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.jelly;

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyTagException;

/**
 * TODO document InterfaceTag
 *
 * <!-- $Id: InterfaceTag.java,v 1.3 2003-03-12 22:34:53 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.3 $
 */
public class InterfaceTag extends TagSupport
{
    public void doTag(XMLOutput xmlOutput) throws JellyTagException {
        try {
            Class interfaceClass =
                    Thread.currentThread().getContextClassLoader().loadClass(getBodyText().trim());
            ((AspectTag) getParent()).setAspectInterface(interfaceClass);
        } catch (ClassNotFoundException e) {
            throw new JellyTagException(e);
        }
    }
}
