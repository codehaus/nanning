/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * (C) 2003 Jon Tirsen
 */
package com.tirsen.nanning;

/**
 * Base class for exceptions related to aspects.
 *
 * <!-- $Id: AspectException.java,v 1.6 2003-05-23 07:43:39 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.6 $
 */
public class AspectException extends RuntimeException {
///CLOVER:OFF
    public AspectException() {
    }
///CLOVER:ON

    public AspectException(String message) {
        super(message);
    }

    public AspectException(Throwable e) {
        super(e);
    }

    public AspectException(String message, Throwable cause) {
        super(message, cause);
    }
}
