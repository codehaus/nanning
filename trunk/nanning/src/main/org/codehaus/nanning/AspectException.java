/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * (C) 2003 Jon Tirsen
 */
package org.codehaus.nanning;

import org.codehaus.nanning.util.WrappedException;

/**
 * Base class for exceptions related to aspects.
 *
 * <!-- $Id: AspectException.java,v 1.2 2003-09-05 07:56:43 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
 */
public class AspectException extends WrappedException {
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
