package org.codehaus.testdox.intellij.config

import javax.swing.Icon
import javax.swing.event.{TableModelEvent, TableModelListener}

import org.specs.SpecificationWithJUnit
import org.codehaus.testdox.intellij.Icons

object PackageTableModelSpec extends SpecificationWithJUnit {

  val model = new PackageTableModel()

  "PackageTableModel" should {

    "define that the first column contain strings and the second column contain icons" in {
      model.getColumnClass(0).getName must be equalTo classOf[String].getName
      model.getColumnClass(1).getName must be equalTo classOf[Icon].getName
    }

    "allow only the mapping column to be editable" in {
      model.isCellEditable(0, 0) must be equalTo true
      model.isCellEditable(0, 1) must be equalTo false
    }

    "return blank for remove column name" in {
      model.getColumnName(1) must be equalTo ""
    }

    "return remove as value for all rows in remove column" in {
      model.getValueAt(0, 1) must be equalTo Icons.getIcon(Icons.REMOVE_ICON)
    }

    "update table when a mapping is added" in {
      val listener = new MyTableModelListener()
      model.addTableModelListener(listener)

      model.getRowCount() must be equalTo 0

      model.addMapping("foo")

      model.getRowCount() must be equalTo 1
      listener.tableChanged must be equalTo true
    }

    "not add a new mapping if the mapping already exists" in {
      model.addMapping("foo")
      model.getRowCount() must be equalTo 1

      val listener = new MyTableModelListener()
      model.addTableModelListener(listener)
      model.addMapping("foo")

      model.getRowCount() must be equalTo 1
      listener.tableChanged must be equalTo false
    }

    "remove a row from the table if the new value is blank" in {
      model.addMapping("foo")
      model.getRowCount() must be equalTo 1

      val listener = new MyTableModelListener()
      model.addTableModelListener(listener)
      model.setValueAt("", 0, 0)

      model.getRowCount() must be equalTo 0
      listener.tableChanged must be equalTo true
    }
  }

  class MyTableModelListener extends TableModelListener {
    var tableChanged: Boolean = _

    def tableChanged(event: TableModelEvent) {
      tableChanged = true
    }
  }
}
