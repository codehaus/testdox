package org.codehaus.testdox.intellij;

import javax.swing.Icon;

import com.intellij.psi.PsiClass;

public class TestInterface extends TestClass {

    public TestInterface(String className, PsiClass psiClass, EditorApi editorApi, NameResolver resolver) {
        super(className, psiClass, editorApi, resolver);
    }

    public String getDisplayString() {
        return new StringBuffer("<i>").append(super.getDisplayString()).append("</i>").toString();
    }

    public Icon getIcon() {
        return (getPsiElement() != null) ? IconHelper.getIcon(IconHelper.INTERFACE_ICON)
                                         : IconHelper.getLockedIcon(IconHelper.INTERFACE_ICON);
    }
}
