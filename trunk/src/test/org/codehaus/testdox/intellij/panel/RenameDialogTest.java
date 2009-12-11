package org.codehaus.testdox.intellij.panel;

import com.intellij.openapi.project.Project;
import junitx.framework.ObjectAssert;
import org.intellij.openapi.testing.DialogCreator;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.MockObjectTestCase;

import javax.swing.*;
import java.awt.*;

public class RenameDialogTest extends MockObjectTestCase {

    protected final Project projectMock = (Project) mock(Project.class).proxy();

    protected RenameDialog dialog;

    protected void setUp() throws Exception {
        MockApplicationManager.clear();
        dialog = createDialog();
    }

    public void testPlacesDefaultKeyboardFocusOnTheSentenceField() {
        String expectedSentence = "some random sentence";
        dialog.setSentence(expectedSentence);

        JComponent preferredFocusedComponent = dialog.getPreferredFocusedComponent();
        ObjectAssert.assertInstanceOf("focused component type", JTextField.class, preferredFocusedComponent);

        JTextField sentenceField = (JTextField) preferredFocusedComponent;
        assertEquals("sentence", expectedSentence, sentenceField.getText());
    }

    public void testEnablesOkIfInitialSentenceIsValid() throws Exception {
        dialog.setSentence("foo bar baz");
        assertTrue(dialog.isOKActionEnabled());
    }

    public void testDisablesOkIfTheFirstCharacterOfTheSentenceIsNotAValidStartOfAJavaIdentifier() throws Exception {
        assertTrue("OK action should be enabled", dialog.isOKActionEnabled());
        dialog.setSentence("-foo");
        assertFalse("OK action should no longer be enabled", dialog.isOKActionEnabled());
    }

    public void testDisablesOkIfTheRemainingOfTheTypedSentenceContainsACharacterThatCannotBeUsedInAJavaIdentifier() throws Exception {
        assertTrue("OK action should be enabled", dialog.isOKActionEnabled());
        dialog.handleRename("mo&o");
        assertFalse("OK action should no longer be enabled", dialog.isOKActionEnabled());
    }

    public void testReenablesOkWhenAnInvalidCharacterIsRemovedFromTheTypedSentence() throws Exception {
        assertTrue("OK action should be enabled", dialog.isOKActionEnabled());

        JTextField sentenceField = (JTextField) dialog.getPreferredFocusedComponent();
        sentenceField.getDocument().insertString(0, "mo&o", null);
        assertFalse("OK action should no longer be enabled", dialog.isOKActionEnabled());

        sentenceField.getDocument().remove(2, 1);
        assertTrue("OK action should be re-enabled", dialog.isOKActionEnabled());
    }

    public void testDisposesAndReturnsOriginalSentenceOnCancel() throws Exception {
        String sentence = "foo";
        dialog.setSentence(sentence);
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                dialog.doCancelAction();
            }
        });
        assertEquals(sentence, dialog.getSentence());
    }

    public void testDisposesAndReturnsTypedSentenceOnOk() throws Exception {
        String newName = "bar";
        dialog.setSentence("foo");
        dialog.handleRename(newName);
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                dialog.doOKAction();
            }
        });
        assertEquals(newName, dialog.getSentence());
    }

    protected final RenameDialog createDialog() throws Exception {
        DialogCreator<RenameDialog> dialogCreator = dialogCreator();
        EventQueue.invokeAndWait(dialogCreator);
        RenameDialog renameDialog = dialogCreator.getDialog();
        renameDialog.createCenterPanel();
        return renameDialog;
    }

    protected <T extends RenameDialog> DialogCreator<T> dialogCreator() {
        return new DialogCreator<T>() {
            protected T create() {
                return (T) new RenameDialog(projectMock, "");
            }
        };
    }
}