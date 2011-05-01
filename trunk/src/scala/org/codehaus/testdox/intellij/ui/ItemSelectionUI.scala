package org.codehaus.testdox.intellij.ui

trait ItemSelectionUI {

  def getSelectedItem: Any

  def setSelectedIndex(index: Int)

  def show()

  def isOK: Boolean

  def wasCancelled: Boolean
}
