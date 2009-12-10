package org.codehaus.testdox.intellij;

import javax.swing.Icon;

import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;

public class TestClass extends AbstractTestElement {

    private final String className;
    private final PsiClass psiClass;
    private final EditorApi editorApi;
    private final NameResolver nameResolver;

    public TestClass(String className, PsiClass psiClass, EditorApi editorApi, NameResolver resolver) {
        this.className = className;
        this.psiClass = psiClass;
        this.editorApi = editorApi;
        this.nameResolver = resolver;
    }

    public String displayString() {
        return new StringBuffer("<b>").append(nameResolver.getRealClassNameForDisplay(className)).append(":</b>").toString();
    }

    public PsiElement getPsiElement() {
        return psiClass;
    }

    public boolean jumpToPsiElement() {
        return editorApi.jumpToPsiClass(psiClass);
    }

    public Icon icon() {
        return (psiClass != null) ? IconHelper.getIcon(IconHelper.CLASS_ICON)
                                  : IconHelper.getLockedIcon(IconHelper.CLASS_ICON);
    }

    public void updatePresentation(Presentation presentation) {
        presentation.setEnabled(true);
    }

    public void rename(TestDoxController testDoxController) {
        editorApi.rename(getTestedClass());
    }

    public void delete(TestDoxController testDoxController) {
        editorApi.delete(getTestedClass());
    }

    public boolean isTestClass() {
        return psiClass != null && nameResolver.isTestClass(psiClass.getName());
    }

    public int compareTo(TestElement object) {
        if (object instanceof TestClass) {
            return displayString().compareTo(object.displayString());
        }
        return 1;
    }

    public boolean equals(Object object) {
        return ((object instanceof TestClass) && (compareTo((TestElement) object) == 0));
    }

    private PsiClass getTestedClass() {
        return editorApi.getPsiClass(nameResolver.getRealClassName(psiClass.getQualifiedName()));
    }
}
