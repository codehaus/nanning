package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.AspectClass;
import com.tirsen.nanning.AspectRepository;
import com.tirsen.nanning.InterceptorDefinition;
import com.tirsen.nanning.AttributesTest;
import junit.framework.TestCase;
import org.prevayler.Prevayler;
import org.prevayler.implementation.SnapshotPrevayler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PrevaylerTest extends TestCase {

    private static AspectRepository aspectRepository;
    private static MySystem mySystem;
    private static Prevayler prevayler;

    private File prevaylerDir;

    protected void setUp() throws Exception {
        super.setUp();

        AttributesTest.compileAttributes();

        aspectRepository = new AspectRepository();
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(MyObject.class);

        if (true) {
            InterceptorDefinition prevaylerInterceptor = new InterceptorDefinition(PrevaylerInterceptor.class);
            aspectRepository.defineInterceptor(prevaylerInterceptor);
            aspectClass.addInterceptor(prevaylerInterceptor);
        }

        aspectClass.setTarget(MyObjectImpl.class);
        aspectRepository.defineClass(aspectClass);

        prevaylerDir = File.createTempFile("test", "");
        prevaylerDir.delete();
        prevaylerDir.mkdirs();
        mySystem = new MySystem();
        prevayler = new SnapshotPrevayler(mySystem, prevaylerDir.getAbsolutePath());

        setupPrevaylerInterceptor();
    }

    private void setupPrevaylerInterceptor() {
        PrevaylerInterceptor prevaylerInterceptor = getPrevaylerInterceptor();
        prevaylerInterceptor.setPrevayler(prevayler);
        prevaylerInterceptor.setConstructCommandClass(MyConstructCommand.class);
        prevaylerInterceptor.setInvokeCommandClass(MyInvokeCommand.class);
    }

    public static PrevaylerInterceptor getPrevaylerInterceptor() {
        InterceptorDefinition interceptorDefinition = aspectRepository.getInterceptor(PrevaylerInterceptor.class);
        PrevaylerInterceptor prevaylerInterceptor = (PrevaylerInterceptor) interceptorDefinition.getSingleton();
        return prevaylerInterceptor;
    }

    public void test() throws IOException, ClassNotFoundException {
        MyObject myObject = (MyObject) aspectRepository.newInstance(MyObject.class);
        myObject.setAttribute("oldValue");
        myObject.setAttribute("newValue");

        List objects = ((MySystem) prevayler.system()).getObjects();
        assertEquals("object not created ", 1, objects.size());
        assertEquals("attribute not correct value", "newValue", ((MyObject) objects.get(0)).getAttribute());

        // reload database
        prevayler = new SnapshotPrevayler(new MySystem(), prevaylerDir.getAbsolutePath());
        mySystem = (MySystem) prevayler.system();
        setupPrevaylerInterceptor();
        objects = ((MySystem) prevayler.system()).getObjects();
        assertEquals("object not persisted", 1, objects.size());
        assertEquals("property not correct value", "newValue", ((MyObject) objects.get(0)).getAttribute());
    }

    public static AspectRepository getAspectRepository() {
        return aspectRepository;
    }

    public static MySystem getMySystem() {
        return mySystem;
    }
}
