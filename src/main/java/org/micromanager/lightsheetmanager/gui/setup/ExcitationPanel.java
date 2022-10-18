package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;

import javax.swing.JLabel;

/**
 * Activate lasers and light sheet.
 */
public class ExcitationPanel extends Panel {

    private CheckBox chkBeam_;
    private CheckBox chkSheet_;

    public ExcitationPanel() {
        super("Scanner");
        init();
    }

    private void init() {
        final JLabel lblExcitation = new JLabel("Excitation:");

        chkBeam_ = new CheckBox("Beam", false);
        chkSheet_ = new CheckBox("Sheet", false);

        createEventHandlers();

        add(lblExcitation, "");
        add(chkBeam_, "");
        add(chkSheet_, "");
    }

    private void createEventHandlers() {

    }
}
