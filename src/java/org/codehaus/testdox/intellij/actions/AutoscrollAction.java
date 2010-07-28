package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;

import org.codehaus.testdox.intellij.IconHelper;

public class AutoscrollAction extends BaseToggleAction {

    public static final String ID = "TestDox.Autoscroll";

    private boolean selected = false;

    public AutoscrollAction() {
        this(false, false);
    }

    public AutoscrollAction(boolean selected, boolean useFromTestDoxToolWindow) {
        super("Autoscroll To Source", "Toggle autoscrolling", IconHelper.getIcon(IconHelper.AUTOSCROLL_ICON), useFromTestDoxToolWindow);
        this.selected = selected;
    }

    public boolean isSelected(AnActionEvent anActionEvent) {
        return selected;
    }

    public void setSelected(AnActionEvent event, boolean selected) {
        this.selected = selected;
        if (event != null) {
            actionEvents.getTestDoxController(event).updateAutoscroll(selected);
        }
    }
}
