/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.jelly;

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import com.tirsen.nanning.definition.AspectDefinition;
import com.tirsen.nanning.definition.AspectRepository;
import com.tirsen.nanning.definition.AspectClass;

/**
 * TODO document AspectTag
 *
 * <!-- $Id: AspectTag.java,v 1.4 2003-01-12 13:25:40 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.4 $
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

    public void addInterceptor(Class interceptorClass) {
        aspectDefinition.addInterceptor(interceptorClass);
    }

    public void setAspectInterface(Class interfaceClass) {
        aspectDefinition.setInterface(interfaceClass);
    }

    public void setTarget(Class targetClass) {
        aspectDefinition.setTarget(targetClass);
    }
}
