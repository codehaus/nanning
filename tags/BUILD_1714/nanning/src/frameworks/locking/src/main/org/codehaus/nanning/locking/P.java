package org.codehaus.nanning.locking;

import org.codehaus.nanning.config.AbstractPointcut;
import org.codehaus.nanning.config.Pointcut;
import org.codehaus.nanning.MixinInstance;
import org.codehaus.nanning.AspectInstance;

import java.lang.reflect.Method;

public class P {
    public static class AndPointcut extends AbstractPointcut {
        private Pointcut pointcut1;
        private Pointcut pointcut2;

        public AndPointcut(Pointcut pointcut1, Pointcut pointcut2) {
            this.pointcut1 = pointcut1;
            this.pointcut2 = pointcut2;
        }

        public boolean adviseMixin(MixinInstance mixin) {
            return pointcut1.adviseMixin(mixin) && pointcut2.adviseMixin(mixin);
        }

        public boolean adviseInstance(AspectInstance instance) {
            return pointcut1.adviseInstance(instance) && pointcut1.adviseInstance(instance);
        }

        public boolean adviseMethod(Method method) {
            return pointcut1.adviseMethod(method) && pointcut2.adviseMethod(method);
        }
    }

    public static Pointcut and(Pointcut pointcut1, Pointcut pointcut2) {
        return new AndPointcut(pointcut1, pointcut2);
    }
}