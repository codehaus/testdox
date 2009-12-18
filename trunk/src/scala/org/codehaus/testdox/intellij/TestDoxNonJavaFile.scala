package org.codehaus.testdox.intellij

import com.intellij.openapi.vfs.VirtualFile
import org.codehaus.testdox.intellij.ui.TestDoxTableModel

class TestDoxNonJavaFile(file: VirtualFile) extends TestDoxFile(file, null, null, null, TestMethod.EMPTY_ARRAY) {

  def updateModel(model: TestDoxTableModel) {
    model.setNotJava()
  }
}

object TestDoxNonJavaFile {
  val NO_CLASS_MESSAGE = "No class selected"

  val TEST_ELEMENT: TestElement = new AbstractTestElement() {
    val displayString = NO_CLASS_MESSAGE
    val icon = IconHelper.getIcon(IconHelper.NOT_JAVA_ICON)
  }
}
