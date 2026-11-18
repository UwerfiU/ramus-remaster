package com.ramussoft.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.ramussoft.common.Attribute;
import com.ramussoft.common.Element;
import com.ramussoft.common.Engine;
import com.ramussoft.common.Qualifier;
import com.ramussoft.core.impl.FileIEngineImpl;
import com.ramussoft.database.FileDatabaseFactory;
import com.ramussoft.database.MemoryDatabase;

/**
 * Model operation test: open the sample, change a Text attribute value of an
 * element through the public Engine API, save to .rsf, reopen and verify that
 * the change is persisted and nothing else was lost.
 */
public class ModelOperationRoundTripTest {

    private static final String SAMPLE = "dest/doc/ru/Model example.rsf";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        System.setProperty("user.home", folder.getRoot().getAbsolutePath());
        System.setProperty("user.ramus.application.name", "RamusModelOpTest");
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    public void attributeChangeSurvivesSaveAndReload() throws Exception {
        File sample = new File(projectRoot(), SAMPLE);
        assertTrue("sample not found: " + sample, sample.isFile());

        MemoryDatabase first = open(sample);
        Engine engine = first.getEngine(null);
        Element targetElement;
        Attribute targetAttribute;
        Qualifier targetQualifier;
        String newValue;
        File saved = new File(folder.getRoot(), "model-op.rsf");
        try {
            // Find the first qualifier that has elements and a Core/Text
            // attribute, then change that attribute on its first element.
            targetElement = null;
            targetAttribute = null;
            targetQualifier = null;
            for (Qualifier q : engine.getQualifiers()) {
                List<Attribute> attrs = q.getAttributes();
                Attribute textAttr = findTextAttribute(attrs);
                if (textAttr == null)
                    continue;
                Hashtable<Element, Object[]> elements =
                        engine.getElements(q, attrs);
                if (elements.isEmpty())
                    continue;
                targetElement = elements.keys().nextElement();
                targetAttribute = textAttr;
                targetQualifier = q;
                break;
            }
            assertNotNull("no qualifier with elements and a Text attribute",
                    targetElement);

            newValue = "RamusRemasterTest" + System.nanoTime();
            engine.setAttribute(targetElement, targetAttribute, newValue);

            FileIEngineImpl impl = delegate(first);
            impl.saveToFile(saved);
        } finally {
            close(first);
        }

        // Reopen the saved file and verify the change is persisted.
        MemoryDatabase second = open(saved);
        Engine engine2 = second.getEngine(null);
        try {
            Qualifier q2 = null;
            for (Qualifier q : engine2.getQualifiers())
                if (q.getId() == targetQualifier.getId())
                    q2 = q;
            assertNotNull("qualifier lost after reload", q2);

            Attribute a2 = null;
            for (Attribute a : q2.getAttributes())
                if (a.getId() == targetAttribute.getId())
                    a2 = a;
            assertNotNull("attribute lost after reload", a2);

            Hashtable<Element, Object[]> els2 =
                    engine2.getElements(q2, q2.getAttributes());
            Element e2 = null;
            for (Element e : els2.keySet())
                if (e.getId() == targetElement.getId())
                    e2 = e;
            assertNotNull("element lost after reload", e2);

            assertEquals("attribute value did not survive save/reload",
                    newValue, engine2.getAttribute(e2, a2));
        } finally {
            close(second);
        }
    }

    private static Attribute findTextAttribute(List<Attribute> attributes) {
        for (Attribute attribute : attributes)
            if ("Core".equals(attribute.getAttributeType().getPluginName())
                    && "Text".equals(
                            attribute.getAttributeType().getTypeName()))
                return attribute;
        return null;
    }

    private static MemoryDatabase open(File file) {
        return (MemoryDatabase) FileDatabaseFactory.createDatabase(file);
    }

    private static FileIEngineImpl delegate(MemoryDatabase database) {
        Engine engine = database.getEngine(null);
        return (FileIEngineImpl) engine.getDeligate();
    }

    private static void close(MemoryDatabase database) throws Exception {
        try {
            delegate(database).close();
        } finally {
            database.close();
        }
    }

    /** Walks up from the test working directory to the Gradle project root. */
    private static File projectRoot() {
        File dir = new File(System.getProperty("user.dir")).getAbsoluteFile();
        while (dir != null && !new File(dir, "settings.gradle").isFile())
            dir = dir.getParentFile();
        if (dir == null)
            throw new IllegalStateException(
                    "No project root (settings.gradle) found from "
                            + System.getProperty("user.dir"));
        return dir;
    }
}
