/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.definition;

import org.codehaus.nanning.*;

import java.lang.reflect.Method;


/**
 * Implement this if you want to do method-level filtering when using AspectClass. Warning! If you're using
 * AspectInstance this won't work, you have to do you method-level filtering manually.
 *
 *
 * @deprecated this is still supported but will be removed before 1.0, migrate to new AspectSystem and InterceptorAspect.
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
 */
public interface FilterMethodsInterceptor extends MethodInterceptor {
    boolean interceptsMethod(Method method);
}
