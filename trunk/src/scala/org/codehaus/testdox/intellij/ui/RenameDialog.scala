package org.codehaus.testdox.intellij.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper

import javax.swing._
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

import java.awt._
import java.awt.GridBagConstraints._

class RenameDialog(project: Project, initialSentence: String) extends DialogWrapper(project, true) with RenameUI {

  private val sentenceField = new JTextField()
  private var originalSentence = initialSentence
  private var currentSentence = initialSentence
  private var cancelled = false

  setTitle("Rename Test")
  setResizable(true)
  this.setSentence(sentence)
  setModal(true)
  init()

  override def sentence = if (cancelled) originalSentence else currentSentence

  override def setSentence(sentence: String) {
    originalSentence = sentence
    currentSentence = sentence
    sentenceField.setText(currentSentence)
    updatePanel()
  }

  override def getPreferredFocusedComponent(): JComponent = sentenceField

  override protected def createActions(): Array[Action] = Array(getOKAction(), getCancelAction())

  private def updatePanel() = setOKActionEnabled(isValidSentence(currentSentence))

  private def isValidSentence(sentence: String): Boolean = (sentence.replaceAll("\\s", ""): Seq[Char]) match {
    case Seq(first, rest @ _*) => Character.isJavaIdentifierStart(first) && rest.forall(Character.isJavaIdentifierPart(_))
    case Seq(_*) => true
  }

  override def doCancelAction() {
    super.doCancelAction()
    cancelled = true
  }

  override def doOKAction() {
    super.doOKAction()
    cancelled = false
  }

  override protected[ui] def createCenterPanel(): JComponent = {
    val panel = new JPanel(new GridBagLayout())
    addDocumentListener(sentenceField)

    val label = new JLabel("Type a sentence that describes the intention of the test: ")
    panel.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0d, 0.0d, WEST, NONE, RenameDialog.INSETS, 0, 0))
    panel.add(sentenceField, new GridBagConstraints(0, 1, 1, 1, 1.0d, 0.0d, WEST, HORIZONTAL, RenameDialog.INSETS, 20, 0))
    return panel
  }

  private def addDocumentListener(sentenceField: JTextField) {
    sentenceField.getDocument().addDocumentListener(new DocumentListener() {
      def insertUpdate(event: DocumentEvent) = handleRename(sentenceField.getText())

      def removeUpdate(event: DocumentEvent) = insertUpdate(event)

      def changedUpdate(event: DocumentEvent) {}
    })
  }

  private[ui] def handleRename(newName: String) {
    currentSentence = newName
    updatePanel()
  }
}

object RenameDialog {
  private val INSETS = new Insets(1, 1, 1, 1)
}
