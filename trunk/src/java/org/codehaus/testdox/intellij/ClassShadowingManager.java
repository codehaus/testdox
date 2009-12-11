package org.codehaus.testdox.intellij;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.refactoring.listeners.RefactoringElementListener;

import org.codehaus.testdox.intellij.config.ConfigurationBean;

class ClassShadowingManager implements RefactoringElementListener {

    private final ConfigurationBean config;
    private final NameResolver nameResolver;
    private final EditorApi editorApi;
    private final TestDoxFile originalFile;

    public ClassShadowingManager(PsiClass psiElement, TestDoxFileFactory testDoxFileFactory, EditorApi editorApi,
                                 ConfigurationBean config, NameResolver nameResolver) {
        this.config = config;
        this.nameResolver = nameResolver;
        this.editorApi = editorApi;
        this.originalFile = testDoxFileFactory.getTestDoxFile(editorApi.getVirtualFile(psiElement));
    }

    public void elementMoved(PsiElement psiElement) {
        if (isValidShadowing(psiElement)) {
            PsiDirectory newPackage = psiElement.getContainingFile().getContainingDirectory();
            editorApi.move((PsiClass) originalFile.getTestClass().psiElement(), newPackage);
        }
    }

    public void elementRenamed(PsiElement psiElement) {
        if (isValidShadowing(psiElement)) {
            String newTestClassName = nameResolver.getTestClassName(((PsiClass) psiElement).getName());
            editorApi.rename(originalFile.getTestClass().psiElement(), newTestClassName);
        }
    }

    private boolean isValidShadowing(PsiElement psiElement) {
        return ((isValidClass(psiElement))
                           && (config.isAutoApplyChangesToTest())
                           && (nameResolver.isRealClass(((PsiClass) psiElement).getName()))
                           && (originalFile.canNavigateToTestClass()));
    }

    private boolean isValidClass(PsiElement psiElement) {
        return (psiElement instanceof PsiClass) && (!isInnerClass((PsiClass) psiElement));
    }

    private boolean isInnerClass(PsiClass psiClass) {
        return (psiClass.getContainingFile().getName().indexOf(psiClass.getName()) < 0);
    }
}
