package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettingsLS;
import org.micromanager.lightsheetmanager.gui.frames.AdvancedTimingFrame;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import java.util.Objects;

public class SliceSettingsPanel extends Panel {

    // regular panel
    private CheckBox cbxUseAdvancedTiming_;
    private CheckBox cbxMinimizeSlicePeriod_;
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

    private AdvancedTimingFrame advTimingFrame_;

    private LightSheetManagerModel model_;

    public SliceSettingsPanel(final LightSheetManagerModel model, final AdvancedTimingFrame advTimingFrame) {
        super("Slice Settings");
        model_ = Objects.requireNonNull(model);
        advTimingFrame_ = Objects.requireNonNull(advTimingFrame);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final DefaultSliceSettingsLS sliceSettingsLS = model_.acquisitions()
                .getAcquisitionSettings().sliceSettingsLS();

        final DefaultSliceSettings sliceSettings = model_.acquisitions()
                .getAcquisitionSettings().sliceSettings();

        final boolean isSlicePeriodMinimized = sliceSettings.isSlicePeriodMinimized();

        // regular panel
        lblSlicePeriod_ = new Label("Slice period [ms]:");
        lblSampleExposure_ = new Label("Sample exposure [ms]:");
        cbxMinimizeSlicePeriod_ = new CheckBox("Minimize slice period", 12, isSlicePeriodMinimized, CheckBox.RIGHT);
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

        // open the advanced timing frame if using advanced timing settings
        final boolean isUsingAdvancedTiming = model_.acquisitions().getAcquisitionSettings().isUsingAdvancedTiming();
        cbxUseAdvancedTiming_ = new CheckBox("Use advanced timing settings", 12, isUsingAdvancedTiming, CheckBox.RIGHT);
        if (isUsingAdvancedTiming) {
            advTimingFrame_.setVisible(true);
            advTimingFrame_.toFront();
        }

        // create the ui based on the camera trigger mode
        switchUI(model_.acquisitions().getAcquisitionSettings().cameraMode());
    }

    /**
     * Setup event handlers for the regular and virtual slit camera trigger mode versions of the ui.
     */
    private void createEventHandlers() {
        final DefaultSliceSettings.Builder ssb_ = model_.acquisitions().getAcquisitionSettingsBuilder().sliceSettingsBuilder();
        final DefaultSliceSettingsLS.Builder ssbLS_ = model_.acquisitions().getAcquisitionSettingsBuilder().sliceSettingsLSBuilder();
        final DefaultAcquisitionSettingsDISPIM.Builder asb =
                model_.acquisitions().getAcquisitionSettingsBuilder();


        // regular panel
        cbxMinimizeSlicePeriod_.registerListener(e -> {
            final boolean selected = !cbxMinimizeSlicePeriod_.isSelected();
            lblSlicePeriod_.setEnabled(selected);
            spnSlicePeriod_.setEnabled(selected);
            ssb_.minimizeSlicePeriod(!selected);
            //System.out.println("isSlicePeriodMinimized: " + acqSettings.getSliceSettings().isSlicePeriodMinimized());
        });

        spnSlicePeriod_.registerListener(e -> {
            ssb_.slicePeriod(spnSlicePeriod_.getDouble());
            //System.out.println("slicePeriod: " + acqSettings.getSliceSettings().slicePeriod());
        });

        spnSampleExposure_.registerListener(e -> {
            ssb_.sampleExposure(spnSampleExposure_.getDouble());
            //System.out.println("sampleExposure: " + acqSettings.getSliceSettings().sampleExposure());
        });

        // virtual slit panel
        spnScanResetTime_.registerListener(e -> {
            ssbLS_.scanResetTime(spnScanResetTime_.getDouble());
            //System.out.println("scanResetTime: " + acqSettings.getSliceSettingsLS().scanResetTime());
        });

        spnScanSettleTime_.registerListener(e -> {
            ssbLS_.scanSettleTime(spnScanSettleTime_.getDouble());
            //System.out.println("scanSettleTime: " + acqSettings.getSliceSettingsLS().scanSettleTime());
        });

        spnShutterWidth_.registerListener(e -> {
            ssbLS_.shutterWidth(spnShutterWidth_.getDouble());
            //System.out.println("shutterWidth: " + acqSettings.getSliceSettingsLS().shutterWidth());
        });

        spnShutterSpeed_.registerListener(e -> {
            ssbLS_.shutterSpeedFactor(spnShutterSpeed_.getDouble());
            //System.out.println("shutterSpeedFactor: " + acqSettings.getSliceSettingsLS().shutterSpeedFactor());
        });

        // disable ui elements
        cbxUseAdvancedTiming_.registerListener(e -> {
            // regular panel
            final boolean selected = !cbxUseAdvancedTiming_.isSelected();
            cbxMinimizeSlicePeriod_.setEnabled(selected);

            if (model_.acquisitions().getAcquisitionSettings().sliceSettings().isSlicePeriodMinimized()) {
                lblSlicePeriod_.setEnabled(false);
                spnSlicePeriod_.setEnabled(false);
            } else {
                lblSlicePeriod_.setEnabled(selected);
                spnSlicePeriod_.setEnabled(selected);
            }
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
                advTimingFrame_.toFront();
            }
            model_.acquisitions().getAcquisitionSettingsBuilder().useAdvancedTiming(!selected);
            //System.out.println("useAdvancedTiming: " + model_.acquisitions().getAcquisitionSettings().isUsingAdvancedTiming());
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
            add(cbxMinimizeSlicePeriod_, "wrap");
            add(lblSlicePeriod_, "");
            add(spnSlicePeriod_, "wrap");
            add(lblSampleExposure_, "");
            add(spnSampleExposure_, "wrap");
            add(cbxUseAdvancedTiming_, "span 2");
        } else {
            add(lblScanResetTime_, "");
            add(spnScanResetTime_, "wrap");
            add(lblScanSettleTime_, "");
            add(spnScanSettleTime_, "wrap");
            add(lblShutterWidth_, "");
            add(spnShutterWidth_, "wrap");
            add(lblShutterSpeed_, "");
            add(spnShutterSpeed_, "wrap");
            add(cbxUseAdvancedTiming_, "span 2");
        }
        revalidate();
        repaint();
    }
}
