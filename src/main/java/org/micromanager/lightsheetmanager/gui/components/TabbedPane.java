package org.micromanager.lightsheetmanager.gui.components;

import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.Insets;

public class TabbedPane extends JTabbedPane {

    public TabbedPane() {
        super(JTabbedPane.TOP);
        setFocusable(false);
        uiSettings();
    }

    public TabbedPane(final int width, final int height) {
        super(JTabbedPane.TOP);
        setAbsoluteSize(width, height);
        setFocusable(false);
        uiSettings();
    }

    private void uiSettings() {
        // makes the ui look nice
        Insets insets = UIManager.getInsets("TabbedPane.contentBorderInsets");
        insets.top = -1;
        insets.bottom = 1;
        insets.right = 1;
        insets.left = 1;
        UIManager.put("TabbedPane.contentBorderInsets", insets);
    }

    public void setAbsoluteSize(final int width, final int height) {
        final Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
    }

}