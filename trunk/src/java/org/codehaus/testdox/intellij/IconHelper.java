package org.codehaus.testdox.intellij;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class IconHelper {

    public static final String TESTDOX_ICON = "/icons/testdox.png";
    public static final String REFRESH_ICON = "/icons/refresh.png";
    public static final String RUN_ICON = "/icons/run.png";
    public static final String SORT_ICON = "/icons/sort.png";
    public static final String CLASS_ICON = "/icons/class.png";
    public static final String INTERFACE_ICON = "/icons/interface.png";
    public static final String DOX_ICON = "/icons/dox.png";
    public static final String WARNING_ICON = "/icons/warning.png";
    public static final String NO_TESTS_ICON = "/icons/notests.png";
    public static final String NOT_JAVA_ICON = "/icons/notjava.png";
    public static final String REMOVE_ICON = "/icons/remove.png";
    public static final String AUTO_SCROLL_ICON = "/icons/autoscroll.png";
    public static final String ADD_TEST_ICON = "/icons/method.png";
    public static final String RENAME_ICON = "/icons/rename.png";
    public static final String DELETE_ICON = "/icons/delete.png";
    public static final String CLOSE_QUICKDOX_ICON = "/icons/close.png";
    public static final String CLOSE_QUICKDOX_PRESSED_ICON = "/icons/close_pressed.png";
    public static final String CLOSE_QUICKDOX_ROLLOVER_ICON = "/icons/close_rollover.png";

    private static final Map ICON_CACHE = new HashMap();
    private static final Map LOCKED_ICON_CACHE = new HashMap();

	public static Icon getIcon(String path) {
        if (ICON_CACHE.containsKey(path)) {
            return (Icon) ICON_CACHE.get(path);
        }

        Icon icon = new ImageIcon(findURL(path));
        ICON_CACHE.put(path, icon);
        return icon;
    }

    public static Icon getLockedIcon(String path) {
        if (LOCKED_ICON_CACHE.containsKey(path)) {
            return (Icon) LOCKED_ICON_CACHE.get(path);
        }

        Icon icon = getIcon(path);
        Icon lockedIcon = getIcon("/icons/locked.png");

        BufferedImage bufferedImage = new BufferedImage(lockedIcon.getIconWidth(), lockedIcon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = bufferedImage.getGraphics();
        g.drawImage(((ImageIcon) icon).getImage(), 0, 0, null);
        g.drawImage(((ImageIcon) lockedIcon).getImage(), 0, 0, null);

        lockedIcon = new ImageIcon(bufferedImage);
        LOCKED_ICON_CACHE.put(path, lockedIcon);
        return lockedIcon;
    }

    private static URL findURL(String path) {
        return IconHelper.class.getResource(path);
    }
}
