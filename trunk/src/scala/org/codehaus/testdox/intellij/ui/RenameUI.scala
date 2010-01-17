package org.codehaus.testdox.intellij.ui

trait RenameUI {

  def sentence: String

  def setSentence(sentence: String): Unit

  def doCancelAction(): Unit

  def doOKAction(): Unit

  def isOK(): Boolean

  def show(): Unit
}
