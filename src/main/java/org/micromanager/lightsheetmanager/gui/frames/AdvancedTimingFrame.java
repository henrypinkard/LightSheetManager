package org.micromanager.lightsheetmanager.gui.frames;

import net.miginfocom.swing.MigLayout;
import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.internal.utils.WindowPositioning;

import javax.swing.JFrame;
import java.util.Objects;

public class AdvancedTimingFrame extends JFrame {

    private Label lblDelayBeforeScan_;
    private Label lblDelayBeforeLaser_;
    private Label lblDelayBeforeCamera_;

    private Label lblLaserTriggerDuration_;
    private Label lblCameraTriggerDuration_;
    private Label lblScanDuration_;
    private Label lblScansPerSlice_;
    private Label lblCameraExposure_;

    private Spinner spnDelayBeforeScan_;
    private Spinner spnDelayBeforeLaser_;
    private Spinner spnDelayBeforeCamera_;
    private Spinner spnLaserTriggerDuration_;
    private Spinner spnCameraTriggerDuration_;
    private Spinner spnScanDuration_;
    private Spinner spnScansPerSlice_;
    private Spinner spnCameraExposure_;

    private CheckBox chkAlternateScanDirection_;

    private DefaultTimingSettings.Builder tsb_;
    private LightSheetManagerModel model_;

    public AdvancedTimingFrame(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        WindowPositioning.setUpBoundsMemory(this, this.getClass(), this.getClass().getSimpleName());
        tsb_ = model_.acquisitions().getTimingSettingsBuilder();
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        setTitle("Advanced Timing Settings");
        setIconImage(Icons.MICROSCOPE.getImage());
        setResizable(false);
        setLayout(new MigLayout(
                "insets 20 20 20 20",
                "",
                ""
        ));

        lblDelayBeforeScan_ = new Label("Delay Before Scan [ms]: ");
        lblDelayBeforeLaser_ = new Label("Delay Before Laser [ms]: ");
        lblDelayBeforeCamera_ = new Label("Delay Before Camera [ms]: ");
        lblLaserTriggerDuration_ = new Label("Laser Trigger Duration [ms]: ");
        lblCameraTriggerDuration_ = new Label("Camera Trigger Duration [ms]: ");
        lblScanDuration_ = new Label("Scan Duration [ms]: ");
        lblScansPerSlice_ = new Label("Scans Per Slice: ");
        lblCameraExposure_ = new Label("Camera Exposure [ms]: ");

        spnDelayBeforeScan_ = Spinner.createDoubleSpinner(1.0, 0.0, 1.0, 1.0);
        spnDelayBeforeLaser_ = Spinner.createDoubleSpinner(1.0, 0.0, 1.0, 1.0);
        spnDelayBeforeCamera_ = Spinner.createDoubleSpinner(1.0, 0.0, 1.0, 1.0);
        spnLaserTriggerDuration_ = Spinner.createDoubleSpinner(1.0, 0.0, 1.0, 1.0);
        spnCameraTriggerDuration_ = Spinner.createDoubleSpinner(1.0, 0.0, 1.0, 1.0);
        spnScanDuration_ = Spinner.createDoubleSpinner(1.0, 0.0, 1.0, 1.0);
        spnScansPerSlice_ = Spinner.createIntegerSpinner(1, 0, 1, 1);
        spnCameraExposure_ = Spinner.createDoubleSpinner(1.0, 0.0, 1.0, 1.0);

        chkAlternateScanDirection_ = new CheckBox(
                "Alternate Scan Direction", false, CheckBox.LEFT);

        add(lblDelayBeforeScan_, "");
        add(spnDelayBeforeScan_, "wrap");
        add(lblScansPerSlice_, "");
        add(spnScansPerSlice_, "wrap");
        add(lblScanDuration_, "");
        add(spnScanDuration_, "wrap");
        add(lblDelayBeforeLaser_, "");
        add(spnDelayBeforeLaser_, "wrap");
        add(lblLaserTriggerDuration_, "");
        add(spnLaserTriggerDuration_, "wrap");
        add(lblDelayBeforeCamera_, "");
        add(spnDelayBeforeCamera_, "wrap");
        add(lblCameraTriggerDuration_, "");
        add(spnCameraTriggerDuration_, "wrap");
        add(lblCameraExposure_, "");
        add(spnCameraExposure_, "wrap");
        add(chkAlternateScanDirection_, "");

        pack();
    }

    private void createEventHandlers() {
        spnDelayBeforeScan_.registerListener(e -> {
            tsb_.delayBeforeScan(spnDelayBeforeScan_.getDouble());
        });

        spnScansPerSlice_.registerListener(e -> {
            tsb_.scansPerSlice(spnScansPerSlice_.getInt());
        });

        spnScanDuration_.registerListener(e -> {
            tsb_.scanDuration(spnScanDuration_.getDouble());
        });

        spnDelayBeforeLaser_.registerListener(e -> {
            tsb_.delayBeforeLaser(spnDelayBeforeLaser_.getDouble());
        });

        spnLaserTriggerDuration_.registerListener(e -> {
            tsb_.laserTriggerDuration(spnDelayBeforeLaser_.getDouble());
        });

        spnDelayBeforeCamera_.registerListener(e -> {
            tsb_.delayBeforeCamera(spnDelayBeforeCamera_.getDouble());
        });

        spnCameraTriggerDuration_.registerListener(e -> {
            tsb_.cameraTriggerDuration(spnCameraTriggerDuration_.getDouble());
        });

        spnCameraExposure_.registerListener(e -> {
            tsb_.cameraExposure(spnCameraExposure_.getDouble());
        });

        chkAlternateScanDirection_.registerListener(e -> {
            tsb_.useAlternateScanDirection(chkAlternateScanDirection_.isSelected());
        });

    }
}
