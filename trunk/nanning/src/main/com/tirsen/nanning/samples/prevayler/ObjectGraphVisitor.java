package com.tirsen.nanning.samples.prevayler;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ObjectGraphVisitor {
    private static final Log logger = LogFactory.getLog(ObjectGraphVisitor.class);
    protected Set visited = new HashSet();

    public Set getVisited() {
        return visited;
    }

    public static void visit(Object o, ObjectGraphVisitor visitor) {
        visitor.visited.add(o);
        visitor.visit(o);
    }

    protected void visit(Object o) {
        visitInClass(o.getClass(), o);
    }

    private void visitInClass(Class aClass, Object o) {
        if (aClass != null && aClass != Object.class) {
            visitInClass(aClass.getSuperclass(), o);
            Field[] fields = aClass.getDeclaredFields();
            for (int fieldIndex = 0; fieldIndex < fields.length; fieldIndex++) {
                visitField(fields[fieldIndex], o);
            }
        }
    }

    protected void visitField(Field field, Object container) {
        if (!Modifier.isStatic(field.getModifiers())) {
            field.setAccessible(true);
            try {
                Object nested = field.get(container);
                if (nested != null && nested.getClass().isArray()) {
                    for (int arrayIndex = 0; arrayIndex < Array.getLength(nested); arrayIndex++) {
                        Object arrayNested = Array.get(nested, arrayIndex);
                        visitNested(arrayNested);
                    }
                } else {
                    visitNested(nested);
                }
            } catch (Exception e) {
                logger.warn("could not enter field " + field + " on object " + container, e);
            }
        }
    }

    private void visitNested(Object nested) {
        if (nested != null && !visited.contains(nested)) {
            visited.add(nested);
            visit(nested);
        }
    }

}
