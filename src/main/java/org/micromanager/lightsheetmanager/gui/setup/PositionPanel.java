package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.TextField;

import javax.swing.JLabel;

public class PositionPanel extends Panel {

    private JLabel lblImagingCenterValue_;

    private JLabel lblSlicePositionValue_;
    private JLabel lblImagingPositionValue_;
    private JLabel lblIllumPositionValue_;


    private Button btnImagingCenterGo_;
    private Button btnImagingCenterSet_;

    private TextField txtSlicePosition_;
    private TextField txtImagingPosition_;
    private TextField txtIllumPosition_;

    private Button btnSliceZero_;
    private Button btnImagingZero_;

    private Button btnIllumGoHome_;
    private Button btnIllumSetHome_;

    private Button btnTestAcq_;

    public PositionPanel() {
        super("Positions");
        init();
    }

    private void init() {
        final JLabel lblImagingCenter = new JLabel("Imaging Center:");
        lblImagingCenterValue_ = new JLabel("0.0 μm");

        Button.setDefaultSize(50, 26);
        btnImagingCenterGo_ = new Button("Go");
        btnImagingCenterSet_ = new Button("Set");

        btnTestAcq_ = new Button("Test Acquisition", 120, 26);

        final JLabel lblSlicePosition = new JLabel("Slice Position:");
        final JLabel lblImagingPosition = new JLabel("Imaging Piezo:");
        txtSlicePosition_ = new TextField();
        txtImagingPosition_ = new TextField();

        Button.setDefaultSize(80, 26);
        btnSliceZero_ = new Button("Go to 0");
        btnImagingZero_ = new Button("Go to 0");

        final JLabel lblIllumPosition = new JLabel("Illumination Piezo:");
        txtIllumPosition_ = new TextField();

        btnIllumGoHome_ = new Button("Go Home");
        btnIllumSetHome_ = new Button("Set Home");

        lblSlicePositionValue_ = new JLabel("0.0 μm");
        lblImagingPositionValue_ = new JLabel("0.0 μm");
        lblIllumPositionValue_ = new JLabel("0.0 μm");

        createEventHandlers();

        add(lblImagingCenter, "");
        add(lblImagingCenterValue_, "");
        add(btnImagingCenterGo_, "");
        add(btnImagingCenterSet_, "split 2");
        add(btnTestAcq_, "wrap");

        add(lblSlicePosition, "");
        add(lblSlicePositionValue_, "");
        add(txtSlicePosition_, "");
        add(btnSliceZero_, "wrap");
        add(lblImagingPosition, "");
        add(lblImagingPositionValue_, "");
        add(txtImagingPosition_, "");
        add(btnImagingZero_, "wrap");
        add(lblIllumPosition, "");
        add(lblIllumPositionValue_, "");
        add(txtIllumPosition_, "");
        add(btnIllumGoHome_, "split 2");
        add(btnIllumSetHome_, "");
    }

    private void createEventHandlers() {

    }
}
