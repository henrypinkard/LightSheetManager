package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.model.AcquisitionSettings;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.gui.channels.ChannelTablePanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.SliceSettingsPanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.VolumeSettingsPanel;
import org.micromanager.lightsheetmanager.gui.frames.AdvancedTimingFrame;
import org.micromanager.lightsheetmanager.gui.frames.XYZGridFrame;
import org.micromanager.lightsheetmanager.gui.playlist.AcquisitionTableFrame;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.gui.components.ToggleButton;
import org.micromanager.lightsheetmanager.model.data.AcquisitionModes;

import org.micromanager.internal.utils.NumberUtils;

import javax.swing.JLabel;
import java.util.Objects;

public class AcquisitionTab extends Panel {

    private Studio studio_;

    private ComboBox cmbAcquisitionModes_;

    private ToggleButton btnRunAcquisition_;
    private ToggleButton btnPauseAcquisition_;
    private Button btnTestAcquisition_;
    private Button btnOpenPlaylist_;

    private CheckBox chkUseChannels_;

    // durations
    private Label lblSliceTime_;
    private Label lblVolumeTime_;
    private Label lblTotalTime_;

    private Label lblSliceTimeValue_;
    private Label lblVolumeTimeValue_;
    private Label lblTotalTimeValue_;

    // time points
    private Label lblNumTimePoints_;
    private Label lblTimePointInterval_;
    private Spinner spnNumTimePoints_;
    private Spinner spnTimePointInterval_;
    private CheckBox chkUseTimePoints_;

    // multiple positions
    private Label lblPostMoveDelay_;
    private Spinner spnPostMoveDelay_;
    private CheckBox chkUseMultiplePositions_;
    private Button btnOpenXYZGrid_;
    private Button btnEditPositionList_;

    private Panel panelButtons_;
    private Panel panelDurations_;
    private Panel panelTimePoints_;
    private Panel panelMultiplePositions_;

    private ChannelTablePanel channelTablePanel_;

    private VolumeSettingsPanel volumeSettingsPanel_;
    private SliceSettingsPanel sliceSettingsPanel_;

    private XYZGridFrame xyzGridFrame_;
    private AdvancedTimingFrame advTimingFrame_;

    private AcquisitionTableFrame acqTableFrame_;

    private LightSheetManagerModel model_;

    public AcquisitionTab(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        studio_ = model_.getStudio();
        acqTableFrame_ = new AcquisitionTableFrame(studio_);
        advTimingFrame_ = new AdvancedTimingFrame(model_);
        xyzGridFrame_ = new XYZGridFrame();
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        //JLabel lblTitle = new JLabel("Acquisitions");
        final AcquisitionSettings acqSettings = model_.acquisitions().getAcquisitionSettings();

        Panel.setMigLayoutDefault(
                "",
                "[]5[]",
                "[]5[]"
        );
        Panel panelLeft = new Panel();
        Panel panelCenter = new Panel();
        Panel panelRight = new Panel();

        volumeSettingsPanel_ = new VolumeSettingsPanel(model_);
        sliceSettingsPanel_ = new SliceSettingsPanel(model_, advTimingFrame_);

        // check boxes for panels
        chkUseMultiplePositions_ = new CheckBox("Multiple positions (XY)", acqSettings.isUsingMultiplePositions());
        chkUseTimePoints_ = new CheckBox("Time Points", acqSettings.isUsingTimePoints());
        chkUseChannels_ = new CheckBox("Channels", acqSettings.isUsingChannels());

        // panels
        panelButtons_ = new Panel();
        panelDurations_ = new Panel("Durations");
        panelTimePoints_ = new Panel(chkUseTimePoints_);
        panelMultiplePositions_ = new Panel(chkUseMultiplePositions_);

        // durations
        lblSliceTime_ = new Label("Slice");
        lblVolumeTime_ = new Label("Volume:");
        lblTotalTime_ = new Label("Total:");

        lblSliceTimeValue_ = new Label("0.0");
        lblVolumeTimeValue_ = new Label("0.0");
        lblTotalTimeValue_ = new Label("0.0");

        // TODO: is there a reasonable max value for these spinners?
        // time points
        lblNumTimePoints_ = new Label("Number:");
        lblTimePointInterval_ = new Label("Interval [s]:");
        spnNumTimePoints_ = Spinner.createIntegerSpinner(acqSettings.getNumTimePoints(), 1, Integer.MAX_VALUE,1);
        spnTimePointInterval_ = Spinner.createIntegerSpinner(acqSettings.getTimePointInterval(), 1, Integer.MAX_VALUE, 10);

        // disable elements based on acqSettings
        setTimePointSpinnersEnabled(acqSettings.isUsingTimePoints());

        // multiple positions
        lblPostMoveDelay_ = new Label("Post-move delay [ms]:");
        spnPostMoveDelay_ = Spinner.createIntegerSpinner(acqSettings.getPostMoveDelay(), 0, Integer.MAX_VALUE, 100);
        btnEditPositionList_ = new Button("Edit Position List", 120, 20);
        btnOpenXYZGrid_ = new Button("XYZ Grid", 80, 20);

        // disable elements based on acqSettings
        setMultiPositionsEnabled(acqSettings.isUsingMultiplePositions());

        ToggleButton.setDefaultSize(120, 30);
        btnRunAcquisition_ = new ToggleButton(
                "Start Acquisition", "Stop Acquisition",
                Icons.ARROW_RIGHT, Icons.CANCEL
        );

        btnPauseAcquisition_ = new ToggleButton(
                "Pause", "Resume",
                Icons.PAUSE, Icons.PLAY
        );

        Button.setDefaultSize(120, 30);
        btnTestAcquisition_ = new Button("Test Acquisition");
        btnOpenPlaylist_ = new Button("Playlist...");

        channelTablePanel_ = new ChannelTablePanel(model_, chkUseChannels_);
        // disable elements based on acqSettings
        if (!acqSettings.isUsingChannels()) {
            channelTablePanel_.setItemsEnabled(false);
        }

        cmbAcquisitionModes_ = new ComboBox(AcquisitionModes.toArray(),
                model_.acquisitions().getAcquisitionSettings().getAcquisitionMode().toString());

        // durations
        panelDurations_.add(lblSliceTime_, "");
        panelDurations_.add(lblSliceTimeValue_, "wrap");
        panelDurations_.add(lblVolumeTime_, "");
        panelDurations_.add(lblVolumeTimeValue_, "wrap");
        panelDurations_.add(lblTotalTime_, "");
        panelDurations_.add(lblTotalTimeValue_, "");

        // time points
        panelTimePoints_.add(lblNumTimePoints_, "");
        panelTimePoints_.add(spnNumTimePoints_, "wrap");
        panelTimePoints_.add(lblTimePointInterval_, "");
        panelTimePoints_.add(spnTimePointInterval_, "");

        // multiple positions
        panelMultiplePositions_.add(btnEditPositionList_, "");
        panelMultiplePositions_.add(btnOpenXYZGrid_, "wrap");
        panelMultiplePositions_.add(lblPostMoveDelay_, "");
        panelMultiplePositions_.add(spnPostMoveDelay_, "");

        // acquisition buttons
        panelButtons_.add(btnRunAcquisition_, "");
        panelButtons_.add(btnPauseAcquisition_, "");
        panelButtons_.add(btnTestAcquisition_, "");
        panelButtons_.add(btnOpenPlaylist_, "");

        // 3 panel layout
        panelLeft.add(panelDurations_, "growx, growy");
        panelLeft.add(panelTimePoints_, "growx, growy, wrap");
        panelLeft.add(panelMultiplePositions_, "growx, span 2");

        panelCenter.add(channelTablePanel_, "wrap");
        panelCenter.add(new JLabel("Acquisition modes:"), "split 2");
        panelCenter.add(cmbAcquisitionModes_, "");

        panelRight.add(volumeSettingsPanel_, "growx, wrap");
        panelRight.add(sliceSettingsPanel_, "growx, wrap");

        //add(lblTitle, "wrap");

        updateDurationLabels();

        add(panelLeft, "");
        add(panelCenter, "");
        add(panelRight, "wrap");
        add(panelButtons_, "span 3");
    }

    private void createEventHandlers() {
        // start/stop acquisitions
        btnRunAcquisition_.registerListener(e -> {
            if (btnRunAcquisition_.isSelected()) {
                model_.acquisitions().requestRun();
                System.out.println("request run");
            } else {
                model_.acquisitions().requestStop();
                System.out.println("request stop");
            }
        });

        btnPauseAcquisition_.registerListener(e -> {
            if (btnPauseAcquisition_.isSelected()) {
                model_.acquisitions().requestPause();
                System.out.println("request pause");
            } else {
                model_.acquisitions().requestRun(); // TODO: requestResume?
                System.out.println("request resume");
            }
        });

        btnOpenPlaylist_.registerListener(e -> acqTableFrame_.setVisible(true));
        btnOpenXYZGrid_.registerListener(e -> xyzGridFrame_.setVisible(true));
        btnEditPositionList_.registerListener(e -> studio_.app().showPositionList());

        chkUseMultiplePositions_.registerListener(e -> {
            final boolean isSelected = chkUseMultiplePositions_.isSelected();
            model_.acquisitions().getAcquisitionSettings().setUsingMultiplePositions(isSelected);
            setMultiPositionsEnabled(isSelected);
        });

        spnPostMoveDelay_.registerListener(e -> {
            model_.acquisitions().getAcquisitionSettings().setPostMoveDelay(spnPostMoveDelay_.getInt());
            System.out.println("getPostMoveDelay: " + model_.acquisitions().getAcquisitionSettings().getPostMoveDelay());
        });

        // time points

        chkUseTimePoints_.registerListener(e -> {
            final boolean selected = chkUseTimePoints_.isSelected();
            model_.acquisitions().getAcquisitionSettings().setUsingTimePoints(selected);
            setTimePointSpinnersEnabled(selected);
        });

        spnNumTimePoints_.registerListener(e -> {
            model_.acquisitions().getAcquisitionSettings().setNumTimePoints(spnNumTimePoints_.getInt());
            System.out.println("getNumTimePoints: " + model_.acquisitions().getAcquisitionSettings().getNumTimePoints());
        });

        spnTimePointInterval_.registerListener(e -> {
            model_.acquisitions().getAcquisitionSettings().setTimePointInterval(spnTimePointInterval_.getInt());
            System.out.println("getTimePointInterval: " + model_.acquisitions().getAcquisitionSettings().getTimePointInterval());
        });

        // use channels
        chkUseChannels_.registerListener(e -> {
            final boolean state = chkUseChannels_.isSelected();
            model_.acquisitions().getAcquisitionSettings().setUsingChannels(state);
            channelTablePanel_.setItemsEnabled(state);
        });

        cmbAcquisitionModes_.registerListener(e -> {
            final int index = cmbAcquisitionModes_.getSelectedIndex();
            model_.acquisitions().getAcquisitionSettings().setAcquisitionMode(AcquisitionModes.getByIndex(index));
            System.out.println("getAcquisitionMode: " + model_.acquisitions().getAcquisitionSettings().getAcquisitionMode());
        });
    }

    public SliceSettingsPanel getSliceSettingsPanel() {
        return sliceSettingsPanel_;
    }

    private void setTimePointSpinnersEnabled(final boolean state) {
        lblNumTimePoints_.setEnabled(state);
        lblTimePointInterval_.setEnabled(state);
        spnNumTimePoints_.setEnabled(state);
        spnTimePointInterval_.setEnabled(state);
    }

    private void setMultiPositionsEnabled(final boolean state) {
        lblPostMoveDelay_.setEnabled(state);
        spnPostMoveDelay_.setEnabled(state);
        btnEditPositionList_.setEnabled(state);
    }

    private void updateDurationLabels() {
        updateSlicePeriodLabel();
        updateVolumeDurationLabel();
        updateTotalTimeDurationLabel();
    }

    private void updateSlicePeriodLabel() {
        final AcquisitionSettings acqSettings = model_.acquisitions().getAcquisitionSettings();
        model_.getAcquisitionEngine().recalculateSliceTiming(acqSettings);
        lblSliceTimeValue_.setText(Double.toString(acqSettings.getTimingSettings().sliceDuration()));
        //System.out.println("updating slice label to: " + acqSettings.getTimingSettings().sliceDuration());
    }

    private void updateVolumeDurationLabel() {
        double duration = computeVolumeDuration(model_.acquisitions().getAcquisitionSettings());
        if (duration > 1000) {
            lblVolumeTimeValue_.setText(
                    NumberUtils.doubleToDisplayString(duration/1000d) +
                            " s"); // round to ms
        } else {
            lblVolumeTimeValue_.setText(
                    NumberUtils.doubleToDisplayString(Math.round(10*duration)/10d) +
                            " ms");  // round to tenth of ms
        }
        //System.out.println("updating volume label to: " + );
    }

    /**
     * Update the displayed total time duration.
     */
    private void updateTotalTimeDurationLabel() {
        String s = "";
        double duration = computeTotalTimeDuration();
        if (duration < 60) {  // less than 1 min
            s += NumberUtils.doubleToDisplayString(duration) + " s";
        } else if (duration < 60*60) { // between 1 min and 1 hour
            s += NumberUtils.doubleToDisplayString(Math.floor(duration/60)) + " min ";
            s += NumberUtils.doubleToDisplayString(Math.round(duration %  60)) + " s";
        } else { // longer than 1 hour
            s += NumberUtils.doubleToDisplayString(Math.floor(duration/(60*60))) + " hr ";
            s +=  NumberUtils.doubleToDisplayString(Math.round((duration % (60*60))/60)) + " min";
        }
        lblTotalTimeValue_.setText(s);
    }

    private double computeTotalTimeDuration() {
        final AcquisitionSettings acqSettings = model_.acquisitions().getAcquisitionSettings();
        final double duration = (acqSettings.getNumTimePoints() - 1) * acqSettings.getTimePointInterval()
                + computeTimePointDuration()/1000;
        return duration;
    }

    /**
     * Compute the time point duration in ms. Only difference from computeVolumeDuration()
     * is that it also takes into account the multiple positions, if any.
     * @return duration in ms
     */
    private double computeTimePointDuration() {
        final AcquisitionSettings acqSettings = model_.acquisitions().getAcquisitionSettings();
        final double volumeDuration = computeVolumeDuration(acqSettings);
        if (acqSettings.isUsingMultiplePositions()) {
            try {
                // use 1.5 seconds motor move between positions
                // (could be wildly off but was estimated using actual system
                // and then slightly padded to be conservative to avoid errors
                // where positions aren't completed in time for next position)
                // could estimate the actual time by analyzing the position's relative locations
                //   and using the motor speed and acceleration time
                return studio_.positions().getPositionList().getNumberOfPositions() *
                        (volumeDuration + 1500 + spnPostMoveDelay_.getInt());
            } catch (Exception e) {
                studio_.logs().showError("Error getting position list for multiple XY positions");
            }
        }
        return volumeDuration;
    }

    public double computeVolumeDuration(final AcquisitionSettings acqSettings) {
//        final MultiChannelModes channelMode = acqSettings.getChannelMode();
//        final int numChannels = acqSettings.numChannels;
//        final int numSides = acqSettings.numSides;
//        final float delayBeforeSide = acqSettings.delayBeforeSide;
//        int numCameraTriggers = acqSettings.numSlices;
//        if (acqSettings.cameraMode == CameraModes.Keys.OVERLAP) {
//            numCameraTriggers += 1;
//        }
//        // stackDuration is per-side, per-channel, per-position
//
//        final double stackDuration = numCameraTriggers * acqSettings.sliceTiming.sliceDuration;
//        if (acqSettings.isStageScanning || acqSettings.isStageStepping) {
//            final double rampDuration = getStageRampDuration(acqSettings);
//            final double retraceTime = getStageRetraceDuration(acqSettings);
//            // TODO double-check these calculations below, at least they are better than before ;-)
//            if (acqSettings.spimMode == AcquisitionModes.Keys.STAGE_SCAN) {
//                if (channelMode == MultichannelModes.Keys.SLICE_HW) {
//                    return retraceTime + (numSides * ((rampDuration * 2) + (stackDuration * numChannels)));
//                } else {  // "normal" stage scan with volume channel switching
//                    if (numSides == 1) {
//                        // single-view so will retrace at beginning of each channel
//                        return ((rampDuration * 2) + stackDuration + retraceTime) * numChannels;
//                    } else {
//                        // will only retrace at very start/end
//                        return retraceTime + (numSides * ((rampDuration * 2) + stackDuration) * numChannels);
//                    }
//                }
//            } else if (acqSettings.spimMode == AcquisitionModes.Keys.STAGE_SCAN_UNIDIRECTIONAL
//                    || acqSettings.spimMode == AcquisitionModes.Keys.STAGE_STEP_SUPPLEMENTAL_UNIDIRECTIONAL
//                    || acqSettings.spimMode == AcquisitionModes.Keys.STAGE_SCAN_SUPPLEMENTAL_UNIDIRECTIONAL) {
//                if (channelMode == MultichannelModes.Keys.SLICE_HW) {
//                    return ((rampDuration * 2) + (stackDuration * numChannels) + retraceTime) * numSides;
//                } else {  // "normal" stage scan with volume channel switching
//                    return ((rampDuration * 2) + stackDuration + retraceTime) * numChannels * numSides;
//                }
//            } else {  // interleaved mode => one-way pass collecting both sides
//                if (channelMode == MultichannelModes.Keys.SLICE_HW) {
//                    // single pass with all sides and channels
//                    return retraceTime + (rampDuration * 2 + stackDuration * numSides * numChannels);
//                } else {  // one-way pass collecting both sides, then rewind for next channel
//                    return ((rampDuration * 2) + (stackDuration * numSides) + retraceTime) * numChannels;
//                }
//            }
//        } else { // piezo scan
//            double channelSwitchDelay = 0;
//            if (channelMode == MultichannelModes.Keys.VOLUME) {
//                channelSwitchDelay = 500;   // estimate channel switching overhead time as 0.5s
//                // actual value will be hardware-dependent
//            }
//            if (channelMode == MultichannelModes.Keys.SLICE_HW) {
//                return numSides * (delayBeforeSide + stackDuration * numChannels);  // channelSwitchDelay = 0
//            } else {
//                return numSides * numChannels
//                        * (delayBeforeSide + stackDuration)
//                        + (numChannels - 1) * channelSwitchDelay;
//            }
//        }
        return 1.0;
    }
}