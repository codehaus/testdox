package org.codehaus.testdox.intellij;

import junit.framework.TestCase;

import org.codehaus.testdox.intellij.config.Configuration;

public class TemplateNameResolverTest extends TestCase {

    private final Configuration configuration = new Configuration();
    private TemplateNameResolver resolver;

    protected void tearDown() throws Exception {
        super.tearDown();
        configuration.removePropertyChangeListener(resolver);
    }

    public void testSubstitutesNameTemplateForClassNameWithNoAdditions() throws Exception {
        String template = TemplateNameResolver.NAME_TOKEN;
        String className = "MyClass";

        assertEquals(className, createTemplateNameResolver(template).getTestClassName(className));
    }

    public void testGivesSuppliedClassNameForTestClassNameIfTemplateMatches() throws Exception {
        String template = TemplateNameResolver.NAME_TOKEN + "Test";
        String className = "MyClassTest";

        assertEquals(className, createTemplateNameResolver(template).getTestClassName(className));
    }

    public void testGivesCorrectConcatenatedRealClassNameForTestClassName() throws Exception {
        String template = "TestThis" + TemplateNameResolver.NAME_TOKEN + "Test";
        String className = "MyClass";

        assertEquals("TestThis" + className + "Test", createTemplateNameResolver(template).getTestClassName(className));
    }

    public void testLeavesNonTokenSuffixOfTemplateInOutputTestClassName() throws Exception {
        String template = TemplateNameResolver.NAME_TOKEN + "Test";
        String className = "MyClass";

        assertEquals(className + "Test", createTemplateNameResolver(template).getTestClassName(className));
    }

    public void testLeavesNonTokenPrefixOfTemplateInOutputTestClassName() throws Exception {
        String template = "Test" + TemplateNameResolver.NAME_TOKEN;
        String className = "MyClass";

        assertEquals("Test" + className, createTemplateNameResolver(template).getTestClassName(className));
    }

    public void testGivesSuppliedClassNameForTestNameIfTemplateDoesNotMatch() throws Exception {
        String template = "Test" + TemplateNameResolver.NAME_TOKEN + "YouTestyTestThing";
        String className = "TestMyClass";

        assertEquals(className, createTemplateNameResolver(template).getRealClassName(className));
    }

    public void testTrimsTestPrefixAndSuffixFromTestClassNameToGetRealClassName() throws Exception {
        String template = "Test" + TemplateNameResolver.NAME_TOKEN + "YouTestyTestThing";
        String className = "TestMyClassYouTestyTestThing";

        assertEquals("MyClass", createTemplateNameResolver(template).getRealClassName(className));
    }

    public void testDetectsRealClassNameForNameThatDoesNotMatchTemplate() throws Exception {
        String template = TemplateNameResolver.NAME_TOKEN + "Test";
        String className = "MyClass";

        assertTrue(createTemplateNameResolver(template).isRealClass(className));
    }

    public void testGivesRealClassNameWhenTestClassIsUnderPackageAndTemplateHasPrefixOnly() {
        String template = "Test" + TemplateNameResolver.NAME_TOKEN;
        String className = "com.acme.foo.NonexistentClass";
        String testClassName = "com.acme.foo.TestNonexistentClass";

        assertEquals(className, createTemplateNameResolver(template).getRealClassName(testClassName));
    }

    public void testGivesCorrectTestClassNameWhenRealClassIsUnderPackageAndTemplateHasPrefixOnly() {
        String template = "Test" + TemplateNameResolver.NAME_TOKEN;
        String className = "com.acme.foo.NonexistentClass";
        String testClassName = "com.acme.foo.TestNonexistentClass";

        assertEquals(testClassName, createTemplateNameResolver(template).getTestClassName(className));
    }

    public void testGivesFullyQualifiedRealClassNameForDisplayWhenTestdoxIsConfiguredToDoSoAndTestClassIsUnderPackageAndTemplateHasPrefixOnly() {
        String template = "Test" + TemplateNameResolver.NAME_TOKEN;
        String className = "com.acme.foo.NonexistentClass";
        String testClassName = "com.acme.foo.TestNonexistentClass";

        configuration.setShowFullyQualifiedClassName(true);
        assertEquals(className, createTemplateNameResolver(template).getRealClassNameForDisplay(testClassName));
    }

    public void testGivesRealClassNameWithoutPackagePrefixForDisplayWhenTestdoxIsConfiguredToDoSoAndTestClassIsUnderPackageAndTemplateHasPrefixOnly() {
        String template = "Test" + TemplateNameResolver.NAME_TOKEN;
        String className = "NonexistentClass";
        String testClassName = "com.acme.foo.TestNonexistentClass";

        configuration.setShowFullyQualifiedClassName(false);
        assertEquals(className, createTemplateNameResolver(template).getRealClassNameForDisplay(testClassName));
    }

    public void testDetectsDoesNotDetectRealClassNameForNameThatMatchesTemplate() throws Exception {
        String template = TemplateNameResolver.NAME_TOKEN + "Test";
        String className = "MyClassTest";

        assertFalse(createTemplateNameResolver(template).isRealClass(className));
    }

    public void testDetectsTestClassNameForNameThatDoesMatchTemplate() throws Exception {
        String template = "A" + TemplateNameResolver.NAME_TOKEN + "Test";
        String className = "AMyClassTest";

        assertTrue(createTemplateNameResolver(template).isTestClass(className));
    }

    public void testDetectsDoesNotDetectTestClassNameForNameThatDoesNotMatchTemplate() throws Exception {
        String template = "A" + TemplateNameResolver.NAME_TOKEN + "Test";
        String className = "MyClassTes";
        assertFalse(createTemplateNameResolver(template).isTestClass(className));
    }

    public void testDoesNotAllowNullAsARealClass() throws Exception {
        assertFalse(createTemplateNameResolver(TemplateNameResolver.NAME_TOKEN + "Test").isRealClass(null));
    }

    public void testDoesNotAllowNullAsATestClass() throws Exception {
        assertFalse(createTemplateNameResolver(TemplateNameResolver.NAME_TOKEN + "Test").isTestClass(null));
    }

    public void testListensToBeanPropertyChangesAndUpdatesWhenChangeOccurs() throws Exception {
        String template = TemplateNameResolver.NAME_TOKEN + "Test";
        String testName = "MyClassTest";
        String otherTestName = "MyClassTestCase";

        TemplateNameResolver nameResolver = createTemplateNameResolver(template);
        assertTrue(nameResolver.isTestClass(testName));
        assertFalse(nameResolver.isTestClass(otherTestName));

        configuration.setTestNameTemplate(TemplateNameResolver.NAME_TOKEN + "TestCase");
        assertFalse(nameResolver.isTestClass(testName));
        assertTrue(nameResolver.isTestClass(otherTestName));
    }

    private TemplateNameResolver createTemplateNameResolver(String template) {
        configuration.setTestNameTemplate(template);
        resolver = new TemplateNameResolver(configuration);
        return resolver;
    }
}
