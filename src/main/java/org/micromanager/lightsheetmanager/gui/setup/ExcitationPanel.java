package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;

/**
 * Activate lasers and light sheet.
 */
public class ExcitationPanel extends Panel {

    private CheckBox chkBeam_;
    private CheckBox chkSheet_;

    public ExcitationPanel() {
        super("Excitation");
        init();
    }

    private void init() {

        chkBeam_ = new CheckBox("Beam", false);
        chkSheet_ = new CheckBox("Sheet", false);

        createEventHandlers();

        add(chkBeam_, "");
        add(chkSheet_, "");
    }

    private void createEventHandlers() {

    }
}
