/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

/**
 * TODO document AspectException
 *
 * <!-- $Id: AspectException.java,v 1.3 2003-02-06 20:33:41 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.3 $
 */
public class AspectException extends RuntimeException {
    public AspectException() {
    }

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
