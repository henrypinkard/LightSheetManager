package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.Panel;

// Note: changes based on camera trigger mode

/**
 * A setup panel for diSPIM.
 */
public class SetupPanel extends Panel {

    private PiezoCalibrationPanel piezoPanel_;
    private BeamSheetControlPanel beamSheetPanel_;

    private PositionPanel positionPanel_;

    private JoystickPanel joystickPanel_;
    private ExcitationPanel excitationPanel_;
    private CameraPanel cameraPanel_;

    private Panel leftPanel_;
    private Panel rightPanel_;

    private int pathNum_;

    public SetupPanel(final int pathNum) {
        pathNum_ = pathNum;
        leftPanel_ = new Panel();
        rightPanel_ = new Panel();

        piezoPanel_ = new PiezoCalibrationPanel();
        beamSheetPanel_ = new BeamSheetControlPanel();
        positionPanel_ = new PositionPanel();

        joystickPanel_ = new JoystickPanel();
        excitationPanel_ = new ExcitationPanel();
        cameraPanel_ = new CameraPanel();

        leftPanel_.add(joystickPanel_, "wrap");
        leftPanel_.add(excitationPanel_, "growx, wrap");
        leftPanel_.add(cameraPanel_, "growx, wrap");

        rightPanel_.add(positionPanel_, "");
        rightPanel_.add(piezoPanel_, "growy, wrap");
        rightPanel_.add(beamSheetPanel_, "span 2, growx, wrap");

        add(leftPanel_, "");
        add(rightPanel_, "aligny top");
    }

    public int getPathNum() {
        return pathNum_;
    }
}
