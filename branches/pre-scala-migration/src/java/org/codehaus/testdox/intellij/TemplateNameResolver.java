package org.codehaus.testdox.intellij;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.codehaus.testdox.intellij.config.ConfigurationBean;

public class TemplateNameResolver implements NameResolver, PropertyChangeListener {

    public static final String NAME_TOKEN = "<classname>";
    public static final String DEFAULT_TEMPLATE = NAME_TOKEN + "Test";
    public static final String DEFAULT_PREFIX = "test";

    private final ConfigurationBean configuration;

    private String prefix;
    private String suffix;

    public TemplateNameResolver(ConfigurationBean configuration) {
        this.configuration = configuration;

        configureTemplate(configuration.getTestNameTemplate());
        configuration.addPropertyChangeListener(this);
    }

    private void configureTemplate(String template) {
        prefix = "";
        suffix = "";
        String[] tokens = template.split(NAME_TOKEN);
        if (tokens.length > 0) {
            if (tokens.length >= 2) {
                prefix = tokens[0];
                suffix = tokens[1];
            } else if (template.startsWith(NAME_TOKEN)) {
                suffix = tokens[0];
            } else if (template.endsWith(NAME_TOKEN)) {
                prefix = tokens[0];
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (ConfigurationBean.TEST_NAME_TEMPLATE.equals(evt.getPropertyName())) {
            configureTemplate((String) evt.getNewValue());
        }
    }

    public String getRealClassName(String className) {
        if (isRealClass(className)) {
            return className;
        }
        String classNameOnly = extractClassNameWithoutPackageName(className);
        if (classNameOnly.startsWith(prefix)) {
            classNameOnly = classNameOnly.substring(prefix.length());
        }
        if (classNameOnly.endsWith(suffix)) {
            classNameOnly = classNameOnly.substring(0, classNameOnly.length() - suffix.length());
        }
        return extractPackagePrefix(className) + classNameOnly;
    }

    public String getTestClassName(String className) {
        if (isTestClass(className)) {
            return className;
        }

        String packagePrefix = extractPackagePrefix(className);
        String classNameOnly = extractClassNameWithoutPackageName(className);
        return packagePrefix + prefix + classNameOnly + suffix;
    }

    public String getRealClassNameForDisplay(String className) {
        return (configuration.isShowFullyQualifiedClassName())
                ? getRealClassName(className)
                : extractClassNameWithoutPackageName(getRealClassName(className));
    }

    private boolean isClassUnderPackage(String className) {
        return (className.indexOf('.') > -1);
    }

    private String extractPackagePrefix(String className) {
        if (isClassUnderPackage(className)) {
            return className.substring(0, className.lastIndexOf('.') + 1);
        }
        return "";
    }

    public String extractClassNameWithoutPackageName(String className) {
        if (isClassUnderPackage(className)) {
            return className.substring(className.lastIndexOf('.') + 1);
        }
        return className;
    }

    public boolean isRealClass(String className) {
        if (className == null) {
            return false;
        }
        String classNameOnly = extractClassNameWithoutPackageName(className);
        return !(classNameOnly.startsWith(prefix) && classNameOnly.endsWith(suffix));
    }

    public boolean isTestClass(String className) {
        if (className == null) {
            return false;
        }
        return !isRealClass(className);
    }
}
