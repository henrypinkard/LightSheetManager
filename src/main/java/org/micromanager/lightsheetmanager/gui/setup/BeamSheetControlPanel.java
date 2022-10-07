package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.TextField;

import javax.swing.JLabel;

public class BeamSheetControlPanel extends Panel {

    private TextField txtSlope_;
    private TextField txtOffset_;

    public BeamSheetControlPanel() {
        init();
    }

    private void init() {
        final JLabel lblIllumPiezo = new JLabel("Illumination Piezo:");

        final JLabel lblSync = new JLabel("Light Sheet Synchronization");

        final JLabel lblSlope = new JLabel("Speed / slope");
        final JLabel lblOffset = new JLabel("Start / offset");
        final JLabel lblSlopeUnits = new JLabel("μ°/px");
        final JLabel lblOffsetUnits = new JLabel("m°");

        txtSlope_ = new TextField();
        txtOffset_ = new TextField();

        createEventHandlers();

        add(lblIllumPiezo, "");
    }

    private void createEventHandlers() {

    }

}
