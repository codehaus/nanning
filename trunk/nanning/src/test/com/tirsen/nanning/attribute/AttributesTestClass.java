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
 * <!-- $Id: AttributesTestClass.java,v 1.3 2003-03-21 17:11:14 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.3 $
 *
 * @class.attribute classValue
 */
public class AttributesTestClass {
    ///CLOVER:OFF
    /**
     * @field.attribute fieldValue
     */
    public String field;

    /**
     * @method.attribute methodValue
     */
    public void method() {
    }

    /**
     * @method.attribute argMethodValue
     */
    public void method(String arg, String arg2) {
    }
    ///CLOVER:ON
}
