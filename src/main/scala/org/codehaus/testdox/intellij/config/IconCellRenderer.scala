package org.codehaus.testdox.intellij.config

import java.awt.Component

import javax.swing.Icon
import javax.swing.JTable
import javax.swing.SwingConstants
import javax.swing.table.DefaultTableCellRenderer

class IconCellRenderer extends DefaultTableCellRenderer {

  override def getTableCellRendererComponent(table: JTable, value: Object, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component = {
    super.getTableCellRendererComponent(table, value, false, false, row, column)
  }

  override protected def setValue(value: Object) {
    setHorizontalAlignment(SwingConstants.CENTER)
    setIcon(value.asInstanceOf[Icon])
  }
}
