package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.AspectClass;
import com.tirsen.nanning.AspectRepository;
import com.tirsen.nanning.InterceptorDefinition;
import junit.framework.TestCase;
import org.prevayler.Prevayler;
import org.prevayler.implementation.SnapshotPrevayler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PrevaylerTest extends TestCase {

    private AspectRepository aspectRepository;
    private static AspectRepository noPrevaylerAspectRepository;
    private Prevayler prevayler;
    private File prevaylerDir;

    protected void setUp() throws Exception {
        super.setUp();

        aspectRepository = createAspectRepository(true);
        noPrevaylerAspectRepository = createAspectRepository(false);

        prevaylerDir = File.createTempFile("test", "");
        prevaylerDir.delete();
        prevaylerDir.mkdirs();
        prevayler = new SnapshotPrevayler(new MySystem(), prevaylerDir.getAbsolutePath());

        setupPrevaylerInterceptor();
    }

    private void setupPrevaylerInterceptor() {
        InterceptorDefinition interceptorDefinition = aspectRepository.getInterceptor(PrevaylerInterceptor.class);
        PrevaylerInterceptor prevaylerInterceptor = (PrevaylerInterceptor) interceptorDefinition.getSingleton();
        prevaylerInterceptor.setPrevayler(prevayler);
        prevaylerInterceptor.setConstructCommandClass(MyConstructCommand.class);
        prevaylerInterceptor.setInvokeCommandClass(MyInvokeCommand.class);
    }

    private AspectRepository createAspectRepository(boolean withPrevayler) {
        AspectRepository aspectRepository = new AspectRepository();
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(MyObject.class);

        if (withPrevayler) {
            InterceptorDefinition prevaylerInterceptor = new InterceptorDefinition(PrevaylerInterceptor.class);
            aspectRepository.defineInterceptor(prevaylerInterceptor);
            aspectClass.addInterceptor(prevaylerInterceptor);
        }

        aspectClass.setTarget(MyObjectImpl.class);
        aspectRepository.defineClass(aspectClass);
        return aspectRepository;
    }

    public void test() throws IOException, ClassNotFoundException {
        MyObject myObject = (MyObject) aspectRepository.newInstance(MyObject.class);
        myObject.setAttribute("oldValue");
        myObject.setAttribute("newValue");

        // reload database
        prevayler = new SnapshotPrevayler(new MySystem(), prevaylerDir.getAbsolutePath());
        setupPrevaylerInterceptor();
        List objects = ((MySystem) prevayler.system()).getObjects();
        assertEquals("object not persisted", 1, objects.size());
        assertEquals("attribute not correct value", "newValue", ((MyObject) objects.get(0)).getAttribute());
    }

    public static AspectRepository getNoPrevaylerAspectRepository() {
        return noPrevaylerAspectRepository;
    }
}
