/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;


/**
 * TODO document AspectDefinition
 *
 * <!-- $Id: SideAspectInstance.java,v 1.2 2002-10-30 20:10:54 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
class SideAspectInstance
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
