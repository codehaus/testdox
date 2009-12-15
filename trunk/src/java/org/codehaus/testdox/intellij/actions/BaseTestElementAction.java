package org.codehaus.testdox.intellij.actions;

import javax.swing.Icon;

import com.intellij.openapi.actionSystem.AnActionEvent;

abstract class BaseTestElementAction extends BaseAction {

    protected final boolean useFromTestDoxToolWindow;

    BaseTestElementAction(String text, String description, Icon icon, boolean useFromTestDoxToolWindow) {
        super(text, description, icon);
        this.useFromTestDoxToolWindow = useFromTestDoxToolWindow;
    }

    public void update(AnActionEvent event) {
        if (useFromTestDoxToolWindow) {
            actionEvents().getTestDoxToolWindowUI(event).updatePresentation(event.getPresentation());
        } else {
            actionEvents().getTestDoxController(event).updatePresentation(event.getPresentation());
        }
    }
}
