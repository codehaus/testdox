package org.codehaus.testdox.intellij.ui

import org.codehaus.testdox.intellij.actions.PresentationUpdater

trait ToolWindowUI extends PresentationUpdater {

  def renameSelectedTestElement(): Unit

  def deleteSelectedTestElement(): Unit
}
