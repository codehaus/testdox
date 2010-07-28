package org.codehaus.testdox.intellij.config;

import org.codehaus.testdox.intellij.TemplateNameResolver;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationBean implements Serializable {

    public static final String ALLOW_CUSTOM_PACKAGES = "allowCustomPackages";
    public static final String CUSTOM_PACKAGES = "customPackages";
    public static final String TEST_NAME_TEMPLATE = "testNameTemplate";
    public static final String SHOW_FULLY_QUALIFIED_CLASS_NAME = "showFullyQualifiedClassName";

    private boolean allowCustomPackages;
    private List<String> customPackages = new ArrayList<String>();
    private String testNameTemplate = TemplateNameResolver.DEFAULT_TEMPLATE;
    private boolean alphabeticalSorting;
    private boolean createTestIfMissing;
    private boolean underscoreMode;
    private boolean showFullyQualifiedClassName;
    private boolean autoscrolling;
    private boolean autoApplyChangesToTests;
    private boolean deletePackageOccurrences;
    private String testMethodPrefix = TemplateNameResolver.DEFAULT_PREFIX;
    private String testMethodAnnotation;
    private boolean usingAnnotations;

    private transient PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public boolean isAllowCustomPackages() {
        return allowCustomPackages;
    }

    public void setAllowCustomPackages(boolean allowCustomPackages) {
        boolean old = this.allowCustomPackages;
        this.allowCustomPackages = allowCustomPackages;
        support.firePropertyChange(ALLOW_CUSTOM_PACKAGES, old, allowCustomPackages);
    }

    public List<String> getCustomPackages() {
        return customPackages;
    }

    public void setCustomPackages(List<String> customPackages) {
        List old = this.customPackages;
        this.customPackages = customPackages;
        support.firePropertyChange(CUSTOM_PACKAGES, old, customPackages);
    }

    public void setTestNameTemplate(String testNameTemplate) {
        String old = this.testNameTemplate;
        this.testNameTemplate = testNameTemplate;
        support.firePropertyChange(TEST_NAME_TEMPLATE, old, testNameTemplate);
    }

    public String getTestNameTemplate() {
        return testNameTemplate;
    }

    public boolean isAlphabeticalSorting() {
        return alphabeticalSorting;
    }

    public void setAlphabeticalSorting(boolean alphabeticalSorting) {
        this.alphabeticalSorting = alphabeticalSorting;
    }

    public boolean isCreateTestIfMissing() {
        return createTestIfMissing;
    }

    public void setCreateTestIfMissing(boolean createTestIfMissing) {
        this.createTestIfMissing = createTestIfMissing;
    }

    public void setUnderscoreMode(boolean underscoreMode) {
        this.underscoreMode = underscoreMode;
    }

    public boolean isUnderscoreMode() {
        return underscoreMode;
    }

    public void setShowFullyQualifiedClassName(boolean showFullyQualifiedClassName) {
        boolean oldValue = this.showFullyQualifiedClassName;
        this.showFullyQualifiedClassName = showFullyQualifiedClassName;
        support.firePropertyChange(SHOW_FULLY_QUALIFIED_CLASS_NAME, oldValue, showFullyQualifiedClassName);
    }

    public boolean isShowFullyQualifiedClassName() {
        return showFullyQualifiedClassName;
    }

    public boolean isAutoscrolling() {
        return autoscrolling;
    }

    public void setAutoscrolling(boolean autoscrolling) {
        this.autoscrolling = autoscrolling;
    }

    public boolean isAutoApplyChangesToTest() {
        return autoApplyChangesToTests;
    }

    public void setAutoApplyChangesToTests(boolean autoApplyChangesToTests) {
        this.autoApplyChangesToTests = autoApplyChangesToTests;
    }

    public boolean isDeletePackageOccurrences() {
        return deletePackageOccurrences;
    }

    public void setDeletePackageOccurrences(boolean deletePackageOccurrences) {
        this.deletePackageOccurrences = deletePackageOccurrences;
    }

    public String getTestMethodPrefix() {
        return testMethodPrefix;
    }

    public void setTestMethodPrefix(String testMethodPrefix) {
        this.testMethodPrefix = testMethodPrefix;
    }

    public boolean isUsingAnnotations() {
        return usingAnnotations;
    }

    public void setUsingAnnotations(boolean usingAnnotations) {
        this.usingAnnotations = usingAnnotations;
    }

    public String getTestMethodAnnotation() {
        return testMethodAnnotation;
    }

    public void setTestMethodAnnotation(String testMethodAnnotation) {
        this.testMethodAnnotation = testMethodAnnotation;
    }

    public String getTestMethodIndicator() {
        return isUsingAnnotations() ? getTestMethodAnnotation() : getTestMethodPrefix();
    }
}
