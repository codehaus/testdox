package org.codehaus.testdox.intellij.actions;

import java.awt.Container;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import static jedi.functional.FunctionalPrimitives.asList;
import static jedi.functional.FunctionalPrimitives.list;

import org.codehaus.testdox.intellij.EditorApi;
import org.codehaus.testdox.intellij.Mocks;
import org.codehaus.testdox.intellij.NullPsiElement;
import org.codehaus.testdox.intellij.Stubs;
import org.codehaus.testdox.intellij.TestDoxActionPresentationUpdater;
import org.codehaus.testdox.intellij.TestDoxController;
import org.codehaus.testdox.intellij.TestDoxProjectComponent;
import org.codehaus.testdox.intellij.panel.TestDoxToolWindowUI;

public class ActionEventsTest extends MockObjectTestCase {

    private static final KeyEvent NULL_KEY_EVENT = new KeyEvent(new Container(), 0, 0, 0, 0, ' ');

    private final Mock mockDataContext = mock(DataContext.class);
    private final Mock mockTestDoxController = mock(TestDoxController.class);
    private final Mock mockEditor = mock(Editor.class);
    private final ActionEvents actionEvents = new ActionEvents();

    private Mock mockTestDoxProjectComponent;
    private AnActionEvent anActionEvent;

    protected void setUp() {
        ApplicationInfo applicationInfo = Stubs.createApplicationInfo(this);
        MockApplicationManager.getMockApplication().registerComponent(ApplicationInfo.class, applicationInfo);
        mockTestDoxProjectComponent = Mocks.createAndRegisterTestDoxProjectComponentMock(this);

        AnAction action = new AnAction() {
            public void actionPerformed(AnActionEvent event) { }
        };
        anActionEvent = createAnActionEvent(action, (DataContext) mockDataContext.proxy());
    }

    protected void tearDown() {
        MockApplicationManager.getMockApplication().removeComponent(ApplicationInfo.class);
    }

    public void testReturnsANullTestdoxToolWindowWhenAProjectIsNotAvailable() {
        setExpectationsForProjectNotBeingAvailable();
        assertSame(ActionEvents.Nulls.TESTDOX_TOOL_WINDOW, actionEvents.getTestDoxToolWindowUI(anActionEvent));
    }

    public void testReturnsANullTestdoxControllerWhenAProjectIsNotAvailable() {
        setExpectationsForProjectNotBeingAvailable();
        assertSame(ActionEvents.Nulls.TESTDOX_CONTROLLER, actionEvents.getTestDoxController(anActionEvent));
    }

    public void testReturnsANonNullTestdoxToolWindowWhenAProjectIsNotAvailable() {
        setExpectationsForProjectBeingAvailable();

        TestDoxActionPresentationUpdater testDoxToolWindowMock = (TestDoxActionPresentationUpdater) mock(TestDoxToolWindowUI.class).proxy();
        mockTestDoxProjectComponent.expects(once()).method("getTestDoxToolWindowUI").will(returnValue(testDoxToolWindowMock));

        assertSame(testDoxToolWindowMock, actionEvents.getTestDoxToolWindowUI(anActionEvent));
    }

    public void testReturnsANonNullTestdoxControllerWhenAProjectIsNotAvailable() {
        setExpectationsForProjectBeingAvailable();

        TestDoxController testDoxControllerMock = (TestDoxController) mock(TestDoxController.class).proxy();
        mockTestDoxProjectComponent.expects(once()).method("getController").will(returnValue(testDoxControllerMock));

        assertSame(testDoxControllerMock, actionEvents.getTestDoxController(anActionEvent));
    }

    public void testDeterminesWhetherTheFileAGivenActionEventOriginatedFromIsAProjectJavaFile() {
        setExpectationsForProjectBeingAvailable();

        Mock mockVirtualFile = Mocks.createAndRegisterVirtualFileMock(this);
        Mock mockEditorApi = mock(EditorApi.class);

        mockDataContext.expects(once()).method("getData").with(eq(DataConstants.VIRTUAL_FILE)).will(returnValue(mockVirtualFile.proxy()));
        mockTestDoxProjectComponent.expects(once()).method("getController").will(returnValue(mockTestDoxController.proxy()));
        mockTestDoxController.expects(once()).method("getEditorApi").will(returnValue(mockEditorApi.proxy()));
        mockEditorApi.expects(once()).method("isJavaFile").with(isA(VirtualFile.class)).will(returnValue(true));

        assertTrue("action event originated from java file", actionEvents.isJavaFile(anActionEvent));
    }

    public void testReturnsTheCurrentPsiElementWhenActionEventOriginatedFromAProjectClassWithinACodeEditor() {
        MockApplicationManager.reset();
        Mock mockPsiFile = mock(PsiFile.class);
        Mock mockCaretModel = mock(CaretModel.class);

        setExpectationsForProjectBeingAvailable();

        PsiElement psiElementMock = (PsiElement) mock(PsiElement.class).proxy();
        mockDataContext.expects(once()).method("getData").with(eq(DataConstants.EDITOR)).will(returnValue(mockEditor.proxy()));
        mockDataContext.expects(once()).method("getData").with(eq("psi.File")).will(returnValue(mockPsiFile.proxy()));
        mockEditor.expects(once()).method("getCaretModel").will(returnValue(mockCaretModel.proxy()));
        mockCaretModel.expects(once()).method("getOffset").will(returnValue(0));
        mockPsiFile.expects(once()).method("findElementAt").with(eq(0)).will(returnValue(psiElementMock));

        PsiElement actualPsiElement = actionEvents.getTargetPsiElement(anActionEvent);
        assertEquals(psiElementMock, actualPsiElement);
    }

    private void setExpectationsForProjectBeingAvailable() {
        Project projectMock = (Project) mock(Project.class).proxy();
        TestDoxProjectComponent testDoxProjectComponentMock = (TestDoxProjectComponent) mockTestDoxProjectComponent.proxy();

        setExpectationsForProjectRetrieval(projectMock, testDoxProjectComponentMock);
    }

    private void setExpectationsForProjectNotBeingAvailable() {
        setExpectationsForProjectRetrieval(null, null);
    }

    private void setExpectationsForProjectRetrieval(Project project, TestDoxProjectComponent testDoxProjectComponent) {
        TestDoxProjectComponent.setInstance(project, testDoxProjectComponent);
        mockDataContext.stubs().method("getData").with(eq(DataConstants.PROJECT)).will(returnValue(project));
    }

    public void testReturnsANullPsiElementWhenActionEventDidNotOriginateFromACodeEditor() {
        mockDataContext.expects(once()).method("getData").with(eq(DataConstants.EDITOR));
        assertSame(NullPsiElement.INSTANCE, actionEvents.getTargetPsiElement(anActionEvent));
    }

    public void testReturnsANullPsiElementWhenActionEventDidNotOriginateFromAFile() {
        mockDataContext.expects(once()).method("getData").with(eq(DataConstants.EDITOR)).will(returnValue(mockEditor.proxy()));
        mockDataContext.expects(once()).method("getData").with(eq("psi.File"));
        assertSame(NullPsiElement.INSTANCE, actionEvents.getTargetPsiElement(anActionEvent));
    }

    static AnActionEvent createAnActionEvent(AnAction action, DataContext dataContext) {
        List<Class> parameterTypes = asList(new Class[] { InputEvent.class, DataContext.class, String.class, Presentation.class, int.class });
        List<?> parameterValues = list(NULL_KEY_EVENT, dataContext, "", action.getTemplatePresentation(), -1);

        int index = parameterTypes.indexOf(Presentation.class) + 1;
        parameterTypes.add(index, ActionManager.class);
        parameterValues.add(index, null);

        try {
            Constructor constructor = AnActionEvent.class.getConstructor((Class[]) parameterTypes.toArray(new Class[0]));
            return (AnActionEvent) constructor.newInstance(parameterValues.toArray());
        } catch (NoSuchMethodException unexpected) {
            throw new RuntimeException(unexpected);
        } catch (InstantiationException unexpected) {
            throw new RuntimeException(unexpected);
        } catch (IllegalAccessException unexpected) {
            throw new RuntimeException(unexpected);
        } catch (InvocationTargetException unexpected) {
            throw new RuntimeException(unexpected);
        }
    }
}
