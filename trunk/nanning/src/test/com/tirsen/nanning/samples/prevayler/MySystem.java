package com.tirsen.nanning.samples.prevayler;

import java.util.ArrayList;
import java.util.List;

import org.prevayler.AlarmClock;

public interface MySystem extends IdentifyingSystem {
    /**
     * @ensures hasObjectID(result)
     * @prevayler-commmand
     */
    MyObject createMyObject();

    List getObjects();
}
