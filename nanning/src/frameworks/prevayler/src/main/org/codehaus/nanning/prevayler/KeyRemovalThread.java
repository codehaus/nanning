package org.codehaus.nanning.prevayler;

import java.lang.ref.SoftReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.Reference;
import java.util.Map;

public class KeyRemovalThread extends Thread {
    private boolean somethingWasGCd;
    private SoftReference owner;
    private ReferenceQueue queue;
    private Map map;

    public KeyRemovalThread(SoftReference owner, Map map, ReferenceQueue queue) {
        this.owner = owner;
        this.map = map;
        this.queue = queue;
        setDaemon(true);
    }

    public void run() {
        while (!interrupted()) {
            try {
                SoftReference reference = (SoftReference) queue.remove();
                if (timeToDie(reference)) {
                    return;
                }
                somethingWasGCd = true;
                map.remove(new Long(((IdentifiableSoftReference) reference).getObjectId()));
            } catch (InterruptedException ignored) {
            }
        }
    }

    private boolean timeToDie(Reference reference) {
        return reference == owner;
    }

    public boolean hasBeenGCdSinceLastCall() {
        if (somethingWasGCd) {
            somethingWasGCd = false;
            return true;
        }
        return false;
    }
}
