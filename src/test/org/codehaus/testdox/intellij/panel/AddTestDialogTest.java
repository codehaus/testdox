package org.codehaus.testdox.intellij.panel;

import org.intellij.openapi.testing.DialogCreator;

public class AddTestDialogTest extends RenameDialogTest {

    public void testHasADifferentTitleThanTheRenameDialog() throws Exception {
        assertEquals("Add Test", createDialog().getTitle());
    }

    protected DialogCreator<AddTestDialog> dialogCreator() {
        return new DialogCreator<AddTestDialog>() {
            protected AddTestDialog create() {
                return new AddTestDialog(projectMock);
            }
        };
    }
}
