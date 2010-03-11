package org.codehaus.testdox.intellij.ui

import java.awt._
import java.awt.event._
import java.beans.{PropertyChangeEvent, PropertyChangeListener}
import javax.swing._
import javax.swing.event.{TableModelEvent, TableModelListener}

import org.codehaus.testdox.intellij.config.Configuration
import org.codehaus.testdox.intellij.{Icons, TestMethod, TestElement, EditorApi}

class QuickDoxDialog(owner: Window, private val editorApi: EditorApi, private val model: TestDoxTableModel, configuration: Configuration)
    extends TableModelListener with PropertyChangeListener {

  private var currentPosition: Point =_
  private var visible: Boolean =_

  model.addTableModelListener(this)
  configuration.addPropertyChangeListener(this)

  private val window = new QuickDoxWindow(owner, model)
  window.pack()

  positionComponent(if (owner != null) owner.getBounds() else QuickDoxDialog.DEFAULT_BOUNDS, window)

  def isVisible() = visible

  def show() {
    window.setVisible(true)
    visible = true
    editorApi.activateSelectedTextEditor()
  }

  def hide() {
    window.setVisible(false)
    window.dispose()
    visible = false
    editorApi.activateSelectedTextEditor()
  }

  def tableChanged(event: TableModelEvent) {
    updateFromModel()
  }

  def propertyChange(event: PropertyChangeEvent) {
    if (Configuration.SHOW_FULLY_QUALIFIED_CLASS_NAME.equals(event.getPropertyName())) {
      updateFromModel()
    }
  }

  private def updateFromModel() = window.setContents(model)

  private def positionComponent(ownerBounds: Rectangle, child: Component) {
    if (currentPosition == null) {
      val x = ownerBounds.x + (ownerBounds.width - child.getWidth()) / 2
      val y = ownerBounds.y + (ownerBounds.height - child.getHeight()) / 2
      child.setLocation(x, y)
      currentPosition = child.getLocation()
    } else {
      child.setLocation(currentPosition.x, currentPosition.y)
    }
  }

  private def createHTML(model: TestDoxTableModel): String = {
    val html = new StringBuilder("<html><body bgcolor=\"" + QuickDoxDialog.TOOLTIP_YELLOW_WEB + "\">")
    html.append(model.getValueAt(0, 0).asInstanceOf[TestElement].displayString)
    if (model.getRowCount() > 1) {
      for (i <- 1 until model.getRowCount()) {
        renderTestElement(html, model.getValueAt(i, 0).asInstanceOf[TestElement])
      }
    }
    html.append("</body></html>")
    html.toString()
  }

  private def renderTestElement(html: StringBuilder, testElement: TestElement) {
    html.append("<br>")
    if (testElement.isInstanceOf[TestMethod]) html.append("&nbsp;&#10004;&nbsp;")
    html.append(testElement.displayString)
  }

  private class QuickDoxWindow(owner: Window, model: TestDoxTableModel) extends JWindow(owner) {

      private val BOUNDS_CONSTANT = 12
      private val contents = new JLabel()

      initContents(model)
      initListeners()

      private def initContents(model: TestDoxTableModel) {
          val mainPanel = createMainPanel()
          val closeButton = createCloseButton()

          val buttonPanel = new JPanel(new BorderLayout())
          buttonPanel.setBackground(Color.decode(QuickDoxDialog.TOOLTIP_YELLOW))
          buttonPanel.add(closeButton, BorderLayout.EAST)

          mainPanel.add(contents, BorderLayout.CENTER)
          mainPanel.add(buttonPanel, BorderLayout.NORTH)
          mainPanel.add(Box.createHorizontalStrut(10), BorderLayout.EAST)
          mainPanel.add(Box.createHorizontalStrut(10), BorderLayout.WEST)
          mainPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH)
          getContentPane().add(mainPanel)

          setContents(model)

          closeButton.setBounds(getWidth() - (BOUNDS_CONSTANT + 1), 1, BOUNDS_CONSTANT, BOUNDS_CONSTANT - 1)
          closeButton.addActionListener(new ActionListener() {
              def actionPerformed(event: ActionEvent) {
                  QuickDoxDialog.this.hide()
              }
          })
      }

      private def createMainPanel() = {
          val mainPanel = new JPanel(new BorderLayout())
          mainPanel.setBackground(Color.decode(QuickDoxDialog.TOOLTIP_YELLOW))
          mainPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1))
          mainPanel
      }

      private def createCloseButton() = {
          val closeButton = new JButton()
          closeButton.setToolTipText("<html>Close QuickDox</html>")
          closeButton.setOpaque(true)
          closeButton.setBorder(null)
          closeButton.setIcon(loadIcon(Icons.CLOSE_QUICKDOX_ICON))
          closeButton.setPressedIcon(loadIcon(Icons.CLOSE_QUICKDOX_PRESSED_ICON))
          closeButton.setRolloverIcon(loadIcon(Icons.CLOSE_QUICKDOX_ROLLOVER_ICON))
          closeButton
      }

      private def loadIcon(closeQuickdoxIcon: String) = new ImageIcon(getClass().getResource(closeQuickdoxIcon))

      private def initListeners() {
          addMouseListener(new MouseAdapter() {
              override def mousePressed(event: MouseEvent) = currentPosition = event.getPoint()
              override def mouseClicked(event: MouseEvent) = if (event.getClickCount() > 1) QuickDoxDialog.this.hide()
          })

          addMouseMotionListener(new MouseMotionAdapter() {
              override def mouseDragged(event: MouseEvent) {
                  val i = (getLocation().x - currentPosition.x) + event.getPoint().x;
                  val j = (getLocation().y - currentPosition.y) + event.getPoint().y;
                  setLocation(i, j)
                  editorApi.activateSelectedTextEditor()
              }
          })
      }

      private[QuickDoxDialog] def setContents(model: TestDoxTableModel) {
          contents.setText(createHTML(model))
          val dimension = contents.getPreferredSize()
          contents.setBounds(BOUNDS_CONSTANT, BOUNDS_CONSTANT, dimension.width, dimension.height)
          setSize(dimension.width + (2 * BOUNDS_CONSTANT), dimension.height + (2 * BOUNDS_CONSTANT))
      }
  }
}

object QuickDoxDialog {

  private val TOOLTIP_YELLOW_WEB = "FFFFE1"
  private val TOOLTIP_YELLOW = "16777185"
  private val DEFAULT_BOUNDS = screenSizeAsRectangle()

  private def screenSizeAsRectangle() = {
    val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
    new Rectangle(0, 0, screenSize.width, screenSize.height)
  }
}
