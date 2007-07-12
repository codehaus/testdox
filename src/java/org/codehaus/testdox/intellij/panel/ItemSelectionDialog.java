package org.codehaus.testdox.intellij.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import static jedi.functional.FunctionalPrimitives.array;

public class ItemSelectionDialog extends DialogWrapper implements ItemSelectionUI {

    private final String instructions;
    private final JList itemList;

    private boolean cancelled;

    public ItemSelectionDialog(Project project, Object[] items, String instructions, String title,
                               ListCellRenderer renderer) {
        super(project, true);
        this.instructions = instructions + ":";
        itemList = createItemList(items, renderer);
        setTitle(title);
        setResizable(false);
        setModal(true);
        updatePanel();
        init();
    }

    public boolean wasCancelled() {
        return cancelled;
    }

    private JList createItemList(Object[] items, ListCellRenderer renderer) {
        JList list = new JList(items);
        list.setBorder(BorderFactory.createEtchedBorder());
        if (renderer != null) {
            list.setCellRenderer(renderer);
        }
        return list;
    }

    public void doCancelAction() {
        cancelled = true;
        super.doCancelAction();
    }

    public void show() {
        int size = itemList.getModel().getSize();
        if (size == 0) {
            doCancelAction();
            return;
        }

        setSelectedIndex(0);

        if (size == 1) {
            doOKAction();
            return;
        }

        super.show();
    }

    protected Action[] createActions() {
        return array(getOKAction(), getCancelAction());
    }

    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel intstructionLabel = new JLabel(instructions);
        panel.add(intstructionLabel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
        addSelectionListener(itemList);
        panel.add(itemList, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
        return panel;
    }

    public JComponent getPreferredFocusedComponent() {
        return itemList;
    }

    private void updatePanel() {
        setOKActionEnabled(itemList.getSelectedValue() != null);
    }

    public Object getSelectedItem() {
        return itemList.getSelectedValue();
    }

    private void addSelectionListener(JList list) {
        list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                updatePanel();
            }
        });
    }

    JList getList() {
        return itemList;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0) {
            itemList.setSelectedIndex(index);
        }
    }
}
