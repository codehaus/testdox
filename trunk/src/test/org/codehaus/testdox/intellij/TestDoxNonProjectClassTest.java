package org.codehaus.testdox.intellij;

import junit.framework.TestCase;

public class TestDoxNonProjectClassTest extends TestCase {

    public void testHasATextualRepresentation() {
        String displayString = "<font color=\"gray\">Not in current project</font>";
        assertEquals("TestDoxNonProject's text representation", displayString, TestDoxNonProjectClass.TEST_ELEMENT.getDisplayString());
    }

    public void testUsesTheWarningIconAsItsGraphicalRepresentation() {
        assertSame("should have the the Warning icon", IconHelper.getIcon(IconHelper.WARNING_ICON), TestDoxNonProjectClass.TEST_ELEMENT.getIcon());
    }
}
