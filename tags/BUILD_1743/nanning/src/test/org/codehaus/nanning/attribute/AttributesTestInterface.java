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
 * <!-- $Id: AttributesTestInterface.java,v 1.1 2003-07-04 10:54:00 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 *
 * @interface.attribute classValue
 */
public interface AttributesTestInterface {
    ///CLOVER:OFF
    /**
     * @interface.attribute methodValue
     */
    void method();

    /**
     * @interface.attribute argMethodValue
     */
    void method(String arg, String arg2);

    /**
     * @interface.attribute arrayArgMethodValue
     */
    void method(String[] args);
    ///CLOVER:ON
}
