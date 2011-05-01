package org.codehaus.testdox.intellij.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.util.xmlb.XmlSerializerUtil
import org.codehaus.testdox.intellij.Icons
import org.codehaus.testdox.intellij.TemplateNameResolver
import org.jetbrains.annotations.NotNull

import javax.swing._

/*
@State(
  name = ConfigurationController.COMPONENT_NAME,
  storages = { @Storage(id = ConfigurationController.COMPONENT_NAME, file = "$PROJECT_FILE$") }
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
      configuration.setCustomPackages(List[String]())
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
    configuration.setCustomPackagesAllowed(panel.customMappingStatus)
    configuration.setCustomPackages(panel.customPackageMappings)
    configuration.setTestNameTemplate(panel.testNameTemplate)
    configuration.createTestIfMissing = panel.createTestIfMissing
    configuration.underscoreMode = panel.useUnderscore
    configuration.setShowFullyQualifiedClassName(panel.showFullyQualifiedClassName)
    configuration.autoApplyChangesToTests = panel.autoApplyChangesToTests
    configuration.deletePackageOccurrences = panel.deletePackageOccurrences

    val prefix = panel.testMethodPrefix
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
    panel = new ConfigurationPanel()
    reset()
    panel.asInstanceOf[ConfigurationPanel]
  }

  def disposeUIResources() { panel = null }

  private[config] def setPanel(panel: ConfigurationUI) { this.panel = panel }

  val getDisplayName = "TestDox"

  val getHelpTopic = ""

  val getIcon = Icons.getIcon(Icons.TESTDOX_ICON)

  def isModified = {
    configuration.getCustomPackagesAllowed != panel.customMappingStatus ||
    configuration.getCustomPackages != panel.customPackageMappings ||
    configuration.getTestNameTemplate != panel.testNameTemplate ||
    configuration.testMethodIndicator != panel.testMethodPrefix ||
    configuration.createTestIfMissing != panel.createTestIfMissing ||
    configuration.underscoreMode != panel.useUnderscore ||
    configuration.getShowFullyQualifiedClassName != panel.showFullyQualifiedClassName ||
    configuration.autoApplyChangesToTests != panel.autoApplyChangesToTests ||
    configuration.deletePackageOccurrences != panel.deletePackageOccurrences
  }

  def reset() {
    panel.customMappingStatus = configuration.getCustomPackagesAllowed
    panel.customPackageMappings = configuration.getCustomPackages
    panel.testNameTemplate = configuration.getTestNameTemplate
    panel.testMethodPrefix = configuration.testMethodIndicator
    panel.createTestIfMissing = configuration.createTestIfMissing
    panel.useUnderscore = configuration.underscoreMode
    panel.showFullyQualifiedClassName = configuration.getShowFullyQualifiedClassName
    panel.autoApplyChangesToTests = configuration.autoApplyChangesToTests
    panel.deletePackageOccurrences = configuration.deletePackageOccurrences
  }

  def getState = configuration

  def loadState(configuration: Configuration) { XmlSerializerUtil.copyBean(configuration, this.configuration) }
}

object ConfigurationController {
  private[config] val COMPONENT_NAME = "TestDoxConfiguration"
}
