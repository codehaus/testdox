package org.codehaus.testdox.intellij.inspections;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.codehaus.testdox.intellij.*;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public class AbstractTestDoxInspectionTest extends MockObjectTestCase {

    static {
        MockApplicationManager.reset();
    }

    protected final Mock mockPsiClass = mock(PsiClass.class);
    protected final Mock mockPsiMethod = mock(PsiMethod.class);
    protected final Mock mockPsiField = mock(PsiField.class);
    protected final Mock mockInspectionManager = mock(InspectionManager.class);
    protected final Mock mockProject = mock(Project.class);
    protected final Mock mockTestDoxController = mock(TestDoxController.class);
    protected final Mock mockEditorApi = mock(EditorApi.class);
    protected final Mock mockTestDoxFileFactory = Mocks.createAndRegisterTestDoxFileFactoryMock(this);
    protected final Mock mockPsiFile = mock(PsiFile.class);
    protected final Mock mockTestDoxClass = Mocks.createAndRegisterTestDoxClassMock(this);

    private final LocalInspectionTool inspectionTool = new AbstractTestDoxInspection() {
        public String getDisplayName() {
            return "N/A";
        }
    };

    protected void setUp() {
        ApplicationInfo applicationInfo = Stubs.createApplicationInfo(this);
        MockApplicationManager.getMockApplication().registerComponent(ApplicationInfo.class, applicationInfo);
    }

    protected void tearDown() {
        MockApplicationManager.getMockApplication().removeComponent(ApplicationInfo.class);
    }

    public void testProvidesAGroupName() {
        assertEquals("TestDox Issues", inspectionTool.getGroupDisplayName());
    }

    public void testRemovesTheInspectionSuffixToMakeUpTheShortNameOfAnInspection() {
        assertEquals("AbstractTestDox", inspectionTool.getShortName());
    }

    protected void setExpectationsForRetrievingTestDoxProjectComponent(Mock mockPsiElement) {
        Mock mockTestDoxProjectComponent = Mocks.createAndRegisterTestDoxProjectComponentMock(this);
        TestDoxProjectComponent.setInstance((Project) mockProject.proxy(), (TestDoxProjectComponent) mockTestDoxProjectComponent.proxy());

        mockInspectionManager.stubs().method("getProject").will(returnValue(mockProject.proxy()));
        mockTestDoxProjectComponent.stubs().method("getController").will(returnValue(mockTestDoxController.proxy()));
        mockTestDoxController.stubs().method("getEditorApi").will(returnValue(mockEditorApi.proxy()));
        mockTestDoxController.stubs().method("getTestDoxFileFactory").will(returnValue(mockTestDoxFileFactory.proxy()));

        mockPsiElement.expects(atLeastOnce()).method("getContainingFile").will(returnValue(mockPsiFile.proxy()));
        mockPsiFile.expects(once()).method("getVirtualFile");
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").with(NULL).will(returnValue(mockTestDoxClass.proxy()));
    }

    protected void assertNumberOfClassRelatedProblemsFoundByInspection(BaseJavaLocalInspectionTool inspectionTool, int problemCount) {
        ProblemDescriptor[] problems = inspectionTool.checkClass((PsiClass) mockPsiClass.proxy(), (InspectionManager) mockInspectionManager.proxy(), true);
        assertNotNull(problems);
        assertEquals(problemCount, problems.length);
    }

    protected void assertNumberOfMethodRelatedProblemsFoundByInspection(BaseJavaLocalInspectionTool inspectionTool, int problemCount) {
        ProblemDescriptor[] problems = inspectionTool.checkMethod((PsiMethod) mockPsiMethod.proxy(), (InspectionManager) mockInspectionManager.proxy(), true);
        assertNotNull(problems);
        assertEquals(problemCount, problems.length);
    }
}
