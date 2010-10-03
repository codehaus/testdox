package org.intellij.openapi.testing;

import org.codehaus.testdox.intellij.EditorApiMockBuilder;
import org.jmock.cglib.MockObjectTestCase;

public class MockObjectFactory {

    private final MockObjectTestCase testCase;

    public MockObjectFactory(MockObjectTestCase testCase) {
        this.testCase = testCase;
    }

    public EditorApiMockBuilder editorApi() {
        return new EditorApiMockBuilder(testCase);
    }
}
