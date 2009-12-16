package org.codehaus.testdox.intellij;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowType;
import static jedi.functional.Coercions.list;
import org.codehaus.testdox.intellij.actions.*;
import org.codehaus.testdox.intellij.config.ConfigurationBean;
import org.codehaus.testdox.intellij.config.ConfigurationController;
import org.codehaus.testdox.intellij.panel.TestDoxModel;
import org.codehaus.testdox.intellij.panel.TestDoxToolWindowPanel;
import org.codehaus.testdox.intellij.panel.TestDoxToolWindowUI;
import org.jetbrains.annotations.NotNull;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDoxProjectComponent implements ProjectComponent {

    static final String TOOL_WINDOW_TOOLBAR_ID = "TestDoxToolbar";
    static final String TOOL_WINDOW_ID = "TestDox";

    private static final Map<Project, TestDoxProjectComponent> INSTANCES = new HashMap();

    private final Project project;
    private final MutablePicoContainer picoContainer;

    protected EditorApiFactory editorApiFactory;
    protected ToolWindow toolWindow;
    protected TestDoxToolWindowPanel testDoxToolWindowPanel;
    protected TestDoxModel model;
    protected EditorApi editorApi;
    protected TestDoxControllerImpl controller;

    private ActionToolbar toolbar;

    public TestDoxProjectComponent(Project project) {
        this(project, new DefaultPicoContainer());
    }

    public TestDoxProjectComponent(Project project, MutablePicoContainer picoContainer) {
        this.project = project;
        this.picoContainer = picoContainer;

        if (project != null) {
            picoContainer.registerComponentInstance(Project.class, project);
        }

        editorApiFactory = new IntelliJApiFactory(picoContainer);
        TestDoxProjectComponent.setInstance(project, this);
    }

    public static void setInstance(Project project, TestDoxProjectComponent instance) {
        INSTANCES.put(project, instance);
    }

    public static TestDoxProjectComponent getInstance(Project project) {
        return INSTANCES.get(project);
    }

    // ProjectComponent ------------------------------------------------------------------------------------------------

    public void projectOpened() {
        ConfigurationController configurationController = project.getComponent(ConfigurationController.class);
        ConfigurationBean configuration = configurationController.getState();
        picoContainer.registerComponentInstance(ConfigurationBean.class, configuration);

        // move container setup somewhere else...
        picoContainer.registerComponentImplementation(TestDoxModel.class);
        picoContainer.registerComponentImplementation(NameResolver.class, TemplateNameResolver.class);
        picoContainer.registerComponentImplementation(SentenceManager.class);
        picoContainer.registerComponentImplementation(TestLookup.class);
        picoContainer.registerComponentImplementation(TestDoxFileFactory.class);
        picoContainer.registerComponentImplementation(TestDoxController.class, TestDoxControllerImpl.class);

        model = (TestDoxModel) picoContainer.getComponentInstance(TestDoxModel.class);
        model.setNotJava();

        editorApi = editorApiFactory.createEditorApi();

        controller = (TestDoxControllerImpl) picoContainer.getComponentInstance(TestDoxController.class);
        controller.initListeners();
        controller.initIntentions();

        toolbar = createToolBar(configuration);
        initToolWindow();
        controller.toolWindow = toolWindow;
    }

    public void projectClosed() {
        controller.removeListeners();
        INSTANCES.remove(project);
    }

    @NotNull
    public String getComponentName() {
        return "TestDoxProjectComponent";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    private void initToolWindow() {
        testDoxToolWindowPanel = new TestDoxToolWindowPanel(getController(), toolbar.getComponent());
        model.addTableModelListener(testDoxToolWindowPanel);

        toolWindow = editorApi.getToolWindowManager().registerToolWindow(TOOL_WINDOW_ID, testDoxToolWindowPanel, ToolWindowAnchor.RIGHT);
        toolWindow.setType(ToolWindowType.DOCKED, null);
        toolWindow.setIcon(IconHelper.getIcon(IconHelper.TESTDOX_ICON));
    }

    private ActionToolbar createToolBar(ConfigurationBean config) {
        ActionManager actionManager = ActionManager.getInstance();
        boolean useFromTestDoxToolWindow = true;

        DefaultActionGroup rootActionGroup = new DefaultActionGroup(TOOL_WINDOW_TOOLBAR_ID, false);
        ActionGroup toolGroup = (ActionGroup) registerAction(actionManager, rootActionGroup, TOOL_WINDOW_TOOLBAR_ID);

        addToolBarActions((DefaultActionGroup) toolGroup, list(
            registerAction(actionManager, new SortTestDoxAction(config.isAlphabeticalSorting(), useFromTestDoxToolWindow), SortTestDoxAction.ID()),
            registerAction(actionManager, new AutoscrollAction(config.isAutoscrolling(), useFromTestDoxToolWindow), AutoscrollAction.ID()),
            Separator.getInstance(),
            registerAction(actionManager, new RenameTestAction(useFromTestDoxToolWindow), RenameTestAction.ID()),
            registerAction(actionManager, new DeleteTestAction(useFromTestDoxToolWindow), DeleteTestAction.ID()),
            Separator.getInstance(),
            registerAction(actionManager, new RefreshTestDoxPanelAction(useFromTestDoxToolWindow), RefreshTestDoxPanelAction.ID)

        ));

        return actionManager.createActionToolbar(TOOL_WINDOW_TOOLBAR_ID, toolGroup, true);
    }

    private AnAction registerAction(ActionManager actionManager, AnAction action, String id) {
        id = project.getName() + "." + id;

        if (actionManager.getAction(id) == null) {
            actionManager.registerAction(id, action);
            return action;
        }

        return actionManager.getAction(id);
    }

    private void addToolBarActions(DefaultActionGroup toolGroup, List<AnAction> actions) {
        toolGroup.removeAll();

        for (AnAction action : actions) {
            toolGroup.add(action);
        }
    }

    public TestDoxController getController() {
        return controller;
    }

    public TestDoxToolWindowUI getTestDoxToolWindowUI() {
        return testDoxToolWindowPanel;
    }
}
