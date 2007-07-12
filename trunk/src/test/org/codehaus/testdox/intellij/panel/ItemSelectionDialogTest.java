package org.codehaus.testdox.intellij.panel;

import com.intellij.openapi.project.Project;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.MockObjectTestCase;

public class ItemSelectionDialogTest extends MockObjectTestCase {

    protected void setUp() {
        MockApplicationManager.reset();
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
        dialog.show();
        assertEquals(ItemSelectionDialog.CANCEL_EXIT_CODE, dialog.getExitCode());
    }

    public void testReturnsOkWithoutShowingDialogIfOnlyOneItemWasProvided() throws Exception {
        String item = "hi";
        ItemSelectionDialog dialog = createDialog(item);
        dialog.show();
        assertEquals(true, dialog.isOK());
        assertEquals(item, dialog.getSelectedItem());
    }

    private ItemSelectionDialog createDialog(Object... items) {
        return new ItemSelectionDialog((Project) mock(Project.class).proxy(), items, "blah", "blah", null);
    }
}
