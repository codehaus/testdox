package org.codehaus.testdox.intellij.actions;

import javax.swing.Icon;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

abstract class BaseAction extends AnAction {

    protected static final boolean DO_NOT_USE_FROM_TESTDOX_TOOL_WINDOW = false;

    protected final ActionEvents actionEvents = ActionEvents.instance;

    BaseAction(String text, String description, Icon icon) {
        super(text, description, icon);
        getTemplatePresentation().setEnabled(false);
    }

    public void update(AnActionEvent event) {
        event.getPresentation().setEnabled(actionEvents.getTestDoxController(event).hasActiveEditors());
    }
}
