package com.tirsen.nanning.samples;

import junit.framework.TestCase;
import com.tirsen.nanning.AspectClass;
import com.tirsen.nanning.AttributesCompiler;
import com.tirsen.nanning.Attributes;

import java.io.File;
import java.net.MalformedURLException;

/**
 * TODO document ContractInterceptorTest
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.1 $
 */
public class ContractInterceptorTest extends TestCase {
    public void test() throws MalformedURLException {
        File targetDir = new File("target" + File.separator + "attributes");
        Attributes.addSearchPath(targetDir.toURL());
        AttributesCompiler attributesCompiler = new AttributesCompiler();
        attributesCompiler.setSrc(new File("src" + File.separator + "test"));
        attributesCompiler.setDest(targetDir);
        attributesCompiler.execute();

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
