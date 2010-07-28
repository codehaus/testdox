package org.codehaus.testdox.intellij.config;

import static jedi.functional.Coercions.list;
import org.codehaus.testdox.intellij.IconHelper;
import org.codehaus.testdox.intellij.TemplateNameResolver;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.util.Collections;
import java.util.List;

public class ConfigurationControllerTest extends MockObjectTestCase {

    public static final String TEST_NAME = "<classname>Test";
    public static final String TEST_PREFIX = "test";
    public static final List<String> PACKAGES = Collections.singletonList("com.acme");

    private static final boolean ALLOW_CUSTOM = true;
    private static final boolean CREATE_TEST = true;
    private static final boolean USE_UNDERSCORE = true;
    private static final boolean SHOW_FULLY_QUALIFIED_CLASS_NAME = true;
    private static final boolean AUTO_APPLY_CHANGES = true;
    private static final boolean DELETE_PACKAGE_OCCURRENCES = true;

    private final Mock mockConfigurationPanel = mock(ConfigurationUI.class);

    private ConfigurationController controller;
    private ConfigurationBean bean;

    protected void setUp() {
        controller = new ConfigurationController() {
            {
                this.panel = (ConfigurationUI) mockConfigurationPanel.proxy();
            }
        };

        bean = controller.getState();
    }

    public void testSetsDefaultValuesOnBeanWhenConstructed() throws Exception {
        assertDefaults(bean);
    }

    public void testDefinesItsComponentName() {
        assertEquals("component name", "TestDoxConfiguration", controller.getComponentName());
    }

    public void testDoesNothingWhenInitialisedByIntellijIdea() {
        controller.initComponent();
    }

    public void testDoesNothingWhenDisposedByIntellijIdea() {
        controller.disposeComponent();
    }

    public void testDoesNothingWhenTheAssociatedProjectIsOpened() {
        controller.projectOpened();
    }

    public void testDoesNothingWhenTheAssociatedProjectIsClosed() {
        controller.projectClosed();
    }

    public void testUsesTestdoxAsItsDisplayName() {
        assertEquals("display name", "TestDox", controller.getDisplayName());
    }

    public void testUsesTheTestdoxIconAsItsRepresentation() {
        assertSame("icon representation", IconHelper.getIcon(IconHelper.TESTDOX_ICON), controller.getIcon());
    }

    public void testDoesNotHaveAnAssociatedHelpTopic() {
        assertEquals("help topic", "", controller.getHelpTopic());
    }

    public void testCopiesCorrectValuesToBeanWhenUsingAnnotations() throws Exception {
        String annotation = "@Bison";
        getSettingsFromPanel(annotation);

        controller.apply();
        assertTrue(bean.isUsingAnnotations());
        assertEquals(annotation, bean.getTestMethodAnnotation());
        assertNull(bean.getTestMethodPrefix());
    }

    public void testCopiesValuesFromPanelToBeanWhenChangesAreApplied() throws Exception {
        getSettingsFromPanel(TEST_PREFIX);

        controller.apply();
        assertEquals(TEST_NAME, bean.getTestNameTemplate());
        assertEquals(TEST_PREFIX, bean.getTestMethodPrefix());
        assertNull(bean.getTestMethodAnnotation());
        assertFalse(bean.isUsingAnnotations());
        assertEquals(PACKAGES, bean.getCustomPackages());
        assertEquals(ALLOW_CUSTOM, bean.isAllowCustomPackages());
        assertEquals(USE_UNDERSCORE, bean.isUnderscoreMode());
        assertEquals(SHOW_FULLY_QUALIFIED_CLASS_NAME, bean.isShowFullyQualifiedClassName());
        assertEquals(AUTO_APPLY_CHANGES, bean.isAutoApplyChangesToTest());
        assertEquals(DELETE_PACKAGE_OCCURRENCES, bean.isDeletePackageOccurrences());
    }

    private void getSettingsFromPanel(String testPrefix) {
        mockConfigurationPanel.expects(once()).method("getCustomPackageMappings").will(returnValue(PACKAGES));
        mockConfigurationPanel.expects(once()).method("getCustomMappingStatus").will(returnValue(ALLOW_CUSTOM));
        mockConfigurationPanel.expects(once()).method("getTestNameTemplate").will(returnValue(TEST_NAME));
        mockConfigurationPanel.expects(once()).method("getTestMethodPrefix").will(returnValue(testPrefix));
        mockConfigurationPanel.expects(once()).method("getCreateTestIfMissing").will(returnValue(CREATE_TEST));
        mockConfigurationPanel.expects(once()).method("getUseUnderscore").will(returnValue(USE_UNDERSCORE));
        mockConfigurationPanel.expects(once()).method("getShowFullyQualifiedClassName").will(returnValue(SHOW_FULLY_QUALIFIED_CLASS_NAME));
        mockConfigurationPanel.expects(once()).method("getAutoApplyChangesToTest").will(returnValue(AUTO_APPLY_CHANGES));
        mockConfigurationPanel.expects(once()).method("getDeletePackageOccurrences").will(returnValue(DELETE_PACKAGE_OCCURRENCES));
    }

    public void testCopiesValuesFromBeanToPanelWhenReset() throws Exception {
        mockConfigurationPanel.expects(once()).method("setCustomPackageMappings").with(eq(PACKAGES));
        mockConfigurationPanel.expects(once()).method("setCustomMappingStatus").with(eq(ALLOW_CUSTOM));
        mockConfigurationPanel.expects(once()).method("setTestNameTemplate").with(eq(TEST_NAME));
        mockConfigurationPanel.expects(once()).method("setTestMethodPrefix").with(eq(TEST_PREFIX));
        mockConfigurationPanel.expects(once()).method("setCreateTestIfMissing").with(eq(CREATE_TEST));
        mockConfigurationPanel.expects(once()).method("setUseUnderscore").with(eq(USE_UNDERSCORE));
        mockConfigurationPanel.expects(once()).method("setShowFullyQualifiedClassName").with(eq(SHOW_FULLY_QUALIFIED_CLASS_NAME));
        mockConfigurationPanel.expects(once()).method("setAutoApplyChangesToTest").with(eq(AUTO_APPLY_CHANGES));
        mockConfigurationPanel.expects(once()).method("setDeletePackageOccurrences").with(eq(DELETE_PACKAGE_OCCURRENCES));

        setSettingsOnBean();
        controller.reset();
    }

    public void testCopiesCorrectAnnotationValuesFromBeanToPanelWhenReset() throws Exception {
        String annotation = "@Monkey";
        mockConfigurationPanel.expects(once()).method("setCustomPackageMappings").with(eq(PACKAGES));
        mockConfigurationPanel.expects(once()).method("setCustomMappingStatus").with(eq(ALLOW_CUSTOM));
        mockConfigurationPanel.expects(once()).method("setTestNameTemplate").with(eq(TEST_NAME));
        mockConfigurationPanel.expects(once()).method("setTestMethodPrefix").with(eq(annotation));
        mockConfigurationPanel.expects(once()).method("setCreateTestIfMissing").with(eq(CREATE_TEST));
        mockConfigurationPanel.expects(once()).method("setUseUnderscore").with(eq(USE_UNDERSCORE));
        mockConfigurationPanel.expects(once()).method("setShowFullyQualifiedClassName").with(eq(SHOW_FULLY_QUALIFIED_CLASS_NAME));
        mockConfigurationPanel.expects(once()).method("setAutoApplyChangesToTest").with(eq(AUTO_APPLY_CHANGES));
        mockConfigurationPanel.expects(once()).method("setDeletePackageOccurrences").with(eq(DELETE_PACKAGE_OCCURRENCES));

        setSettingsOnBean();
        bean.setTestMethodPrefix(null);
        bean.setTestMethodAnnotation(annotation);
        bean.setUsingAnnotations(true);

        controller.reset();
    }

    private void setSettingsOnBean() {
        bean.setAllowCustomPackages(ALLOW_CUSTOM);
        bean.setCustomPackages(PACKAGES);
        bean.setTestNameTemplate(TEST_NAME);
        bean.setTestMethodPrefix(TEST_PREFIX);
        bean.setCreateTestIfMissing(CREATE_TEST);
        bean.setUnderscoreMode(USE_UNDERSCORE);
        bean.setShowFullyQualifiedClassName(SHOW_FULLY_QUALIFIED_CLASS_NAME);
        bean.setAutoApplyChangesToTests(AUTO_APPLY_CHANGES);
        bean.setDeletePackageOccurrences(DELETE_PACKAGE_OCCURRENCES);
    }

    public void testIndicatesNotModifiedIfValuesOnBeanAndPanelMatch() throws Exception {
        getSettingsFromPanel(TEST_PREFIX);
        setSettingsOnBean();
        assertFalse(controller.isModified());
    }

    public void testIndicatesModifiedIfAtLeastOneValueOnBeanDoesNotMatchPanel() throws Exception {
        mockConfigurationPanel.expects(once()).method("getCustomPackageMappings").will(returnValue(PACKAGES));
        mockConfigurationPanel.expects(once()).method("getCustomMappingStatus").will(returnValue(ALLOW_CUSTOM));
        mockConfigurationPanel.expects(once()).method("getTestNameTemplate").will(returnValue(TEST_NAME));

        bean.setAllowCustomPackages(ALLOW_CUSTOM);
        bean.setCustomPackages(PACKAGES);
        bean.setTestNameTemplate("blarg");

        assertTrue(controller.isModified());
    }

    public void testIndicatesModifiedIfAValueInThePackageArrayOnThePanelDoesNotMatchTheBean() throws Exception {
        mockConfigurationPanel.expects(once()).method("getCustomPackageMappings").will(returnValue(Collections.singletonList("blarg")));
        mockConfigurationPanel.expects(once()).method("getCustomMappingStatus").will(returnValue(ALLOW_CUSTOM));

        bean.setAllowCustomPackages(ALLOW_CUSTOM);
        bean.setCustomPackages(PACKAGES);
        bean.setTestNameTemplate(TEST_NAME);

        assertTrue(controller.isModified());
    }

    public void testSetsDefaultValuesOnBeanForMissingConfigurationInformation() {
        assertDefaults(bean);
    }

    public void testSetsValuesFromElementOnBeanWhenReadingValidConfigurationData() throws Exception {
        ConfigurationBean configuration = new ConfigurationBean();
        configuration.setCustomPackages(list("FOO", "BAR"));
        controller.loadState(configuration);
        assertEquals("custom packages", configuration.getCustomPackages(), bean.getCustomPackages());
    }

    private void assertDefaults(ConfigurationBean bean) {
        assertEquals(0, bean.getCustomPackages().size());
        assertEquals(TemplateNameResolver.DEFAULT_TEMPLATE, bean.getTestNameTemplate());
        assertEquals(TemplateNameResolver.DEFAULT_PREFIX, bean.getTestMethodIndicator());
        assertFalse(bean.isAllowCustomPackages());
        assertFalse(bean.isAlphabeticalSorting());
        assertFalse(bean.isUnderscoreMode());
        assertTrue(bean.isCreateTestIfMissing());
        assertTrue(bean.isAutoApplyChangesToTest());
        assertTrue(bean.isDeletePackageOccurrences());
    }
}
