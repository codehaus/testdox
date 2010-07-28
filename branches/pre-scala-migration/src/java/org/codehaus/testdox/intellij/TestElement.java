package org.codehaus.testdox.intellij;

import javax.swing.Icon;

import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.psi.PsiElement;

public interface TestElement extends Comparable {

    PsiElement getPsiElement();

    boolean jumpToPsiElement();

    String getDisplayString();

    Icon getIcon();

    void updatePresentation(Presentation presentation);

    void rename(TestDoxController testDoxController);

    void delete(TestDoxController testDoxController);
}
