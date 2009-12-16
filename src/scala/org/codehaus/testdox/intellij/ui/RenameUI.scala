package org.codehaus.testdox.intellij.ui

trait RenameUI {

  val sentence: String

  def setSentence(sentence: String): Unit

  def doCancelAction(): Unit

  def doOKAction(): Unit

  def isOK(): Boolean

  def show(): Unit
}
