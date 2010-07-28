package org.codehaus.testdox.intellij;

import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.psi.PsiElement;

public abstract class AbstractTestElement implements TestElement {

    public PsiElement getPsiElement() {
        return NullPsiElement.INSTANCE;
    }

    public boolean jumpToPsiElement() {
        return false;
    }

    public void updatePresentation(Presentation presentation) {
        presentation.setEnabled(false);
    }

    public void rename(TestDoxController testDoxController) { }

    public void delete(TestDoxController testDoxController) { }

    public int compareTo(Object object) {
        return (equals(object) ? 0 : -1);
    }

    public String toString() {
        return getDisplayString();
    }
}
