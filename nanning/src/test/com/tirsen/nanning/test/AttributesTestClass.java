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
 * <!-- $Id: AttributesTestClass.java,v 1.2 2002-10-30 21:39:28 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 *
 * @classAttribute classValue
 */
public class AttributesTestClass
{
    ///CLOVER:OFF
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
    public void method(String arg, String arg2)
    {
    }
    ///CLOVER:ON
}
