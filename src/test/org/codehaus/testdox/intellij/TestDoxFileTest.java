package org.codehaus.testdox.intellij;

import junit.framework.TestCase;

import org.codehaus.testdox.intellij.panel.TestDoxModel;

public class TestDoxFileTest extends TestCase {

    private TestDoxFile testDoxFile = new TestDoxFile(null, null, null, null, TestMethod.EMPTY_ARRAY()) {
        public void updateModel(TestDoxModel model) { }
    };

    public void testCanBeRepresentedAsText() {
        assertEquals(testDoxFile.getClass().getName() + " { file: null; className: null; testClass: null; testedClass: null }",
                     testDoxFile.toString());
    }

    public void testDoesNotRepresentATestClass() {
        assertFalse(testDoxFile.isTestedClass());
    }

    public void testCannotBeUnitTestedByDefault() {
        assertFalse(testDoxFile.canBeUnitTested());
    }

    public void testCannotNavigateToTestClassByDefault() {
        assertFalse(testDoxFile.canNavigateToTestClass());
    }

    public void testCannotNavigateToTestedClassByDefault() {
        assertFalse(testDoxFile.canNavigateToTestedClass());
    }
}
