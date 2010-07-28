package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;

import org.codehaus.testdox.intellij.IconHelper;

public class ActivateTestDoxPanelAction extends BaseAction {

    public ActivateTestDoxPanelAction() {
        super("Toggle TestDox", "Shows/hides the TestDox panel for the current class", IconHelper.getIcon(IconHelper.TESTDOX_ICON));
    }

    public void actionPerformed(AnActionEvent event) {
        actionEvents.getTestDoxController(event).toggleToolWindow();
    }
}
