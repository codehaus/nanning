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
 * <!-- $Id: AspectException.java,v 1.2 2002-11-17 14:03:33 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class AspectException extends RuntimeException
{
    public AspectException(Throwable e)
    {
        super(e);
    }

    public AspectException(String message, Throwable cause) {
        super(message, cause);
    }
}
