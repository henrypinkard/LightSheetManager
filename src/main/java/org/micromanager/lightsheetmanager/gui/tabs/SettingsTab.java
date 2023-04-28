package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.api.internal.DefaultScanSettings;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.JLabel;
import java.util.Objects;

public class SettingsTab extends Panel {

    private Spinner spnScanAcceleration_;
    private Spinner  spnScanOvershootDist_;
    private Spinner spnScanRetraceSpeed_;
    private Spinner spnScanAngleFirstView_;

    private CheckBox cbxScanFromCurrentPosition_;
    private CheckBox cbxScanNegativeDirection_;
    private CheckBox cbxReturnToOriginalPosition_;

    private LightSheetManagerModel model_;

    public SettingsTab(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final DefaultAcquisitionSettingsDISPIM acqSettings =
                model_.acquisitions().getAcquisitionSettings();

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
        final DefaultScanSettings.Builder scsb = model_.acquisitions()
                .getAcquisitionSettingsBuilder().scanSettingsBuilder();

        // Spinners
        spnScanAcceleration_.registerListener(e -> scsb.scanAccelerationFactor(spnScanAcceleration_.getDouble()));
        spnScanOvershootDist_.registerListener(e -> scsb.scanOvershootDistance(spnScanOvershootDist_.getInt()));
        spnScanRetraceSpeed_.registerListener(e -> scsb.scanRetraceSpeed(spnScanRetraceSpeed_.getDouble()));
        spnScanAngleFirstView_.registerListener(e -> scsb.scanAngleFirstView(spnScanAngleFirstView_.getDouble()));

        // CheckBoxes
        cbxScanFromCurrentPosition_.registerListener(e -> scsb.scanFromCurrentPosition(cbxScanFromCurrentPosition_.isSelected()));
        cbxScanNegativeDirection_.registerListener(e -> scsb.scanFromNegativeDirection(cbxScanNegativeDirection_.isSelected()));
        cbxReturnToOriginalPosition_.registerListener(e -> scsb.scanReturnToOriginalPosition(cbxReturnToOriginalPosition_.isSelected()));
    }
}
