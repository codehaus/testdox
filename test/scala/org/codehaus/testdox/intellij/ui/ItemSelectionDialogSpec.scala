package org.codehaus.testdox.intellij.ui

import org.specs.mock.JMocker
import org.specs.SpecificationWithJUnit
import org.intellij.openapi.testing.{DialogCreator, MockApplicationManager}
import java.awt.EventQueue
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper.CANCEL_EXIT_CODE

object ItemSelectionDialogSpec extends SpecificationWithJUnit with JMocker {

  "ItemSelectionDialog" should {
    doBefore { MockApplicationManager.clear() }

    "enable the OK button when the selection is made" in {
      val dialog = createDialog("hi", "bye")
      dialog.getList().addSelectionInterval(0, 0)
      dialog.isOKActionEnabled must be equalTo true
    }

    "initially disable the OK button" in {
      createDialog("hi", "bye").isOKActionEnabled must be equalTo false
    }

    "return the exit code for CANCEL without showing itself if no items were provided" in {
      val dialog = createDialog()
      show(dialog)
      dialog.getExitCode must be equalTo CANCEL_EXIT_CODE
    }

    "return the exit code for OK without showing itself if only one item was provided" in {
      val item = "hi"
      val dialog = createDialog(item)
      show(dialog)
      dialog.isOK must be equalTo true
      dialog.getSelectedItem() must be equalTo item
    }
  }

  private def createDialog(items: Object*) = {
    val dialogCreator = new DialogCreator[ItemSelectionDialog]() {
      def create() = new ItemSelectionDialog(mock[Project], items.toArray[Object], "blah", "blah", null)
    }
    EventQueue.invokeAndWait(dialogCreator)
    dialogCreator.getDialog
  }

  private def show(dialog: ItemSelectionDialog) {
    EventQueue.invokeAndWait(new Runnable() {
      def run() { dialog.show() }
    })
  }
}
