package org.codehaus.testdox.intellij.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper

import javax.swing._
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import java.awt._
import java.awt.GridBagConstraints.{WEST, HORIZONTAL}

class ItemSelectionDialog(project: Project, items: Array[Object], var instructions: String, title: String, renderer: ListCellRenderer)
    extends DialogWrapper(project, true) with ItemSelectionUI {

  private val itemList = createItemList(items, renderer)
  private var cancelled = false

  this.instructions = instructions + ":"
  setTitle(title)
  setResizable(false)
  setModal(true)
  updatePanel()
  init()

  def wasCancelled = cancelled

  private def createItemList(items: Array[Object], renderer: ListCellRenderer) = {
    val list = new JList(items)
    list.setBorder(BorderFactory.createEtchedBorder())
    if (renderer != null) list.setCellRenderer(renderer)
    list
  }

  override def doCancelAction() {
    cancelled = true
    super.doCancelAction()
  }

  override def show() {
    val size = itemList.getModel().getSize()
    if (size == 0) {
      doCancelAction()
      return
    }

    setSelectedIndex(0)

    if (size == 1) {
      doOKAction()
      return
    }

    super.show()
  }

  override protected def createActions(): Array[Action] = Array(getOKAction(), getCancelAction())

  override protected def createCenterPanel(): JComponent = {
    val panel = new JPanel(new GridBagLayout())
    val instructionLabel = new JLabel(instructions)
    panel.add(instructionLabel, new GridBagConstraints(0, 0, 1, 1, 1, 0, WEST, HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0))
    addSelectionListener(itemList);
    panel.add(itemList, new GridBagConstraints(0, 1, 1, 1, 1, 0, WEST, HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    return panel;
  }

  override def getPreferredFocusedComponent = itemList

  private def updatePanel() = setOKActionEnabled(itemList.getSelectedValue() != null)

  override def getSelectedItem() = itemList.getSelectedValue()

  private def addSelectionListener(list: JList) {
    list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      def valueChanged(event: ListSelectionEvent) = updatePanel()
    })
  }

  private[ui] def getList() = itemList

  def setSelectedIndex(index: Int) = if (index >= 0) itemList.setSelectedIndex(index)
}
