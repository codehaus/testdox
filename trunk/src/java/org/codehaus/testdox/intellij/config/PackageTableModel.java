package org.codehaus.testdox.intellij.config;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.codehaus.testdox.intellij.IconHelper;

class PackageTableModel implements TableModel, ListSelectionListener {

    private final List<String> mappings = new ArrayList<String>();
    private final List<TableModelListener> listeners = new ArrayList<TableModelListener>();

    PackageTableModel() {
    }

    public void setMappings(List<String> mappings) {
        this.mappings.clear();
        this.mappings.addAll(mappings);
    }

    public List<String> getMappings() {
        return mappings;
    }

    public Class getColumnClass(int columnIndex) {
        return columnIndex == 0 ? String.class : Icon.class;
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int columnIndex) {
        return columnIndex == 0 ? "Package mappings" : "";
    }

    public int getRowCount() {
        return mappings.size();
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            mappings.remove(rowIndex);
            if (aValue != null && ((String) aValue).length() > 0) {
                mappings.add(rowIndex, (String) aValue);
            }
            fireTableModelChanged();
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return columnIndex == 1 ? IconHelper.getIcon(IconHelper.REMOVE_ICON) : mappings.get(rowIndex);
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    public void addTableModelListener(TableModelListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    public void addMapping(String text) {
        if (!mappings.contains(text)) {
            mappings.add(text);
            fireTableModelChanged();
        }
    }

    private void fireTableModelChanged() {
        for (TableModelListener listener : listeners) {
            listener.tableChanged(new TableModelEvent(this));
        }
    }

    public void valueChanged(ListSelectionEvent event) {
    }
}
