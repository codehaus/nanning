/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * (C) 2003 Jon Tirsen
 */
package org.codehaus.nanning;

/**
 * Base class for exceptions related to aspects.
 *
 * <!-- $Id: AspectException.java,v 1.1 2003-07-04 10:53:59 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
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
