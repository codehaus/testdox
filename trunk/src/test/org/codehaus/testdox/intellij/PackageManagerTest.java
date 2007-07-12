package org.codehaus.testdox.intellij;

import junit.framework.TestCase;

public class PackageManagerTest extends TestCase {

    public void testReturnsEmptyPackageForNullTemplate() throws Exception {
        PackageManager manager = new PackageManager("com.foo");
        assertEquals("", manager.getPackage(null));
    }

    public void testReturnEmptyPackageForEmptyTemplate() throws Exception {
        PackageManager manager = new PackageManager("com.foo");
        assertEquals("", manager.getPackage(""));
    }

    public void testReturnUnmodifiedPackageForTemplateThatIsJustThePackageKeyword() throws Exception {
        String sourcePackage = "com.acme.foo.bar";
        PackageManager manager = new PackageManager(sourcePackage);
        assertEquals(sourcePackage, manager.getPackage(PackageManager.PACKAGE_TOKEN));
    }

    public void testPopsOnePackageLevelForOnePopTemplate() throws Exception {
        PackageManager manager = new PackageManager("com.acme.foo.bar");
        assertEquals("com.acme.foo", manager.getPackage(PackageManager.PACKAGE_TOKEN + PackageManager.POP_TOKEN));
    }

    public void testPopsOnePackageLevelForEachPopTemplateFound() throws Exception {
        PackageManager manager = new PackageManager("com.acme.foo.bar");
        assertEquals("com.acme", manager.getPackage(PackageManager.PACKAGE_TOKEN + PackageManager.POP_TOKEN + PackageManager.POP_TOKEN));
    }

    public void testIgnoresPackageTokenIfSourcePackageIsEmpty() throws Exception {
        PackageManager manager = new PackageManager("");
        assertEquals("", manager.getPackage(PackageManager.PACKAGE_TOKEN + PackageManager.POP_TOKEN + PackageManager.POP_TOKEN));
    }

    public void testReturnsEmptyPackageIfThereAreMorePopTemplatesThanPackageLevels() throws Exception {
        PackageManager manager = new PackageManager("com.acme.foo.bar");
        assertEquals("", manager.getPackage(PackageManager.PACKAGE_TOKEN + PackageManager.POP_TOKEN + PackageManager.POP_TOKEN + PackageManager.POP_TOKEN + PackageManager.POP_TOKEN + PackageManager.POP_TOKEN));
    }

    public void testAppendsAdditionalPackagesToResolvedSourcePackage() throws Exception {
        PackageManager manager = new PackageManager("com.acme.foo.bar");
        assertEquals("com.acme.foo.test", manager.getPackage(PackageManager.PACKAGE_TOKEN + PackageManager.POP_TOKEN + "/test"));
    }

    public void testAppendsAdditionalPackagesAfterMultiplePops() throws Exception {
        PackageManager manager = new PackageManager("com.acme.foo.bar");
        assertEquals("com.acme.test", manager.getPackage(PackageManager.PACKAGE_TOKEN + PackageManager.POP_TOKEN + PackageManager.POP_TOKEN + "/test"));
    }
}
