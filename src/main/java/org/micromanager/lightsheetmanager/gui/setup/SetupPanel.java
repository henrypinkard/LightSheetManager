package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.Panel;

// Note: changes based on camera trigger mode

/**
 * A setup panel for diSPIM.
 */
public class SetupPanel extends Panel {

    private PiezoCalibrationPanel piezoPanel_;
    private BeamSheetControlPanel beamSheetPanel_;

    private ExcitationPanel excitationPanel_;

    public SetupPanel() {

        piezoPanel_ = new PiezoCalibrationPanel();
        beamSheetPanel_ = new BeamSheetControlPanel();
        excitationPanel_ = new ExcitationPanel();

        add(piezoPanel_, "wrap");
        add(excitationPanel_, "wrap");
        add(beamSheetPanel_, "");
    }
}
