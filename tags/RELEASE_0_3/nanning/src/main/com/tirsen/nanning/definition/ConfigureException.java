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
 * <!-- $Id: ConfigureException.java,v 1.2 2003-03-21 17:11:11 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
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
