package com.tirsen.nanning.samples.prevayler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.tirsen.nanning.AspectClass;
import com.tirsen.nanning.AspectRepository;
import com.tirsen.nanning.AttributesTest;
import com.tirsen.nanning.InterceptorDefinition;
import junit.framework.TestCase;
import org.prevayler.implementation.SnapshotPrevayler;

public class PrevaylerTest extends TestCase {

    private static AspectRepository aspectRepository;

    private File prevaylerDir;

    protected void setUp() throws Exception {
        super.setUp();

        AttributesTest.compileAttributes();

        aspectRepository = new AspectRepository();

        aspectRepository.defineInterceptor(new InterceptorDefinition(PrevaylerInterceptor.class));

        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(MyObject.class);
        aspectClass.addInterceptor(aspectRepository.getInterceptor(PrevaylerInterceptor.class));
        aspectClass.setTarget(MyObjectImpl.class);
        aspectRepository.defineClass(aspectClass);

        CurrentPrevayler.setAspectRepository(aspectRepository);

        prevaylerDir = File.createTempFile("test", "");
        prevaylerDir.delete();
        prevaylerDir.mkdirs();

        initPrevayler();
    }

    public void test() throws IOException, ClassNotFoundException {
        MyObject myObject = (MyObject) aspectRepository.newInstance(MyObject.class);
        myObject.setAttribute("oldValue");
        myObject.setAttribute("newValue");

        List objects = ((MySystem) CurrentPrevayler.getSystem()).getObjects();
        assertEquals("object not created ", 1, objects.size());
        assertEquals("attribute not correct value", "newValue", ((MyObject) objects.get(0)).getAttribute());

        // reload database
        initPrevayler();
        objects = ((MySystem) CurrentPrevayler.getSystem()).getObjects();
        assertEquals("object not persisted", 1, objects.size());
        assertEquals("property not correct value", "newValue", ((MyObject) objects.get(0)).getAttribute());
    }

    private void initPrevayler() throws IOException, ClassNotFoundException {
        SnapshotPrevayler prevayler = new SnapshotPrevayler(new MySystem(), prevaylerDir.getAbsolutePath());
        CurrentPrevayler.setPrevayler(prevayler);
    }
}
