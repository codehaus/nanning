/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.jelly;

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import com.tirsen.nanning.AspectDefinition;
import com.tirsen.nanning.AspectRepository;
import com.tirsen.nanning.AspectClass;

/**
 * TODO document AspectTag
 *
 * <!-- $Id: AspectTag.java,v 1.2 2002-11-30 18:23:56 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class AspectTag extends TagSupport
{
    protected AspectDefinition aspectDefinition;
    private Class aspectRef;

    public void setInterface(String interfaceClass) throws ClassNotFoundException
    {
        this.aspectRef =
                Thread.currentThread().getContextClassLoader().loadClass(interfaceClass);
    }

    public AspectDefinition getAspectDefinition()
    {
        return aspectDefinition;
    }

    public void doTag(XMLOutput xmlOutput) throws Exception
    {
        // find repository and class where I'm contained
        AspectRepository aspectRepository = null;
        AspectClass aspectClass = null;
        AspectRepositoryTag aspectRepositoryTag =
                (AspectRepositoryTag) findAncestorWithClass(AspectRepositoryTag.class);
        if (aspectRepositoryTag != null)
        {
            aspectRepository = aspectRepositoryTag.getAspectRepository();
        }
        AspectClassTag aspectClassTag = (AspectClassTag) findAncestorWithClass(AspectClassTag.class);
        if (aspectClassTag != null)
        {
            aspectClass = aspectClassTag.getAspectClass();
        }

        // find or instantiate aspect
        if(aspectRef == null)
        {
            aspectDefinition = new AspectDefinition();
        }
        else
        {
            aspectDefinition = aspectRepository.getAspect(aspectRef);
        }

        invokeBody(xmlOutput);

        // add aspect to class or define in repository
        if(aspectClass != null)
        {
            aspectClass.addAspect(aspectDefinition);
        }
        else if(aspectRepository != null)
        {
            aspectRepository.defineAspect(aspectDefinition);
        }
        else
        {
            throw new IllegalStateException("Must be contained within 'aspect-repository' or 'class'.");
        }
    }
}
