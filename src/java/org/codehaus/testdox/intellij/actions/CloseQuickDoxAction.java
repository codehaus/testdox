package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.codehaus.testdox.intellij.IconHelper;

public class CloseQuickDoxAction extends BaseAction {

    public CloseQuickDoxAction() {
        super("Close QuickDox", "Closes the TestDox tooltip", IconHelper.getIcon(IconHelper.DOX_ICON));
    }

    public void actionPerformed(AnActionEvent event) {
        actionEvents.getTestDoxController(event).closeQuickDox();
    }
}
