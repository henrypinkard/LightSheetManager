package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.api.SliceSettings;
import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettingsLS;
import org.micromanager.lightsheetmanager.api.internal.DefaultVolumeSettings;
import org.micromanager.lightsheetmanager.gui.frames.AdvancedTimingFrame;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.model.AcquisitionSettings;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import java.util.Objects;

// TODO: changes based on camera trigger mode
public class SliceSettingsPanel extends Panel {

    // regular panel
    private CheckBox chkUseAdvancedTiming_;
    private CheckBox chkMinimizeSlicePeriod_;
    private Label lblSlicePeriod_;
    private Label lblSampleExposure_;
    private Spinner spnSlicePeriod_;
    private Spinner spnSampleExposure_;

    // virtual slit panel
    private Label lblScanResetTime_;
    private Label lblScanSettleTime_;
    private Label lblShutterWidth_;
    private Label lblShutterSpeed_;
    private Spinner spnScanResetTime_;
    private Spinner spnScanSettleTime_;
    private Spinner spnShutterWidth_;
    private Spinner spnShutterSpeed_;

    private DefaultSliceSettingsLS.Builder ssbLS_;
    private DefaultSliceSettings.Builder ssb_;

    private AdvancedTimingFrame advTimingFrame_;

    private LightSheetManagerModel model_;

    public SliceSettingsPanel(final LightSheetManagerModel model, final AdvancedTimingFrame advTimingFrame) {
        super("Slice Settings");
        model_ = Objects.requireNonNull(model);
        advTimingFrame_ = Objects.requireNonNull(advTimingFrame);
        ssbLS_ = model_.acquisitions().getSliceSettingsBuilderLS();
        ssb_ = model_.acquisitions().getSliceSettingsBuilder();
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {

        final DefaultSliceSettingsLS sliceSettingsLS = model_.acquisitions()
                .getAcquisitionSettings().getSliceSettingsLS();

        final DefaultSliceSettings sliceSettings = model_.acquisitions()
                .getAcquisitionSettings().getSliceSettings();

        final boolean isSlicePeriodMinimized = sliceSettings.isSlicePeriodMinimized();

        // regular panel
        lblSlicePeriod_ = new Label("Slice period [ms]:");
        lblSampleExposure_ = new Label("Sample exposure [ms]:");
        chkMinimizeSlicePeriod_ = new CheckBox("Minimize slice period", 12, isSlicePeriodMinimized, CheckBox.RIGHT);
        spnSlicePeriod_ = Spinner.createDoubleSpinner(sliceSettings.slicePeriod(), 0.0, Double.MAX_VALUE, 0.25);
        spnSampleExposure_ = Spinner.createDoubleSpinner(sliceSettings.sampleExposure(), 0.0, Double.MAX_VALUE, 0.25);

        if (isSlicePeriodMinimized) {
            lblSlicePeriod_.setEnabled(false);
            spnSlicePeriod_.setEnabled(false);
        }

        // virtual slit panel
        lblScanResetTime_ = new Label("Scan Reset Time [ms]:");
        lblScanSettleTime_ = new Label("Scan Settle Time [ms]:");
        lblShutterWidth_ = new Label("Shutter Width [\u00B5s]:");
        lblShutterSpeed_ = new Label("1 / (shutter speed):");
        spnScanResetTime_ = Spinner.createDoubleSpinner(sliceSettingsLS.scanResetTime(), 1.0, 100.0, 0.25);
        spnScanSettleTime_ = Spinner.createDoubleSpinner(sliceSettingsLS.scanSettleTime(), 0.25, 100.0, 0.25);
        spnShutterWidth_ = Spinner.createDoubleSpinner(sliceSettingsLS.shutterWidth(),0.1, 100.0, 1.0);
        spnShutterSpeed_ = Spinner.createDoubleSpinner(sliceSettingsLS.shutterSpeedFactor(), 1.0, 10.0, 1.0);

        chkUseAdvancedTiming_ = new CheckBox("Use advanced timing settings", 12, false, CheckBox.RIGHT);

        // create the ui based on the camera trigger mode
        switchUI(model_.acquisitions().getAcquisitionSettings().getCameraMode());
    }

    /**
     * Setup event handlers for the regular and virtual slit camera trigger mode versions of the ui.
     */
    private void createEventHandlers() {
        final AcquisitionSettings acqSettings = model_.acquisitions().getAcquisitionSettings();

        // regular panel
        chkMinimizeSlicePeriod_.registerListener(e -> {
            final boolean selected = !chkMinimizeSlicePeriod_.isSelected();
            lblSlicePeriod_.setEnabled(selected);
            spnSlicePeriod_.setEnabled(selected);
            ssb_.minimizeSlicePeriod(!selected);
            acqSettings.setSliceSettings(ssb_.build());
            System.out.println("isSlicePeriodMinimized: " + acqSettings.getSliceSettings().isSlicePeriodMinimized());
        });

        spnSlicePeriod_.registerListener(e -> {
            ssb_.slicePeriod(spnSlicePeriod_.getDouble());
            acqSettings.setSliceSettings(ssb_.build());
            System.out.println("slicePeriod: " + acqSettings.getSliceSettings().slicePeriod());
        });

        spnSampleExposure_.registerListener(e -> {
            ssb_.sampleExposure(spnSampleExposure_.getDouble());
            acqSettings.setSliceSettings(ssb_.build());
            System.out.println("sampleExposure: " + acqSettings.getSliceSettings().sampleExposure());
        });

        // virtual slit panel
        spnScanResetTime_.registerListener(e -> {
            ssbLS_.scanResetTime(spnScanResetTime_.getDouble());
            acqSettings.setSliceSettingsLS(ssbLS_.build());
            System.out.println("scanResetTime: " + acqSettings.getSliceSettingsLS().scanResetTime());
        });

        spnScanSettleTime_.registerListener(e -> {
            ssbLS_.scanSettleTime(spnScanSettleTime_.getDouble());
            acqSettings.setSliceSettingsLS(ssbLS_.build());
            System.out.println("scanSettleTime: " + acqSettings.getSliceSettingsLS().scanSettleTime());
        });

        spnShutterWidth_.registerListener(e -> {
            ssbLS_.shutterWidth(spnShutterWidth_.getDouble());
            acqSettings.setSliceSettingsLS(ssbLS_.build());
            System.out.println("shutterWidth: " + acqSettings.getSliceSettingsLS().shutterWidth());
        });

        spnShutterSpeed_.registerListener(e -> {
            ssbLS_.shutterSpeedFactor(spnShutterSpeed_.getDouble());
            acqSettings.setSliceSettingsLS(ssbLS_.build());
            System.out.println("shutterSpeedFactor: " + acqSettings.getSliceSettingsLS().shutterSpeedFactor());
        });

        // TODO: save to acqSettings
        // disable ui elements
        chkUseAdvancedTiming_.registerListener(e -> {
            // regular panel
            final boolean selected = !chkUseAdvancedTiming_.isSelected();
            chkMinimizeSlicePeriod_.setEnabled(selected);
            lblSlicePeriod_.setEnabled(selected);
            spnSlicePeriod_.setEnabled(selected);
            lblSampleExposure_.setEnabled(selected);
            spnSampleExposure_.setEnabled(selected);
            // virtual slit panel
            lblScanResetTime_.setEnabled(selected);
            lblScanSettleTime_.setEnabled(selected);
            lblShutterWidth_.setEnabled(selected);
            lblShutterSpeed_.setEnabled(selected);
            spnScanResetTime_.setEnabled(selected);
            spnScanSettleTime_.setEnabled(selected);
            spnShutterWidth_.setEnabled(selected);
            spnShutterSpeed_.setEnabled(selected);
            // show frame
            if (!advTimingFrame_.isVisible()) {
                advTimingFrame_.setVisible(true);
            }
        });
    }

    /**
     * Switches the displayed ui based on the camera trigger mode.
     *
     * @param cameraMode the current camera trigger mode
     */
    public void switchUI(final CameraModes cameraMode) {
        removeAll();
        if (cameraMode != CameraModes.VIRTUAL_SLIT) {
            add(chkMinimizeSlicePeriod_, "wrap");
            add(lblSlicePeriod_, "");
            add(spnSlicePeriod_, "wrap");
            add(lblSampleExposure_, "");
            add(spnSampleExposure_, "wrap");
            add(chkUseAdvancedTiming_, "span 2");
        } else {
            add(lblScanResetTime_, "");
            add(spnScanResetTime_, "wrap");
            add(lblScanSettleTime_, "");
            add(spnScanSettleTime_, "wrap");
            add(lblShutterWidth_, "");
            add(spnShutterWidth_, "wrap");
            add(lblShutterSpeed_, "");
            add(spnShutterSpeed_, "wrap");
            add(chkUseAdvancedTiming_, "span 2");
        }
        revalidate();
        repaint();
    }
}
