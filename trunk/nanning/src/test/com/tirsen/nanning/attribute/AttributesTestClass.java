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
 * <!-- $Id: AttributesTestClass.java,v 1.2 2003-02-20 15:36:06 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
 *
 * @class.attribute classValue
 */
public class AttributesTestClass
{
    ///CLOVER:OFF
    /**
     * @field.attribute fieldValue
     */
    public String field;

    /**
     * @method.attribute methodValue
     */
    public void method()
    {
    }

    /**
     * @method.attribute argMethodValue
     */
    public void method(String arg, String arg2)
    {
    }
    ///CLOVER:ON
}
