package com.tirsen.nanning.samples;

/**
 * Always need to use this-reference, jexl doesn't support "default"-variable.
 *
 * @invariant getText() > 0
 */
public interface ContractIntf {
    public int getValue();
    public void setValue(int value);

    /**
     * "{old this.method()}" will be executed before the invocation of the method but used in the
     * post-condition. Variables are named #arg0, #arg1, #arg2 and so on... (in wait for new commons-attributes which
     * could perhaps handle this better.)
     *
     * @requires #arg0 > 0
     * @ensures {old getText()} + #arg0 == getText()
     */
    public void increaseBy(int value);
}
