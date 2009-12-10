package org.codehaus.testdox.intellij.panel;

import static jedi.functional.Coercions.asList;
import org.codehaus.testdox.intellij.*;
import org.codehaus.testdox.intellij.config.ConfigurationBean;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestDoxModel extends DefaultTableModel {

    private final ConfigurationBean configuration;

    private final List<TestElement> definitionOrderData = new ArrayList<TestElement>();
    private final List<TestElement> alphaOrderData = new ArrayList<TestElement>();
    private boolean hasDox;

    public TestDoxModel(ConfigurationBean configuration) {
        this.configuration = configuration;
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public Object getValueAt(int row, int column) {
        return getDox(row);
    }

    private Object getDox(int index) {
        if (index < 0 || index >= definitionOrderData.size()) {
            return TestDoxNonJavaFile.TEST_ELEMENT;
        }

        return configuration.isAlphabeticalSorting() ? alphaOrderData.get(index) : definitionOrderData.get(index);
    }

    public int getColumnCount() {
        return 1;
    }

    public Class getColumnClass(int columnIndex) {
        return TestElement.class;
    }

    public int getRowCount() {
        return definitionOrderData == null ? 0 : definitionOrderData.size();
    }

    public boolean hasDox() {
        return hasDox;
    }

    public void setNotJava() {
        clearLists();

        definitionOrderData.add(TestDoxNonJavaFile.TEST_ELEMENT);
        alphaOrderData.add(TestDoxNonJavaFile.TEST_ELEMENT);
        hasDox = false;

        fireDataChange();
    }

    public void setTestDoxForNonProjectClass(TestDoxFile file) {
        clearLists();

        hasDox = false;
        definitionOrderData.add(TestDoxNonProjectClass.TEST_ELEMENT);
        alphaOrderData.add(TestDoxNonProjectClass.TEST_ELEMENT);

        prependTestClassAndNotify(file);
    }

    public void setTestDoxForInterface(TestDoxFile file) {
        clearLists();

        hasDox = false;
        definitionOrderData.add(TestDoxInterface.TEST_ELEMENT);
        alphaOrderData.add(TestDoxInterface.TEST_ELEMENT);

        prependTestClassAndNotify(file);
    }

    public void setTestDoxForClass(TestDoxFile file) {
        clearLists();

        TestElement[] testMethods = file.getTestMethods();
        if (testMethods.length == 0) {
            hasDox = false;
            definitionOrderData.add(TestDoxClass.NO_DOX_ELEMENT);
            alphaOrderData.add(TestDoxClass.NO_DOX_ELEMENT);
        } else {
            hasDox = true;
            definitionOrderData.addAll(asList(testMethods));
            alphaOrderData.addAll(asList(testMethods));
            Collections.sort(alphaOrderData);
        }

        prependTestClassAndNotify(file);
    }

    private void prependTestClassAndNotify(TestDoxFile file) {
        TestElement testClass = file.getTestClass();
        definitionOrderData.add(0, testClass);
        alphaOrderData.add(0, testClass);
        fireDataChange();
    }

    private void fireDataChange() {
        fireTableDataChanged();
    }

    private void clearLists() {
        definitionOrderData.clear();
        alphaOrderData.clear();
    }

    public void sortInAlphabeticalOrder() {
        if (hasDox) {
            configuration.setAlphabeticalSorting(true);
            fireDataChange();
        }
    }

    public void sortInDefinitionOrder() {
        configuration.setAlphabeticalSorting(false);
        fireDataChange();
    }
}
