package org.codehaus.testdox.intellij;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import com.intellij.util.IncorrectOperationException;

public interface EditorApi {

    ToolWindowManager getToolWindowManager();

    void activateSelectedTextEditor();

    Editor getSelectedTextEditor();

    VirtualFile getCurrentFile();

    VirtualFile getVirtualFile(PsiClass aClass);

    PsiDirectory getPsiDirectory(VirtualFile file);

    PsiJavaFile getPsiJavaFile(VirtualFile file);

    PsiClass getPsiClass(String className);

    PsiMethod[] getMethods(PsiClass aClass);

    boolean isJavaFile(VirtualFile file);

    boolean isInterface(String className);

    boolean jumpToPsiElement(PsiElement psiElement);

    boolean jumpToPsiClass(PsiClass psiClass);

    void createTestClass(TestDoxFile testDoxFile);

    void addMethod(PsiClass psiClass, String methodSignatureAndBody);

    void move(PsiClass psiClass, PsiDirectory destinationPackage);

    void rename(PsiElement element);

    void rename(PsiElement element, String newName);

    void delete(PsiElement psiElement);

    void deleteAsynchronously(PsiElement psiElement);

    void deleteAsynchronously(PsiDirectory[] directories, String question, String title, Runnable callback);

    TestMethod getCurrentTestMethod(PsiElement element, SentenceManager sentenceManager, VirtualFile currentFile);

    boolean isTestMethod(PsiElement element);

    void addFileEditorManagerListener(FileEditorManagerListener listener);

    void removeFileEditorManagerListener(FileEditorManagerListener listener);

    void addRefactoringElementListenerProvider(RefactoringElementListenerProvider listener);

    void removeRefactoringElementListenerProvider(RefactoringElementListenerProvider listener);

    void addPsiTreeChangeListener(PsiTreeChangeListener listener);

    void removePsiTreeChangeListener(PsiTreeChangeListener listener);

    void addVirtualFileListener(VirtualFileListener listener);

    void removeVirtualFileListener(VirtualFileListener listener);

    void commitAllDocuments();

    void decorateClassWithTestTemplate(PsiClass aClass, PsiManager psiManager) throws IncorrectOperationException;

    PsiDirectory createOrMoveToCorrectDirectory(PsiDirectory startDirectory, String targetPackage) throws IncorrectOperationException;

    PsiMethod findNearestMethodAnchorInSelectedFile();
}
