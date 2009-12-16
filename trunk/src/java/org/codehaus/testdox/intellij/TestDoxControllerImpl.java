package org.codehaus.testdox.intellij;

import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import org.codehaus.testdox.intellij.actions.DeleteTestAction;
import org.codehaus.testdox.intellij.actions.RenameTestAction;
import org.codehaus.testdox.intellij.config.ConfigurationBean;
import org.codehaus.testdox.intellij.panel.*;
import org.codehaus.testdox.intellij.ui.RenameUI;

public class TestDoxControllerImpl implements TestDoxController {

    private final Project project;
    private final EditorApi editorApi;
    private final TestDoxModel model;
    private final NameResolver nameResolver;
    private final SentenceManager sentenceManager;
    private final TestDoxFileFactory testDoxFileFactory;
    private final VirtualFileListener deletionShadowingManager;

    ToolWindow toolWindow;

    protected PsiTreeChangeListener psiTreeChangeListener;

    private VirtualFile currentFile;
    private ConfigurationBean configuration;
    private QuickDoxDialog dialog;

    public TestDoxControllerImpl(Project project, EditorApi editorApi, TestDoxModel model,
                                 ConfigurationBean configuration, NameResolver nameResolver,
                                 SentenceManager sentenceManager, TestDoxFileFactory testDoxFileFactory) {
        this.project = project;
        this.editorApi = editorApi;
        this.model = model;
        this.configuration = configuration;
        this.nameResolver = nameResolver;
        this.sentenceManager = sentenceManager;
        this.testDoxFileFactory = testDoxFileFactory;
        this.deletionShadowingManager = new DeletionShadowingManager(editorApi, configuration, nameResolver);

        psiTreeChangeListener = new PsiTreeChangeAdapter() {
            public void childrenChanged(PsiTreeChangeEvent event) {
                getCurrentTestDoxFile().updateModel(TestDoxControllerImpl.this.model);
            }
        };
    }

    void initIntentions() {
        IntentionManager intentionManager = IntentionManager.getInstance();
        intentionManager.addAction(new RenameTestAction());
        intentionManager.addAction(new DeleteTestAction());
    }

    void initListeners() {
        editorApi.addFileEditorManagerListener(this);
        editorApi.addRefactoringElementListenerProvider(this);
        editorApi.addPsiTreeChangeListener(psiTreeChangeListener);
        editorApi.addVirtualFileListener(deletionShadowingManager);
    }

    void removeListeners() {
        editorApi.removeFileEditorManagerListener(this);
        editorApi.removeRefactoringElementListenerProvider(this);
        editorApi.removePsiTreeChangeListener(psiTreeChangeListener);
        editorApi.removeVirtualFileListener(deletionShadowingManager);
    }

    public EditorApi getEditorApi() {
        return editorApi;
    }

    public TestDoxFileFactory getTestDoxFileFactory() {
        return testDoxFileFactory;
    }

    public TestDoxModel getModel() {
        return model;
    }

    public ConfigurationBean getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationBean configuration) {
        this.configuration = configuration;
    }

    public void selectedFileChanged(FileEditorManagerEvent event) {
        selectedFileChanged(event.getNewFile());
    }

    public void selectedFileChanged(VirtualFile file) {
        if (file == null) {
            hideQuickDoxForCurrentFile();
            toolWindow.setTitle("");
            model.setNotJava();
        } else {
            toolWindow.setTitle(file.getNameWithoutExtension());
            testDoxFileFactory.getTestDoxFile(file).updateModel(model);
        }
        currentFile = file;
    }

    public boolean hasActiveEditors() {
        return editorApi.getCurrentFile() != null;
    }

    public TestDoxFile getCurrentTestDoxFile() {
        return testDoxFileFactory.getTestDoxFile(editorApi.getCurrentFile());
    }

    public void addTest() {
        RenameUI addTestDialog = createAddTestDialog();
        addTestDialog.show();
        if (addTestDialog.isOK()) {
            StringBuffer methodSignatureAndBody = new StringBuffer();
            if (configuration.isUsingAnnotations()) {
                methodSignatureAndBody.append(configuration.getTestMethodAnnotation()).append("\n");
            }
            methodSignatureAndBody.append("public void ").append(sentenceManager.buildMethodName(addTestDialog.sentence())).append("() {\n}");
            editorApi.addMethod((PsiClass) getCurrentTestDoxFile().testClass().psiElement(), methodSignatureAndBody.toString());
        }
    }

    protected RenameUI createAddTestDialog() {
        return new AddTestDialog(project);
    }

    public void delete(PsiElement element) {
        TestMethod testMethod = getCurrentTestMethod(element);
        if (testMethod != null) {
            editorApi.delete(testMethod.psiElement());
        }
    }

    public void startRename(PsiElement element) {
        startRename(getCurrentTestMethod(element));
    }

    public void startRename(TestMethod testMethod) {
        if (testMethod != null) {
            RenameUI renameDialog = createRenameDialog(testMethod.displayString());
            renameDialog.show();
            if (renameDialog.isOK()) {
                editorApi.rename(testMethod.psiElement(), sentenceManager.buildMethodName(renameDialog.sentence()));
            }
        }
    }

    protected RenameUI createRenameDialog(String testMethodName) {
        return new RenameDialog(project, testMethodName);
    }

    public boolean canCurrentFileBeUnitTested() {
        return getCurrentTestDoxFile().canBeUnitTested();
    }

    public void toggleQuickDox() {
        if (!hideQuickDoxForCurrentFile()) {
            if (dialog == null) {
                dialog = new QuickDoxDialog(WindowManager.getInstance().suggestParentWindow(project), editorApi, model, configuration);
            }
            dialog.show();
        }
    }

    public void closeQuickDox() {
        hideQuickDoxForCurrentFile();
    }

    private boolean hideQuickDoxForCurrentFile() {
        if ((dialog != null) && (dialog.isVisible())) {
            dialog.hide();
            return true;
        }
        return false;
    }

    public void toggleTestClassAndTestedClass() {
        TestDoxFile testDoxFile = getCurrentTestDoxFile();
        if (testDoxFile.isTestedClass()) {
            if (testDoxFile.canNavigateToTestClass()) {
                jumpToTestElement(testDoxFile.testClass(), false);
            } else
            if (testDoxFile.canBeUnitTested() && configuration.isCreateTestIfMissing() && shouldCreateTestClass()) {
                editorApi.createTestClass(testDoxFile);
            }
        } else if (testDoxFile.canNavigateToTestedClass()) {
            jumpToTestElement(testDoxFile.testedClass(), false);
        }
    }

    protected boolean shouldCreateTestClass() {
        String question = "Would you like to create a test for this class?";
        return Messages.showYesNoDialog(project, question, "Test not found", Messages.getQuestionIcon()) == DialogWrapper.OK_EXIT_CODE;
    }

    public void jumpToTestElement(TestElement selectedTestElement, boolean autoscrolling) {
        selectedTestElement.jumpToPsiElement();

        if (!autoscrolling && (toolWindow.getType().equals(ToolWindowType.SLIDING) || toolWindow.isAutoHide())) {
            toolWindow.hide(null);
        }
    }

    public void updateSort(boolean alphabetical) {
        if (alphabetical) {
            model.sortInAlphabeticalOrder();
        } else {
            model.sortInDefinitionOrder();
        }
    }

    public void refreshToolWindow() {
        selectedFileChanged(currentFile);
    }

    public void toggleToolWindow() {
        if (toolWindow.isVisible()) {
            toolWindow.hide(null);
        } else {
            toolWindow.activate(null);
            refreshToolWindow();
        }
    }

    public void updateAutoscroll(boolean autoscrolling) {
        configuration.setAutoscrolling(autoscrolling);
    }

    public void update(Presentation presentation) {
        TestDoxFile testDoxFile = getCurrentTestDoxFile();
        presentation.setEnabled(testDoxFile.canNavigateToTestedClass() || testDoxFile.canNavigateToTestClass());
    }

    public void updatePresentation(Presentation presentation, PsiElement targetPsiElement) {
        presentation.setEnabled(getCurrentTestMethod(targetPsiElement) != null);
    }

    private TestMethod getCurrentTestMethod(PsiElement element) {
        return editorApi.getCurrentTestMethod(element, sentenceManager, getCurrentTestDoxFile().file());
    }

    // FileEditorManagerListener ---------------------------------------------------------------------------------------

    public void fileOpened(FileEditorManager fileEditorManager, VirtualFile file) {
    }

    public void fileClosed(FileEditorManager fileEditorManager, VirtualFile file) {
    }

    public void selectionChanged(FileEditorManagerEvent event) {
        selectedFileChanged(event.getNewFile());
    }

    // RefactoringElementListenerProvider ------------------------------------------------------------------------------

    public RefactoringElementListener getListener(PsiElement psiElement) {
        if (psiElement instanceof PsiClass) {
            return new ClassShadowingManager((PsiClass) psiElement, testDoxFileFactory, editorApi, configuration, nameResolver);
        }
        return null;
    }
}
