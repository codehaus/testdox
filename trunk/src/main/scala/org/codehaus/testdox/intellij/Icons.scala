package org.codehaus.testdox.intellij

import java.awt.image.BufferedImage
import javax.swing.{Icon, ImageIcon}
import scala.collection.mutable.HashMap

object Icons {

  val TESTDOX_ICON = "/icons/testdox.png"
  val REFRESH_ICON = "/icons/refresh.png"
  val RUN_ICON = "/icons/run.png"
  val SORT_ICON = "/icons/sort.png"
  val CLASS_ICON = "/icons/class.png"
  val INTERFACE_ICON = "/icons/interface.png"
  val DOX_ICON = "/icons/dox.png"
  val WARNING_ICON = "/icons/warning.png"
  val NO_TESTS_ICON = "/icons/notests.png"
  val NOT_JAVA_ICON = "/icons/notjava.png"
  val REMOVE_ICON = "/icons/remove.png"
  val AUTO_SCROLL_ICON = "/icons/autoscroll.png"
  val ADD_TEST_ICON = "/icons/method.png"
  val RENAME_ICON = "/icons/rename.png"
  val DELETE_ICON = "/icons/delete.png"
  val CLOSE_QUICKDOX_ICON = "/icons/close.png"
  val CLOSE_QUICKDOX_PRESSED_ICON = "/icons/close_pressed.png"
  val CLOSE_QUICKDOX_ROLLOVER_ICON = "/icons/close_rollover.png"

  private val ICON_CACHE = new HashMap[String, Icon]()
  private val LOCKED_ICON_CACHE = new HashMap[String, Icon]()

  def getIcon(path: String): Icon = {
      if (ICON_CACHE.isDefinedAt(path)) return ICON_CACHE.get(path).get.asInstanceOf[Icon]

      val icon = new ImageIcon(findURL(path))
      ICON_CACHE.put(path, icon)
      return icon
  }

  def getLockedIcon(path: String): Icon = {
      if (LOCKED_ICON_CACHE.isDefinedAt(path)) return LOCKED_ICON_CACHE.get(path).get.asInstanceOf[Icon]

      val icon = getIcon(path)
      var lockedIcon = getIcon("/icons/locked.png")

      val bufferedImage = new BufferedImage(lockedIcon.getIconWidth, lockedIcon.getIconHeight, BufferedImage.TYPE_4BYTE_ABGR)
      val g = bufferedImage.getGraphics
      g.drawImage(icon.asInstanceOf[ImageIcon].getImage, 0, 0, null)
      g.drawImage(lockedIcon.asInstanceOf[ImageIcon].getImage, 0, 0, null)

      lockedIcon = new ImageIcon(bufferedImage)
      LOCKED_ICON_CACHE.put(path, lockedIcon)
      return lockedIcon
  }

  private def findURL(path: String) = classOf[NameResolver].getResource(path)
}
