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
import org.prevayler.PrevalentSystem;
import org.prevayler.Prevayler;

public class PrevaylerTest extends TestCase {

    private AspectRepository aspectRepository;

    private File prevaylerDir;
    private PrevaylerInterceptor prevaylerInterceptor;
    private SnapshotPrevayler prevayler;
    private MySystem system;

    protected void setUp() throws Exception {
        super.setUp();

        AttributesTest.compileAttributes();

        aspectRepository = new AspectRepository();

        aspectRepository.defineInterceptor(new InterceptorDefinition(PrevaylerInterceptor.class));
        prevaylerInterceptor =
                (PrevaylerInterceptor) aspectRepository.getInterceptor(PrevaylerInterceptor.class).getSingleton();

        {
            AspectClass aspectClass = new AspectClass();
            aspectClass.setInterface(MyObject.class);
            aspectClass.addInterceptor(aspectRepository.getInterceptor(PrevaylerInterceptor.class));
            aspectClass.setTarget(MyObjectImpl.class);
            aspectRepository.defineClass(aspectClass);
        }

        {
            AspectClass aspectClass = new AspectClass();
            aspectClass.setInterface(MySystem.class);
            aspectClass.addInterceptor(aspectRepository.getInterceptor(PrevaylerInterceptor.class));
            aspectClass.setTarget(MySystemImpl.class);
            aspectRepository.defineClass(aspectClass);
        }

        Aspects.setContextAspectFactory(aspectRepository);

        prevaylerDir = File.createTempFile("test", "");
        prevaylerDir.delete();
        prevaylerDir.mkdirs();
        prevaylerDir.deleteOnExit();

        initPrevayler();
    }

    public void test() throws IOException, ClassNotFoundException {
        MyObject myObject = system.createMyObject();
        myObject.setAttribute("oldValue");
        myObject.setAttribute("newValue");

        List objects = system.getObjects();
        assertEquals("object not created ", 1, objects.size());
        assertEquals("attribute not correct value", "newValue", ((MyObject) objects.get(0)).getAttribute());

        // reload database
        checkMySystem();

        // reload database with snapshot
        prevayler.takeSnapshot();
        checkMySystem();
    }

    private void checkMySystem() throws IOException, ClassNotFoundException {
        initPrevayler();
        List objects = system.getObjects();
        assertEquals("object not persisted", 1, objects.size());
        assertEquals("property not correct value", "newValue", ((MyObject) objects.get(0)).getAttribute());
    }

    private void initPrevayler() throws IOException, ClassNotFoundException {
        prevayler = new SnapshotPrevayler((PrevalentSystem) aspectRepository.newInstance(MySystem.class),
                                prevaylerDir.getAbsolutePath());
        system = (MySystem) prevayler.system();
        CurrentPrevayler.setPrevayler(prevayler);
    }
}
