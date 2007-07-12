package org.codehaus.testdox.intellij;

import junit.framework.TestCase;

public class TokensTest extends TestCase {

    public void testCanBeUsedInRegularExpressions() throws Exception {
        String templateSuffix = "Foo";
        try {
            String template = PackageManager.PACKAGE_TOKEN + templateSuffix;
            template.split(PackageManager.PACKAGE_TOKEN);
            template = TemplateNameResolver.NAME_TOKEN + templateSuffix;
            template.split(TemplateNameResolver.NAME_TOKEN);
        } catch (Exception e) {
            fail();
        }
    }
}
