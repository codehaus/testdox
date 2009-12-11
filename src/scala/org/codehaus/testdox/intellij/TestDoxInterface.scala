package org.codehaus.testdox.intellij

import com.intellij.openapi.vfs.VirtualFile
import panel.TestDoxModel

class TestDoxInterface(file: VirtualFile, className: String, testClass: TestClass, testedClass: TestClass)
    extends TestDoxFile(file, className, testClass, testedClass, TestMethod.EMPTY_ARRAY) {

  def updateModel(model: TestDoxModel) {
    model.setTestDoxForInterface(this)
  }
}

object TestDoxInterface {

  val TEST_ELEMENT: TestElement = new AbstractTestElement() {
    val displayString = "<font color=\"gray\">Interfaces do not have unit tests.</font>"
    val icon = IconHelper.getIcon(IconHelper.WARNING_ICON)
  }
}
