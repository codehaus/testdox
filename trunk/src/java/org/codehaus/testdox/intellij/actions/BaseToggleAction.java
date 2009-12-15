package org.codehaus.testdox.intellij.actions;

import javax.swing.Icon;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;

abstract class BaseToggleAction extends ToggleAction {

    protected final ActionEvents actionEvents = ActionEvents.instance;

    private final boolean useFromTestDoxToolWindow;

    BaseToggleAction(String text, String description, Icon icon, boolean useFromTestDoxToolWindow) {
        super(text, description, icon);
        this.useFromTestDoxToolWindow = useFromTestDoxToolWindow;
    }

    public void update(AnActionEvent event) {
        super.update(event);

        if (useFromTestDoxToolWindow) {
            actionEvents.getTestDoxToolWindowUI(event).update(event.getPresentation());
        } else {
            actionEvents.getTestDoxController(event).update(event.getPresentation());
        }
    }
}
