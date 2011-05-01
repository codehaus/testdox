package org.codehaus.testdox.intellij.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import org.codehaus.testdox.intellij.Icons._

class AddTestAction extends BaseAction("Add Test", "Adds a test to the current unit test class", getIcon(ADD_TEST_ICON)) {

  def actionPerformed(event: AnActionEvent) {
    actionEvents.getTestDoxController(event).addTest()
  }

  override def update(event: AnActionEvent) {
    val testDoxFile = actionEvents.getTestDoxController(event).getCurrentTestDoxFile
    event.getPresentation.setEnabled(testDoxFile != null && testDoxFile.canNavigateToTestedClass)
  }
}
