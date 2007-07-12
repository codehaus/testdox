package org.codehaus.testdox.intellij.panel;

import junitx.framework.Assert;

public class AddTestDialogTest extends RenameDialogTest {

    public void testHasADifferentTitleThanTheRenameDialog() {
        String addTestDialogTitle = this.createDialog().getTitle();
        String renameDialogTitle = super.createDialog().getTitle();

        assertEquals("Add Test", addTestDialogTitle);
        assertEquals("Rename Test", renameDialogTitle);
        Assert.assertNotEquals("AddTestDialog title", renameDialogTitle, addTestDialogTitle);
    }

    protected RenameDialog createDialog() {
        AddTestDialog addTestDialog = new AddTestDialog(projectMock);
        addTestDialog.createCenterPanel();
        return addTestDialog;
    }
}
