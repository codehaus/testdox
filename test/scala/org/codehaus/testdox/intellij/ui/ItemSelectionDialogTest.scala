package org.codehaus.testdox.intellij.ui

import com.intellij.openapi.project.Project
import org.intellij.openapi.testing.DialogCreator
import org.intellij.openapi.testing.MockApplicationManager
import org.jmock.MockObjectTestCase

import java.awt._
import junit.framework.Assert._
import com.intellij.openapi.ui.DialogWrapper.CANCEL_EXIT_CODE

class ItemSelectionDialogTest extends MockObjectTestCase {

  override protected def setUp() {
    MockApplicationManager.clear()
  }

  def testEnablesOkButtonIfSelectionIsMade() {
    val dialog = createDialog("hi", "bye")
    dialog.getList().addSelectionInterval(0, 0)
    assertTrue(dialog.isOKActionEnabled())
  }

  def testInitiallyDisablesOkButton() {
    assertFalse(createDialog("hi", "bye").isOKActionEnabled())
  }

  def testReturnsCancelledWithoutShowingItselfIfNoItemsWereProvided() {
    val dialog = createDialog()
    show(dialog)
    assertEquals(CANCEL_EXIT_CODE, dialog.getExitCode())
  }

  def testReturnsOkWithoutShowingDialogIfOnlyOneItemWasProvided() {
    val item = "hi"
    val dialog = createDialog(item)
    show(dialog)
    assertEquals(true, dialog.isOK())
    assertEquals(item, dialog.getSelectedItem())
  }

  private def createDialog(items: Object*): ItemSelectionDialog = {
    val dialogCreator = new DialogCreator[ItemSelectionDialog]() {
      def create() = new ItemSelectionDialog(mock(classOf[Project]).proxy().asInstanceOf[Project], items.toArray[Object], "blah", "blah", null)
    }
    EventQueue.invokeAndWait(dialogCreator)
    dialogCreator.getDialog()
  }

  private def show(dialog: ItemSelectionDialog) {
    EventQueue.invokeAndWait(new Runnable() {
      def run() = dialog.show()
    })
  }
}
