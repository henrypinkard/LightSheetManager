package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;

import javax.swing.JLabel;

public class SettingsTab extends Panel {

    private Spinner spnScanAcceleration_;
    private Spinner  spnScanOvershootDist_;
    private Spinner spnScanRetraceSpeed_;
    private Spinner spnScanAngleFirstView_;

    private CheckBox cbxScanFromCurrentPosition_;
    private CheckBox cbxScanNegativeDirection_;
    private CheckBox cbxReturnToOriginalPosition_;

    public SettingsTab() {
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {

        final Panel pnlScan = new Panel("Stage Scan Settings");
        pnlScan.setMigLayout("", "[]5[]", "[]5[]");

        final JLabel lblScanAcceleration = new JLabel("Relative acceleration time:");
        final JLabel lblScanOvershootDist = new JLabel("Scan overshoot distance [" + "\u00B5"+ "m]:");
        final JLabel lblScanRetraceSpeed = new JLabel("Scan retrace speed [% of max]:");
        final JLabel lblScanAngleFirstView = new JLabel("Path A stage/objective angle [\u00B0]:");

        spnScanAcceleration_ = Spinner.createFloatSpinner(1.0f, 0.1f, 1000.0f, 1.0f);
        spnScanOvershootDist_ = Spinner.createIntegerSpinner(0,0, 1000, 10);
        spnScanRetraceSpeed_ = Spinner.createFloatSpinner(67.0f,0.01f, 99.0f, 1.0f);
        spnScanAngleFirstView_ = Spinner.createFloatSpinner(45.0f,1.0f, 89.0f, 1.0f);

        cbxScanFromCurrentPosition_ = new CheckBox("Scan from current position instead of center", false);
        cbxScanNegativeDirection_ = new CheckBox("Scan negative direction", false);
        cbxReturnToOriginalPosition_ = new CheckBox("Return to original position after scan", false);

        pnlScan.add(lblScanAcceleration, "");
        pnlScan.add(spnScanAcceleration_, "wrap");
        pnlScan.add(lblScanOvershootDist, "");
        pnlScan.add(spnScanOvershootDist_, "wrap");
        pnlScan.add(lblScanRetraceSpeed, "");
        pnlScan.add(spnScanRetraceSpeed_, "wrap");
        pnlScan.add(lblScanAngleFirstView, "");
        pnlScan.add(spnScanAngleFirstView_, "wrap");

        pnlScan.add(cbxScanFromCurrentPosition_, "wrap");
        pnlScan.add(cbxScanNegativeDirection_, "wrap");
        pnlScan.add(cbxReturnToOriginalPosition_, "wrap");

        add(pnlScan, "wrap");
    }

    private void createEventHandlers() {
        spnScanAcceleration_.registerListener(e -> {});
        spnScanOvershootDist_.registerListener(e -> {});
        spnScanRetraceSpeed_.registerListener(e -> {});
        spnScanAngleFirstView_.registerListener(e -> {});
        cbxScanFromCurrentPosition_.registerListener(e -> {});
        cbxScanNegativeDirection_.registerListener(e -> {});
        cbxReturnToOriginalPosition_.registerListener(e -> {});
    }
}
