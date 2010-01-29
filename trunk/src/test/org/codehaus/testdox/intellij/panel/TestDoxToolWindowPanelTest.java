package org.codehaus.testdox.intellij.panel;

import com.intellij.psi.PsiMethod;
import static jedi.functional.Coercions.array;
import org.codehaus.testdox.intellij.*;
import org.codehaus.testdox.intellij.config.Configuration;
import org.codehaus.testdox.intellij.ui.TestDoxTableModel;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class TestDoxToolWindowPanelTest extends MockObjectTestCase {

    private static final TestMethod[] TEST_METHODS = array(
        Mocks.createTestMethod("testOne"), Mocks.createTestMethod("testTwo"), Mocks.createTestMethod("testThree")
    );

    private final Mock mockTestDoxController = mock(TestDoxController.class);

    private Component actionToolbarComponent = new JPanel();
    private Configuration configuration = new Configuration();
    private TestDoxTableModel model = new TestDoxTableModel(configuration);
    private JTable table = new JTable();
    private TestDoxToolWindowPanel window;

    protected void setUp() {
        configuration.setUnderscoreMode(true);
        table.setModel(model);

        mockTestDoxController.expects(once()).method("getConfiguration").will(returnValue(configuration));
        window = new TestDoxToolWindowPanel((TestDoxController) mockTestDoxController.proxy(), table, actionToolbarComponent) {
            void handleSelection() {
            }
        };

        model.addTableModelListener(window);
        mockTestDoxController.expects(once()).method("getModel").will(returnValue(model));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        configuration.removePropertyChangeListener(window);
        model.removeTableModelListener(window);
    }

    public void testShowsNoDoxMessageAndDisablesDoxListWhenSettingJavaFileWhichHasNoDox() {
        updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, TestMethod.EMPTY_ARRAY()));
        assertFalse(table.isEnabled());
        assertEquals(2, table.getModel().getRowCount());
    }

    public void testClearsPreviousDoxListWhenSettingNewDox() {
        updateTestDoxModelUsingTestDoxFile(new TestDoxNonJavaFile(null));
        assertNoDox(table, TestDoxNonJavaFile.TEST_ELEMENT());
    }

    public void testShowsNoClassMessageAndDisablesDoxListWhenSettingNoFile() {
        model.setNotJava();
        assertNoDox(table, TestDoxNonJavaFile.TEST_ELEMENT());
    }

    public void testShowsNoDoxMessageAtStartup() {
        model.setNotJava();
        assertNoDox(table, TestDoxNonJavaFile.TEST_ELEMENT());
    }

    public void testShowsMethodBasedDoxEvenIfTestDoxFileHasNoClassReferences() {
        updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, TEST_METHODS));
        assertDox(TEST_METHODS, table);
    }

    public void testNavigatesToSourceOnDoubleClick() throws Exception {
        updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, TEST_METHODS));
        table.changeSelection(1, -1, false, false);

        mockTestDoxController.expects(once()).method("jumpToTestElement").with(eq(TEST_METHODS[0]), eq(false));

        MouseEvent event = new MouseEvent(table, 0, System.currentTimeMillis(), 0, 0, 0, 2, false);
        window.handleMouseEvent(event);
    }

    public void testForwardsNavigateToSourceCommandToProjectComponentIfEnterKeyPressed() throws Exception {
        mockTestDoxController.expects(once()).method("jumpToTestElement").with(eq(TEST_METHODS[0]), eq(false));

        updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, TEST_METHODS));
        table.changeSelection(1, -1, false, false);
        window.handleKeyEvent(createKeyEvent(KeyEvent.VK_ENTER, KeyEvent.VK_UNDEFINED));
    }

    public void testForwardsRenameCommandToProjectComponentIfRenameKeyPressed() throws Exception {
        mockTestDoxController.expects(once()).method("startRename").with(eq(TEST_METHODS[0]));

        initialisePanelAndSelectFirstRow(TEST_METHODS);
        table.changeSelection(1, 1, false, false);
        window.handleKeyEvent(createKeyEvent(KeyEvent.VK_F6, KeyEvent.VK_UNDEFINED));
    }

    public void testIgnoresRenameKeyEventIfSelectedItemIsNotATestElement() throws Exception {
        model.setNotJava();
        table.changeSelection(0, 0, false, false);
        window.handleKeyEvent(createKeyEvent(KeyEvent.VK_F6, KeyEvent.VK_UNDEFINED));
    }

    public void testUsesTheSelectedTestElementToDeleteItselfWhenTheDeleteKeyIsPressed() throws Exception {
        MockApplicationManager.reset();
        Mock mockPsiMethod = mock(PsiMethod.class);
        Mock mockEditorApi = mock(EditorApi.class);

        mockPsiMethod.stubs().method("getName").will(returnValue("testSomething"));
        mockEditorApi.expects(once()).method("delete").with(isA(PsiMethod.class));

        initialisePanelAndSelectFirstRow(
            new TestMethod(
                (PsiMethod) mockPsiMethod.proxy(),
                (EditorApi) mockEditorApi.proxy(),
                new SentenceManager(new Configuration())
            )
        );
        window.handleKeyEvent(createKeyEvent(KeyEvent.VK_DELETE, KeyEvent.VK_UNDEFINED));
    }

    private void initialisePanelAndSelectFirstRow(TestMethod... testMethods) {
        updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, testMethods));
        table.changeSelection(1, -1, false, false);
    }

    private void assertDox(TestMethod[] dox, JTable table) {
        assertTrue(table.isEnabled());
        TableModel tableModel = table.getModel();
        assertEquals(dox.length + 1, tableModel.getRowCount());
        for (int i = 1; i < tableModel.getRowCount(); i++) {
            assertEquals(dox[i - 1].displayString(), ((TestElement) tableModel.getValueAt(i, 1)).displayString());
        }
    }

    private void assertNoDox(JTable table, TestElement... noDoxElements) {
        assertFalse(table.isEnabled());
        assertEquals(noDoxElements.length, table.getModel().getRowCount());
        for (int i = 0; i < noDoxElements.length; i++) {
            assertEquals(noDoxElements[i], table.getModel().getValueAt(i, 0));
        }
    }

    private void updateTestDoxModelUsingTestDoxFile(TestDoxFile file) {
        file.updateModel(model);
    }

    private KeyEvent createKeyEvent(int code, int modifiers) {
        return new KeyEvent(table, 0, System.currentTimeMillis(), modifiers, code, (char) code);
    }
}