/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.attribute;

/**
 * TODO document AttributesTestClass
 *
 * <!-- $Id: AttributesTestClass.java,v 1.1 2003-07-04 10:54:00 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 *
 * @class.attribute classValue
 */
public class AttributesTestClass implements AttributesTestInterface {
    ///CLOVER:OFF

    /**
     * @inner.attribute innerValue
     */
    public static class InnerClass {
        /**
         * @inner.field.attribute innerFieldValue
         */
        String innerField;
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
