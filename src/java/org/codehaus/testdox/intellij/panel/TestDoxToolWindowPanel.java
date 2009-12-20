package org.codehaus.testdox.intellij.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;

import com.intellij.openapi.actionSystem.Presentation;

import org.codehaus.testdox.intellij.TestDoxController;
import org.codehaus.testdox.intellij.TestElement;
import org.codehaus.testdox.intellij.config.Configuration;
import org.codehaus.testdox.intellij.ui.TestElementCellRenderer;
import org.codehaus.testdox.intellij.ui.ToolWindowUI;

public class TestDoxToolWindowPanel extends JPanel implements TableModelListener, ToolWindowUI, PropertyChangeListener {

    private static final int ROW_HEIGHT = 18;
    private static final int ROW_MARGIN = 2;

    private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

    private final TestDoxController testDoxController;
    private final JTable table;

    public TestDoxToolWindowPanel(TestDoxController testDoxController, Component actionToolbarComponent) {
        this(testDoxController, new JTable(testDoxController.getModel()), actionToolbarComponent);
    }

    public TestDoxToolWindowPanel(TestDoxController testDoxController, JTable table, Component actionToolbarComponent) {
        this.table = table;
        this.testDoxController = testDoxController;

        setLayout(new BorderLayout());
        add(actionToolbarComponent, BorderLayout.NORTH);
        add(createDoxList(), BorderLayout.CENTER);

        registerTableListeners(table);
        testDoxController.getConfiguration().addPropertyChangeListener(this);
    }

    private void registerTableListeners(final JTable table) {
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                handleMouseEvent(event);
            }
        });

        table.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                handleKeyEvent(event);
            }
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                handleSelection();
            }
        });
    }

    void handleSelection() {
        if (testDoxController.getConfiguration().autoscrolling()) {
            testDoxController.jumpToTestElement(getSelectedTestElement(), true);
        }
    }

    void handleMouseEvent(MouseEvent event) {
        table.requestFocus();
        if (event.getClickCount() == 2) {
            testDoxController.jumpToTestElement(getSelectedTestElement(), false);
        }
    }

    void handleKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
        case KeyEvent.VK_ENTER:
             testDoxController.jumpToTestElement(getSelectedTestElement(), false);
             break;
        case KeyEvent.VK_F6:
             renameSelectedTestElement();
             event.consume();
             break;
        case KeyEvent.VK_DELETE:
             deleteSelectedTestElement();
             break;
        }
    }

    private Component createDoxList() {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setRowMargin(ROW_MARGIN);
        table.setRowHeight(ROW_HEIGHT + ROW_MARGIN);
        table.setRowHeight(0, ROW_HEIGHT + ROW_MARGIN + 5);
        table.setTableHeader(null);
        table.setDefaultRenderer(TestElement.class, new TestElementCellRenderer());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(table, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, EMPTY_INSETS, 0, 0));

        JScrollPane scroller = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroller.setBorder(BorderFactory.createEtchedBorder());
        scroller.getVerticalScrollBar().setUnitIncrement(table.getRowHeight());
        return scroller;
    }

    public void tableChanged(TableModelEvent event) {
        resizeTable();
        table.setEnabled(testDoxController.getModel().hasDox());
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (Configuration.SHOW_FULLY_QUALIFIED_CLASS_NAME().equals(event.getPropertyName())) {
            testDoxController.getModel().fireTableDataChanged();
        }
    }

    public void update(Presentation presentation) {
        getSelectedTestElement().update(presentation);
    }

    public void renameSelectedTestElement() {
        getSelectedTestElement().rename(testDoxController);
    }

    public void deleteSelectedTestElement() {
        getSelectedTestElement().delete(testDoxController);
    }

    private TestElement getSelectedTestElement() {
        int selectedRow = (table.isEnabled() && (table.getSelectedRow() == -1)) ? 0 : table.getSelectedRow();
        return (TestElement) table.getModel().getValueAt(selectedRow, 1);
    }

    private void resizeTable() {
        int width = 0;
        for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer renderer = table.getCellRenderer(row, 0);
            Object value = table.getValueAt(row, 0);
            Component component = renderer.getTableCellRendererComponent(table, value, false, false, row, 0);
            width = Math.max(width, component.getPreferredSize().width);
        }
        table.getColumnModel().getColumn(0).setPreferredWidth(width + 10);
    }
}
