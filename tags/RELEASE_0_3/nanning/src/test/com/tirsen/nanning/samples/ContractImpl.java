package com.tirsen.nanning.samples;

/**
 * TODO document ContractImpl
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.2 $
 */
public class ContractImpl implements ContractIntf {
    private int value = 1;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void increaseBy(int value) {
        this.value += value;
    }
}
