package org.codehaus.testdox.intellij.inspections;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import junitx.framework.ListAssert;

public class TestDoxInspectionProviderTest extends TestCase {

    private final TestDoxInspectionProvider inspectionProvider = new TestDoxInspectionProvider();

    public void testDefinesItsComponentName() {
        assertEquals("TestDoxInspectionProvider", inspectionProvider.getComponentName());
    }

    public void testDoesNothingWhenInitialisedByIntellijIdea() {
        inspectionProvider.initComponent();
    }

    public void testDoesNothingWhenDisposedByIntellijIdea() {
        inspectionProvider.disposeComponent();
    }

    public void testReturnsAnArrayOfAllKnownInspectionClasses() {
        List inspectionClasses = Arrays.asList(inspectionProvider.getInspectionClasses());
        assertEquals("number of inspections", 2, inspectionClasses.size());
        ListAssert.assertContains(inspectionClasses, EmptyTestClassInspection.class);
        ListAssert.assertContains(inspectionClasses, EmptyTestMethodInspection.class);
    }
}
