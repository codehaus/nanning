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
 * <!-- $Id: ConfigureException.java,v 1.4 2003-05-11 13:40:52 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.4 $
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
