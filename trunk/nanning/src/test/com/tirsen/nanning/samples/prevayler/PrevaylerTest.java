package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.AspectRepository;
import com.tirsen.nanning.definition.InterceptorDefinition;
import org.prevayler.PrevalentSystem;
import org.prevayler.Prevayler;
import org.prevayler.implementation.SnapshotPrevayler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PrevaylerTest extends AbstractAttributesTest {

    private AspectRepository aspectRepository;

    private File prevaylerDir;

    protected void setUp() throws Exception {
        super.setUp();

        aspectRepository = new AspectRepository();

        aspectRepository.defineInterceptor(new InterceptorDefinition(PrevaylerInterceptor.class));

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
    }

    public void test() throws IOException, ClassNotFoundException {
        SnapshotPrevayler prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                MyObject myObject = currentMySystem().createMyObject();
                myObject.setAttribute("oldValue");
                myObject.setAttribute("newValue");

                List objects = currentMySystem().getObjects();
                assertEquals("object not created ", 2, objects.size());
                assertEquals("attribute not correct value", "newValue", ((MyObject) objects.get(1)).getAttribute());
            }
        });

        // reload database
        checkMySystem();

        // reload database with snapshot
        prevayler.takeSnapshot();
        checkMySystem();
    }

    private void checkMySystem() throws IOException, ClassNotFoundException {
        Prevayler prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                List objects = currentMySystem().getObjects();
                assertEquals("object not persisted", 2, objects.size());
                assertEquals("property not correct value", "newValue", ((MyObject) objects.get(1)).getAttribute());
            }
        });
    }

    private static MySystem currentMySystem() {
        return (MySystem) CurrentPrevayler.getSystem();
    }

    private SnapshotPrevayler newPrevayler() throws IOException, ClassNotFoundException {
        SnapshotPrevayler prevayler = new SnapshotPrevayler((PrevalentSystem) aspectRepository.newInstance(MySystem.class),
                                prevaylerDir.getAbsolutePath());
        return prevayler;
    }
}
