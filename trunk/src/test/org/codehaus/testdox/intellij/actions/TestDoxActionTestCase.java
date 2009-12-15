package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.psi.PsiElement;
import org.codehaus.testdox.intellij.Stubs;
import org.codehaus.testdox.intellij.TestDoxController;
import org.codehaus.testdox.intellij.TestDoxControllerImpl;
import org.codehaus.testdox.intellij.panel.TestDoxToolWindowUI;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public abstract class TestDoxActionTestCase extends MockObjectTestCase {

    protected final Mock mockActionEvents = mock(ActionEvents.class);
    protected final Mock mockTestDoxController = mock(TestDoxController.class);
    protected final Mock mockTestDoxToolWindowUI = mock(TestDoxToolWindowUI.class);
    protected final Mock mockDataContext = mock(DataContext.class);

    protected void setUp() {
        ApplicationInfo applicationInfo = Stubs.createApplicationInfo(this);
        MockApplicationManager.getMockApplication().registerComponent(ApplicationInfo.class, applicationInfo);

        ActionEvents.instance = (ActionEvents) mockActionEvents.proxy();
    }

    protected void tearDown() {
        MockApplicationManager.getMockApplication().removeComponent(ApplicationInfo.class);
    }

    protected void executeAction(AnAction action) {
        action.actionPerformed(createAnActionEvent(action));
    }

    protected void checkActionIsEnabled(AnAction action, boolean enabled) {
        checkActionIsEnabled(action, true, enabled);
    }

    protected void checkActionIsEnabled(AnAction action, boolean expectTestDoxControllerUpdatePresentation, boolean enabled) {
        useMockTestDoxController();

        AnActionEvent actionEvent = createAnActionEvent(action);
        Presentation presentation = actionEvent.getPresentation();

        if (expectTestDoxControllerUpdatePresentation) {
            mockTestDoxController.expects(once()).method("update").with(same(presentation));
        }
        action.update(actionEvent);
    }

    protected void useMockTestDoxController() {
        mockActionEvents.expects(once()).method("getTestDoxController").with(isA(AnActionEvent.class))
                .will(returnValue(mockTestDoxController.proxy()));
    }

    protected void useMockTestDoxToolWindowUI() {
        mockActionEvents.expects(once()).method("getTestDoxToolWindowUI").with(isA(AnActionEvent.class))
                .will(returnValue(mockTestDoxToolWindowUI.proxy()));
    }

    protected void assertActionEnabledInTestDoxToolWindowIfEventOriginatedFromClassInProject(AnAction action, boolean isJavaFile) {
        assertActionEnabledInTestDoxToolWindow(action, isJavaFile);
    }

    protected void assertActionEnabledInTestDoxToolWindow(AnAction action, final boolean enabled) {
        TestDoxToolWindowUI testDoxToolWindow = new TestDoxToolWindowUI() {
            public void update(Presentation presentation) {
                presentation.setEnabled(enabled);
            }

            public void renameSelectedTestElement() {}

            public void deleteSelectedTestElement() {}
        };

        mockActionEvents.expects(once()).method("getTestDoxToolWindowUI").with(isA(AnActionEvent.class))
                .will(returnValue(testDoxToolWindow));

        assertActionEnabledInsideOrOutsideOfTheToolWindow(action, enabled);
    }

    protected void assertActionEnabledOutsideOfTheToolWindow(AnAction action, final boolean enabled) {
        TestDoxController controller = new TestDoxControllerImpl(null, null, null, null, null, null, null) {
            public void updatePresentation(Presentation presentation, PsiElement targetPsiElement) {
                presentation.setEnabled(enabled);
            }
        };
        mockActionEvents.expects(once()).method("getTestDoxController").with(isA(AnActionEvent.class)).will(returnValue(controller));

        PsiElement targetPsiElement = (PsiElement) (enabled ? mock(PsiElement.class).proxy() : null);
        mockActionEvents.expects(once()).method("getTargetPsiElement").with(isA(AnActionEvent.class)).will(returnValue(targetPsiElement));

        assertActionEnabledInsideOrOutsideOfTheToolWindow(action, enabled);
    }

    private void assertActionEnabledInsideOrOutsideOfTheToolWindow(AnAction action, boolean enabled) {
        action.getTemplatePresentation().setEnabled(!enabled);

        AnActionEvent actionEvent = createAnActionEvent(action);
        action.update(actionEvent);
        assertEquals("action enabled? ", enabled, actionEvent.getPresentation().isEnabled());
    }

    protected final AnActionEvent createAnActionEvent(AnAction action) {
        return ActionEventsTest.createAnActionEvent(action, (DataContext) mockDataContext.proxy());
    }
}
