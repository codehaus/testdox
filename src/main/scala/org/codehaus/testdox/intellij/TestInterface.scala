package org.codehaus.testdox.intellij

import com.intellij.psi.PsiClass

class TestInterface(className: String, psiClass: PsiClass, editorApi: EditorApi, resolver: NameResolver)
    extends TestClass(className, psiClass, editorApi, resolver) {

  override def displayString = "<i>" + super.displayString + "</i>"

  override val icon = {
    if (psiElement != null)
      Icons.getIcon(Icons.INTERFACE_ICON)
    else
      Icons.getLockedIcon(Icons.INTERFACE_ICON)
  }
}
