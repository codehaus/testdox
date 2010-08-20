package org.codehaus.testdox.intellij.config

import org.specs.Specification

object ConfigurationPanelSpec extends Specification {

  val panel = new ConfigurationPanel()

  "ConfigurationPanel" should {

    "toggle the activation of entry widgets when the custom packaging checkbox is clicked" in {
      assertControls(false)

      panel.allowCustom.doClick
      assertControls(true)

      panel.allowCustom.doClick
      assertControls(false)
    }

    "remove the current row when the delete column is clicked" in {
      panel.packageInputField.setText("foo")
      panel.addMapping

      panel.table.changeSelection(0, 1, false, false)
      panel.deleteRow

      panel.table.getRowCount() must be equalTo 0
    }

    "not remove the current row if the mapping column is clicked" in {
      panel.packageInputField.setText("foo")
      panel.addMapping

      panel.table.changeSelection(0, 0, false, false)
      panel.deleteRow

      panel.table.getRowCount must be equalTo 1
    }
  }

  private def assertControls(enabled: Boolean) {
    panel.packageInputField.isEnabled must be equalTo enabled
    panel.addButton.isEnabled must be equalTo enabled
    panel.table.isEnabled must be equalTo enabled
    panel.customMappingStatus must be equalTo enabled
  }
}
