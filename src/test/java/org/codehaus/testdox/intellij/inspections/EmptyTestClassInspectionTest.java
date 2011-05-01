package org.codehaus.testdox.intellij.inspections;

import com.intellij.psi.PsiMethod;
import static jedi.functional.Coercions.array;
import org.codehaus.testdox.intellij.TestMethod;

public class EmptyTestClassInspectionTest extends AbstractTestDoxInspectionTest {

    private final EmptyTestClassInspection inspection = new EmptyTestClassInspection();

    public void testDoesNotReportAProblemIfAGivenClassIsAnInnerClass() throws NoSuchFieldException {
        mockPsiClass.expects(exactly(2)).method("getName").will(returnValue("InnerBar"));
        mockPsiClass.expects(once()).method("getContainingFile").will(returnValue(mockPsiFile.proxy()));
        mockPsiFile.expects(once()).method("getName").will(returnValue("~/src/java/com/acme/foo/bar/FooBar.java"));

        assertNumberOfClassRelatedProblemsFoundByInspection(inspection, 0);
    }

    public void testDoesNotReportAProblemIfClassIsATestSuite() throws Exception {
        mockPsiClass.expects(exactly(2)).method("getName").will(returnValue("FooBar"));
        mockPsiClass.expects(once()).method("getContainingFile").will(returnValue(mockPsiFile.proxy()));
        mockPsiFile.expects(once()).method("getName").will(returnValue("~/src/java/com/acme/foo/bar/FooBar.java"));

        mockPsiClass.expects(once()).method("findMethodsByName").with(eq("suite"), eq(true))
            .will(returnValue(array((PsiMethod) mockPsiMethod.proxy())));

        assertNumberOfClassRelatedProblemsFoundByInspection(inspection, 0);
    }

    public void testDoesNotReportAProblemIfAGivenClassIsNotATestClass() throws NoSuchFieldException {
        mockPsiClass.expects(exactly(2)).method("getName").will(returnValue("FooBar"));
        mockPsiFile.expects(once()).method("getName").will(returnValue("~/src/java/com/acme/foo/bar/FooBar.java"));

        mockPsiClass.expects(once()).method("findMethodsByName").with(eq("suite"), eq(true)).will(returnValue(PsiMethod.EMPTY_ARRAY));

        setExpectationsForRetrievingTestDoxProjectComponent(mockPsiClass);

        mockTestDoxClass.expects(once()).method("isTestedClass").will(returnValue(true));

        assertNumberOfClassRelatedProblemsFoundByInspection(inspection, 0);
    }

    public void testDoesNotReportAProblemIfAGivenTestClassHasTestMethods() throws NoSuchFieldException {
        mockPsiClass.expects(exactly(2)).method("getName").will(returnValue("FooBar"));
        mockPsiFile.expects(once()).method("getName").will(returnValue("~/src/java/com/acme/foo/bar/FooBar.java"));

        mockPsiClass.expects(once()).method("findMethodsByName").with(eq("suite"), eq(true)).will(returnValue(PsiMethod.EMPTY_ARRAY));

        setExpectationsForRetrievingTestDoxProjectComponent(mockPsiClass);

        mockTestDoxClass.expects(once()).method("isTestedClass").will(returnValue(false));
        mockTestDoxClass.expects(once()).method("canNavigateToTestedClass").will(returnValue(true));
        mockTestDoxClass.expects(once()).method("testMethods").will(returnValue(new TestMethod[1]));

        assertNumberOfClassRelatedProblemsFoundByInspection(inspection, 0);
    }

    public void testReportsAProblemIfAGivenClassIsATestClassWithNoTests() throws NoSuchFieldException {
        mockPsiClass.expects(exactly(2)).method("getName").will(returnValue("FooBar"));
        mockPsiFile.expects(once()).method("getName").will(returnValue("~/src/java/com/acme/foo/bar/FooBar.java"));

        mockPsiClass.expects(once()).method("findMethodsByName").with(eq("suite"), eq(true)).will(returnValue(PsiMethod.EMPTY_ARRAY));

        setExpectationsForRetrievingTestDoxProjectComponent(mockPsiClass);

        mockTestDoxClass.expects(once()).method("isTestedClass").will(returnValue(false));
        mockTestDoxClass.expects(once()).method("canNavigateToTestedClass").will(returnValue(true));
        mockTestDoxClass.expects(once()).method("testMethods").will(returnValue(TestMethod.EMPTY_ARRAY()));

        mockPsiClass.expects(once()).method("getNameIdentifier");
        mockInspectionManager.expects(once()).method("createProblemDescriptor");

        assertNumberOfClassRelatedProblemsFoundByInspection(inspection, 1);
    }
}
