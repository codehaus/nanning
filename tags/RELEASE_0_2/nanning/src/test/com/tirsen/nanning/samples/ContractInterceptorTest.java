package com.tirsen.nanning.samples;

import java.net.MalformedURLException;

import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.definition.AspectClass;

/**
 * TODO document ContractInterceptorTest
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.5 $
 */
public class ContractInterceptorTest extends AbstractAttributesTest {
    public void test() throws MalformedURLException {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(ContractIntf.class);
        aspectClass.addInterceptor(ContractInterceptor.class);
        aspectClass.setTarget(ContractImpl.class);

        ContractIntf contract = (ContractIntf) aspectClass.newInstance();

        try {
            contract.increaseBy(-1);
            fail("call allowed with pre-condition violoation");
        } catch (AssertionError shouldHappen) {
        }

        contract.increaseBy(1);

        try {
            contract.setValue(-1);
            fail("call did not fail with class-invariant violoation");
        } catch (AssertionError shouldHappen) {
        }
    }
}
