package org.codehaus.testdox.intellij.config

import org.codehaus.testdox.intellij.IconHelper

import javax.swing._
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

import scala.collection.mutable.ArrayBuffer

private[config] class PackageTableModel extends TableModel with ListSelectionListener {

  val mappings = new ArrayBuffer[String]()

  private val listeners = new ArrayBuffer[TableModelListener]()

  def setMappings(newMappings: List[String]) {
    mappings.clear()
    mappings ++= newMappings
  }

  def getColumnClass(columnIndex: Int): Class[_] = if (columnIndex == 0) classOf[String] else classOf[Icon]

  val getColumnCount = 2

  def getColumnName(columnIndex: Int) = if (columnIndex == 0) "Package mappings" else ""

  def getRowCount = mappings.size

  def setValueAt(aValue: Object, rowIndex: Int, columnIndex: Int) {
    if (columnIndex == 0) {
      mappings.remove(rowIndex)
      if (aValue != null && aValue.asInstanceOf[String].length() > 0) {
        mappings.insert(rowIndex, aValue.asInstanceOf[String])
      }
      fireTableModelChanged()
    }
  }

  def getValueAt(rowIndex: Int, columnIndex: Int): Object = {
    if (columnIndex == 1) IconHelper.getIcon(IconHelper.REMOVE_ICON) else mappings(rowIndex)
  }

  def isCellEditable(rowIndex: Int, columnIndex: Int) = columnIndex == 0

  def addTableModelListener(listener: TableModelListener) {
    if (!listeners.contains(listener)) {
      listeners += listener
    }
  }

  def removeTableModelListener(listener: TableModelListener) {
    listeners -= listener
  }

  def addMapping(text: String) {
    if (!mappings.contains(text)) {
      mappings += text
      fireTableModelChanged()
    }
  }

  private def fireTableModelChanged() {
    for (val listener <- listeners) {
      listener.tableChanged(new TableModelEvent(this))
    }
  }

  def valueChanged(event: ListSelectionEvent) {}
}
