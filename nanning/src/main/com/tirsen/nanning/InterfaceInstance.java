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
 * <!-- $Id: InterfaceInstance.java,v 1.1 2002-10-22 18:28:09 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
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

    public void addInterspector(Object aspect)
    {
        ArrayList arrayList;
        if (interceptors != null)
        {
            arrayList = new ArrayList(interceptors.length);
            arrayList.addAll(Arrays.asList(interceptors));
        }
        else
        {
            arrayList = new ArrayList();
        }
        arrayList.add(aspect);
        interceptors = (Interceptor[]) arrayList.toArray(new Interceptor[0]);
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
