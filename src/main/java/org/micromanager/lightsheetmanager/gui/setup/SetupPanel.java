package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.Panel;

// Note: changes based on camera trigger mode

/**
 * A setup panel for diSPIM.
 */
public class SetupPanel extends Panel {

    private PiezoCalibrationPanel piezoPanel_;
    private BeamSheetControlPanel beamSheetPanel_;

    public SetupPanel() {

        piezoPanel_ = new PiezoCalibrationPanel();

        add(piezoPanel_, "");
    }
}
