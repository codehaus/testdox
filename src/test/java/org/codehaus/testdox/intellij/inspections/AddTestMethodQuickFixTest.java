package org.codehaus.testdox.intellij.inspections;

import org.intellij.openapi.testing.MockApplicationManager;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.application.ApplicationInfo;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import org.codehaus.testdox.intellij.Mocks;
import org.codehaus.testdox.intellij.TestDoxController;
import org.codehaus.testdox.intellij.TestDoxProjectComponent;
import org.codehaus.testdox.intellij.Stubs;

public class AddTestMethodQuickFixTest extends MockObjectTestCase {

    private final AddTestMethodQuickFix quickFix = new AddTestMethodQuickFix();

    protected void setUp() {
        ApplicationInfo applicationInfo = Stubs.createApplicationInfo(this);
        MockApplicationManager.getMockApplication().registerComponent(ApplicationInfo.class, applicationInfo);
    }

    protected void tearDown() {
        MockApplicationManager.getMockApplication().removeComponent(ApplicationInfo.class);
    }

    public void testDefinesAName() {
        assertEquals("name", "Add Test", quickFix.getName());
    }

    public void testSuppliesAFamilyNameSoThatItCanBeCategorisedUnderAFamilyOfRelatedQuickFixes() {
        assertEquals("family name", "TestDox Quick Fixes", quickFix.getFamilyName());
    }

    public void testAddsATestMethodUsingATestdoxController() {
        Project projectMock = (Project) mock(Project.class).proxy();

        Mock mockProblemDescriptor = mock(ProblemDescriptor.class);
        Mock mockTestDoxProjectComponent = Mocks.createAndRegisterTestDoxProjectComponentMock(this);
        TestDoxProjectComponent.setInstance(projectMock, (TestDoxProjectComponent) mockTestDoxProjectComponent.proxy());

        Mock mockTestDoxController = mock(TestDoxController.class);
        mockTestDoxProjectComponent.expects(once()).method("getController").will(returnValue(mockTestDoxController.proxy()));
        mockTestDoxController.expects(once()).method("addTest");

        quickFix.applyFix(projectMock, (ProblemDescriptor) mockProblemDescriptor.proxy());
    }
}
