/**
 * Created by IntelliJ IDEA.
 * User: behemoth
 * Date: Dec 3, 2002
 * Time: 3:41:05 PM
 * To change this template use Options | File Templates.
 */
package com.tirsen.nanning;

public interface ConstructionInterceptor extends Interceptor {
    void construct(ConstructionInvocation invocation);

    boolean interceptsConstructor(Class interfaceClass);
}
