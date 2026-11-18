package com.ramussoft.local;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ramussoft.common.persistent.PersistentWrapper;

/**
 * Unit tests for the H2 2.x reserved-word column name mapping. The mapping is
 * critical for .rsf compatibility: columns whose names are reserved words in
 * H2 2.x (e.g. "value") are stored internally as "<name>_column", but files
 * must be written with the ORIGINAL names so that older versions of Ramus
 * (H2 1.x) can read them.
 */
public class SafeNameMappingTest {

    @Test
    public void reservedWordGetsSuffix() {
        assertEquals("value_column", PersistentWrapper.safeDatabaseName("value"));
    }

    @Test
    public void nonReservedNameUnchanged() {
        assertEquals("element_name",
                PersistentWrapper.safeDatabaseName("element_name"));
    }

    @Test
    public void safeMappingIsIdempotent() {
        assertEquals("value_column",
                PersistentWrapper.safeDatabaseName("value_column"));
    }

    @Test
    public void originalNameRestoresUpperCasedH2Name() {
        // H2 reports unquoted identifiers in upper case.
        assertEquals("VALUE",
                PersistentWrapper.originalDatabaseName("VALUE_COLUMN"));
    }

    @Test
    public void originalNameLeavesPlainNamesAlone() {
        assertEquals("ELEMENT_NAME",
                PersistentWrapper.originalDatabaseName("ELEMENT_NAME"));
        // "name" is not an H2 reserved word, so a column literally called
        // NAME_COLUMN must keep its name.
        assertEquals("NAME_COLUMN",
                PersistentWrapper.originalDatabaseName("NAME_COLUMN"));
    }

    @Test
    public void roundTripThroughBothMappings() {
        String original = "value";
        String stored = PersistentWrapper.safeDatabaseName(original);
        // Export writes the upper-cased stored name, import maps it back.
        assertEquals(original.toUpperCase(), PersistentWrapper
                .originalDatabaseName(stored.toUpperCase()));
    }
}
