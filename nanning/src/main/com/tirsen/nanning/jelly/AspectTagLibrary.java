/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.jelly;

import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyContext;

import java.util.Collection;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedList;

import com.tirsen.nanning.AspectRepository;
import com.tirsen.nanning.AspectDefinition;
import com.tirsen.nanning.AspectClass;

/**
 * TODO document AspectTagLibrary
 *
 * <!-- $Id: AspectTagLibrary.java,v 1.2 2002-11-03 18:45:47 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class AspectTagLibrary extends TagLibrary
{
    public static final String TAG_LIBRARY_URI = "http://nanning.sf.net/jelly/taglib";

    public static class AspectRepositoryTag extends TagSupport
    {
        private String id = "default";
        private AspectRepository aspectRepository;

        public void setId(String id)
        {
            this.id = id;
        }

        public AspectRepository getAspectRepository()
        {
            return aspectRepository;
        }

        public void doTag(XMLOutput xmlOutput) throws Exception
        {
            aspectRepository = new AspectRepository();
            invokeBody(xmlOutput);
            getContext().setVariable(id, "parent", aspectRepository);
        }
    }

    public static class AspectTag extends TagSupport
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
                aspectClass.addSideAspect(aspectDefinition);
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

    public static class AspectClassTag extends AspectTag
    {
        public void doTag(XMLOutput xmlOutput) throws Exception
        {
            aspectDefinition = new AspectClass();
            invokeBody(xmlOutput);
            ((AspectRepositoryTag) getParent()).getAspectRepository().defineClass(getAspectClass());
        }

        private AspectClass getAspectClass()
        {
            return (AspectClass) aspectDefinition;
        }
    }

    public static class InterfaceTag extends TagSupport
    {
        public void doTag(XMLOutput xmlOutput) throws Exception
        {
            Class interfaceClass =
                    Thread.currentThread().getContextClassLoader().loadClass(getBodyText().trim());
            ((AspectTag) getParent()).getAspectDefinition().setInterface(interfaceClass);
        }
    }

    public static class InterceptorTag extends TagSupport
    {
        public void doTag(XMLOutput xmlOutput) throws Exception
        {
            Class interceptorClass =
                    Thread.currentThread().getContextClassLoader().loadClass(getBodyText().trim());
            ((AspectTag) getParent()).getAspectDefinition().addInterceptor(interceptorClass);
        }
    }

    public static class TargetTag extends TagSupport
    {
        public void doTag(XMLOutput xmlOutput) throws Exception
        {
            Class targetClass =
                    Thread.currentThread().getContextClassLoader().loadClass(getBodyText().trim());
            ((AspectTag) getParent()).getAspectDefinition().setTarget(targetClass);
        }
    }

    public AspectTagLibrary()
    {
        registerTag("aspect-repository", AspectRepositoryTag.class);
        registerTag("class", AspectClassTag.class);
        registerTag("aspect", AspectTag.class);
        registerTag("interface", InterfaceTag.class);
        registerTag("interceptor", InterceptorTag.class);
        registerTag("target", TargetTag.class);
    }

    public static Collection findDefinedRepositories(JellyContext context)
    {
        Collection result = new LinkedList();
        Map variables = context.getVariables();
        for (Iterator iterator = variables.values().iterator(); iterator.hasNext();)
        {
            Object value = iterator.next();
            if(value instanceof AspectRepository)
            {
                result.add(value);
            }
        }
        return result;
    }
}
