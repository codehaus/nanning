/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.jelly;

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * TODO document TargetTag
 *
 * <!-- $Id: TargetTag.java,v 1.1 2002-11-03 19:05:33 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class TargetTag extends TagSupport
{
    public void doTag(XMLOutput xmlOutput) throws Exception
    {
        Class targetClass =
                Thread.currentThread().getContextClassLoader().loadClass(getBodyText().trim());
        ((AspectTag) getParent()).getAspectDefinition().setTarget(targetClass);
    }
}
