package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.codehaus.testdox.intellij.EditorApi;
import org.codehaus.testdox.intellij.Mocks;
import org.codehaus.testdox.intellij.TestDoxController;
import org.codehaus.testdox.intellij.TestDoxProjectComponent;
import org.codehaus.testdox.intellij.panel.TestDoxToolWindowUI;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.Mock;

public class BaseTestMethodIntentionActionTest extends TestDoxActionTestCase {

    private static final boolean USE_WITHIN_TESTDOX_TOOL_WINDOW  = true;
    private static final boolean USE_OUTSIDE_TESTDOX_TOOL_WINDOW = false;
    private static final boolean IS_A_TEST_METHOD = true;

    static {
        MockApplicationManager.reset();
    }

    protected final Mock mockEditor = mock(Editor.class);
    protected final Mock mockPsiFile = mock(PsiFile.class);
    protected final Mock mockPsiElement = mock(PsiElement.class);
    protected final Project projectMock = (Project) mock(Project.class).proxy();

    public void testDefinesATextualDescriptionToBeDisplayedInAListOfAvailableIntentions() {
        BaseTestMethodIntentionAction action = createTestMethodIntentionAction(USE_OUTSIDE_TESTDOX_TOOL_WINDOW);
        assertEquals("action's text", "short description", action.getText());
    }

    public void testDefinesAFamilyNameForAllTestMethodIntentions() {
        BaseTestMethodIntentionAction action = createTestMethodIntentionAction(USE_OUTSIDE_TESTDOX_TOOL_WINDOW);
        assertEquals("action's family name", "TestDox.TestMethodIntentions", action.getFamilyName());
    }

    public void testDoesNotStartInWriteAction() {
        BaseTestMethodIntentionAction action = createTestMethodIntentionAction(USE_OUTSIDE_TESTDOX_TOOL_WINDOW);
        assertFalse("intention must not start in write action", action.startInWriteAction());
    }

    public void testIsEnabledIfActionEventOriginatedFromATestMethodInTheTestdoxToolWindow() {
        BaseTestMethodIntentionAction action = createTestMethodIntentionAction(USE_WITHIN_TESTDOX_TOOL_WINDOW);
        assertActionEnabledInTestDoxToolWindow(action, IS_A_TEST_METHOD);
    }

    public void testIsNotEnabledIfActionEventDidNotOriginateFromATestMethodInTheTestdoxToolWindow() {
        BaseTestMethodIntentionAction action = createTestMethodIntentionAction(USE_WITHIN_TESTDOX_TOOL_WINDOW);
        assertActionEnabledInTestDoxToolWindow(action, !IS_A_TEST_METHOD);
    }

    public void testIsEnabledIfActionEventOriginatedFromATestMethodOutsideOfTheToolWindow() {
        BaseTestMethodIntentionAction action = createTestMethodIntentionAction(USE_OUTSIDE_TESTDOX_TOOL_WINDOW);
        assertActionEnabledOutsideOfTheToolWindow(action, IS_A_TEST_METHOD);
    }

    public void testIsNotEnabledIfActionEventDidNotOriginateFromATestMethodOutsideOfTheToolWindow() {
        BaseTestMethodIntentionAction action = createTestMethodIntentionAction(USE_OUTSIDE_TESTDOX_TOOL_WINDOW);
        assertActionEnabledOutsideOfTheToolWindow(action, !IS_A_TEST_METHOD);
    }

    public void testIsAvailableIfIntentionOriginatedFromATestMethod() {
        BaseTestMethodIntentionAction action = createTestMethodIntentionAction(USE_OUTSIDE_TESTDOX_TOOL_WINDOW);
        assertActionAvailableIfEditorCaretIsWithinTestMethod(action, IS_A_TEST_METHOD);
    }

    public void testIsNotAvailableIfIntentionDidNotOriginateFromATestMethod() {
        BaseTestMethodIntentionAction action = createTestMethodIntentionAction(USE_OUTSIDE_TESTDOX_TOOL_WINDOW);
        assertActionAvailableIfEditorCaretIsWithinTestMethod(action, !IS_A_TEST_METHOD);
    }

    private void assertActionAvailableIfEditorCaretIsWithinTestMethod(BaseTestMethodIntentionAction action, boolean isTestMethod) {
        setExpectationsForRetrievingTestDoxProjectComponentAndPsiElement(projectMock);

        Mock mockEditorApi = mock(EditorApi.class);
        mockTestDoxController.expects(once()).method("getEditorApi").will(returnValue(mockEditorApi.proxy()));

        mockPsiElement.expects(once()).method("getParent");
        mockEditorApi.expects(once()).method("isTestMethod").will(returnValue(isTestMethod));

        assertEquals("action available? ", isTestMethod, action.isAvailable(projectMock, (Editor) mockEditor.proxy(), (PsiFile) mockPsiFile.proxy()));
    }

    void setExpectationsForRetrievingTestDoxProjectComponentAndPsiElement(Project project) {
        Mock mockTestDoxProjectComponent = Mocks.createAndRegisterTestDoxProjectComponentMock(this);
        TestDoxProjectComponent.setInstance(project, (TestDoxProjectComponent) mockTestDoxProjectComponent.proxy());
        mockTestDoxProjectComponent.expects(once()).method("getController").will(returnValue(mockTestDoxController.proxy()));

        Mock mockCaretModel = mock(CaretModel.class);
        mockEditor.expects(once()).method("getCaretModel").will(returnValue(mockCaretModel.proxy()));
        mockCaretModel.expects(once()).method("getOffset").will(returnValue(0));
        mockPsiFile.expects(once()).method("findElementAt").with(eq(0)).will(returnValue(mockPsiElement.proxy()));
    }

    void invokeTestMethodIntention(BaseTestMethodIntentionAction action) throws IncorrectOperationException {
        setExpectationsForRetrievingTestDoxProjectComponentAndPsiElement(projectMock);
        action.invoke(projectMock, (Editor) mockEditor.proxy(), (PsiFile) mockPsiFile.proxy());
    }

    private BaseTestMethodIntentionAction createTestMethodIntentionAction(boolean useFromToolWindow) {
        return new BaseTestMethodIntentionAction("short description", "description", null, useFromToolWindow) {
            public void execute(TestDoxController testDoxController, PsiElement targetPsiElement) { }

            public void executeUsingTestDoxToolWindow(TestDoxToolWindowUI testDoxToolWindow) { }
        };
    }
}
