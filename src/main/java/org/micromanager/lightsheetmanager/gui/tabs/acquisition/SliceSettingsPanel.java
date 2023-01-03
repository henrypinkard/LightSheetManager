package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.gui.frames.AdvancedTimingFrame;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
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

        // regular panel
        lblSlicePeriod_ = new Label("Slice period [ms]:");
        lblSampleExposure_ = new Label("Sample exposure [ms]:");
        chkMinimizeSlicePeriod_ = new CheckBox("Minimize slice period", 12, false, CheckBox.RIGHT);
        spnSlicePeriod_ = Spinner.createDoubleSpinner(30.0, 0.0, Double.MAX_VALUE, 0.25);
        spnSampleExposure_ = Spinner.createDoubleSpinner(10.0, 0.0, Double.MAX_VALUE, 0.25);

        // virtual slit panel
        lblScanResetTime_ = new Label("Scan Reset Time [ms]:");
        lblScanSettleTime_ = new Label("Scan Settle Time [ms]:");
        lblShutterWidth_ = new Label("Shutter Width [\u00B5s]:");
        lblShutterSpeed_ = new Label("1 / (shutter speed):");
        spnScanResetTime_ = Spinner.createDoubleSpinner(1.0, 1.0, 100.0, 0.25);
        spnScanSettleTime_ = Spinner.createDoubleSpinner(1.0, 0.25, 100.0, 0.25);
        spnShutterWidth_ = Spinner.createDoubleSpinner(1.0,0.1, 100.0, 1.0);
        spnShutterSpeed_ = Spinner.createIntegerSpinner(1, 1, 10, 1);

        chkUseAdvancedTiming_ = new CheckBox("Use advanced timing settings", 12, false, CheckBox.RIGHT);

        // create the ui based on the camera trigger mode
        switchUI(model_.acquisitions().getAcquisitionSettings().getCameraMode());
    }

    /**
     * Setup event handlers for the regular and virtual slit camera trigger mode versions of the ui.
     */
    private void createEventHandlers() {
        chkMinimizeSlicePeriod_.registerListener(e -> {
            final boolean selected = !chkMinimizeSlicePeriod_.isSelected();
            lblSlicePeriod_.setEnabled(selected);
            spnSlicePeriod_.setEnabled(selected);
        });

        spnSlicePeriod_.registerListener(e -> {
            //model_.acquisitions().getAcquisitionSettings().getSliceSettings().;
        });

        spnSampleExposure_.registerListener(e -> {

        });

        chkUseAdvancedTiming_.registerListener(e -> {
            // disable ui elements
            final boolean selected = !chkUseAdvancedTiming_.isSelected();
            chkMinimizeSlicePeriod_.setEnabled(selected);
            lblSlicePeriod_.setEnabled(selected);
            spnSlicePeriod_.setEnabled(selected);
            lblSampleExposure_.setEnabled(selected);
            spnSampleExposure_.setEnabled(selected);
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
