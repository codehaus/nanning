/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * TODO document InterfaceDefinition
 *
 * <!-- $Id: InterfaceInstance.java,v 1.2 2002-10-23 21:26:43 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
class InterfaceInstance
{
    private Class interfaceClass;
    private Interceptor[] interceptors;
    private Object target;

    public void setInterface(Class interfaceClass)
    {
        this.interfaceClass = interfaceClass;
    }

    void setInterceptors(Interceptor[] interceptors)
    {
        this.interceptors = interceptors;
    }

    public void setTarget(Object target)
    {
        this.target = target;
    }

    public Class getInterfaceClass()
    {
        return interfaceClass;
    }

    public Interceptor[] getInterceptors()
    {
        return interceptors;
    }

    public Object getTarget()
    {
        return target;
    }
}
