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
 * <!-- $Id: AttributesTestClass.java,v 1.6 2003-06-09 17:40:42 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.6 $
 *
 * @class.attribute classValue
 */
public class AttributesTestClass implements AttributesTestInterface {
    ///CLOVER:OFF

    /**
     * @inner.attribute innerValue
     */
    public static class InnerClass {
    }

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
