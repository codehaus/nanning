/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

/**
 * TODO document AttributesTestClass
 *
 * <!-- $Id: AttributesTestClass.java,v 1.1 2002-10-28 21:45:34 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 *
 * @classAttribute classValue
 */
public class AttributesTestClass
{
    /**
     * @fieldAttribute fieldValue
     */
    public String field;

    /**
     * @methodAttribute methodValue
     */
    public void method()
    {
    }

    /**
     * @methodAttribute argMethodValue
     */
    public void method(String arg)
    {
    }
}
