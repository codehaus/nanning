package com.tirsen.nanning.samples;

import java.net.MalformedURLException;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.config.Aspect;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.config.Introductor;
import com.tirsen.nanning.config.InterceptorAspect;

/**
 * TODO document ContractInterceptorTest
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirs?n</a>
 * @version $Revision: 1.8 $
 */
public class ContractInterceptorTest extends AbstractAttributesTest {
    public void test() throws MalformedURLException {

        AspectSystem system = new AspectSystem();
        system.addAspect(new InterceptorAspect(new ContractInterceptor()));
        system.addAspect(new Introductor(ContractIntf.class, ContractImpl.class));
        
        ContractIntf contract = (ContractIntf) system.newInstance(ContractIntf.class);

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
