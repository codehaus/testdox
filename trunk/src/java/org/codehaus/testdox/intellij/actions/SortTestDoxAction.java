package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;

import org.codehaus.testdox.intellij.IconHelper;

public class SortTestDoxAction extends BaseToggleAction {

    public static final String ID = "TestDox.SortTestDox";

    private boolean selected;

    public SortTestDoxAction() {
        this(false, false);
    }

    public SortTestDoxAction(boolean selected, boolean useFromTestDoxToolWindow) {
        super("Sort Alphabetically", "Toggle sorting", IconHelper.getIcon(IconHelper.SORT_ICON), useFromTestDoxToolWindow);
        this.selected = selected;
    }

    public boolean isSelected(AnActionEvent anActionEvent) {
        return selected;
    }

    public void setSelected(AnActionEvent event, boolean selected) {
        this.selected = selected;
        if (event != null) {
            actionEvents.getTestDoxController(event).updateSort(selected);
        }
    }
}
