package org.codehaus.testdox.intellij.ui

import org.intellij.openapi.testing.DialogCreator
import org.specs.SpecificationWithJUnit
import com.intellij.openapi.project.Project
import java.awt.EventQueue
import org.specs.mock.JMocker

object AddTestDialogSpec extends SpecificationWithJUnit with JMocker {

  "AddTestDialog" isSpecifiedBy(RenameDialogSpec)
  "and" should {
    "have a title different to that of RenameDialog" in {
      createDialog().getTitle() must be equalTo "Add Test"
    }
  }

  private def createDialog() = {
    val dialogCreator = new DialogCreator[AddTestDialog]() {
      def create() = new AddTestDialog(mock[Project])
    }
    EventQueue.invokeAndWait(dialogCreator)
    val dialog = dialogCreator.getDialog()
    dialog.createCenterPanel()
    dialog
  }
}
