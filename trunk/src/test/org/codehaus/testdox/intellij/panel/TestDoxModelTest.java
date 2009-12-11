package org.codehaus.testdox.intellij.panel;

import static jedi.functional.Coercions.array;
import org.codehaus.testdox.intellij.*;
import org.codehaus.testdox.intellij.config.ConfigurationBean;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public class TestDoxModelTest extends MockObjectTestCase {

    private static final TestMethod[] TEST_METHODS = array(
        Mocks.createTestMethod("blarg"), Mocks.createTestMethod("zarg"), Mocks.createTestMethod("blerg")
    );

    private final Mock mockTestClass = Mocks.createAndRegisterTestClassMock(this);

    private final TestDoxModel model = new TestDoxModel(new ConfigurationBean());

    public void testHasNoEditableCells() {
        assertFalse(model.isCellEditable(-1, -1));
        assertFalse(model.isCellEditable(0, 0));
        assertFalse(model.isCellEditable(model.getRowCount(), model.getColumnCount()));
    }

    public void testHasNoDoxWhenConstructed() throws Exception {
        assertFalse(model.hasDox());
    }

    public void testHasNoDoxOutsideModelBoundaries() {
        assertSame(TestDoxNonJavaFile.TEST_ELEMENT, model.getValueAt(-1, 0));
        assertSame(TestDoxNonJavaFile.TEST_ELEMENT, model.getValueAt(model.getRowCount() + 1, 0));
    }

    public void testPopulatesDoxListButReportsHasNoDoxIfNoDoxSet() {
        model.setNotJava();
        assertFalse(model.hasDox());
        assertEquals(1, model.getRowCount());
        assertSame(TestDoxNonJavaFile.TEST_ELEMENT, model.getValueAt(0, 0));
    }

    public void testPopulatesDoxListButReportsHasNoDoxIfTestdoxFileIsAnInterface() {
        TestDoxInterface testDoxInterface = new TestDoxInterface(null, "com.acme.Foo", (TestClass) mockTestClass.proxy(), null);
        testDoxInterface.updateModel(model);

        assertFalse(model.hasDox());
        assertEquals(2, model.getRowCount());
        assertEquals(mockTestClass.proxy(), model.getValueAt(0, 0));
        assertSame(TestDoxInterface.TEST_ELEMENT(), model.getValueAt(1, 0));
    }

    public void testPopulatesDoxListButReportsHasNoDoxIfTestdoxFileIsANonProjectClass() {
        TestDoxNonProjectClass testDoxNonProjectClass = new TestDoxNonProjectClass(null, "com.acme.Foo", (TestClass) mockTestClass.proxy(), null);
        testDoxNonProjectClass.updateModel(model);

        assertFalse(model.hasDox());
        assertEquals(2, model.getRowCount());
        assertEquals(mockTestClass.proxy(), model.getValueAt(0, 0));
        assertSame(TestDoxNonProjectClass.TEST_ELEMENT, model.getValueAt(1, 0));
    }

    public void testPopulatesDoxListAndReportsHasDoxIfDoxSet() {
        TestMethod methodMock = Mocks.createTestMethod("blarg");
        mockTestClass.expects(once()).method("displayString").will(returnValue("foo"));
        createTestDoxClass(methodMock).updateModel(model);

        assertTrue(model.hasDox());
        assertEquals(2, model.getRowCount());
        assertEquals("foo", ((TestElement) model.getValueAt(0, 1)).displayString());
        assertEquals(methodMock.displayString(), ((TestElement) model.getValueAt(1, 1)).displayString());
    }

    public void testClearsPreviousDoxListWhenNoDoxSet() {
        createTestDoxClass(Mocks.createTestMethod("blarg")).updateModel(model);
        assertTrue(model.hasDox());

        model.setNotJava();
        assertFalse(model.hasDox());
        assertEquals(1, model.getRowCount());
    }

    public void testClearsPreviousDoxListWhenNewDoxSet() {
        TestMethod expectedTestMethodA = Mocks.createTestMethod("methodA");
        createTestDoxClass(expectedTestMethodA).updateModel(model);
        assertEquals(2, model.getRowCount());
        assertEquals(expectedTestMethodA, model.getValueAt(1, 0));

        Object actualTestElement1 = model.getValueAt(0, 0);
        Object actualTestElement2 = model.getValueAt(1, 0);

        TestMethod expectedTestMethodB = Mocks.createTestMethod("methodB");
        createTestDoxClass(expectedTestMethodB).updateModel(model);
        assertEquals(2, model.getRowCount());
        assertEquals(expectedTestMethodB, model.getValueAt(1, 0));

        assertSame(actualTestElement1, model.getValueAt(0, 0));
        assertNotSame(actualTestElement2, model.getValueAt(1, 0));
    }

    public void testCanSortEntriesAlphabetically() {
        createTestDoxClass(TEST_METHODS).updateModel(model);

        mockTestClass.expects(once()).method("displayString").will(returnValue("foo"));

        model.sortInAlphabeticalOrder();

        assertOrder(
            array("foo", TEST_METHODS[0].displayString(), TEST_METHODS[2].displayString(), TEST_METHODS[1].displayString()),
            model
        );
    }

    public void testCanRevertSortToDefinitionOrderFromAlphabeticalOrder() {
        createTestDoxClass(TEST_METHODS).updateModel(model);

        mockTestClass.expects(once()).method("displayString").will(returnValue("foo"));

        model.sortInAlphabeticalOrder();
        model.sortInDefinitionOrder();

        assertOrder(
            array("foo", TEST_METHODS[0].displayString(), TEST_METHODS[1].displayString(), TEST_METHODS[2].displayString()),
            model
        );
    }

    public void testSetsInitialSortOrderBasedOnStoredConfigurationSetting() {
        ConfigurationBean config = new ConfigurationBean();
        config.setAlphabeticalSorting(true);

        TestDoxModel testDoxModel = new TestDoxModel(config);
        createTestDoxClass(TEST_METHODS).updateModel(testDoxModel);

        mockTestClass.expects(once()).method("displayString").will(returnValue("foo"));

        assertOrder(
            array("foo", TEST_METHODS[0].displayString(), TEST_METHODS[2].displayString(), TEST_METHODS[1].displayString()),
            testDoxModel
        );
    }

    public void testDoesNotPerformSortIfNoDoxAreAvailable() {
        model.setNotJava();
        model.sortInAlphabeticalOrder();

        assertOrder(array(TestDoxNonJavaFile.NO_CLASS_MESSAGE), model);
    }

    private void assertOrder(Object[] values, TestDoxModel model) {
        assertEquals(values.length, model.getRowCount());
        for (int i = 0; i < values.length; i++) {
            TestElement testElement = (TestElement) model.getValueAt(i, 1);
            assertEquals(values[i], testElement.displayString());
        }
    }

    private TestDoxClass createTestDoxClass(TestMethod... testMethods) {
        return new TestDoxClass(null, "com.acme.Foo", true, (TestClass) mockTestClass.proxy(), null, testMethods);
    }
}
