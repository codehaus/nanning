package com.tirsen.nanning.prevayler;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

import org.apache.commons.collections.ReferenceMap;

public class SoftMap extends ReferenceMap implements Externalizable {
    static final long serialVersionUID = -7396446944555084132L;

    /* Only for externalization */
    private transient List readBackValues;
    private boolean valuesAreSoft;


    public static SoftMap createSoftValuesMap() {
        return new SoftMap();
    }

    public static SoftMap createSoftKeysMap() {
        return new SoftMap(false);
    }

    /**
     * Create a map with soft values
     */
    public SoftMap() {
        this(true);
    }

    public SoftMap(SoftMap map) {
        this.valuesAreSoft = map.valuesAreSoft;
        for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    private SoftMap(boolean valuesAreSoft) {
        super(valuesAreSoft ? HARD : SOFT, valuesAreSoft ? SOFT : HARD);
        this.valuesAreSoft = valuesAreSoft;
    }

    public void clear() {
        buildMap();
        super.clear();
    }

    public boolean containsKey(Object key) {
        buildMap();
        return super.containsKey(key);
    }

    public Set entrySet() {
        buildMap();
        return super.entrySet();
    }

    public Object get(Object key) {
        buildMap();
        return super.get(key);
    }

    public boolean isEmpty() {
        buildMap();
        return super.isEmpty();
    }

    public Set keySet() {
        buildMap();
        return super.keySet();
    }

    public Object put(Object key, Object value) {
        buildMap();
        return super.put(key, value);
    }

    public Object remove(Object key) {
        buildMap();
        return super.remove(key);
    }

    public int size() {
        buildMap();
        return super.size();
    }

    public Collection values() {
        buildMap();
        return super.values();
    }

    protected Object clone() throws CloneNotSupportedException {
        buildMap();
        return super.clone();
    }

    public boolean containsValue(Object value) {
        buildMap();
        return super.containsValue(value);
    }

    public boolean equals(Object o) {
        buildMap();
        return super.equals(o);
    }

    public int hashCode() {
        buildMap();
        return super.hashCode();
    }

    public void putAll(Map t) {
        buildMap();
        super.putAll(t);
    }

    public String toString() {
        buildMap();
        return super.toString();
    }

    /**
     * This provides lazy deserialization which is needed if you have dynamic proxies as keys
     */
    private synchronized void buildMap() {
        if (readBackValues != null) {
            List values = readBackValues;
            readBackValues = null;
            for (Iterator i = values.iterator(); i.hasNext();) {
                Object key = i.next();
                Object value = i.next();
                put(key, value);
            }
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        Set keys = keySet();
        out.writeInt(keys.size());

        for (Iterator i = keys.iterator(); i.hasNext();) {
            Object key = i.next();
            Object value = get(key);
            out.writeObject(key);
            out.writeObject(value);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int num = in.readInt();
        readBackValues = new ArrayList(num * 2);

        for (int i = 0; i < num; i++) {
            Object key = in.readObject();
            assert key != null;
            Object value = in.readObject();
            assert value != null;

            readBackValues.add(key);
            readBackValues.add(value);
        }
    }
}

