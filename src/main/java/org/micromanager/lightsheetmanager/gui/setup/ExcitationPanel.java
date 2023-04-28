package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;

import javax.swing.JLabel;

/**
 * Activate lasers and light sheet.
 */
public class ExcitationPanel extends Panel {

    private CheckBox cbxBeam_;
    private CheckBox cbxSheet_;

    public ExcitationPanel() {
        super("Scanner");
        init();
    }

    private void init() {
        final JLabel lblExcitation = new JLabel("Excitation:");

        cbxBeam_ = new CheckBox("Beam", false);
        cbxSheet_ = new CheckBox("Sheet", false);

        createEventHandlers();

        add(lblExcitation, "");
        add(cbxBeam_, "");
        add(cbxSheet_, "");
    }

    private void createEventHandlers() {

    }
}
