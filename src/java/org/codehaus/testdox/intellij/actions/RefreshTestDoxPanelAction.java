package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;

import org.codehaus.testdox.intellij.IconHelper;

public class RefreshTestDoxPanelAction extends BaseTestElementAction {

    public static final String ID = "TestDox.RefreshTestDoxPanel";

    public RefreshTestDoxPanelAction() {
        this(BaseAction.DO_NOT_USE_FROM_TESTDOX_TOOL_WINDOW);
    }

    public RefreshTestDoxPanelAction(boolean useFromTestDoxToolWindow) {
        super("Refresh", "Refresh TestDox", IconHelper.getIcon(IconHelper.REFRESH_ICON), useFromTestDoxToolWindow);
    }

    public void actionPerformed(AnActionEvent event) {
        actionEvents.getTestDoxController(event).refreshToolWindow();
    }
}
