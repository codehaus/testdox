package org.codehaus.testdox.intellij;

import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import org.codehaus.testdox.intellij.actions.PresentationUpdater;
import org.codehaus.testdox.intellij.config.ConfigurationBean;
import org.codehaus.testdox.intellij.ui.TestDoxTableModel;

public interface TestDoxController extends FileEditorManagerListener,
                                           RefactoringElementListenerProvider,
                                           PresentationUpdater {
    EditorApi getEditorApi();

    TestDoxFileFactory getTestDoxFileFactory();

    TestDoxTableModel getModel();

    ConfigurationBean getConfiguration();

    void setConfiguration(ConfigurationBean configuration);

    void selectedFileChanged(FileEditorManagerEvent event);

    void selectedFileChanged(VirtualFile file);

    boolean hasActiveEditors();

    boolean canCurrentFileBeUnitTested();

    TestDoxFile getCurrentTestDoxFile();

    void addTest();

    void delete(PsiElement element);

    void startRename(PsiElement element);

    void startRename(TestMethod testMethod);

    void toggleQuickDox();

    void closeQuickDox();

    void toggleTestClassAndTestedClass();

    void jumpToTestElement(TestElement selectedTestElement, boolean autoscrolling);

    void updateSort(boolean alphabetical);

    void refreshToolWindow();

    void toggleToolWindow();

    void updateAutoscroll(boolean autoscrolling);

    void updatePresentation(Presentation presentation, PsiElement targetPsiElement);
}
