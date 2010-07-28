package org.codehaus.testdox.intellij;

public class PackageManager {

    public static final String POP_TOKEN = "/..";
    public static final String PACKAGE_TOKEN = "<package>";

    private final String sourcePackage;

    public PackageManager(String sourcePackage) {
        this.sourcePackage = sourcePackage;
    }

    public String getPackage(String packageTemplate) {
        if (packageTemplate == null || packageTemplate.length() == 0) {
            return "";
        }
        if (packageTemplate.equalsIgnoreCase(PACKAGE_TOKEN)) {
            return sourcePackage;
        }
        String resolvedPackage = sourcePackage;
        if (includesPackageToken(packageTemplate)) {
            resolvedPackage = resolvePackage(packageTemplate);
        } else {
            resolvedPackage = trimNavigationMarkers(packageTemplate);
        }
        return resolvedPackage;
    }

    private String trimNavigationMarkers(String packageTemplate) {
        return packageTemplate.replaceAll("/?\\.\\.", "");
    }

    private String resolvePackage(String packageTemplate) {
        int dotdotCount = countNavigationMarkers(packageTemplate);
        packageTemplate = trimNavigationMarkers(packageTemplate);
        return insertPackage(pop(sourcePackage, dotdotCount), packageTemplate);
    }

    private String insertPackage(String pkg, String packageTemplate) {
        int index = packageTemplate.indexOf(PACKAGE_TOKEN);
        if (index >= 0) {
            return clean(packageTemplate.substring(0, index)) + pkg + clean(packageTemplate.substring(index + PACKAGE_TOKEN.length()));
        }
        return packageTemplate;
    }

    private String clean(String s) {
        return s.replaceAll("/", ".");
    }

    private String pop(String sourcePackage, int dotdotCount) {
        for (int i = 0; i < dotdotCount; i++) {
            int dot = sourcePackage.lastIndexOf(".");
            if (dot >= 0) {
                sourcePackage = sourcePackage.substring(0, dot);
            } else {
                return "";
            }
        }
        return sourcePackage;
    }

    private int countNavigationMarkers(String packageTemplate) {
        int count = 0;
        int current = 0;
        boolean done = false;
        while (!done) {
            int index = packageTemplate.indexOf(POP_TOKEN, current);
            if (index >= 0) {
                count++;
                current = index + POP_TOKEN.length();
            } else {
                done = true;
            }
        }
        return count;
    }

    private boolean includesPackageToken(String packageTemplate) {
        return packageTemplate.indexOf(PACKAGE_TOKEN) >= 0;
    }
}
