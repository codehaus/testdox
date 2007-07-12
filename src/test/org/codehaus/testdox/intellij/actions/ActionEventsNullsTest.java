package org.codehaus.testdox.intellij.actions;

import junit.framework.TestCase;

import com.intellij.openapi.actionSystem.Presentation;

public class ActionEventsNullsTest extends TestCase {

    private final Presentation actionPresentation = new ToggleTestClassAction().getTemplatePresentation();

    protected void setUp() {
        actionPresentation.setEnabled(true);
    }

    public void testANullTestdoxControllerDisablesThePresentationOfAnAction() {
        ActionEvents.Nulls.TESTDOX_CONTROLLER.updatePresentation(actionPresentation);
        assertFalse("action presentation should be disabled", actionPresentation.isEnabled());
    }

    public void testANullTestdoxControllerJustReturnsNullWhenNotAskedToUpdateThePresentationOfAnAction() {
        assertNull("should return null as the configuration", ActionEvents.Nulls.TESTDOX_CONTROLLER.getConfiguration());
        assertTrue("action presentation should still be enabled", actionPresentation.isEnabled());
    }

    public void testANullTestdoxToolWindowDisablesThePresentationOfAnAction() {
        ActionEvents.Nulls.TESTDOX_TOOL_WINDOW.updatePresentation(actionPresentation);
        assertFalse("action presentation should be disabled", actionPresentation.isEnabled());
    }

    public void testANullTestdoxToolWindowJustReturnsNullWhenNotAskedToUpdateThePresentationOfAnAction() {
        assertNull("should return null as the configuration", ActionEvents.Nulls.TESTDOX_TOOL_WINDOW.toString());
        assertTrue("action presentation should still be enabled", actionPresentation.isEnabled());
    }
}
