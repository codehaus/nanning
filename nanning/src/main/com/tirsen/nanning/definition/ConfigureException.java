/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.definition;

/**
 * TODO document ConfigureException
 *
 * <!-- $Id: ConfigureException.java,v 1.1 2003-01-12 13:25:40 tirsen Exp $ -->
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
