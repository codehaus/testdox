package org.codehaus.testdox.intellij.panel;

import static jedi.functional.Coercions.array;
import org.codehaus.testdox.intellij.*;
import org.codehaus.testdox.intellij.config.ConfigurationBean;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public class QuickDoxDialogTest extends MockObjectTestCase {

    public void testIsOnlyVisibleWhenExplicitlyShown() {
        Mock mockTestClass = Mocks.createAndRegisterTestClassMock(this);
        mockTestClass.expects(once()).method("displayString").will(returnValue("FooClass"));

        Mock mockEditorApi = mock(EditorApi.class);
        mockEditorApi.expects(exactly(2)).method("activateSelectedTextEditor");

        ConfigurationBean configuration = new ConfigurationBean();
        TestDoxModel model = new TestDoxModel(configuration);
        TestDoxFile testDoxClass = createTestDoxFileRepresentingAProjectClass((TestClass) mockTestClass.proxy());
        testDoxClass.updateModel(model);

        QuickDoxDialog dialog = new QuickDoxDialog(null, (EditorApi) mockEditorApi.proxy(), model, configuration);
        assertFalse(dialog.isVisible());

        dialog.show();
        assertTrue(dialog.isVisible());

        dialog.hide();
        assertFalse(dialog.isVisible());

        configuration.removePropertyChangeListener(dialog);
    }

    private TestDoxClass createTestDoxFileRepresentingAProjectClass(TestClass testClass) {
        return new TestDoxClass(null, "blarg", true, testClass, null, array(
            Mocks.createTestMethod("foo"),
            Mocks.createTestMethod("bar"),
            Mocks.createTestMethod("baz")
        ));
    }
}
