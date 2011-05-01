package org.codehaus.testdox.intellij;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public class TestDoxClassTest extends MockObjectTestCase {

    private final Mock mockTestClass = Mocks.createAndRegisterTestClassMock(this);

    public void testCanBeUnitTested() {
        TestDoxClass testDoxClass = new TestDoxClass(null, "com.acme.Foo", true, Mocks.createTestClass(), null, TestMethod.EMPTY_ARRAY());
        assertTrue("classes can be unit tested", testDoxClass.canBeUnitTested());
    }

    public void testCanNavigateToTestClassIfOneHasBeenIdentifiedAsSuch() {
        TestDoxClass testDoxClass = new TestDoxClass(null, "com.acme.Foo", true, (TestClass) mockTestClass.proxy(), null, TestMethod.EMPTY_ARRAY());
        mockTestClass.expects(once()).method("isTestClass").will(returnValue(true));
        assertTrue(testDoxClass.canNavigateToTestClass());
    }

    public void testCanNavigateToTestedClassIfOneHasBeenIdentifiedAsSuch() {
        TestDoxClass testDoxClass = new TestDoxClass(null, "com.acme.FooTest", true, null, (TestClass) mockTestClass.proxy(), TestMethod.EMPTY_ARRAY());
        mockTestClass.expects(once()).method("isTestClass").will(returnValue(false));
        assertTrue(testDoxClass.canNavigateToTestedClass());
    }
}
