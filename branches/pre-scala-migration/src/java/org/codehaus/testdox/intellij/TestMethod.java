package org.codehaus.testdox.intellij;

import javax.swing.Icon;

import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;

public class TestMethod extends AbstractTestElement {

    public static final TestMethod[] EMPTY_ARRAY = new TestMethod[0];

    private final PsiMethod method;
    private final EditorApi editorApi;
    private final SentenceManager sentenceManager;

    public TestMethod(PsiMethod method, EditorApi editorApi, SentenceManager sentenceManager) {
        this.method = method;
        this.editorApi = editorApi;
        this.sentenceManager = sentenceManager;
    }

    public String getMethodName() {
        return method.getName();
    }

    public PsiElement getPsiElement() {
        return method;
    }

    public boolean jumpToPsiElement() {
        return editorApi.jumpToPsiElement(getPsiElement());
    }

    public String getDisplayString() {
        return sentenceManager.buildSentence(getMethodName());
    }

    public Icon getIcon() {
        return IconHelper.getIcon(IconHelper.DOX_ICON);
    }

    public void updatePresentation(Presentation presentation) {
        presentation.setEnabled(true);
    }

    public void rename(TestDoxController testDoxController) {
        testDoxController.startRename(this);
    }

    public void delete(TestDoxController testDoxController) {
        editorApi.delete(getPsiElement());
    }

    public int compareTo(Object object) {
        if (object instanceof TestMethod) {
            return getDisplayString().compareTo(((TestMethod) object).getDisplayString());
        }
        return 0;
    }
}

