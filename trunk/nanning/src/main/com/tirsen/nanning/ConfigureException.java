/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

/**
 * TODO document ConfigureException
 *
 * <!-- $Id: ConfigureException.java,v 1.1 2002-11-03 17:14:28 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class ConfigureException extends RuntimeException
{
    public ConfigureException()
    {
    }

    public ConfigureException(Throwable cause)
    {
        super(cause);
    }

    public ConfigureException(String message)
    {
        super(message);
    }
}
