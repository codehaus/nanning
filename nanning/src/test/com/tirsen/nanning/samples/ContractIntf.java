package com.tirsen.nanning.samples;

/**
 * Always need to use this-reference, jexl doesn't support "default"-variable.
 *
 * @invariant this.getValue() > 0
 */
public interface ContractIntf {
    public int getValue();
    public void setValue(int value);

    /**
     * "old this.method()" will be executed before the invocation of the method but used in the
     * post-condition. Variables are named $0, $1, $2 and so on... (in wait for new commons-attributes which will
     * probably handle this better.)
     *
     * @requires $0 > 0
     * @ensures old this.getValue() + $0 == this.getValue()
     */
    public void increaseBy(int value);
}
