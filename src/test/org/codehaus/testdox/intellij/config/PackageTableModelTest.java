package org.codehaus.testdox.intellij.config;

import javax.swing.Icon;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.codehaus.testdox.intellij.IconHelper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PackageTableModelTest {

    private final PackageTableModel model = new PackageTableModel();

    @Test
    public void definesAllColumnsAsStrings() throws Exception {
        assertEquals(String.class, model.getColumnClass(0));
        assertEquals(Icon.class, model.getColumnClass(1));
    }

    @Test
    public void allowsOnlyTheMappingColumnToBeEditable() throws Exception {
        assertTrue(model.isCellEditable(0, 0));
        assertFalse(model.isCellEditable(0, 1));
    }

    @Test
    public void returnsBlankForRemoveColumnName() throws Exception {
        assertEquals("", model.getColumnName(1));
    }

    @Test
    public void returnsRemoveAsValueForAllRowsInRemoveColumn() throws Exception {
        assertEquals(IconHelper.getIcon(IconHelper.REMOVE_ICON), model.getValueAt(0, 1));
    }

    @Test
    public void updatesTableWhenAMappingIsAdded() throws Exception {
        final MyTableModelListener listener = new MyTableModelListener();
        model.addTableModelListener(listener);

        assertEquals(0, model.getRowCount());
        model.addMapping("foo");
        assertEquals(1, model.getRowCount());

        assertTrue(listener.tableChanged);
    }

    @Test
    public void doesNotAddNewMappingIfMappingAlreadyExists() throws Exception {
        model.addMapping("foo");
        assertEquals(1, model.getRowCount());

        final MyTableModelListener listener = new MyTableModelListener();
        model.addTableModelListener(listener);

        model.addMapping("foo");
        assertEquals(1, model.getRowCount());

        assertFalse(listener.tableChanged);
    }

    @Test
    public void removesRowFromTableIfNewValueIsBlank() throws Exception {
        model.addMapping("foo");
        assertEquals(1, model.getRowCount());

        final MyTableModelListener listener = new MyTableModelListener();
        model.addTableModelListener(listener);

        model.setValueAt("", 0, 0);

        assertEquals(0, model.getRowCount());
        assertTrue(listener.tableChanged);
    }

    private static class MyTableModelListener implements TableModelListener {

        private boolean tableChanged;

        public void tableChanged(TableModelEvent event) {
            tableChanged = true;
        }
    }
}
