package org.codehaus.nanning.util;

import java.util.*;

public class LinkedHashSet implements Set {
    private final List list;
    private final HashSet set;

    public LinkedHashSet(int initialCapacity) {
        list = new ArrayList(initialCapacity);
        set = new HashSet(initialCapacity);
    }

    public LinkedHashSet() {
        this(1);
    }

    public LinkedHashSet(Collection collection) {
        this(collection.size());
        addAll(collection);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Object o) {
        return set.contains(o);
    }

    public Iterator iterator() {
        return list.iterator();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public Object[] toArray(Object a[]) {
        return list.toArray(a);
    }

    public boolean add(Object o) {
        if (set.add(o)) {
            return list.add(o);
        }
        return false;
    }

    public boolean remove(Object o) {
        if (set.remove(o)) {
            return list.remove(o);
        }
        return false;
    }

    public boolean containsAll(Collection c) {
        return list.containsAll(c);
    }

    public boolean addAll(Collection c) {
        Set newElements = new HashSet(c);
        newElements.removeAll(set);
        if (set.addAll(c)) {
            return list.addAll(newElements);
        }
        return false;
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        set.clear();
        list.clear();
    }
}
