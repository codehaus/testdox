package org.codehaus.testdox.intellij

import com.intellij.openapi.vfs.VirtualFile

import org.codehaus.testdox.intellij.ui.TestDoxTableModel

abstract class TestDoxFile(val file: VirtualFile,
                           val className: String,
                           val testClass: TestClass,
                           val testedClass: TestClass,
                           val testMethods: Array[TestMethod]) {

  def updateModel(model: TestDoxTableModel): Unit

  def isTestedClass = false

  def canBeUnitTested = false

  def canNavigateToTestClass = false

  def canNavigateToTestedClass = false

  override def toString: String = {
    new StringBuilder(getClass().getName())
        .append(" { file: ").append(file)
        .append("; className: ").append(className)
        .append("; testClass: ").append(testClass)
        .append("; testedClass: ").append(testedClass)
        .append(" }")
        .toString
  }
}
