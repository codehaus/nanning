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
 * <!-- $Id: AspectException.java,v 1.5 2003-04-23 20:44:36 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.5 $
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
