package com.ramussoft.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.zip.ZipFile;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.ramussoft.common.Engine;
import com.ramussoft.core.impl.FileIEngineImpl;
import com.ramussoft.database.FileDatabaseFactory;
import com.ramussoft.database.MemoryDatabase;

/**
 * Round-trip test for the .rsf format: open the sample project in the
 * embedded H2 database (no GUI), save it to a temporary file, reopen the
 * saved file and verify that the row counts of the key tables
 * (ramus_elements, ramus_attributes, ramus_qualifiers) are preserved.
 */
public class RsfRoundTripTest {

    private static final String SAMPLE = "dest/doc/ru/Model example.rsf";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        // FileIEngineImpl creates a session directory under user.home;
        // isolate it so the test does not touch the real home.
        System.setProperty("user.home", folder.getRoot().getAbsolutePath());
        System.setProperty("user.ramus.application.name", "RamusRoundTripTest");
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    public void sampleIsPresent() {
        assertTrue("sample not found: " + sampleFile(),
                sampleFile().isFile());
    }

    @Test
    public void rsfSurvivesRoundTrip() throws Exception {
        File sample = sampleFile();

        // 1) Open the original sample and remember the key table sizes.
        MemoryDatabase first = open(sample);
        int[] before;
        File saved = new File(folder.getRoot(), "saved.rsf");
        try {
            before = countKeyTables(first);
            assertTrue("the sample has no elements", before[0] > 0);
            assertTrue("the sample has no attributes", before[1] > 0);
            assertTrue("the sample has no qualifiers", before[2] > 0);

            // 2) Save the opened project to a temporary .rsf.
            FileIEngineImpl impl = delegate(first);
            impl.saveToFile(saved);
        } finally {
            close(first);
        }

        // The saved file must be a readable .rsf archive with the core
        // table exports inside.
        ZipFile zip = new ZipFile(saved);
        try {
            assertTrue("the saved file has no data entries: " + saved,
                    hasEntry(zip, "data/elements.xml")
                            && hasEntry(zip, "data/attributes.xml")
                            && hasEntry(zip, "data/qualifiers.xml"));
        } finally {
            zip.close();
        }

        // 3) Reopen the saved file and compare the key table sizes.
        MemoryDatabase second = open(saved);
        try {
            int[] after = countKeyTables(second);
            assertEquals("ramus_elements row count changed", before[0],
                    after[0]);
            assertEquals("ramus_attributes row count changed", before[1],
                    after[1]);
            assertEquals("ramus_qualifiers row count changed", before[2],
                    after[2]);
        } finally {
            close(second);
        }
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

    /** Returns row counts of ramus_elements, ramus_attributes, ramus_qualifiers. */
    private static int[] countKeyTables(MemoryDatabase database)
            throws Exception {
        Connection connection = database.getTemplate().getConnection();
        Statement st = connection.createStatement();
        try {
            return new int[]{count(st, "ramus_elements"),
                    count(st, "ramus_attributes"), count(st, "ramus_qualifiers")};
        } finally {
            st.close();
        }
    }

    private static int count(Statement st, String table) throws Exception {
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table);
        try {
            assertTrue(rs.next());
            return rs.getInt(1);
        } finally {
            rs.close();
        }
    }

    private static boolean hasEntry(ZipFile zip, String entry) {
        Enumeration<? extends java.util.zip.ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements())
            if (entry.equals(entries.nextElement().getName()))
                return true;
        return false;
    }

    private static File sampleFile() {
        return new File(projectRoot(), SAMPLE);
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
