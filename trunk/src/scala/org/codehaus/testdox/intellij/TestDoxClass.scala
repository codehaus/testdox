package org.codehaus.testdox.intellij

import com.intellij.openapi.vfs.VirtualFile;

import org.codehaus.testdox.intellij.ui.TestDoxTableModel;

class TestDoxClass(override val file: VirtualFile,
                   override val className: String,
                   private val isTheTestedClass: Boolean,
                   override val testClass: TestClass,
                   override val testedClass: TestClass,
                   override val testMethods: Array[TestMethod]) extends TestDoxFile(file, className, testClass, testedClass, testMethods) {

  override val isTestedClass = isTheTestedClass

  override val canBeUnitTested = true

  override def canNavigateToTestClass = testClass != null && testClass.isTestClass

  override def canNavigateToTestedClass = testedClass != null && !testedClass.isTestClass

  def updateModel(model: TestDoxTableModel) {
    model.setTestDoxForClass(this)
  }
}

object TestDoxClass {
  val NO_DOX_ELEMENT = new AbstractTestElement() {
    val displayString = "<font color=\"red\">No tests found for current class!</font>"
    val icon = Icons.getIcon(Icons.NO_TESTS_ICON)
  }
}
