/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.definition;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.nanning.AspectFactory;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.AspectException;
import org.codehaus.nanning.jelly.AspectTagLibrary;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;

/**
 * TODO document AspectRepository
 *
 * <!-- $Id: AspectRepository.java,v 1.1 2003-07-04 10:53:57 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 * 
 * @deprecated please use the new {@link org.codehaus.nanning.config.AspectSystem} framework instead.
 */
public class AspectRepository implements AspectFactory {
    private static AspectRepository instance;

    protected final Map interceptorDefinitions = new HashMap();
    protected final Map aspectDefinitions = new HashMap();
    protected final Map aspectClasses = new HashMap();

    public void reinitialize(AspectInstance aspectInstance) {
    }

    public void defineInterceptor(InterceptorDefinition interceptorDefinition) {
        interceptorDefinitions.put(interceptorDefinition.getInterceptorClass(), interceptorDefinition);
    }

    public InterceptorDefinition getInterceptor(Class interceptorClass) {
        InterceptorDefinition interceptorDefinition =
                (InterceptorDefinition) interceptorDefinitions.get(interceptorClass);
        if (interceptorDefinition == null) {
            throw new IllegalArgumentException("no such interceptor defined: " + interceptorClass);
        }
        return interceptorDefinition;
    }

    public void defineAspect(AspectDefinition aspectDefinition) {
        aspectDefinitions.put(aspectDefinition.getInterfaceClass(), aspectDefinition);
    }

    public AspectDefinition getAspect(Class interfaceClass) {
        return (AspectDefinition) aspectDefinitions.get(interfaceClass);
    }

    public void defineClass(AspectClass aspectClass) {
        aspectClass.setAspectRepository(this);
        aspectClasses.put(aspectClass.getInterfaceClass(), aspectClass);
    }

    public AspectClass getClass(Class interfaceClass) {
        AspectClass aspectClass = (AspectClass) aspectClasses.get(interfaceClass);
        if (aspectClass == null) {
            throw new IllegalArgumentException("Did not find aspect-class with interface " +
                                               interfaceClass.getName());
        }
        return aspectClass;
    }

    public static AspectFactory getInstance() {
        if (instance == null) {
            instance = new AspectRepository();
            try {
                instance.configure(AspectRepository.class.getResource("/aspect-repository.xml"));
            } catch (Exception e) {
                throw new AspectException("failed to configure default instance");
            }
        }
        return instance;
    }

    /**
     * Merges all defined aspect-repositories of the xml-file into this one, at least one needs to be defined.
     *
     * @param resource
     * @throws org.codehaus.nanning.definition.ConfigureException
     */
    public void configure(URL resource) throws ConfigureException {
        JellyContext context = new JellyContext();
        try {
            context.registerTagLibrary(AspectTagLibrary.TAG_LIBRARY_URI, new AspectTagLibrary());
            context.registerTagLibrary("", new AspectTagLibrary());
            XMLOutput xmlOutput = XMLOutput.createXMLOutput(new ByteArrayOutputStream());
            context.runScript(resource, xmlOutput);
        } catch (Exception e) {
            throw new ConfigureException(e);
        }

        Collection aspectRepositories = AspectTagLibrary.findDefinedRepositories(context);
        Iterator iterator = aspectRepositories.iterator();
        if (!iterator.hasNext()) {
            throw new ConfigureException("No aspect-repository defined.");
        }
        while (iterator.hasNext()) {
            AspectRepository configuredRepository = (AspectRepository) iterator.next();
            this.interceptorDefinitions.putAll(configuredRepository.interceptorDefinitions);
            this.aspectClasses.putAll(configuredRepository.aspectClasses);
            this.aspectDefinitions.putAll(configuredRepository.aspectDefinitions);
        }
    }

    public Collection getClasses() {
        return aspectClasses.values();
    }

    public Object newInstance(Class aspectInterface) {
        return newInstance(aspectInterface, null);
    }

    public Object newInstance(Class aspectInterface, Object[] targets) {
        assert aspectInterface instanceof Class : "aspect-classes are identified by the interface-class of their first mixin";
        Object instance = getClass(aspectInterface).newInstance(targets);
        return instance;
    }

}
