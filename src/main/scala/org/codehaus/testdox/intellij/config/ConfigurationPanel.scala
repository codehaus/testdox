package org.codehaus.testdox.intellij.config

import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.FlowLayout
import java.awt.event._
import javax.swing._
import org.codehaus.testdox.intellij.PackageResolver._
import org.codehaus.testdox.intellij.TemplateNameResolver
import scalaj.collection.Imports._

class ConfigurationPanel extends JPanel with ConfigurationUI {
  
  private val model = new PackageTableModel()
  private val createTestIfMissingCheckBox = new JCheckBox("Prompt to create missing test classes")
  private val useUnderscoreCheckBox = new JCheckBox("Use underscore (_) as acronym delimiter")
  private val showFullyQualifiedClassNameCheckBox = new JCheckBox("Show fully qualified class name for tested classes")
  private val autoApplyChangesToTestsCheckBox = new JCheckBox("When manually deleting/moving/renaming a class, automatically delete/move/rename the associated test class")
  private val deletePackageOccurrencesCheckBox = new JCheckBox("When manually deleting a package, prompt to delete other occurrences of that package")
  private val testName = new JTextField(50)
  private val methodNamePrefix = new JTextField(50)
 
  private[config] val allowCustom = new JCheckBox("Allow custom test packages")
  private[config] val packageInputField = new JTextField(20)
  private[config] val addButton = new JButton("Add")
  private[config] val table = new JTable(model)

  setLayout(new BorderLayout())

  private val box = Box.createVerticalBox()
  box.add(createBehaviourPanel())
  box.add(Box.createVerticalStrut(10))
  box.add(createTestMethodNamePanel())
  box.add(Box.createVerticalStrut(10))
  box.add(createTestClassNamePanel())
  box.add(Box.createVerticalStrut(10))
  box.add(createPackagingPanel())
  add(box, BorderLayout.CENTER)
  togglePackageInputWidgetState()

  def customPackageMappings = model.mappings.toList
  def customPackageMappings_=(mappings: List[String]) { model.setMappings(mappings) }
  def setCustomPackageMappings(mappings: java.util.List[String]) { customPackageMappings = mappings.asScala.toList }

  def customMappingStatus = allowCustom.isSelected
  def customMappingStatus_=(active: Boolean) {
    allowCustom.setSelected(active)
    togglePackageInputWidgetState()
  }

  def testNameTemplate = testName.getText
  def testNameTemplate_=(template: String) { testName.setText(template) }

  def testMethodPrefix = methodNamePrefix.getText
  def testMethodPrefix_=(prefix: String) { methodNamePrefix.setText(prefix) }

  def createTestIfMissing = createTestIfMissingCheckBox.isSelected
  def createTestIfMissing_=(selected: Boolean) { createTestIfMissingCheckBox.setSelected(selected) }

  def useUnderscore = useUnderscoreCheckBox.isSelected
  def useUnderscore_=(selected: Boolean) { useUnderscoreCheckBox.setSelected(selected) }

  def showFullyQualifiedClassName = showFullyQualifiedClassNameCheckBox.isSelected
  def showFullyQualifiedClassName_=(selected: Boolean) { showFullyQualifiedClassNameCheckBox.setSelected(selected) }

  def autoApplyChangesToTests = autoApplyChangesToTestsCheckBox.isSelected
  def autoApplyChangesToTests_=(selected: Boolean) { autoApplyChangesToTestsCheckBox.setSelected(selected) }

  def deletePackageOccurrences = deletePackageOccurrencesCheckBox.isSelected
  def deletePackageOccurrences_=(selected: Boolean) { deletePackageOccurrencesCheckBox.setSelected(selected) }

  private def createBehaviourPanel() = {
    val gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 2, 2)
    val behaviourPanel = new JPanel(new GridBagLayout())
    behaviourPanel.setBorder(BorderFactory.createTitledBorder("Behaviour"))
    behaviourPanel.add(useUnderscoreCheckBox, gbc)
    gbc.gridy += 1
    behaviourPanel.add(showFullyQualifiedClassNameCheckBox, gbc)
    gbc.gridy += 1
    behaviourPanel.add(createTestIfMissingCheckBox, gbc)
    gbc.gridy += 1
    behaviourPanel.add(autoApplyChangesToTestsCheckBox, gbc)
    gbc.gridy += 1
    behaviourPanel.add(deletePackageOccurrencesCheckBox, gbc)
    behaviourPanel
  }

  private def createTestMethodNamePanel() = {
    val namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT))
    namePanel.setBorder(BorderFactory.createTitledBorder("Test Method Naming"))
    namePanel.add(createInstructedTextEntryControl(methodNamePrefix, "Please specify your test method prefix or annotation (e.g. 'test' for JUnit, 'should' for JBehave or '@Test' for TestNG)"))
    namePanel
  }

  private def createTestClassNamePanel() = {
    val namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT))
    namePanel.setBorder(BorderFactory.createTitledBorder("Test Class Naming"))
    namePanel.add(createInstructedTextEntryControl(testName, "Template format: " + TemplateNameResolver.NAME_TOKEN + " surrounded by possible prefix and/or suffix, such as " + TemplateNameResolver.DEFAULT_TEMPLATE))
    namePanel
  }

  private def createInstructedTextEntryControl(widget: JTextField, instructions: String) = {
    val box = Box.createVerticalBox()
    box.add(createLeftAlignedBox(widget))
    box.add(createLeftAlignedBox(new JLabel(instructions)))
    box
  }

  private def createLeftAlignedBox(component: JComponent) = {
    val box = Box.createHorizontalBox()
    box.add(component)
    box.add(Box.createHorizontalGlue())
    box
  }

  private def createPackagingPanel() = {
    val packagingPanel = new JPanel(new BorderLayout())
    packagingPanel.setBorder(BorderFactory.createTitledBorder("Test Packaging"))
    packagingPanel.add(createPackagingControls(), BorderLayout.NORTH)
    packagingPanel.add(createPackageTable(), BorderLayout.CENTER)
    packagingPanel
  }

  private def createPackageTable() = {
    table.addMouseListener(new MouseAdapter() {
      override def mouseClicked(event: MouseEvent) { deleteRow() }
    })
    table.setAutoCreateColumnsFromModel(false)
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS)
    table.setShowVerticalLines(false)
    table.setColumnSelectionAllowed(true)
    table.setRowSelectionAllowed(true)
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    table.setDefaultRenderer(classOf[Icon], new IconCellRenderer())

    val iconColumn = table.getColumnModel.getColumn(1)
    iconColumn.setMaxWidth(20)
    iconColumn.setResizable(false)

    new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
  }

  private[config] def deleteRow() {
    if (table.isColumnSelected(1)) {
      model.setValueAt("", table.getSelectedRow, 0)
    }
  }

  private def createPackagingControls() = {
    val verticalBox = Box.createVerticalBox()
    verticalBox.add(createCustomPackagingOptionControl())
    verticalBox.add(Box.createVerticalStrut(10))
    verticalBox.add(createPackageInputControls())
    verticalBox.add(Box.createVerticalStrut(20))
    verticalBox
  }

  private def createPackageInputControls() = {
    val inputPanel = Box.createHorizontalBox()
    inputPanel.add(Box.createHorizontalStrut(5))
    inputPanel.add(createInputBox())
    inputPanel.add(Box.createHorizontalStrut(10))
    inputPanel.add(createAddButton())
    inputPanel.add(Box.createHorizontalGlue())
    inputPanel
  }

  private def createAddButton() = {
    val box = Box.createVerticalBox()
    addButton.addActionListener(new ActionListener() {
      def actionPerformed(event: ActionEvent) { addMapping() }
    })
    box.add(addButton)
    box.add(Box.createVerticalGlue())
    box
  }

  private[config] def addMapping() {
    model.addMapping(packageInputField.getText)
    packageInputField.setText("")
  }

  private def createInputBox() = {
    val box = Box.createVerticalBox()
    box.add(packageInputField)
    box.add(new JLabel("Template format: " + PACKAGE_TOKEN + " denotes the package of the current file, and " + POP_TOKEN + " pops a package level, e.g. " + PACKAGE_TOKEN + POP_TOKEN + "/test"))
    box.add(new JLabel("Remember: The current package of a source file is always checked for the corresponding test."))
    box
  }

  private def createCustomPackagingOptionControl() = {
    val box = Box.createHorizontalBox()
    allowCustom.addActionListener(new ActionListener() {
      def actionPerformed(event: ActionEvent) { togglePackageInputWidgetState() }
    })
    box.add(allowCustom)
    box.add(Box.createHorizontalGlue())
    box
  }

  private def togglePackageInputWidgetState() {
    packageInputField.setEnabled(allowCustom.isSelected)
    addButton.setEnabled(allowCustom.isSelected)
    table.setEnabled(allowCustom.isSelected)
  }
}
