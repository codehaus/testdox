package org.codehaus.testdox.intellij.panel;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.codehaus.testdox.intellij.TestElement;

public class TestElementCellRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        TestElement element = (TestElement) value;
        setIcon(element.icon());
        return super.getTableCellRendererComponent(table, "<html>" + element.displayString() + "</html>", isSelected, false, row, column);
    }
}
