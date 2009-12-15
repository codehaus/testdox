package org.codehaus.testdox.intellij.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import org.codehaus.testdox.intellij.IconHelper
import org.codehaus.testdox.intellij.IconHelper._

class ToggleTestClassAction extends BaseAction("Toggle Class/Test", "Switches back and forth between a class and its unit test class", getIcon(IconHelper.DOX_ICON)) {

  def actionPerformed(event: AnActionEvent) {
    actionEvents.getTestDoxController(event).toggleTestClassAndTestedClass()
  }

  override def update(event: AnActionEvent) {
    event.getPresentation.setEnabled(actionEvents.getTestDoxController(event).canCurrentFileBeUnitTested())
  }
}
