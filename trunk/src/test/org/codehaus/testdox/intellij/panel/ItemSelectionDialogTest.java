package org.codehaus.testdox.intellij.panel;

import com.intellij.openapi.project.Project;
import org.intellij.openapi.testing.DialogCreator;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.MockObjectTestCase;

import java.awt.*;

public class ItemSelectionDialogTest extends MockObjectTestCase {

    protected void setUp() {
        MockApplicationManager.clear();
    }

    public void testEnablesOkButtonIfSelectionIsMade() throws Exception {
        ItemSelectionDialog dialog = createDialog("hi", "bye");
        dialog.getList().addSelectionInterval(0, 0);
        assertTrue(dialog.isOKActionEnabled());
    }

    public void testInitiallyDisablesOkButton() throws Exception {
        assertFalse(createDialog("hi", "bye").isOKActionEnabled());
    }

    public void testReturnsCancelledWithoutShowingItselfIfNoItemsWereProvided() throws Exception {
        ItemSelectionDialog dialog = createDialog();
        show(dialog);
        assertEquals(ItemSelectionDialog.CANCEL_EXIT_CODE, dialog.getExitCode());
    }

    public void testReturnsOkWithoutShowingDialogIfOnlyOneItemWasProvided() throws Exception {
        String item = "hi";
        ItemSelectionDialog dialog = createDialog(item);
        show(dialog);
        assertEquals(true, dialog.isOK());
        assertEquals(item, dialog.getSelectedItem());
    }

    private ItemSelectionDialog createDialog(final Object... items) throws Exception {
        DialogCreator<ItemSelectionDialog> dialogCreator = new DialogCreator<ItemSelectionDialog>() {
            protected ItemSelectionDialog create() {
                return new ItemSelectionDialog((Project) mock(Project.class).proxy(), items, "blah", "blah", null);
            }
        };
        EventQueue.invokeAndWait(dialogCreator);
        return dialogCreator.getDialog();
    }

    private void show(final ItemSelectionDialog dialog) throws Exception {
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                dialog.show();
            }
        });
    }
}
