package org.codehaus.testdox.intellij.config;

import javax.swing.Icon;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import junit.framework.TestCase;

import org.codehaus.testdox.intellij.IconHelper;

public class PackageTableModelTest extends TestCase {

    private final PackageTableModel model = new PackageTableModel();

    public void testDefinesAllColumnsAsStrings() throws Exception {
        assertEquals(String.class, model.getColumnClass(0));
        assertEquals(Icon.class, model.getColumnClass(1));
    }

    public void testAllowsOnlyTheMappingColumnToBeEditable() throws Exception {
        assertTrue(model.isCellEditable(0, 0));
        assertFalse(model.isCellEditable(0, 1));
    }

    public void testReturnsBlankForRemoveColumnName() throws Exception {
        assertEquals("", model.getColumnName(1));
    }

    public void testReturnsRemoveAsValueForAllRowsInRemoveColumn() throws Exception {
        assertEquals(IconHelper.getIcon(IconHelper.REMOVE_ICON), model.getValueAt(0, 1));
    }

    public void testUpdatesTableWhenAMappingIsAdded() throws Exception {
        final MyTableModelListener listener = new MyTableModelListener();
        model.addTableModelListener(listener);

        assertEquals(0, model.getRowCount());
        model.addMapping("foo");
        assertEquals(1, model.getRowCount());

        assertTrue(listener.tableChanged);
    }

    public void testDoesNotAddNewMappingIfMappingAlreadyExists() throws Exception {
        model.addMapping("foo");
        assertEquals(1, model.getRowCount());

        final MyTableModelListener listener = new MyTableModelListener();
        model.addTableModelListener(listener);

        model.addMapping("foo");
        assertEquals(1, model.getRowCount());

        assertFalse(listener.tableChanged);
    }

    public void testRemovesRowFromTableIfNewValueIsBlank() throws Exception {
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
