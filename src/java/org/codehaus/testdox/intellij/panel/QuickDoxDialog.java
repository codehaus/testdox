package org.codehaus.testdox.intellij.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.codehaus.testdox.intellij.EditorApi;
import org.codehaus.testdox.intellij.IconHelper;
import org.codehaus.testdox.intellij.TestElement;
import org.codehaus.testdox.intellij.TestMethod;
import org.codehaus.testdox.intellij.config.ConfigurationBean;

public class QuickDoxDialog implements TableModelListener, PropertyChangeListener {

    private static final String TOOLTIP_YELLOW_WEB = "FFFFE1";
    private static final String TOOLTIP_YELLOW = "16777185";
    private static final Rectangle DEFAULT_BOUNDS = screenSizeAsRectangle();

    private static Rectangle screenSizeAsRectangle() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Rectangle(0, 0, screenSize.width, screenSize.height);
    }

    private final EditorApi editorApi;
    private final TestDoxModel model;
    private final QuickDoxWindow window;

    private Point currentPosition;
    private boolean visible;

    public QuickDoxDialog(Window owner, EditorApi editorApi, TestDoxModel model, ConfigurationBean configuration) {
        this.editorApi = editorApi;
        this.model = model;

        model.addTableModelListener(this);
        configuration.addPropertyChangeListener(this);

        window = new QuickDoxWindow(owner, model);
        window.pack();

        positionComponent((owner != null) ? owner.getBounds() : DEFAULT_BOUNDS, window);
    }

    public boolean isVisible() {
        return visible;
    }

    public void show() {
        window.setVisible(true);
        visible = true;
        editorApi.activateSelectedTextEditor();
    }

    public void hide() {
        window.setVisible(false);
        window.dispose();
        visible = false;
        editorApi.activateSelectedTextEditor();
    }

    public void tableChanged(TableModelEvent event) {
        updateFromModel();
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (ConfigurationBean.SHOW_FULLY_QUALIFIED_CLASS_NAME.equals(event.getPropertyName())) {
            updateFromModel();
        }
    }

    private void updateFromModel() {
        window.setContents(model);
    }

    private void positionComponent(Rectangle ownerBounds, Component child) {
        if (currentPosition == null) {
            double x = ownerBounds.x + (ownerBounds.width - child.getWidth()) / 2;
            double y = ownerBounds.y + (ownerBounds.height - child.getHeight()) / 2;
            child.setLocation((int) x, (int) y);
            currentPosition = child.getLocation();
        } else {
            child.setLocation(currentPosition.x, currentPosition.y);
        }
    }

    private String createHTML(TestDoxModel model) {
        StringBuffer html = new StringBuffer("<html><body bgcolor=\"" + TOOLTIP_YELLOW_WEB + "\">");
        html.append(((TestElement) model.getValueAt(0, 0)).getDisplayString());
        if (model.getRowCount() > 1) {
            for (int i = 1; i < model.getRowCount(); i++) {
                renderTestElement(html, (TestElement) model.getValueAt(i, 0));
            }
        }
        html.append("</body></html>");
        return html.toString();
    }

    private void renderTestElement(StringBuffer html, TestElement testElement) {
        html.append("<br>");
        if (testElement instanceof TestMethod) {
            html.append("&nbsp;&#10004;&nbsp;");
        }
        html.append(testElement.getDisplayString());
    }

    private class QuickDoxWindow extends JWindow {

        private static final int BOUNDS_CONSTANT = 12;
        private final JLabel contents = new JLabel();

        private QuickDoxWindow(Window owner, TestDoxModel model) {
            super(owner);
            initContents(model);
            initListeners();
        }

        private void initContents(TestDoxModel model) {
            JPanel mainPanel = createMainPanel();
            JButton closeButton = createCloseButton();

            JPanel buttonPanel = new JPanel(new BorderLayout());
            buttonPanel.setBackground(Color.decode(TOOLTIP_YELLOW));
            buttonPanel.add(closeButton, BorderLayout.EAST);

            mainPanel.add(contents, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.NORTH);
            mainPanel.add(Box.createHorizontalStrut(10), BorderLayout.EAST);
            mainPanel.add(Box.createHorizontalStrut(10), BorderLayout.WEST);
            mainPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
            getContentPane().add(mainPanel);

            setContents(model);

            closeButton.setBounds(getWidth() - (BOUNDS_CONSTANT + 1), 1, BOUNDS_CONSTANT, BOUNDS_CONSTANT - 1);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    QuickDoxDialog.this.hide();
                }
            });
        }

        private JPanel createMainPanel() {
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.decode(TOOLTIP_YELLOW));
            mainPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
            return mainPanel;
        }

        private JButton createCloseButton() {
            JButton closeButton = new JButton();
            closeButton.setToolTipText("<html>Close QuickDox</html>");
            closeButton.setOpaque(true);
            closeButton.setBorder(null);
            closeButton.setIcon(loadIcon(IconHelper.CLOSE_QUICKDOX_ICON));
            closeButton.setPressedIcon(loadIcon(IconHelper.CLOSE_QUICKDOX_PRESSED_ICON));
            closeButton.setRolloverIcon(loadIcon(IconHelper.CLOSE_QUICKDOX_ROLLOVER_ICON));
            return closeButton;
        }

        private ImageIcon loadIcon(String closeQuickdoxIcon) {
            return new ImageIcon(getClass().getResource(closeQuickdoxIcon));
        }

        private void initListeners() {
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent event) {
                    currentPosition = event.getPoint();
                }

                public void mouseClicked(MouseEvent event) {
                    if (event.getClickCount() > 1) {
                        QuickDoxDialog.this.hide();
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent event) {
                    int i = (getLocation().x - currentPosition.x) + event.getPoint().x;
                    int j = (getLocation().y - currentPosition.y) + event.getPoint().y;
                    setLocation(i, j);
                    editorApi.activateSelectedTextEditor();
                }
            });
        }

        private void setContents(TestDoxModel model) {
            contents.setText(createHTML(model));
            Dimension dimension = contents.getPreferredSize();
            contents.setBounds(BOUNDS_CONSTANT, BOUNDS_CONSTANT, dimension.width, dimension.height);
            setSize(dimension.width + (2 * BOUNDS_CONSTANT), dimension.height + (2 * BOUNDS_CONSTANT));
        }
    }
}
