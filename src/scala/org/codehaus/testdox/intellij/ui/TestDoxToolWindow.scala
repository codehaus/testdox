package org.codehaus.testdox.intellij.ui

import java.awt.BorderLayout
import java.awt.BorderLayout.{CENTER, NORTH}
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.{BOTH, WEST}
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.{VK_DELETE, VK_ENTER, VK_F6}
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.JTable.AUTO_RESIZE_OFF
import javax.swing.ListSelectionModel.SINGLE_SELECTION
import javax.swing.ScrollPaneConstants.{HORIZONTAL_SCROLLBAR_AS_NEEDED, VERTICAL_SCROLLBAR_AS_NEEDED}
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import com.intellij.openapi.actionSystem.Presentation

import org.codehaus.testdox.intellij.TestDoxController
import org.codehaus.testdox.intellij.TestElement
import org.codehaus.testdox.intellij.config.Configuration.SHOW_FULLY_QUALIFIED_CLASS_NAME
import org.codehaus.testdox.intellij.ui.TestDoxToolWindow._

private[ui] class TestDoxToolWindow(testDoxController: TestDoxController, table: JTable, actionToolbarComponent: Component) extends JPanel
    with ToolWindowUI with TableModelListener with PropertyChangeListener {

  setLayout(new BorderLayout())
  add(actionToolbarComponent, NORTH)
  add(createDoxList(), CENTER)

  registerTableListeners(table)
  testDoxController.getConfiguration().addPropertyChangeListener(this)

  def this(testDoxController: TestDoxController, actionToolbarComponent: Component) {
    this(testDoxController, new JTable(testDoxController.getModel()), actionToolbarComponent)
  }

  private def registerTableListeners(table: JTable) {
    table.addMouseListener(new MouseAdapter() {
      override def mouseClicked(event: MouseEvent) = handleMouseEvent(event)
    })

    table.addKeyListener(new KeyAdapter() {
      override def keyPressed(event: KeyEvent) = handleKeyEvent(event)
    })

    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      override def valueChanged(event: ListSelectionEvent) = handleSelection()
    })
  }

  private[ui] def handleSelection() {
    if (testDoxController.getConfiguration().autoScrolling) {
      testDoxController.jumpToTestElement(getSelectedTestElement(), true)
    }
  }

  private[ui] def handleMouseEvent(event: MouseEvent) {
    table.requestFocus()
    if (event.getClickCount() == 2) {
      testDoxController.jumpToTestElement(getSelectedTestElement(), false)
    }
  }

  private[ui] def handleKeyEvent(event: KeyEvent) {
    event.getKeyCode() match {
      case VK_ENTER => testDoxController.jumpToTestElement(getSelectedTestElement(), false)
      case VK_F6 => {renameSelectedTestElement(); event.consume()}
      case VK_DELETE => deleteSelectedTestElement()
    }
  }

  private def createDoxList(): Component = {
    table.setAutoResizeMode(AUTO_RESIZE_OFF)
    table.setColumnSelectionAllowed(false)
    table.setRowSelectionAllowed(true)
    table.setSelectionMode(SINGLE_SELECTION)
    table.setShowGrid(false)
    table.setRowMargin(ROW_MARGIN)
    table.setRowHeight(ROW_HEIGHT + ROW_MARGIN)
    table.setRowHeight(0, ROW_HEIGHT + ROW_MARGIN + 5)
    table.setTableHeader(null)
    table.setDefaultRenderer(classOf[TestElement], new TestElementCellRenderer())

    val panel = new JPanel(new GridBagLayout())
    panel.add(table, new GridBagConstraints(0, 0, 1, 1, 1, 1, WEST, BOTH, EMPTY_INSETS, 0, 0))

    val scroller = new JScrollPane(panel, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED)
    scroller.setBorder(BorderFactory.createEtchedBorder())
    scroller.getVerticalScrollBar().setUnitIncrement(table.getRowHeight())
    return scroller
  }

  def tableChanged(event: TableModelEvent) {
    resizeTable()
    table.setEnabled(testDoxController.getModel().hasDox)
  }

  def propertyChange(event: PropertyChangeEvent) {
    if (SHOW_FULLY_QUALIFIED_CLASS_NAME.equals(event.getPropertyName())) {
      testDoxController.getModel().fireTableDataChanged()
    }
  }

  def update(presentation: Presentation) = getSelectedTestElement().update(presentation)

  def renameSelectedTestElement() = getSelectedTestElement().rename(testDoxController)

  def deleteSelectedTestElement() = getSelectedTestElement().delete(testDoxController)

  private def getSelectedTestElement(): TestElement = {
    val selectedRow = if (table.isEnabled() && (table.getSelectedRow() == -1)) 0 else table.getSelectedRow()
    return table.getModel().getValueAt(selectedRow, 1).asInstanceOf[TestElement]
  }

  private def resizeTable() {
    var width = 0
    for (row <- 0 until table.getRowCount()) {
      val renderer = table.getCellRenderer(row, 0)
      val value = table.getValueAt(row, 0)
      val component: Component = renderer.getTableCellRendererComponent(table, value, false, false, row, 0)
      width = Math.max(width, component.getPreferredSize().width)
    }
    table.getColumnModel().getColumn(0).setPreferredWidth(width + 10)
  }
}

object TestDoxToolWindow {
  val ROW_HEIGHT = 18
  val ROW_MARGIN = 2

  val EMPTY_INSETS = new Insets(0, 0, 0, 0)
}