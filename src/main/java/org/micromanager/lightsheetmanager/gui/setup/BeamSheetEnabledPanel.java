package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;

import javax.swing.JLabel;

/**
 * Activate lasers and light sheet.
 */
public class BeamSheetEnabledPanel extends Panel {

    private CheckBox chkBeam_;
    private CheckBox chkSheet_;

    public BeamSheetEnabledPanel() {
        super("");
        init();
    }

    private void init() {
        final JLabel lblPathName = new JLabel("Excitation:");

        chkBeam_ = new CheckBox("Beam", false);
        chkSheet_ = new CheckBox("Sheet", false);

        createEventHandlers();

        add(lblPathName, "");
        add(chkBeam_, "");
        add(chkSheet_, "");
    }

    private void createEventHandlers() {

    }
}
