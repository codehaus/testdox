package org.codehaus.testdox.intellij.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.util.xmlb.XmlSerializerUtil
import org.codehaus.testdox.intellij.IconHelper
import org.codehaus.testdox.intellij.TemplateNameResolver
import org.jetbrains.annotations.NotNull

import javax.swing._
import scala.collection.JavaConversions

/*
@State(
    name = ConfigurationController.COMPONENT_NAME,
    storages = {
    @Storage(
        id = ConfigurationController.COMPONENT_NAME,
        file = "$PROJECT_FILE$"
    )}
)
*/

class ConfigurationController extends ProjectComponent with Configurable with PersistentStateComponent[Configuration] {

  private var panel: ConfigurationUI = null

  private val configuration = new Configuration()

  setDefaults()

  // BaseComponent ---------------------------------------------------------------------------------------------------

  def initComponent() {}

  def projectOpened() {}

  def projectClosed() {}

  def disposeComponent() {}

  @NotNull
  override val getComponentName = ConfigurationController.COMPONENT_NAME

  private def setDefaults() {
    if (configuration.getCustomPackages == null) {
      configuration.setCustomPackages(new java.util.ArrayList[String])
    }
    if (configuration.getTestNameTemplate == null) {
      configuration.setTestNameTemplate(TemplateNameResolver.DEFAULT_TEMPLATE)
    }
    if (configuration.testMethodPrefix == null && configuration.testMethodAnnotation == null) {
      configuration.testMethodPrefix = TemplateNameResolver.DEFAULT_PREFIX
    }
    configuration.createTestIfMissing = true
    configuration.autoApplyChangesToTests = true
    configuration.deletePackageOccurrences = true
  }

  // UnnamedConfigurable ---------------------------------------------------------------------------------------------

  @throws(classOf[ConfigurationException])
  def apply() {
    configuration.setCustomPackagesAllowed(panel.getCustomMappingStatus())
    configuration.setCustomPackages(panel.getCustomPackageMappings())
    configuration.setTestNameTemplate(panel.getTestNameTemplate())
    configuration.createTestIfMissing = panel.getCreateTestIfMissing()
    configuration.underscoreMode = panel.getUseUnderscore()
    configuration.setShowFullyQualifiedClassName(panel.getShowFullyQualifiedClassName())
    configuration.autoApplyChangesToTests = panel.getAutoApplyChangesToTest()
    configuration.deletePackageOccurrences = panel.getDeletePackageOccurrences()

    val prefix = panel.getTestMethodPrefix()
    if (prefix != null && prefix.startsWith("@")) {
      configuration.testMethodPrefix = null
      configuration.testMethodAnnotation = prefix
      configuration.usingAnnotations= true
    } else {
      configuration.testMethodPrefix = prefix
      configuration.testMethodAnnotation = null
      configuration.usingAnnotations = false
    }
  }

  def createComponent(): JComponent = {
    setPanel(new ConfigurationPanel())
    reset()
    panel.asInstanceOf[ConfigurationPanel]
  }

  def disposeUIResources() = setPanel(null)

  // TODO: must be package private
  def setPanel(panel: ConfigurationUI) = this.panel = panel

  val getDisplayName = "TestDox"

  val getHelpTopic = ""

  val getIcon = IconHelper.getIcon(IconHelper.TESTDOX_ICON)

  def isModified = {
    configuration.getCustomPackagesAllowed != panel.getCustomMappingStatus() ||
        configuration.getCustomPackages != panel.getCustomPackageMappings() ||
        configuration.getTestNameTemplate != panel.getTestNameTemplate() ||
        configuration.testMethodIndicator != panel.getTestMethodPrefix() ||
        configuration.createTestIfMissing != panel.getCreateTestIfMissing() ||
        configuration.underscoreMode != panel.getUseUnderscore() ||
        configuration.getShowFullyQualifiedClassName != panel.getShowFullyQualifiedClassName() ||
        configuration.autoApplyChangesToTests != panel.getAutoApplyChangesToTest() ||
        configuration.deletePackageOccurrences != panel.getDeletePackageOccurrences()
  }

  def reset() {
    panel.setCustomMappingStatus(configuration.getCustomPackagesAllowed)
    panel.setCustomPackageMappings(JavaConversions.asList(JavaConversions.asBuffer(configuration.getCustomPackages)))
    panel.setTestNameTemplate(configuration.getTestNameTemplate)
    panel.setTestMethodPrefix(configuration.testMethodIndicator)
    panel.setCreateTestIfMissing(configuration.createTestIfMissing)
    panel.setUseUnderscore(configuration.underscoreMode)
    panel.setShowFullyQualifiedClassName(configuration.getShowFullyQualifiedClassName)
    panel.setAutoApplyChangesToTest(configuration.autoApplyChangesToTests)
    panel.setDeletePackageOccurrences(configuration.deletePackageOccurrences)
  }

  def getState = configuration

  def loadState(configuration: Configuration) {
    XmlSerializerUtil.copyBean(configuration, this.configuration)
  }
}

object ConfigurationController {
  private[config] val COMPONENT_NAME = "TestDoxConfiguration"
}
