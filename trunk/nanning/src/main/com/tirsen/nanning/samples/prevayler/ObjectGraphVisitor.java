package com.tirsen.nanning.samples.prevayler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

public class ObjectGraphVisitor {
    private static final Log logger = LogFactory.getLog(ObjectGraphVisitor.class);
    protected Set visited = new HashSet();

    public Set getVisited() {
        return visited;
    }

    public static void visit(Object o, ObjectGraphVisitor visitor) {
        visitor.visit(o);
    }

    protected void visit(Object o) {
        visited.add(o);
        Field[] fields = o.getClass().getDeclaredFields();
        for (int fieldIndex = 0; fieldIndex < fields.length; fieldIndex++) {
            Field field = fields[fieldIndex];
            field.setAccessible(true);
            try {
                Object nested = field.get(o);
                if(nested != null && nested.getClass().isArray()) {
                    for (int arrayIndex = 0; arrayIndex < Array.getLength(nested); arrayIndex++) {
                        Object arrayNested = Array.get(nested, arrayIndex);
                        visitNested(arrayNested);
                    }
                } else {
                    visitNested(nested);
                }
            } catch (Exception e) {
                logger.warn("could not enter field " + field + " on object " + o, e);
            }
        }
    }

    private void visitNested(Object nested) {
        if (nested != null && !visited.contains(nested)) {
            visit(nested);
        }
    }

}
