package org.codehaus.testdox.intellij;

import junitx.framework.ComparableAssert;

import org.intellij.openapi.testing.MockApplicationManager;

import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.psi.PsiMethod;

import org.codehaus.testdox.intellij.actions.RenameTestAction;
import org.codehaus.testdox.intellij.config.Configuration;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import javax.swing.*;

public class TestMethodTest extends MockObjectTestCase {

    static {
        MockApplicationManager.reset();
    }

    private final Mock mockEditorApi = mock(EditorApi.class);
    private final Mock mockPsiMethod = mock(PsiMethod.class);

    private EditorApi editorApiMock = (EditorApi) mockEditorApi.proxy();

    public void testUseTheEditorApiToJumpToTheCorrespondingMethod() {
        mockEditorApi.expects(once()).method("jumpToPsiElement").will(returnValue(true));
        assertTrue(new TestMethod((PsiMethod) mockPsiMethod.proxy(), editorApiMock, null).jumpToPsiElement());
    }

    public void testAlwaysEnablesTheRepresentationOfAnActionWhenAskedToUpdateIt() {
        Presentation presentation = new RenameTestAction().getTemplatePresentation();
        presentation.setEnabled(false);

        TestMethod testMethod = new TestMethod(null, null, null);
        testMethod.update(presentation);

        assertTrue("action representation should have been enabled", presentation.isEnabled());
    }

    public void testUsesItsDisplayStringToDefineNaturalOrderForComparison() {
        String methodName1 = "someMethod";
        String methodName2 = "someOtherMethod";

        Mock mockPsiMethod2 = mock(PsiMethod.class);
        SentenceManager sentenceManager = new SentenceManager(new Configuration());

        mockPsiMethod.expects(atLeastOnce()).method("getName").will(returnValue(methodName1));
        mockPsiMethod2.expects(atLeastOnce()).method("getName").will(returnValue(methodName2));

        TestMethod testMethod1 = new TestMethod((PsiMethod) mockPsiMethod.proxy(), editorApiMock, sentenceManager);
        TestMethod testMethod2 = new TestMethod((PsiMethod) mockPsiMethod2.proxy(), editorApiMock, sentenceManager);

        ComparableAssert.assertGreater(methodName2, testMethod1, testMethod2);
    }

    public void testReturnsZeroWhenComparedToAnObjectThatIsNotATestMethod() {
        assertEquals("comparison result", 0, new TestMethod(null, null, null).compareTo(new TestInterface(null, null, null, null)));
    }

    public void testUsesItsDisplayStringAsItsTextualRepresentation() {
        SentenceManager sentenceManager = new SentenceManager(new Configuration());
        TestMethod testMethod = new TestMethod((PsiMethod) mockPsiMethod.proxy(), editorApiMock, sentenceManager);

        mockPsiMethod.expects(atLeastOnce()).method("getName").will(returnValue("someMethod"));
        assertEquals(testMethod.displayString(), testMethod.toString());
    }
}
