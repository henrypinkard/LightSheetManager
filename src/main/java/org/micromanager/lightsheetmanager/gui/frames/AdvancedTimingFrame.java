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

    private CheckBox cbxAlternateScanDirection_;

    private LightSheetManagerModel model_;

    public AdvancedTimingFrame(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        WindowPositioning.setUpBoundsMemory(this, this.getClass(), this.getClass().getSimpleName());
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

        final DefaultTimingSettings timingSettings = model_.acquisitions().getAcquisitionSettings().timingSettings();

        spnDelayBeforeScan_ = Spinner.createDoubleSpinner(timingSettings.delayBeforeScan(), 0.0, 10000.0, 0.25);
        spnDelayBeforeLaser_ = Spinner.createDoubleSpinner(timingSettings.delayBeforeLaser(), 1.0, 10000.0, 0.25);
        spnDelayBeforeCamera_ = Spinner.createDoubleSpinner(timingSettings.delayBeforeCamera(), 0.0, 10000.0, 0.25);
        spnLaserTriggerDuration_ = Spinner.createDoubleSpinner(timingSettings.laserTriggerDuration(), 0.0, 10000.0, 0.25);
        spnCameraTriggerDuration_ = Spinner.createDoubleSpinner(timingSettings.cameraTriggerDuration(), 0.0, 1000.0, 0.25);
        spnScanDuration_ = Spinner.createDoubleSpinner(timingSettings.scanDuration(), 0.1, 10000.0, 0.25);
        spnScansPerSlice_ = Spinner.createIntegerSpinner(timingSettings.scansPerSlice(), 1, 1000, 1);
        spnCameraExposure_ = Spinner.createDoubleSpinner(timingSettings.cameraExposure(), 0.0, 1000.0, 0.25);

        cbxAlternateScanDirection_ = new CheckBox(
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
        add(cbxAlternateScanDirection_, "");

        pack();
    }

    private void createEventHandlers() {
        final DefaultTimingSettings.Builder tsb_ = model_.acquisitions().getAcquisitionSettingsBuilder().timingSettingsBuilder();

        spnDelayBeforeScan_.registerListener(e -> {
            tsb_.delayBeforeScan(spnDelayBeforeScan_.getDouble());
            //acqSettings.setTimingSettings(tsb_.build());
            //System.out.println("delayBeforeScan: " + acqSettings.getTimingSettings().delayBeforeScan());
        });

        spnScansPerSlice_.registerListener(e -> {
            tsb_.scansPerSlice(spnScansPerSlice_.getInt());
            //acqSettings.setTimingSettings(tsb_.build());
            //System.out.println("scansPerSlice: " + acqSettings.getTimingSettings().scansPerSlice());
        });

        spnScanDuration_.registerListener(e -> {
            tsb_.scanDuration(spnScanDuration_.getDouble());
            //.setTimingSettings(tsb_.build());
            //System.out.println("scanDuration: " + acqSettings.getTimingSettings().scanDuration());
        });

        spnDelayBeforeLaser_.registerListener(e -> {
            tsb_.delayBeforeLaser(spnDelayBeforeLaser_.getDouble());
            //acqSettings.setTimingSettings(tsb_.build());
            //System.out.println("delayBeforeLaser: " + acqSettings.getTimingSettings().delayBeforeLaser());
        });

        spnLaserTriggerDuration_.registerListener(e -> {
            tsb_.laserTriggerDuration(spnLaserTriggerDuration_.getDouble());
            //acqSettings.setTimingSettings(tsb_.build());
            //System.out.println("laserTriggerDuration: " + acqSettings.getTimingSettings().laserTriggerDuration());
        });

        spnDelayBeforeCamera_.registerListener(e -> {
            tsb_.delayBeforeCamera(spnDelayBeforeCamera_.getDouble());
            //acqSettings.setTimingSettings(tsb_.build());
            //System.out.println("delayBeforeCamera: " + acqSettings.getTimingSettings().delayBeforeCamera());
        });

        spnCameraTriggerDuration_.registerListener(e -> {
            tsb_.cameraTriggerDuration(spnCameraTriggerDuration_.getDouble());
            //acqSettings.setTimingSettings(tsb_.build());
            //System.out.println("cameraTriggerDuration: " + acqSettings.getTimingSettings().cameraTriggerDuration());
        });

        spnCameraExposure_.registerListener(e -> {
            tsb_.cameraExposure(spnCameraExposure_.getDouble());
            //acqSettings.setTimingSettings(tsb_.build());
            //System.out.println("cameraExposure: " + acqSettings.getTimingSettings().cameraExposure());
        });

        cbxAlternateScanDirection_.registerListener(e -> {
            tsb_.useAlternateScanDirection(cbxAlternateScanDirection_.isSelected());
            //acqSettings.setTimingSettings(tsb_.build());
            //System.out.println("useAlternateScanDirection: " + acqSettings.getTimingSettings().useAlternateScanDirection());
        });

    }
}
