/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.definition;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.*;

import com.tirsen.nanning.jelly.AspectTagLibrary;
import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.AspectDefinition;
import com.tirsen.nanning.AspectFactory;
import com.tirsen.nanning.Aspects;

/**
 * TODO document AspectRepository
 *
 * <!-- $Id: AspectRepository.java,v 1.2 2003-01-16 11:01:23 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
 */
public class AspectRepository implements AspectFactory {
    private static AspectRepository instance;
    private static final Log logger = LogFactory.getLog(AspectRepository.class);

    private final Map interceptorDefinitions = new HashMap();
    private final Map aspectDefinitions = new HashMap();
    protected final Map aspectClasses = new HashMap();

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
                logger.warn("failed to configure default instance");
            }
        }
        return instance;
    }

    /**
     * Merges all defined aspect-repositories of the xml-file into this one, at least one needs to be defined.
     *
     * @param resource
     * @throws com.tirsen.nanning.definition.ConfigureException
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

    public Object newInstance(Object aspectInterface) {
        return newInstance(aspectInterface, null);
    }

    public Object newInstance(Object aspectInterface, Object[] targets) {
        assert aspectInterface instanceof Class : "aspect-classes are identified by the interface-class of their first mixin";
        Object instance = getClass((Class) aspectInterface).newInstance(targets);
        return instance;
    }

    public static AspectRepository getCurrentAspectRepository() {
        return (AspectRepository) Aspects.getCurrentAspectFactory();
    }

}
