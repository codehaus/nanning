package org.codehaus.nanning.contract;

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.MixinInstance;
import org.codehaus.nanning.attribute.AbstractAttributesTest;

import java.net.MalformedURLException;

/**
 * TODO document ContractInterceptorTest
 *
 * @author <a href="mailto:jon_tirsen@yahoo.org">Jon Tirs?n</a>
 * @version $Revision: 1.1 $
 */
public class ContractInterceptorTest extends AbstractAttributesTest {
    public void test() throws MalformedURLException {
        AspectInstance instance = new AspectInstance();
        instance.addMixin(new MixinInstance(ContractIntf.class, new ContractImpl()));
        instance.addInterceptor(new ContractInterceptor());

        ContractIntf contract = (ContractIntf) instance.getProxy();

        try {
            contract.increaseBy(-1);
            fail("call allowed with pre-condition violation");
        } catch (AssertionError shouldHappen) {
        }

        contract.increaseBy(1);

        try {
            contract.setValue(-1);
            fail("call did not fail with class-invariant violation");
        } catch (AssertionError shouldHappen) {
        }
    }
}
