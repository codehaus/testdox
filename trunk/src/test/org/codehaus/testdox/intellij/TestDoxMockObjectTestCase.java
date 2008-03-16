package org.codehaus.testdox.intellij;

import org.intellij.openapi.testing.MockObjectFactory;
import org.intellij.openapi.testing.RealObjectBuilder;
import org.jmock.cglib.MockObjectTestCase;

public abstract class TestDoxMockObjectTestCase extends MockObjectTestCase {

    protected MockObjectFactory mock() {
        return new MockObjectFactory(this);
    }

    protected RealObjectBuilder real() {
        return new RealObjectBuilder();
    }
}
