package org.codehaus.testdox.intellij.config;

import static jedi.functional.Coercions.list;

import org.codehaus.testdox.intellij.Constants;
import org.codehaus.testdox.intellij.IconHelper;
import org.codehaus.testdox.intellij.TemplateNameResolver;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class ConfigurationControllerTest extends MockObjectTestCase {

    public static final String TEST_NAME = "<classname>Test";
    public static final String TEST_PREFIX = "test";

    private static final boolean ALLOW_CUSTOM = true;
    private static final boolean CREATE_TEST = true;
    private static final boolean USE_UNDERSCORE = true;
    private static final boolean SHOW_FULLY_QUALIFIED_CLASS_NAME = true;
    private static final boolean AUTO_APPLY_CHANGES = true;
    private static final boolean DELETE_PACKAGE_OCCURRENCES = true;

    private final Mock mockConfigurationPanel = mock(ConfigurationUI.class);

    private ConfigurationController controller;
    private Configuration bean;

    protected void setUp() {
        controller = new ConfigurationController();
        controller.setPanel((ConfigurationUI) mockConfigurationPanel.proxy());
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
        assertTrue(bean.usingAnnotations());
        assertEquals(annotation, bean.testMethodAnnotation());
        assertNull(bean.testMethodPrefix());
    }

    public void testCopiesValuesFromPanelToBeanWhenChangesAreApplied() throws Exception {
        getSettingsFromPanel(TEST_PREFIX);

        controller.apply();
        assertEquals(TEST_NAME, bean.getTestNameTemplate());
        assertEquals(TEST_PREFIX, bean.testMethodPrefix());
        assertNull(bean.testMethodAnnotation());
        assertFalse(bean.usingAnnotations());
        assertEquals(Constants.PACKAGES(), bean.getCustomPackages());
        assertEquals(ALLOW_CUSTOM, bean.getCustomPackagesAllowed());
        assertEquals(USE_UNDERSCORE, bean.underscoreMode());
        assertEquals(SHOW_FULLY_QUALIFIED_CLASS_NAME, bean.getShowFullyQualifiedClassName());
        assertEquals(AUTO_APPLY_CHANGES, bean.autoApplyChangesToTests());
        assertEquals(DELETE_PACKAGE_OCCURRENCES, bean.deletePackageOccurrences());
    }

    private void getSettingsFromPanel(String testPrefix) {
        mockConfigurationPanel.expects(once()).method("customPackageMappings").will(returnValue(Constants.PACKAGES()));
        mockConfigurationPanel.expects(once()).method("customMappingStatus").will(returnValue(ALLOW_CUSTOM));
        mockConfigurationPanel.expects(once()).method("testNameTemplate").will(returnValue(TEST_NAME));
        mockConfigurationPanel.expects(once()).method("testMethodPrefix").will(returnValue(testPrefix));
        mockConfigurationPanel.expects(once()).method("createTestIfMissing").will(returnValue(CREATE_TEST));
        mockConfigurationPanel.expects(once()).method("useUnderscore").will(returnValue(USE_UNDERSCORE));
        mockConfigurationPanel.expects(once()).method("showFullyQualifiedClassName").will(returnValue(SHOW_FULLY_QUALIFIED_CLASS_NAME));
        mockConfigurationPanel.expects(once()).method("autoApplyChangesToTests").will(returnValue(AUTO_APPLY_CHANGES));
        mockConfigurationPanel.expects(once()).method("deletePackageOccurrences").will(returnValue(DELETE_PACKAGE_OCCURRENCES));
    }

    public void testCopiesValuesFromBeanToPanelWhenReset() throws Exception {
        mockConfigurationPanel.expects(once()).method("customPackageMappings_$eq").with(eq(Constants.PACKAGES()));
        mockConfigurationPanel.expects(once()).method("customMappingStatus_$eq").with(eq(ALLOW_CUSTOM));
        mockConfigurationPanel.expects(once()).method("testNameTemplate_$eq").with(eq(TEST_NAME));
        mockConfigurationPanel.expects(once()).method("testMethodPrefix_$eq").with(eq(TEST_PREFIX));
        mockConfigurationPanel.expects(once()).method("createTestIfMissing_$eq").with(eq(CREATE_TEST));
        mockConfigurationPanel.expects(once()).method("useUnderscore_$eq").with(eq(USE_UNDERSCORE));
        mockConfigurationPanel.expects(once()).method("showFullyQualifiedClassName_$eq").with(eq(SHOW_FULLY_QUALIFIED_CLASS_NAME));
        mockConfigurationPanel.expects(once()).method("autoApplyChangesToTests_$eq").with(eq(AUTO_APPLY_CHANGES));
        mockConfigurationPanel.expects(once()).method("deletePackageOccurrences_$eq").with(eq(DELETE_PACKAGE_OCCURRENCES));

        setSettingsOnBean();
        controller.reset();
    }

    public void testCopiesCorrectAnnotationValuesFromBeanToPanelWhenReset() throws Exception {
        String annotation = "@Monkey";
        mockConfigurationPanel.expects(once()).method("customPackageMappings_$eq").with(eq(Constants.PACKAGES()));
        mockConfigurationPanel.expects(once()).method("customMappingStatus_$eq").with(eq(ALLOW_CUSTOM));
        mockConfigurationPanel.expects(once()).method("testNameTemplate_$eq").with(eq(TEST_NAME));
        mockConfigurationPanel.expects(once()).method("testMethodPrefix_$eq").with(eq(annotation));
        mockConfigurationPanel.expects(once()).method("createTestIfMissing_$eq").with(eq(CREATE_TEST));
        mockConfigurationPanel.expects(once()).method("useUnderscore_$eq").with(eq(USE_UNDERSCORE));
        mockConfigurationPanel.expects(once()).method("showFullyQualifiedClassName_$eq").with(eq(SHOW_FULLY_QUALIFIED_CLASS_NAME));
        mockConfigurationPanel.expects(once()).method("autoApplyChangesToTests_$eq").with(eq(AUTO_APPLY_CHANGES));
        mockConfigurationPanel.expects(once()).method("deletePackageOccurrences_$eq").with(eq(DELETE_PACKAGE_OCCURRENCES));

        setSettingsOnBean();
        bean.setTestMethodPrefix(null);
        bean.setTestMethodAnnotation(annotation);
        bean.setUsingAnnotations(true);

        controller.reset();
    }

    private void setSettingsOnBean() {
        bean.setCustomPackagesAllowed(ALLOW_CUSTOM);
        bean.setCustomPackages(Constants.PACKAGES());
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
        mockConfigurationPanel.expects(once()).method("customPackageMappings").will(returnValue(Constants.PACKAGES()));
        mockConfigurationPanel.expects(once()).method("customMappingStatus").will(returnValue(ALLOW_CUSTOM));
        mockConfigurationPanel.expects(once()).method("testNameTemplate").will(returnValue(TEST_NAME));

        bean.setCustomPackagesAllowed(ALLOW_CUSTOM);
        bean.setCustomPackages(Constants.PACKAGES());
        bean.setTestNameTemplate("blarg");

        assertTrue(controller.isModified());
    }

    public void testIndicatesModifiedIfAValueInThePackageArrayOnThePanelDoesNotMatchTheBean() throws Exception {
        mockConfigurationPanel.expects(once()).method("customPackageMappings").will(returnValue(Constants.list("blarg")));
        mockConfigurationPanel.expects(once()).method("customMappingStatus").will(returnValue(ALLOW_CUSTOM));

        bean.setCustomPackagesAllowed(ALLOW_CUSTOM);
        bean.setCustomPackages(Constants.PACKAGES());
        bean.setTestNameTemplate(TEST_NAME);

        assertTrue(controller.isModified());
    }

    public void testSetsDefaultValuesOnBeanForMissingConfigurationInformation() {
        assertDefaults(bean);
    }

    public void testSetsValuesFromElementOnBeanWhenReadingValidConfigurationData() throws Exception {
        Configuration configuration = new Configuration();
        configuration.setCustomPackages(list("FOO", "BAR"));
        controller.loadState(configuration);
        assertEquals("custom packages", configuration.getCustomPackages(), bean.getCustomPackages());
    }

    private void assertDefaults(Configuration bean) {
        assertEquals(0, bean.getCustomPackages().size());
        assertEquals(TemplateNameResolver.DEFAULT_TEMPLATE, bean.getTestNameTemplate());
        assertEquals(TemplateNameResolver.DEFAULT_PREFIX, bean.testMethodIndicator());
        assertFalse(bean.getCustomPackagesAllowed());
        assertFalse(bean.alphabeticalSorting());
        assertFalse(bean.underscoreMode());
        assertTrue(bean.createTestIfMissing());
        assertTrue(bean.autoApplyChangesToTests());
        assertTrue(bean.deletePackageOccurrences());
    }
}
