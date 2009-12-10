package org.codehaus.testdox.intellij;

import javax.swing.Icon;

import junit.framework.TestCase;

import org.intellij.openapi.testing.MockApplicationManager;

import com.intellij.openapi.actionSystem.Presentation;

import org.codehaus.testdox.intellij.actions.RenameTestAction;

public class AbstractTestElementTest extends TestCase {

    private AbstractTestElement abstractTestElement = new AbstractTestElement() {
        public String displayString() {
            throw new UnsupportedOperationException();
        }

        public Icon icon() {
            throw new UnsupportedOperationException();
        }
    };

    public void testIsAssociatedToANullPsiElementByDefault() {
        MockApplicationManager.reset();
        assertSame(NullPsiElement.INSTANCE, abstractTestElement.psiElement());
    }

    public void testDoesNotJumpToPsiElementByDefault() {
        assertFalse(abstractTestElement.jumpToPsiElement());
    }

    public void testUsesReferenceEqualityToDefineTheDefaultNaturalOrder() {
        assertEquals(-1, abstractTestElement.compareTo(new AbstractTestElement() {
                         public String displayString() {
                             throw new UnsupportedOperationException();
                         }

                         public Icon icon() {
                             throw new UnsupportedOperationException();
                         }
                     }));

        assertEquals(0, abstractTestElement.compareTo(abstractTestElement));
    }

    public void testAlwaysDisablesTheRepresentationOfAnActionWhenAskedToUpdateIt() {
        Presentation presentation = new RenameTestAction().getTemplatePresentation();
        presentation.setEnabled(true);

        abstractTestElement.updatePresentation(presentation);
        assertFalse("action representation should have been disabled", presentation.isEnabled());
    }

    public void testDoesNothingWhenAskedToRenameTheUnderlyingTestedClass() {
        abstractTestElement.rename(null);
    }

    public void testDoesNothingWhenAskedToDeleteTheUnderlyingTestedClass() {
        abstractTestElement.delete(null);
    }
}
