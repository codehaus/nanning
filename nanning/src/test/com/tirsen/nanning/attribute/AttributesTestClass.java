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
 * <!-- $Id: AttributesTestClass.java,v 1.5 2003-05-20 07:45:09 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.5 $
 *
 * @class.attribute classValue
 */
public class AttributesTestClass implements AttributesTestInterface {
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

    /**
     * @method.attribute arrayArgMethodValue
     */
    public void method(String[] args) {

    }
    ///CLOVER:ON
}
