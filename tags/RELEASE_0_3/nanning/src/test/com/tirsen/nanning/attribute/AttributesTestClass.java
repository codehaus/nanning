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
 * <!-- $Id: AttributesTestClass.java,v 1.4 2003-04-14 17:33:00 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.4 $
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

    /**
     * @method.attribute arrayArgMethodValue
     */
    public void method(String[] args) {

    }
    ///CLOVER:ON
}
