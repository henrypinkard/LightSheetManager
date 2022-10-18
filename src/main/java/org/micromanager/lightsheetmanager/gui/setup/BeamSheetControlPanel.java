package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.TextField;

import javax.swing.JLabel;

public class BeamSheetControlPanel extends Panel {

    // first panel
    private TextField txtSlope_;
    private TextField txtOffset_;
    private Button btnPlotProfile_;

    // second panel
    private CheckBox chkAutoSheetWidth_;
    private TextField txtSheetWidth_;
    private Button btnCenterOffset_;

    private Panel firstPanel_;  // virtual slit camera trigger mode active
    private Panel secondPanel_; // all other camera trigger modes

    public BeamSheetControlPanel() {
        super("Light Sheet Synchronization");
        init();
    }

    private void init() {
        firstPanel_ = new Panel();
        secondPanel_ = new Panel();

        // first panel for virtual slit mode
        final JLabel lblSync = new JLabel("Light Sheet Synchronization:");
        final JLabel lblSlope = new JLabel("Speed / slope:");
        final JLabel lblOffset = new JLabel("Start / offset:");
        final JLabel lblSlopeUnits = new JLabel("μ°/px");
        final JLabel lblOffsetUnits = new JLabel("m°");

        btnPlotProfile_ = new Button("Plot Profile", 100, 26);
        txtSlope_ = new TextField();
        txtOffset_ = new TextField();

        // second panel for other camera trigger modes

        final JLabel lblSheetWidth = new JLabel("Sheet width:");
        final JLabel lblSheetOffset = new JLabel("Sheet offset:");
        final JLabel lblSlopeUnits2 = new JLabel("μ°/px"); // TODO: reuse labels like this?

        chkAutoSheetWidth_ = new CheckBox("Automatic", false);
        txtSheetWidth_ = new TextField();
        btnCenterOffset_ = new Button("Center", 100, 26);

        createEventHandlers();

        firstPanel_.add(lblSync, "wrap");
        firstPanel_.add(lblSlope, "split 3");
        firstPanel_.add(txtSlope_, "");
        firstPanel_.add(lblSlopeUnits, "wrap");
        firstPanel_.add(lblOffset, "split 3");
        firstPanel_.add(txtOffset_, "");
        firstPanel_.add(lblOffsetUnits, "");
        firstPanel_.add(btnPlotProfile_, "");

        secondPanel_.add(lblSheetWidth, "");
        secondPanel_.add(chkAutoSheetWidth_, "");
        secondPanel_.add(txtSheetWidth_, "");
        secondPanel_.add(lblSlopeUnits2, "wrap");
        secondPanel_.add(lblSheetOffset, "");
        secondPanel_.add(btnCenterOffset_, "");

        // TODO: select panel based on Microscope Geometry
        add(secondPanel_, "");
    }

    private void createEventHandlers() {
        // first panel
        btnPlotProfile_.registerListener(e -> {

        });

        // second panel
        chkAutoSheetWidth_.registerListener(e -> {

        });

        btnCenterOffset_.registerListener(e -> {

        });

    }

}
