package com.tirsen.nanning.samples.prevayler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.tirsen.nanning.*;
import com.tirsen.nanning.attribute.AttributesTest;
import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.AspectRepository;
import com.tirsen.nanning.definition.InterceptorDefinition;
import junit.framework.TestCase;
import org.prevayler.implementation.SnapshotPrevayler;

public class PrevaylerTest extends TestCase {

    private AspectFactory aspectRepository;

    private File prevaylerDir;

    protected void setUp() throws Exception {
        super.setUp();

        AttributesTest.compileAttributes();

        aspectRepository = new AspectRepository();

        AspectRepository aspectRepository1 = new AspectRepository();
        aspectRepository1.defineInterceptor(new InterceptorDefinition(PrevaylerInterceptor.class));

        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(MyObject.class);
        aspectClass.addInterceptor(aspectRepository1.getInterceptor(PrevaylerInterceptor.class));
        aspectClass.setTarget(MyObjectImpl.class);
        aspectRepository1.defineClass(aspectClass);
        aspectRepository = aspectRepository1;

        Aspects.setContextAspectFactory(aspectRepository);

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
