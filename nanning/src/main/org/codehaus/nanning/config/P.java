package org.codehaus.nanning.config;

import org.codehaus.nanning.config.Pointcut;
import org.codehaus.nanning.Mixin;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.attribute.Attributes;

import java.lang.reflect.Method;
import org.codehaus.nanning.util.RegexpPattern;
import java.util.Iterator;

public class P {
    public static Pointcut and(Pointcut pointcut1, Pointcut pointcut2) {
        return new And(pointcut1, pointcut2);
    }

    public static Pointcut all() {
        return new All();
    }

    public static Pointcut isMixinInterface(final Class interfaceClass) {
        return new IsMixinInterface(interfaceClass);
    }

    public static Pointcut not(final Pointcut pointcut) {
        return new Not(pointcut);
    }

    public static Pointcut methodAttribute(String attribute) {
        return new MethodAttribute(attribute);
    }

    public static Pointcut empty() {
        return new Pointcut() {
            public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
                return false;
            }
        };
    }

    public static Pointcut or(final Pointcut pointcut1, final Pointcut pointcut2) {
        return new Or(pointcut1, pointcut2);
    }

    public static Pointcut methodName(final String pattern) {
        return new MethodName(pattern);
    }

    public static Pointcut classAttribute(final String attribute) {
        return new ClassAttribute(attribute);
    }

    public static Pointcut isClass(final Class classIdentifier) {
        return new IsClass(classIdentifier);
    }

    public static class And extends Pointcut {
        private Pointcut pointcut1;
        private Pointcut pointcut2;

        public And(Pointcut pointcut1, Pointcut pointcut2) {
            this.pointcut1 = pointcut1;
            this.pointcut2 = pointcut2;
        }

        public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
            return pointcut1.adviseMethod(instance, mixin, method) && pointcut2.adviseMethod(instance, mixin, method);
        }
    }

    private static class Not extends Pointcut {
        private final Pointcut pointcut;

        public Not(Pointcut pointcut) {
            this.pointcut = pointcut;
        }

        public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
            return !pointcut.adviseMethod(instance, mixin, method);
        }
    }

    public static class All extends Pointcut {
        public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
            return true;
        }

        public boolean introduceOn(AspectInstance instance) {
            return true;
        }
    }

    private static class IsMixinInterface extends Pointcut {
        private final Class interfaceClass;

        public IsMixinInterface(Class interfaceClass) {
            this.interfaceClass = interfaceClass;
        }

        public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
            return checkMixin(mixin);
        }

        private boolean checkMixin(Mixin mixin) {
            return mixin.getInterfaceClass() == interfaceClass;
        }

        public boolean introduceOn(AspectInstance instance) {
            for (Iterator iterator = instance.getMixins().iterator(); iterator.hasNext();) {
                Mixin mixin = (Mixin) iterator.next();
                if (checkMixin(mixin)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class MethodAttribute extends Pointcut {
        private String attribute;

        public MethodAttribute(String attribute) {
            this.attribute = attribute;
        }

        public String getAttribute() {
            return attribute;
        }

        public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
            return Attributes.hasAttribute(method, attribute);
        }
    }

    private static class Or extends Pointcut {
        private final Pointcut pointcut1;
        private final Pointcut pointcut2;

        public Or(Pointcut pointcut1, Pointcut pointcut2) {
            this.pointcut1 = pointcut1;
            this.pointcut2 = pointcut2;
        }

        public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
            return pointcut1.adviseMethod(instance, mixin, method) || pointcut2.adviseMethod(instance, mixin, method);
        }
    }

    private static class MethodName extends Pointcut {
        private final RegexpPattern pattern;

        public MethodName(String pattern) {
            this.pattern = RegexpPattern.compile(pattern);
        }

        public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
            return pattern.matcher(method.getName()).matches();
        }
    }

    private static class ClassAttribute extends Pointcut {
        private final String attribute;

        public ClassAttribute(String attribute) {
            this.attribute = attribute;
        }

        public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
            return matches(instance);
        }

        public boolean introduceOn(AspectInstance instance) {
            return matches(instance);
        }

        private boolean matches(AspectInstance instance) {
            return Attributes.hasInheritedAttribute(instance.getClassIdentifier(), attribute);
        }
    }

    private static class IsClass extends Pointcut {
        private final Class classIdentifier;

        public IsClass(Class classIdentifier) {
            this.classIdentifier = classIdentifier;
        }

        public boolean introduceOn(AspectInstance instance) {
            return matches(instance);
        }

        private boolean matches(AspectInstance instance) {
            return classIdentifier.equals(instance.getClassIdentifier());
        }

        public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
            return matches(instance);
        }
    }
}
