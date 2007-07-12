package org.codehaus.testdox.intellij.inspections;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiEmptyStatement;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiStatement;
import org.jmock.Mock;

import static jedi.functional.FunctionalPrimitives.array;

public class EmptyTestMethodInspectionTest extends AbstractTestDoxInspectionTest {

    private final EmptyTestMethodInspection inspection = new EmptyTestMethodInspection();

    public void testDoesNotReportAProblemIfAGivenMethodDoesNotBelongToATestClass() {
        setExpectationsForRetrievingTestDoxProjectComponent(mockPsiMethod);

        mockTestDoxClass.expects(once()).method("isTestedClass").will(returnValue(true));

        assertNumberOfMethodRelatedProblemsFoundByInspection(inspection, 0);
    }

    public void testDoesNotReportAProblemIfAGivenMethodInATestClassIsNotATestMethod() {
        setExpectationsForRetrievingTestDoxProjectComponent(mockPsiMethod);

        mockTestDoxClass.expects(once()).method("isTestedClass").will(returnValue(false));
        mockTestDoxClass.expects(once()).method("canNavigateToTestedClass").will(returnValue(true));
        mockEditorApi.expects(once()).method("isTestMethod").with(isA(PsiMethod.class)).will(returnValue(false));

        assertNumberOfMethodRelatedProblemsFoundByInspection(inspection, 0);
    }

    public void testDoesNotReportAProblemIfAGivenMethodIsATestMethodWithANullBody() {
        setExpectationsForRetrievingTestDoxProjectComponent(mockPsiMethod);
        setExpectationsForDetectingATestMethodInATestClass();

        mockPsiMethod.expects(once()).method("getBody").will(returnValue(null));

        assertNumberOfMethodRelatedProblemsFoundByInspection(inspection, 0);
    }

    public void testDoesNotReportAProblemIfAGivenMethodIsATestMethodThatHasAtLeastOneNonEmptyStatement() {
        setExpectationsForRetrievingTestDoxProjectComponent(mockPsiMethod);
        setExpectationsForDetectingATestMethodInATestClass();

        Mock mockPsiCodeBlock = mock(PsiCodeBlock.class);
        mockPsiMethod.expects(once()).method("getBody").will(returnValue(mockPsiCodeBlock.proxy()));

        PsiStatement[] statements = array(
                (PsiStatement) mock(PsiEmptyStatement.class).proxy(),
                (PsiStatement) mock(PsiExpressionStatement.class).proxy(),
                (PsiStatement) mock(PsiEmptyStatement.class).proxy()
        );

        mockPsiCodeBlock.expects(once()).method("getStatements").will(returnValue(statements));

        assertNumberOfMethodRelatedProblemsFoundByInspection(inspection, 0);
    }

    public void testReportsAProblemIfAGivenMethodIsAnEmptyTestMethodInATestClass() {
        setExpectationsForRetrievingTestDoxProjectComponent(mockPsiMethod);
        setExpectationsForDetectingATestMethodInATestClass();

        Mock mockPsiCodeBlock = mock(PsiCodeBlock.class);
        mockPsiMethod.expects(once()).method("getBody").will(returnValue(mockPsiCodeBlock.proxy()));
        mockPsiCodeBlock.expects(once()).method("getStatements").will(returnValue(PsiStatement.EMPTY_ARRAY));

        mockPsiMethod.expects(once()).method("getNameIdentifier");
        mockInspectionManager.expects(once()).method("createProblemDescriptor");

        assertNumberOfMethodRelatedProblemsFoundByInspection(inspection, 1);
    }

    public void testReportsAProblemIfAGivenMethodIsATestMethodThatHasOnlyEmptyStatements() {
        setExpectationsForRetrievingTestDoxProjectComponent(mockPsiMethod);
        setExpectationsForDetectingATestMethodInATestClass();

        Mock mockPsiCodeBlock = mock(PsiCodeBlock.class);
        mockPsiMethod.expects(once()).method("getBody").will(returnValue(mockPsiCodeBlock.proxy()));

        PsiStatement[] statements = array(
                (PsiStatement) mock(PsiEmptyStatement.class).proxy(),
                (PsiStatement) mock(PsiEmptyStatement.class).proxy(),
                (PsiStatement) mock(PsiEmptyStatement.class).proxy()
        );
        mockPsiCodeBlock.expects(once()).method("getStatements").will(returnValue(statements));

        mockPsiMethod.expects(once()).method("getNameIdentifier");
        mockInspectionManager.expects(once()).method("createProblemDescriptor");

        assertNumberOfMethodRelatedProblemsFoundByInspection(inspection, 1);
    }

    private void setExpectationsForDetectingATestMethodInATestClass() {
        mockTestDoxClass.expects(once()).method("isTestedClass").will(returnValue(false));
        mockTestDoxClass.expects(once()).method("canNavigateToTestedClass").will(returnValue(true));
        mockEditorApi.expects(once()).method("isTestMethod").with(isA(PsiMethod.class)).will(returnValue(true));
    }
}
