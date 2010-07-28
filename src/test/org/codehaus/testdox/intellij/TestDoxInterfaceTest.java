package org.codehaus.testdox.intellij;

import org.codehaus.testdox.intellij.config.ConfigurationBean;
import org.codehaus.testdox.intellij.panel.TestDoxModel;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class TestDoxInterfaceTest extends MockObjectTestCase {

    public void testUpdateCallbackHookInvokesTheRelevantMethodOnTestdoxModel() {
        Mock mockEditorApi = mock(EditorApi.class);
        EditorApi editorApiMock = (EditorApi) mockEditorApi.proxy();

        ConfigurationBean configuration = new ConfigurationBean();
        configuration.setTestNameTemplate(TemplateNameResolver.DEFAULT_TEMPLATE);

        String className = "com.acme.SomeInterface";
        TestInterface testInterface = new TestInterface(className, null, editorApiMock, new TemplateNameResolver(configuration));

        TestDoxInterface testDoxInterface = new TestDoxInterface(null, className, testInterface, null);
        TestDoxModel testDoxModel = new TestDoxModel(configuration);
        testDoxInterface.updateModel(testDoxModel);

        assertFalse("interfaces should not have TestDox data", testDoxModel.hasDox());
        assertEquals(testInterface, testDoxModel.getValueAt(0, 0));
        assertEquals(TestDoxInterface.TEST_ELEMENT, testDoxModel.getValueAt(1, 0));
    }

    public void testHasATextualRepresentation() {
        String displayString = "<font color=\"gray\">Interfaces do not have unit tests.</font>";
        assertEquals("TestDoxInterface's text representation", displayString, TestDoxInterface.TEST_ELEMENT.getDisplayString());
    }

    public void testUsesTheWarningIconAsItsGraphicalRepresentation() {
        assertSame("should have the the Warning icon", IconHelper.getIcon(IconHelper.WARNING_ICON), TestDoxInterface.TEST_ELEMENT.getIcon());
    }
}
