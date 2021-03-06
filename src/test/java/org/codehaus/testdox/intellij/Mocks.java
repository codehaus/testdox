package org.codehaus.testdox.intellij;

import org.codehaus.testdox.intellij.config.Configuration;
import org.codehaus.testdox.intellij.ui.TestDoxTableModel;
import org.intellij.openapi.testing.MockVirtualFile;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import javax.swing.*;

public final class Mocks {

    /**
     * Sole constructor hidden to enfore non-instantiability.
     */
    private Mocks() {
    }

    // Factory methods for obtaining verified mocks --------------------------------------------------------------------

    public static Mock createAndRegisterTestLookupMock(MockObjectTestCase testCase) {
        return testCase.mock(MockableTestLookup.class);
    }

    public static Mock createAndRegisterTestDoxProjectComponentMock(MockObjectTestCase testCase) {
        return testCase.mock(MockableTestDoxProjectComponent.class);
    }

    public static Mock createAndRegisterTestDoxTableModelMock(MockObjectTestCase testCase) {
        return testCase.mock(MockableTestDoxTableModel.class);
    }

    public static Mock createAndRegisterVirtualFileMock(MockObjectTestCase testCase) {
        return testCase.mock(MockableVirtualFile.class);
    }

    public static Mock createAndRegisterTestDoxFileFactoryMock(MockObjectTestCase testCase) {
        return testCase.mock(MockableTestDoxFileFactory.class);
    }

    public static Mock createAndRegisterTestDoxFileMock(MockObjectTestCase testCase) {
        return testCase.mock(MockableTestDoxFile.class);
    }

    public static Mock createAndRegisterTestDoxClassMock(MockObjectTestCase testCase) {
        return testCase.mock(MockableTestDoxClass.class);
    }

    public static Mock createAndRegisterTestClassMock(MockObjectTestCase testCase) {
        return testCase.mock(MockableTestClass.class);
    }

    public static Mock createAndRegisterTestMethodMock(MockObjectTestCase testCase) {
        return testCase.mock(MockableTestMethod.class);
    }

    public static Mock createAndRegisterSentenceTranslatorMock(MockObjectTestCase testCase) {
        return testCase.mock(MockableSentenceTranslator.class);
    }

    // Factory methods -------------------------------------------------------------------------------------------------

    public static TestClass createTestClass() {
        return new MockableTestClass();
    }

    public static TestMethod createTestMethod(String methodName) {
        return new MockableTestMethod(methodName);
    }

    // Mockable classes ------------------------------------------------------------------------------------------------

    public static class MockableTestLookup extends TestLookup {

        public MockableTestLookup() {
            super(null, null);
        }
    }

    public static class MockableTestDoxProjectComponent extends TestDoxProjectComponent {

        public MockableTestDoxProjectComponent() {
            super(null);
        }
    }

    public static class MockableTestDoxTableModel extends TestDoxTableModel {

        public MockableTestDoxTableModel() {
            super(null);
        }
    }

    public static class MockableVirtualFile extends MockVirtualFile {

        public MockableVirtualFile() {
            super("", false);
        }
    }

    public static class MockableTestDoxFileFactory extends TestDoxFileFactory {

        public MockableTestDoxFileFactory() {
            super(null, null, null);
        }
    }

    public static class MockableTestDoxFile extends TestDoxFile {

        public MockableTestDoxFile() {
            super(null, null, null, null, null);
        }

        public void updateModel(TestDoxTableModel model) {
        }
    }

    public static class MockableTestDoxClass extends TestDoxClass {

        public MockableTestDoxClass() {
            super(null, null, true, null, null, null);
        }

        public void updateModel(TestDoxTableModel model) {
        }
    }

    public static class MockableTestClass extends TestClass {

        public MockableTestClass() {
            super(null, null, null, null);
        }

        public String displayString() {
            return null;
        }

        public Icon icon() {
            return null;
        }
    }

    public static class MockableTestMethod extends TestMethod {

        private String name;

        public MockableTestMethod() {
            super(null, null, null);
        }

        private MockableTestMethod(String name) {
            super(null, null, new SentenceTranslator(new Configuration()));
            this.name = name;
        }

        public String methodName() {
            return name;
        }
    }

    public static class MockableSentenceTranslator extends SentenceTranslator {

        public MockableSentenceTranslator() {
            super(null);
        }
    }
}
