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
 * <!-- $Id: ConfigureException.java,v 1.5 2003-05-22 20:18:32 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.5 $
 *
 * @deprecated please use the new {@link com.tirsen.nanning.config.AspectSystem} framework instead.
 */
public class ConfigureException extends RuntimeException {
    public ConfigureException() {
    }

    public ConfigureException(Throwable cause) {
        super(cause);
    }

    public ConfigureException(String message) {
        super(message);
    }
}
