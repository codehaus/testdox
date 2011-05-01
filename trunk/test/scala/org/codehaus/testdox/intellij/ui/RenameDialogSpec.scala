package org.codehaus.testdox.intellij.ui

import com.intellij.openapi.project.Project
import org.intellij.openapi.testing.DialogCreator
import org.intellij.openapi.testing.MockApplicationManager

import java.awt._
import javax.swing._
import org.specs.SpecificationWithJUnit
import org.specs.mock.JMocker

object RenameDialogSpec extends SpecificationWithJUnit with JMocker {

  val project = mock[Project]
  private var dialog: RenameDialog =_

  "RenameDialog" should {
    doBefore {
      MockApplicationManager.clear()
      dialog = createDialog()    
    }

    "place the default keyboard focus on the sentence field" in {
      val expectedSentence = "some random sentence"
      dialog.setSentence(expectedSentence)

      val preferredFocusedComponent = dialog.getPreferredFocusedComponent()
      preferredFocusedComponent must haveSuperClass[JTextField]

      val sentenceField = preferredFocusedComponent.asInstanceOf[JTextField]
      sentenceField.getText must be equalTo expectedSentence
    }

    "enable the OK button if the initial sentence is valid" in {
      dialog.setSentence("foo bar baz")
      dialog.isOKActionEnabled must be equalTo true
    }

    "disable the OK button if the first character of the sentence cannot be used at the start of a Java identifier" in {
      dialog.isOKActionEnabled must be equalTo true
      dialog.setSentence("-foo")
      dialog.isOKActionEnabled must be equalTo false
    }

    "disable the OK button if the remaining of the sentence contains a character that cannot be used in a Java identifier" in {
      dialog.isOKActionEnabled must be equalTo true
      dialog.handleRename("mo&o")
      dialog.isOKActionEnabled must be equalTo false
    }

    "re-enable the OK button when an invalid character is removed from the sentence" in {
      dialog.isOKActionEnabled must be equalTo true

      val sentenceField = dialog.getPreferredFocusedComponent().asInstanceOf[JTextField]
      sentenceField.getDocument.insertString(0, "mo&o", null)
      dialog.isOKActionEnabled must be equalTo false

      sentenceField.getDocument.remove(2, 1)
      dialog.isOKActionEnabled must be equalTo true
    }

    "dispose and return the initial sentence when cancelled" in {
      val sentence = "foo"
      dialog.setSentence(sentence)
      EventQueue.invokeAndWait(new Runnable() {
        def run() {
          dialog.doCancelAction()
        }
      })
      dialog.sentence must be equalTo sentence
    }

    "dispose and return the new sentence when confirmed" in {
      val newName = "bar"
      dialog.setSentence("foo")
      dialog.handleRename(newName)
      EventQueue.invokeAndWait(new Runnable() {
        def run() {
          dialog.doOKAction()
        }
      })
      dialog.sentence must be equalTo newName
    }
  }

  private def createDialog() = {
    val dialogCreator = new DialogCreator[RenameDialog]() {
      def create() = new RenameDialog(project, "")
    }
    EventQueue.invokeAndWait(dialogCreator)
    val dialog = dialogCreator.getDialog
    dialog.createCenterPanel()
    dialog
  }
}
