package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.codehaus.testdox.intellij.IconHelper;

public class ToggleTestClassAction extends BaseAction {

    public ToggleTestClassAction() {
        super("Toggle Class/Test", "Switches back and forth between a class and its unit test class", IconHelper.getIcon(IconHelper.DOX_ICON));
    }

    public void actionPerformed(AnActionEvent event) {
        actionEvents.getTestDoxController(event).toggleTestClassAndTestedClass();
    }

    public void update(AnActionEvent event) {
        event.getPresentation().setEnabled(actionEvents.getTestDoxController(event).canCurrentFileBeUnitTested());
    }
}
