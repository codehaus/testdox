package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;

import org.codehaus.testdox.intellij.IconHelper;
import org.codehaus.testdox.intellij.TestDoxFile;

public class AddTestAction extends BaseAction {

    public AddTestAction() {
        super("Add Test", "Adds a test to the current unit test class", IconHelper.getIcon(IconHelper.ADD_TEST_ICON));
    }

    public void actionPerformed(AnActionEvent event) {
        actionEvents.getTestDoxController(event).addTest();
    }

    public void update(AnActionEvent event) {
        TestDoxFile testDoxFile = actionEvents.getTestDoxController(event).getCurrentTestDoxFile();
        event.getPresentation().setEnabled((testDoxFile != null) && (testDoxFile.canNavigateToTestedClass()));
    }
}
