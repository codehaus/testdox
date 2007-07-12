package org.codehaus.testdox.intellij.config;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class IconCellRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        return super.getTableCellRendererComponent(table, value, false, false, row, column);
    }

    protected void setValue(Object value) {
        setHorizontalAlignment(JLabel.CENTER);
        setIcon((Icon) value);
    }
}
