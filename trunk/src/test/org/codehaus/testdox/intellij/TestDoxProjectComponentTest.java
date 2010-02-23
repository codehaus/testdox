package org.codehaus.testdox.intellij;

import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.psi.PsiTreeChangeListener;
import org.codehaus.testdox.intellij.actions.*;
import org.codehaus.testdox.intellij.config.ConfigurationController;
import org.codehaus.testdox.intellij.ui.ToolWindowUI;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.swing.*;

public class TestDoxProjectComponentTest extends MockObjectTestCase {

    private final Mock mockProject = mock(Project.class);
    private final Mock mockEditorApiFactory = mock(EditorApiFactory.class);
    private final Mock mockEditorApi = mock(EditorApi.class);

    private TestDoxProjectComponent projectComponent;

    protected void setUp() {
        ApplicationInfo applicationInfo = Stubs.createApplicationInfo(this);
        MockApplicationManager.getMockApplication().registerComponent(ApplicationInfo.class, applicationInfo);

        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        picoContainer.registerComponentInstance(EditorApi.class, mockEditorApi.proxy());

        projectComponent = new TestDoxProjectComponent((Project) mockProject.proxy(), picoContainer);
        projectComponent.editorApiFactory = (EditorApiFactory) mockEditorApiFactory.proxy();
    }

    protected void tearDown() {
        MockApplicationManager.getMockApplication().removeComponent(ApplicationInfo.class);
    }

    public void testDefinesItsComponentName() {
        assertEquals("project component name", "TestDoxProjectComponent", projectComponent.getComponentName());
    }

    public void testDoesNothingWhenInitialisedByIntellijIdea() {
        projectComponent.initComponent();
    }

    public void testDoesNothingWhenDisposedByIntellijIdea() {
        projectComponent.disposeComponent();
    }

    public void testRegistersTheToolbarAndAssociatedActionsAndInitialisesListenersIntentionsAndToolWindowWhenAProjectIsOpened() {
        String expectedProjectNameWithoutExtension = "projectName";
        int expectedNumberOfActionsIncludingActionGroup = 6;

        ConfigurationController configurationController = new ConfigurationController();
        mockProject.expects(once()).method("getComponent").with(eq(ConfigurationController.class)).will(returnValue(configurationController));
        mockEditorApiFactory.expects(once()).method("createEditorApi").will(returnValue(mockEditorApi.proxy()));

        mockProject.expects(exactly(expectedNumberOfActionsIncludingActionGroup)).method("getName").will(returnValue(expectedProjectNameWithoutExtension));

        MockApplicationManager.reset();
        Mock mockActionManager = mock(ActionManager.class);
        MockApplicationManager.getMockApplication().registerComponent(ActionManager.class, mockActionManager.proxy());

        Mock mockIntentionManager = mock(IntentionManager.class);
        MockApplicationManager.getMockApplication().registerComponent(IntentionManager.class, mockIntentionManager.proxy());

        String actionKeyForAutoScrollAction = expectedProjectNameWithoutExtension + '.' + AutoScrollAction.ID();
        String actionKeyForDeleteTestAction = expectedProjectNameWithoutExtension + '.' + DeleteTestAction.ID();
        String actionKeyForRefreshTestDoxAction = expectedProjectNameWithoutExtension + '.' + RefreshTestDoxPanelAction.ID();
        String actionKeyForRenameTestAction = expectedProjectNameWithoutExtension + '.' + RenameTestAction.ID();
        String actionKeyForSortTestDoxAction = expectedProjectNameWithoutExtension + '.' + SortTestDoxAction.ID();
        String actionKeyForToolbarActionGroup = expectedProjectNameWithoutExtension + '.' + TestDoxProjectComponent.TOOL_WINDOW_TOOLBAR_ID;

        mockActionManager.expects(once()).method("getAction").with(eq(actionKeyForAutoScrollAction));
        mockActionManager.expects(once()).method("getAction").with(eq(actionKeyForDeleteTestAction));
        mockActionManager.expects(once()).method("getAction").with(eq(actionKeyForRefreshTestDoxAction));
        mockActionManager.expects(once()).method("getAction").with(eq(actionKeyForRenameTestAction));
        mockActionManager.expects(once()).method("getAction").with(eq(actionKeyForSortTestDoxAction));
        mockActionManager.expects(once()).method("getAction").with(eq(actionKeyForToolbarActionGroup));

        mockActionManager.expects(once()).method("registerAction").with(eq(actionKeyForAutoScrollAction), isA(AutoScrollAction.class));
        mockActionManager.expects(once()).method("registerAction").with(eq(actionKeyForDeleteTestAction), isA(DeleteTestAction.class));
        mockActionManager.expects(once()).method("registerAction").with(eq(actionKeyForRefreshTestDoxAction), isA(RefreshTestDoxPanelAction.class));
        mockActionManager.expects(once()).method("registerAction").with(eq(actionKeyForRenameTestAction), isA(RenameTestAction.class));
        mockActionManager.expects(once()).method("registerAction").with(eq(actionKeyForSortTestDoxAction), isA(SortTestDoxAction.class));
        mockActionManager.expects(once()).method("registerAction").with(eq(actionKeyForToolbarActionGroup), isA(DefaultActionGroup.class));

        Mock mockActionToolbar = mock(ActionToolbar.class);
        mockActionManager.expects(once()).method("createActionToolbar")
            .with(eq(TestDoxProjectComponent.TOOL_WINDOW_TOOLBAR_ID), isA(DefaultActionGroup.class), eq(true))
            .will(returnValue(mockActionToolbar.proxy()));

        mockEditorApi.expects(once()).method("addFileEditorManagerListener").with(isA(TestDoxController.class));
        mockEditorApi.expects(once()).method("addRefactoringElementListenerProvider").with(isA(TestDoxController.class));
        mockEditorApi.expects(once()).method("addPsiTreeChangeListener").with(isA(PsiTreeChangeListener.class));
        mockEditorApi.expects(once()).method("addVirtualFileListener").with(isA(VirtualFileListener.class));

        mockIntentionManager.expects(once()).method("addAction").with(isA(RenameTestAction.class));
        mockIntentionManager.expects(once()).method("addAction").with(isA(DeleteTestAction.class));

        mockActionToolbar.expects(once()).method("getComponent").will(returnValue(new JToolBar()));

        Mock mockToolWindowManager = mock(ToolWindowManager.class);
        mockEditorApi.expects(once()).method("getToolWindowManager").will(returnValue(mockToolWindowManager.proxy()));

        Mock mockToolWindow = mock(ToolWindow.class);
        mockToolWindowManager.expects(once()).method("registerToolWindow")
            .with(eq(TestDoxProjectComponent.TOOL_WINDOW_ID), isA(ToolWindowUI.class), same(ToolWindowAnchor.RIGHT))
            .will(returnValue(mockToolWindow.proxy()));

        mockToolWindow.expects(once()).method("setType").with(same(ToolWindowType.DOCKED), NULL);
        mockToolWindow.expects(once()).method("setIcon").with(same(IconHelper.getIcon(IconHelper.TESTDOX_ICON)));

        projectComponent.projectOpened();
    }

    public void testRemovesAllListenersAndDetachesTheProjectComponentInstanceForTheCurrentProjectWhenTheProjectIsClosed() {
        Project projectMock = (Project) mockProject.proxy();

        Mock mockEditorApi = mock(EditorApi.class);
        projectComponent.controller = new TestDoxControllerImpl(projectMock, (EditorApi) mockEditorApi.proxy(), null, null, null, null, null);

        mockEditorApi.expects(once()).method("removeFileEditorManagerListener").with(same(projectComponent.controller));
        mockEditorApi.expects(once()).method("removeRefactoringElementListenerProvider").with(same(projectComponent.controller));
        mockEditorApi.expects(once()).method("removePsiTreeChangeListener").with(isA(PsiTreeChangeListener.class));
        mockEditorApi.expects(once()).method("removeVirtualFileListener").with(isA(VirtualFileListener.class));

        assertSame("testDoxProjectComponent should be attached", projectComponent, TestDoxProjectComponent.getInstance(projectMock));
        projectComponent.projectClosed();
        assertNull("testDoxProjectComponent should no longer be attached", TestDoxProjectComponent.getInstance(projectMock));
    }
}
