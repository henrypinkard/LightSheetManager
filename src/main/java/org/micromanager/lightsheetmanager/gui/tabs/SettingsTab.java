package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.gui.components.Panel;

import javax.swing.JLabel;

public class SettingsTab extends Panel {

    public SettingsTab() {
        init();
    }

    public void init() {
        JLabel lblTitle = new JLabel("Settings");
        add(lblTitle, "");
    }
}
