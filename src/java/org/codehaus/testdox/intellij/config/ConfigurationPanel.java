package org.codehaus.testdox.intellij.config;

import org.codehaus.testdox.intellij.PackageManager;
import org.codehaus.testdox.intellij.TemplateNameResolver;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ConfigurationPanel extends JPanel implements ConfigurationUI {

    protected final PackageTableModel model = new PackageTableModel();

    protected JCheckBox allowCustom;
    protected JTextField packageInputField;
    protected JButton addButton;
    protected JTable table;

    private JTextField testName;
    private JCheckBox createTestIfMissing;
    private JCheckBox useUnderscore;
    private JCheckBox showFullyQualifiedClassName;
    private JCheckBox autoApplyChangesToTests;
    private JCheckBox deletePackageOccurrences;
    private JTextField methodNamePrefix;

    public ConfigurationPanel() {
        setLayout(new BorderLayout());
        Box box = Box.createVerticalBox();
        box.add(createBehaviourPanel());
        box.add(Box.createVerticalStrut(10));
        box.add(createTestMethodNamePanel());
        box.add(Box.createVerticalStrut(10));
        box.add(createTestClassNamePanel());
        box.add(Box.createVerticalStrut(10));
        box.add(createPackagingPanel());
        add(box, BorderLayout.CENTER);
        togglePackageInputWidgetState();
    }

    private Component createBehaviourPanel() {
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 2, 2);
        JPanel behaviourPanel = new JPanel(new GridBagLayout());
        behaviourPanel.setBorder(BorderFactory.createTitledBorder("Behaviour"));
        behaviourPanel.add(createUseUnderscoreControl(), gbc);
        gbc.gridy++;
        behaviourPanel.add(createShowFullyQualifiedClassNameControl(), gbc);
        gbc.gridy++;
        behaviourPanel.add(createCreateTestControl(), gbc);
        gbc.gridy++;
        behaviourPanel.add(createAutoRenameTestControl(), gbc);
        gbc.gridy++;
        behaviourPanel.add(createDeletePackageOccurrencesControl(), gbc);
        return behaviourPanel;
    }

    private Component createUseUnderscoreControl() {
        useUnderscore = new JCheckBox("Use underscore (_) as acronym delimiter");
        return useUnderscore;
    }

    private Component createShowFullyQualifiedClassNameControl() {
        showFullyQualifiedClassName = new JCheckBox("Show fully qualified class name for tested classes");
        return showFullyQualifiedClassName;
    }

    private Component createCreateTestControl() {
        createTestIfMissing = new JCheckBox("Prompt to create missing test classes");
        return createTestIfMissing;
    }

    private Component createAutoRenameTestControl() {
        autoApplyChangesToTests = new JCheckBox("When manually deleting/moving/renaming a class, automatically delete/move/rename the associated test class");
        return autoApplyChangesToTests;
    }

    private Component createDeletePackageOccurrencesControl() {
        deletePackageOccurrences = new JCheckBox("When manually deleting a package, prompt to delete other occurrences of that package");
        return deletePackageOccurrences;
    }

    private Component createTestMethodNamePanel() {
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBorder(BorderFactory.createTitledBorder("Test Method Naming"));
        methodNamePrefix = new JTextField(50);
        namePanel.add(createInstructedTextEntryControl(methodNamePrefix, "Please specify your test method annotation or prefix (e.g. '@Test' for JUnit 4 / TestNG and 'test' for JUnit 3)"));
        return namePanel;
    }

    private Component createTestClassNamePanel() {
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBorder(BorderFactory.createTitledBorder("Test Class Naming"));
        testName = new JTextField(50);
        namePanel.add(createInstructedTextEntryControl(testName, "Template format: " + TemplateNameResolver.NAME_TOKEN + " surrounded by possible prefix and/or suffix, such as " + TemplateNameResolver.DEFAULT_TEMPLATE));
        return namePanel;
    }

    private Component createInstructedTextEntryControl(JTextField widget, String instructions) {
        Box nameWidget = createLeftAlignedBox(widget);
        Box instructionsBox = createLeftAlignedBox(new JLabel(instructions));
        Box box = Box.createVerticalBox();
        box.add(nameWidget);
        box.add(instructionsBox);
        return box;
    }

    private Box createLeftAlignedBox(JComponent comp) {
        Box box = Box.createHorizontalBox();
        box.add(comp);
        box.add(Box.createHorizontalGlue());
        return box;
    }

    private Component createPackagingPanel() {
        JPanel packagingPanel = new JPanel(new BorderLayout());
        packagingPanel.setBorder(BorderFactory.createTitledBorder("Test Packaging"));
        packagingPanel.add(createPackagingControls(), BorderLayout.NORTH);
        packagingPanel.add(createPackageTable(), BorderLayout.CENTER);
        return packagingPanel;
    }

    private JScrollPane createPackageTable() {
        table = new JTable(model);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                deleteRow();
            }
        });
        table.setAutoCreateColumnsFromModel(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setShowVerticalLines(false);
        table.setColumnSelectionAllowed(true);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Icon.class, new IconCellRenderer());

        TableColumn iconCol = table.getColumnModel().getColumn(1);
        iconCol.setMaxWidth(20);
        iconCol.setResizable(false);

        return new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    void deleteRow() {
        if (table.isColumnSelected(1)) {
            model.setValueAt("", table.getSelectedRow(), 0);
        }
    }

    private Box createPackagingControls() {
        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(createCustomPackagingOptionControl());
        verticalBox.add(Box.createVerticalStrut(10));
        verticalBox.add(createPackageInputControls());
        verticalBox.add(Box.createVerticalStrut(20));
        return verticalBox;
    }

    private Box createPackageInputControls() {
        Box inputPanel = Box.createHorizontalBox();
        inputPanel.add(Box.createHorizontalStrut(5));
        inputPanel.add(createInputBox());
        inputPanel.add(Box.createHorizontalStrut(10));
        inputPanel.add(createAddButton());
        inputPanel.add(Box.createHorizontalGlue());
        return inputPanel;
    }

    private Component createAddButton() {
        Box box = Box.createVerticalBox();
        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                addMapping();
            }
        });
        box.add(addButton);
        box.add(Box.createVerticalGlue());
        return box;
    }

    void addMapping() {
        model.addMapping(packageInputField.getText());
        packageInputField.setText("");
    }

    private Component createInputBox() {
        Box box = Box.createVerticalBox();
        packageInputField = new JTextField(20);
        box.add(packageInputField);
        box.add(new JLabel("Template format: " + PackageManager.PACKAGE_TOKEN + " denotes the package of the current file, and " + PackageManager.POP_TOKEN + " pops a package level, e.g. " + PackageManager.PACKAGE_TOKEN + PackageManager.POP_TOKEN + "/test"));
        box.add(new JLabel("Remember: The current package of a source file is always checked for the corresponding test."));
        return box;
    }

    private Component createCustomPackagingOptionControl() {
        Box box = Box.createHorizontalBox();
        allowCustom = new JCheckBox("Allow custom test packages");
        allowCustom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                togglePackageInputWidgetState();
            }
        });
        box.add(allowCustom);
        box.add(Box.createHorizontalGlue());
        return box;
    }

    private void togglePackageInputWidgetState() {
        packageInputField.setEnabled(allowCustom.isSelected());
        addButton.setEnabled(allowCustom.isSelected());
        table.setEnabled(allowCustom.isSelected());
    }

    public void setCustomPackageMappings(List<String> mappings) {
        model.setMappings(mappings);
    }

    public List<String> getCustomPackageMappings() {
        return model.getMappings();
    }

    public void setCustomMappingStatus(boolean active) {
        allowCustom.setSelected(active);
        togglePackageInputWidgetState();
    }

    public boolean getCustomMappingStatus() {
        return allowCustom.isSelected();
    }

    public void setTestNameTemplate(String template) {
        testName.setText(template);
    }

    public String getTestNameTemplate() {
        return testName.getText();
    }

    public void setTestMethodPrefix(String prefix) {
        methodNamePrefix.setText(prefix);
    }

    public String getTestMethodPrefix() {
        return methodNamePrefix.getText();
    }

    public boolean getCreateTestIfMissing() {
        return createTestIfMissing.isSelected();
    }

    public void setCreateTestIfMissing(boolean createTestIfMissing) {
        this.createTestIfMissing.setSelected(createTestIfMissing);
    }

    public boolean getUseUnderscore() {
        return useUnderscore.isSelected();
    }

    public void setUseUnderscore(boolean userUnderscore) {
        this.useUnderscore.setSelected(userUnderscore);
    }

    public boolean getShowFullyQualifiedClassName() {
        return showFullyQualifiedClassName.isSelected();
    }

    public void setShowFullyQualifiedClassName(boolean showFullyQualifiedClassName) {
        this.showFullyQualifiedClassName.setSelected(showFullyQualifiedClassName);
    }

    public boolean getAutoApplyChangesToTest() {
        return autoApplyChangesToTests.isSelected();
    }

    public void setAutoApplyChangesToTest(boolean autoMirrorChanges) {
        this.autoApplyChangesToTests.setSelected(autoMirrorChanges);
    }

    public boolean getDeletePackageOccurrences() {
        return deletePackageOccurrences.isSelected();
    }

    public void setDeletePackageOccurrences(boolean deletePackageOccurrences) {
        this.deletePackageOccurrences.setSelected(deletePackageOccurrences);
    }
}
