/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.jelly;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * TODO document InterfaceTag
 *
 * <!-- $Id: InterfaceTag.java,v 1.1 2003-07-04 10:53:57 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class InterfaceTag extends TagSupport {
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
