package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.gui.data.Icons;
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

    // time points
    private Label lblNumTimePoints_;
    private Label lblInterval_;
    private Spinner spnNumTimePoints_;
    private Spinner spnInterval_;
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

    public AcquisitionTab(final Studio studio, final LightSheetManagerModel model) {
        studio_ = Objects.requireNonNull(studio);
        model_ = Objects.requireNonNull(model);
        acqTableFrame_ = new AcquisitionTableFrame(studio_);
        advTimingFrame_ = new AdvancedTimingFrame(model_);
        xyzGridFrame_ = new XYZGridFrame();
        createUserInterface();
    }

    private void createUserInterface() {
        //JLabel lblTitle = new JLabel("Acquisitions");

        Panel.setMigLayoutDefault(
                "",
                "[]5[]",
                "[]5[]"
        );
        Panel panelLeft = new Panel();
        Panel panelCenter = new Panel();
        Panel panelRight = new Panel();

        volumeSettingsPanel_ = new VolumeSettingsPanel(model_);
        sliceSettingsPanel_ = new SliceSettingsPanel(advTimingFrame_);

        // check boxes for panels
        chkUseMultiplePositions_ = new CheckBox("Multiple positions (XY)", false);
        chkUseTimePoints_ = new CheckBox("Time Points", false);
        chkUseChannels_ = new CheckBox("Channels", false);

        // panels
        panelButtons_ = new Panel();
        panelDurations_ = new Panel("Durations");
        panelTimePoints_ = new Panel(chkUseTimePoints_);
        panelMultiplePositions_ = new Panel(chkUseMultiplePositions_);

        // durations
        lblSliceTime_ = new Label("Slice");
        lblVolumeTime_ = new Label("Volume:");
        lblTotalTime_ = new Label("Total:");

        // time points
        lblNumTimePoints_ = new Label("Number:");
        lblInterval_ = new Label("Interval [s]:");
        spnNumTimePoints_ = Spinner.createIntegerSpinner(1, 1, 1,1);
        spnInterval_ = Spinner.createFloatSpinner(1.0f, 1.0f, 1.0f, 1.0f);

        // multiple positions
        lblPostMoveDelay_ = new Label("Post-move delay [ms]:");
        spnPostMoveDelay_ = Spinner.createIntegerSpinner(0, 0, Integer.MAX_VALUE, 100);
        btnEditPositionList_ = new Button("Edit Position List", 120, 20);
        btnOpenXYZGrid_ = new Button("XYZ Grid", 80, 20);

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

        channelTablePanel_ = new ChannelTablePanel(chkUseChannels_);

        String[] labels = {"Mode 1", "Mode 2"};
        cmbAcquisitionModes_ = new ComboBox(labels, labels[0]);

        createEventHandlers();

        // durations
        panelDurations_.add(lblSliceTime_, "wrap");
        panelDurations_.add(lblVolumeTime_, "wrap");
        panelDurations_.add(lblTotalTime_, "");

        // time points
        panelTimePoints_.add(lblNumTimePoints_, "");
        panelTimePoints_.add(spnNumTimePoints_, "wrap");
        panelTimePoints_.add(lblInterval_, "");
        panelTimePoints_.add(spnInterval_, "");

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
        panelLeft.add(panelDurations_, "growx");
        panelLeft.add(panelTimePoints_, "growx, growy, wrap");
        panelLeft.add(panelMultiplePositions_, "growx, span 2");

        panelCenter.add(channelTablePanel_, "wrap");
        panelCenter.add(new JLabel("Acquisition modes:"), "split 2");
        panelCenter.add(cmbAcquisitionModes_, "");

        panelRight.add(volumeSettingsPanel_, "growx, wrap");
        panelRight.add(sliceSettingsPanel_, "growx, wrap");

        //add(lblTitle, "wrap");

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

        });

        chkUseTimePoints_.registerListener(e -> {
            //model_.acquisitions().getAcquisitionSettings().
        });

        // use channel checkbox
        chkUseChannels_.registerListener(e -> {
            channelTablePanel_.setItemsEnabled(chkUseChannels_.isSelected());
        });

        spnNumTimePoints_.registerListener(e -> {

        });

        spnInterval_.registerListener(e -> {

        });
    }
}