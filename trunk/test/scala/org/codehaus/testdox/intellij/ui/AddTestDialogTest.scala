package org.codehaus.testdox.intellij.ui

import org.intellij.openapi.testing.DialogCreator
import junit.framework.Assert.assertEquals

class AddTestDialogTest extends RenameDialogTest {

  def testHasADifferentTitleThanTheRenameDialog() {
    assertEquals("Add Test", createDialog().getTitle())
  }

  def testStuff() {
    ("foo bar baz".replaceAll("\\s", ""): Seq[Char]) match {
      case Seq(first, rest@_*) => println("first: " + first + ", rest: " + rest)
      case Seq(_*) => println("nada")
    }
  }

  override protected def createDialogCreator() = new DialogCreator[RenameDialog]() {
    def create(): RenameDialog = new AddTestDialog(projectMock).asInstanceOf[RenameDialog]
  }
}
