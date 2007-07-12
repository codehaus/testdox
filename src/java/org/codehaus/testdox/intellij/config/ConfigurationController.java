package org.codehaus.testdox.intellij.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.codehaus.testdox.intellij.IconHelper;
import org.codehaus.testdox.intellij.TemplateNameResolver;
import org.jdom.Element;

public class ConfigurationController implements ProjectComponent, Configurable, JDOMExternalizable {

    protected ConfigurationUI panel;

    private final ConfigurationBean bean = new ConfigurationBean();

    public ConfigurationController() {
        setDefaults();
    }

    // BaseComponent ---------------------------------------------------------------------------------------------------

    public void initComponent() { }

    public void projectOpened() { }

    public void projectClosed() { }

    public void disposeComponent() { }

    public String getComponentName() {
        return "TestDox.ConfigurationController";
    }

    private void setDefaults() {
        if (bean.getCustomPackages() == null) {
            bean.setCustomPackages(Collections.EMPTY_LIST);
        }
        if (bean.getTestNameTemplate() == null) {
            bean.setTestNameTemplate(TemplateNameResolver.DEFAULT_TEMPLATE);
        }
        if (bean.getTestMethodPrefix() == null && bean.getTestMethodAnnotation() == null) {
            bean.setTestMethodPrefix(TemplateNameResolver.DEFAULT_PREFIX);
        }
        bean.setCreateTestIfMissing(true);
        bean.setAutoApplyChangesToTests(true);
        bean.setDeletePackageOccurrences(true);
    }

    // UnnamedConfigurable ---------------------------------------------------------------------------------------------

    public void apply() throws ConfigurationException {
        bean.setAllowCustomPackages(panel.getCustomMappingStatus());
        bean.setCustomPackages(panel.getCustomPackageMappings());
        bean.setTestNameTemplate(panel.getTestNameTemplate());
        bean.setCreateTestIfMissing(panel.getCreateTestIfMissing());
        bean.setUnderscoreMode(panel.getUseUnderscore());
        bean.setShowFullyQualifiedClassName(panel.getShowFullyQualifiedClassName());
        bean.setAutoApplyChangesToTests(panel.getAutoApplyChangesToTest());
        bean.setDeletePackageOccurrences(panel.getDeletePackageOccurrences());
        String prefix = panel.getTestMethodPrefix();
        if (prefix != null && prefix.startsWith("@")) {
            bean.setTestMethodPrefix(null);
            bean.setTestMethodAnnotation(prefix);
            bean.setUsingAnnotations(true);
        } else {
            bean.setTestMethodPrefix(prefix);
            bean.setTestMethodAnnotation(null);
            bean.setUsingAnnotations(false);
        }
    }

    public JComponent createComponent() {
        panel = new ConfigurationPanel();
        reset();
        return (ConfigurationPanel) panel;
    }

    public void disposeUIResources() {
        panel = null;
    }

    public String getDisplayName() {
        return "TestDox";
    }

    public String getHelpTopic() {
        return "";
    }

    public Icon getIcon() {
        return IconHelper.getIcon(IconHelper.TESTDOX_ICON);
    }

    public boolean isModified() {
        return bean.isAllowCustomPackages() != panel.getCustomMappingStatus()
               || !equals(bean.getCustomPackages(), panel.getCustomPackageMappings())
               || !bean.getTestNameTemplate().equals(panel.getTestNameTemplate())
               || !bean.getTestMethodIndicator().equals(panel.getTestMethodPrefix())
               || bean.isCreateTestIfMissing() != panel.getCreateTestIfMissing()
               || bean.isUnderscoreMode() != panel.getUseUnderscore()
               || bean.isShowFullyQualifiedClassName() != panel.getShowFullyQualifiedClassName()
               || bean.isAutoApplyChangesToTest() != panel.getAutoApplyChangesToTest()
               || bean.isDeletePackageOccurrences() != panel.getDeletePackageOccurrences();
    }

    private boolean equals(List strings1, List strings2) {
        if (strings1.size() != strings2.size()) {
            return false;
        }
        for (int i = 0; i < strings1.size(); i++) {
            if (!strings1.get(i).equals(strings2.get(i))) {
                return false;
            }
        }
        return true;
    }

    public void reset() {
        panel.setCustomMappingStatus(bean.isAllowCustomPackages());
        panel.setCustomPackageMappings(bean.getCustomPackages());
        panel.setTestNameTemplate(bean.getTestNameTemplate());
        panel.setTestMethodPrefix(bean.getTestMethodIndicator());
        panel.setCreateTestIfMissing(bean.isCreateTestIfMissing());
        panel.setUseUnderscore(bean.isUnderscoreMode());
        panel.setShowFullyQualifiedClassName(bean.isShowFullyQualifiedClassName());
        panel.setAutoApplyChangesToTest(bean.isAutoApplyChangesToTest());
        panel.setDeletePackageOccurrences(bean.isDeletePackageOccurrences());
    }

    public ConfigurationBean getConfigurationBean() {
        return bean;
    }

    public void readExternal(Element element) throws InvalidDataException {
        DefaultJDOMExternalizer.readExternal(bean, element);
        readPackagingData(bean, element);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        DefaultJDOMExternalizer.writeExternal(bean, element);
        writePackagingData(bean, element);
    }

    private void writePackagingData(ConfigurationBean bean, Element root) {
        Element packaging = new Element("custom-packaging");
        packaging.setAttribute("allow", String.valueOf(bean.isAllowCustomPackages()));
        List customPackages = bean.getCustomPackages();
        if (customPackages != null) {
            for (Iterator iterator = customPackages.iterator(); iterator.hasNext();) {
                String customPackage = (String) iterator.next();
                Element custom = new Element("package");
                custom.addContent(customPackage);
                packaging.addContent(custom);
            }
        }
        root.addContent(packaging);
    }

    private void readPackagingData(ConfigurationBean bean, Element root) {
        List<String> packages = new ArrayList<String>();
        Element packaging = root.getChild("custom-packaging");
        if (packaging != null) {
            String allowValue = packaging.getAttributeValue("allow");
            boolean allowCustomPackages = (allowValue != null) && Boolean.valueOf(allowValue);
            bean.setAllowCustomPackages(allowCustomPackages);
            for (Iterator iterator = packaging.getChildren().iterator(); iterator.hasNext();) {
                Element child = (Element) iterator.next();
                packages.add(child.getTextTrim());
            }
        }
        bean.setCustomPackages(packages);
    }
}
