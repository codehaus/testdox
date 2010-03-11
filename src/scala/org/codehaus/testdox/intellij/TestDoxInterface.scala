package org.codehaus.testdox.intellij

import com.intellij.openapi.vfs.VirtualFile
import org.codehaus.testdox.intellij.ui.TestDoxTableModel

class TestDoxInterface(file: VirtualFile, className: String, testClass: TestClass, testedClass: TestClass)
    extends TestDoxFile(file, className, testClass, testedClass, TestMethod.EMPTY_ARRAY) {

  def updateModel(model: TestDoxTableModel) {
    model.setTestDoxForInterface(this)
  }
}

object TestDoxInterface {

  val TEST_ELEMENT = new AbstractTestElement() {
    val displayString = "<font color=\"gray\">Interfaces do not have unit tests.</font>"
    val icon = Icons.getIcon(Icons.WARNING_ICON)
  }
}
