package org.codehaus.testdox.intellij

import com.intellij.openapi.vfs.VirtualFile

import org.codehaus.testdox.intellij.ui.TestDoxTableModel

class TestDoxNonProjectClass(file: VirtualFile, className: String, testClass: TestClass, testedClass: TestClass)
    extends TestDoxFile(file, className, testClass, testedClass, TestMethod.EMPTY_ARRAY) {

  def updateModel(model: TestDoxTableModel) {
    model.setTestDoxForNonProjectClass(this)
  }
}

object TestDoxNonProjectClass {

  val TEST_ELEMENT = new AbstractTestElement() {
    val displayString = "<font color=\"gray\">Not in current project</font>"
    val icon = IconHelper.getIcon(IconHelper.WARNING_ICON)
  }
}
