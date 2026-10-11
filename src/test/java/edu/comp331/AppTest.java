package edu.comp331;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }

    public void testCountWords() {
        assertEquals(3, TextUtils.countWords("one two three"));
        assertEquals(3, TextUtils.countWords("  one   two\tthree\n"));
    }

    public void testCountWordsWithEmptyInput() {
        assertEquals(0, TextUtils.countWords(null));
        assertEquals(0, TextUtils.countWords(""));
        assertEquals(0, TextUtils.countWords(" \t\n "));
    }

}
