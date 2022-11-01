package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;

import javax.swing.JLabel;

public class SettingsTab extends Panel {

    public SettingsTab() {
        createUserInterface();
    }

    private void createUserInterface() {
        JLabel lblTitle = new JLabel("Settings");

        createEventHandlers();

        add(lblTitle, "wrap");
    }

    private void createEventHandlers() {

    }
}
