/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.attribute;

/**
 * TODO document AttributesTestClass
 *
 * <!-- $Id: AttributesTestClass.java,v 1.1 2003-01-12 13:25:40 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
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
