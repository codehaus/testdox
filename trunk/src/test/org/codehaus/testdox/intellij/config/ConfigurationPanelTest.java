package org.codehaus.testdox.intellij.config;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import junit.framework.TestCase;

public class ConfigurationPanelTest extends TestCase {

    public void testTogglesActivationOfEntryWidgetsWhenCustomPackagingCheckboxClicked() throws Exception {
        PanelMock panel = new PanelMock();
        assertFalse(panel.getPackageField().isEnabled());
        assertFalse(panel.getAddButton().isEnabled());
        assertFalse(panel.getMappingTable().isEnabled());

        panel.check();
        assertTrue(panel.getPackageField().isEnabled());
        assertTrue(panel.getAddButton().isEnabled());
        assertTrue(panel.getMappingTable().isEnabled());

        panel.uncheck();
        assertFalse(panel.getPackageField().isEnabled());
        assertFalse(panel.getAddButton().isEnabled());
        assertFalse(panel.getMappingTable().isEnabled());
    }

    public void testRemovesCurrentRowWhenDeleteColumnClicked() throws Exception {
        PanelMock panel = new PanelMock();
        panel.getPackageField().setText("foo");
        panel.addMapping();
        panel.getMappingTable().changeSelection(0, 1, false, false);
        panel.deleteRow();
        assertEquals(0, panel.getMappingTable().getRowCount());
    }

    public void testDoNotRemoveCurrentRowIfMappingColumnClicked() throws Exception {
        PanelMock panel = new PanelMock();
        panel.getPackageField().setText("foo");
        panel.addMapping();
        panel.getMappingTable().changeSelection(0, 0, false, false);
        panel.deleteRow();
        assertEquals(1, panel.getMappingTable().getRowCount());
    }

    private class PanelMock extends ConfigurationPanel {

        public JTextField getPackageField() {
            return packageInputField();
        }

        public JButton getAddButton() {
            return addButton();
        }

        public void check() {
            assertFalse(customMappingStatus());
            allowCustom().doClick();
        }

        public void uncheck() {
            assertTrue(customMappingStatus());
            allowCustom().doClick();
        }

        public JTable getMappingTable() {
            return table();
        }
    }
}
