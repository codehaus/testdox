package org.codehaus.testdox.intellij.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.codehaus.testdox.intellij.IconHelper;
import org.codehaus.testdox.intellij.TemplateNameResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

@State(
        name = ConfigurationController.COMPONENT_NAME,
        storages = {
                @Storage(
                        id = ConfigurationController.COMPONENT_NAME,
                        file = "$PROJECT_FILE$"
                )}
)

public class ConfigurationController implements ProjectComponent, Configurable, PersistentStateComponent<ConfigurationBean> {

    static final String COMPONENT_NAME = "TestDoxConfiguration";

    protected ConfigurationUI panel;

    private final ConfigurationBean bean = new ConfigurationBean();

    public ConfigurationController() {
        setDefaults();
    }

    // BaseComponent ---------------------------------------------------------------------------------------------------

    public void initComponent() {
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    private void setDefaults() {
        if (bean.getCustomPackages() == null) {
            bean.setCustomPackages(Collections.<String>emptyList());
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

    public ConfigurationBean getState() {
        return bean;
    }

    public void loadState(ConfigurationBean configuration) {
        XmlSerializerUtil.copyBean(configuration, bean);
    }
}
