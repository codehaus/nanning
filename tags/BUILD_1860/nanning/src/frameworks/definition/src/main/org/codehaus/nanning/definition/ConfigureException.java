/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.definition;

import org.codehaus.nanning.util.WrappedException;

/**
 * TODO document ConfigureException
 *
 * <!-- $Id: ConfigureException.java,v 1.2 2003-09-05 07:56:42 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
 *
 * @deprecated please use the new {@link org.codehaus.nanning.config.AspectSystem} framework instead.
 */
public class ConfigureException extends WrappedException {
    public ConfigureException() {
    }

    public ConfigureException(Throwable cause) {
        super(cause);
    }

    public ConfigureException(String message) {
        super(message);
    }
}
