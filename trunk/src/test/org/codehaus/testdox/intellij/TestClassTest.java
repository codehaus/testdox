package org.codehaus.testdox.intellij;

import junitx.framework.Assert;
import junitx.framework.ComparableAssert;

import org.intellij.openapi.testing.MockApplicationManager;

import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.psi.PsiClass;

import org.codehaus.testdox.intellij.actions.RenameTestAction;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class TestClassTest extends MockObjectTestCase {

    static {
        MockApplicationManager.reset();
    }

    private final Mock mockPsiClass = mock(PsiClass.class);
    private final Mock mockEditorApi = mock(EditorApi.class);
    private final Mock mockNameResolver = mock(NameResolver.class);

    private final PsiClass psiClassMock = (PsiClass) mockPsiClass.proxy();
    private final EditorApi editorApiMock = (EditorApi) mockEditorApi.proxy();
    private final NameResolver nameResolverMock = (NameResolver) mockNameResolver.proxy();

    public void testReportsThatANullSourceClassIsNotATest() throws Exception {
        String className = "com.acme.SomeClass";
        TestClass testClass = new TestClass(className, null, editorApiMock, nameResolverMock);
        assertFalse(className + " is not a test class", testClass.isTestClass());
    }

    public void testShowsRealClassNameWithColonSuffixAsDisplayStringForAClass() throws Exception {
        String className = "foo";
        String realClassName = "realClass";

        TestClass testClass = new TestClass(className, psiClassMock, editorApiMock, nameResolverMock);
        mockNameResolver.expects(once()).method("getRealClassNameForDisplay").with(eq(className)).will(returnValue(realClassName));

        assertEquals("<b>" + realClassName + ":</b>", testClass.displayString());
    }

    public void testShowsRealClassNameWithColonSuffixAsDisplayStringForAnInterface() throws Exception {
        String className = "foo";
        String realClassName = "realClass";

        mockNameResolver.expects(once()).method("getRealClassNameForDisplay").with(eq(className)).will(returnValue(realClassName));

        TestInterface testInterface = new TestInterface(className, psiClassMock, editorApiMock, nameResolverMock);
        assertEquals("<i><b>" + realClassName + ":</b></i>", testInterface.displayString());
    }

    public void testCanBeIdentifiedAsATestClassIfItsFullyQualifiedClassNameResolvesAsSuch() {
        String className = "com.acme.FooTest";
        TestClass testClass = new TestClass(className, psiClassMock, editorApiMock, nameResolverMock);

        mockPsiClass.expects(once()).method("getName").will(returnValue(className));
        mockNameResolver.expects(once()).method("isTestClass").with(eq(className)).will(returnValue(true));

        assertTrue("should be identified as a test class when its name resolves as such", testClass.isTestClass());
    }

    public void testReturnsAClassIconIfTheUnderlyingClassBelongsToTheCurrentProject() {
        TestClass testClass = new TestClass("", psiClassMock, editorApiMock, null);
        assertSame("class icon for project class", IconHelper.getIcon(IconHelper.CLASS_ICON), testClass.icon());
    }

    public void testReturnsALockedClassIconIfTheUnderlyingClassDoesNotBelongToTheCurrentProject() {
        TestClass testClass = new TestClass("", null, editorApiMock, null);
        assertSame("locked class icon for non-project class", IconHelper.getLockedIcon(IconHelper.CLASS_ICON), testClass.icon());
    }

    public void testReturnsAnInterfaceIconIfTheUnderlyingInterfaceBelongsToTheCurrentProject() {
        TestInterface testInterface = new TestInterface("", psiClassMock, editorApiMock, null);
        assertSame("interface icon for project interface", IconHelper.getIcon(IconHelper.INTERFACE_ICON), testInterface.icon());
    }

    public void testReturnsALockedInterfaceIconIfTheUnderlyingInterfaceDoesNotBelongToTheCurrentProject() {
        TestInterface testInterface = new TestInterface("", null, editorApiMock, null);
        assertSame("locked interface icon for non-project interface", IconHelper.getLockedIcon(IconHelper.INTERFACE_ICON), testInterface.icon());
    }

    public void testUsesItsDisplayStringToDefineNaturalOrderForComparison() {
        String className1 = "com.acme.SomeClass";
        String className2 = "com.acme.SomeOtherClass";

        TestClass testClass1 = new TestClass(className1, null, editorApiMock, nameResolverMock);
        TestClass testClass2 = new TestClass(className2, null, editorApiMock, nameResolverMock);

        mockNameResolver.expects(atLeastOnce()).method("getRealClassNameForDisplay").with(eq(className1)).will(returnValue(className1));
        mockNameResolver.expects(atLeastOnce()).method("getRealClassNameForDisplay").with(eq(className2)).will(returnValue(className2));

        ComparableAssert.assertGreater("second test class", testClass1, testClass2);
    }

    public void testWillAlwaysBeGreaterThanATestMethod() {
        String className = "com.acme.SomeClass";
        TestClass testClass = new TestClass(className, null, editorApiMock, nameResolverMock);
        TestMethod testMethod = Mocks.createTestMethod("aMethod");

        mockNameResolver.stubs().method("getRealClassNameForDisplay").with(eq(className)).will(returnValue(className));

        ComparableAssert.assertLesser("test method", testClass, testMethod);
    }

    public void testUsesItsDisplayStringToDefineEquality() {
        String className1 = "com.acme.SomeClass";
        String className2 = "com.acme.SomeOtherClass";

        TestClass testClass1 = new TestClass(className1, null, editorApiMock, nameResolverMock);
        TestClass testClass2 = new TestClass(className2, null, editorApiMock, nameResolverMock);

        mockNameResolver.expects(atLeastOnce()).method("getRealClassNameForDisplay").with(eq(className1)).will(returnValue(className1));
        mockNameResolver.expects(atLeastOnce()).method("getRealClassNameForDisplay").with(eq(className2)).will(returnValue(className2));

        Assert.assertNotEquals(className1 + " should not be equal to " + className1 + ',', testClass1, testClass2);
    }

    public void testUsesItsDisplayStringAsItsTextualRepresentation() {
        String className = "com.acme.SomeClass";
        TestClass testClass = new TestClass(className, null, editorApiMock, nameResolverMock);

        mockNameResolver.expects(atLeastOnce()).method("getRealClassNameForDisplay").with(eq(className)).will(returnValue(className));

        assertEquals(testClass.displayString(), testClass.toString());
    }

    public void testAlwaysEnablesTheRepresentationOfAnActionWhenAskedToUpdateIt() {
        Presentation presentation = new RenameTestAction().getTemplatePresentation();
        presentation.setEnabled(false);

        TestClass testClass = new TestClass(null, null, null, null);
        testClass.update(presentation);

        assertTrue("action representation should have been enabled", presentation.isEnabled());
    }

    public void testDelegatesTheRenamingOfTheUnderlyingTestedClassToTheEditorApi() {
        PsiClass testedClass = (PsiClass) mock(PsiClass.class, "testedPsiClass").proxy();
        TestClass testClass = createTestClassAndSetExpectationsForRetrievingAssociatedTestedClass(testedClass);

        mockEditorApi.expects(once()).method("rename").with(same(testedClass));

        testClass.rename(null);
    }

    public void testDelegatesTheDeletionOfTheUnderlyingTestedClassToTheEditorApi() {
        PsiClass testedClass = (PsiClass) mock(PsiClass.class, "testedPsiClass").proxy();
        TestClass testClass = createTestClassAndSetExpectationsForRetrievingAssociatedTestedClass(testedClass);

        mockEditorApi.expects(once()).method("delete").with(same(testedClass));

        testClass.delete(null);
    }

    private TestClass createTestClassAndSetExpectationsForRetrievingAssociatedTestedClass(PsiClass testedClass) {
        String testedClassName = "com.acme.SomeClass";
        String className = testedClassName + "Test";

        TestClass testClass = new TestClass(className, psiClassMock, editorApiMock, nameResolverMock);

        mockPsiClass.expects(once()).method("getQualifiedName").will(returnValue(className));
        mockNameResolver.expects(once()).method("getRealClassName").with(same(className)).will(returnValue(testedClassName));
        mockEditorApi.expects(once()).method("getPsiClass").with(same(testedClassName)).will(returnValue(testedClass));

        return testClass;
    }
}
