package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.codehaus.testdox.intellij.IconHelper;

public class ToggleQuickDoxAction extends BaseAction {

    public ToggleQuickDoxAction() {
        super("Show QuickDox", "Shows TestDox in a tooltip", IconHelper.getIcon(IconHelper.DOX_ICON));
    }

    public void actionPerformed(AnActionEvent event) {
        actionEvents.getTestDoxController(event).toggleQuickDox();
    }
}
