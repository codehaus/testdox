package org.codehaus.testdox.intellij

import com.intellij.psi.PsiClass

class TestInterface(className: String, psiClass: PsiClass, editorApi: EditorApi, resolver: NameResolver)
    extends TestClass(className, psiClass, editorApi, resolver) {

  override def displayString = "<i>" + super.displayString + "</i>"

  override val icon = {
    if (psiElement != null)
      IconHelper.getIcon(IconHelper.INTERFACE_ICON)
    else
      IconHelper.getLockedIcon(IconHelper.INTERFACE_ICON)
  }
}
