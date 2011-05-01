package org.codehaus.testdox.intellij.ui

import java.awt.Component

import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

import org.codehaus.testdox.intellij.TestElement

class TestElementCellRenderer extends DefaultTableCellRenderer {

  override def getTableCellRendererComponent(table: JTable, value: Any, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component = {
    val element = value.asInstanceOf[TestElement]
    setIcon(element.icon)
    super.getTableCellRendererComponent(table, "<html>" + element.displayString + "</html>", isSelected, false, row, column);
  }
}
