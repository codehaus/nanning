package org.codehaus.nanning.contract;

/**
 * TODO document ContractImpl
 *
 * @author <a href="mailto:jon_tirsen@yahoo.org">Jon Tirsén</a>
 * @version $Revision: 1.1 $
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
