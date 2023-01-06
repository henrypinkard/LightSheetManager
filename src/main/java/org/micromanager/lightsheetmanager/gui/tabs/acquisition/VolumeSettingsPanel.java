package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.api.internal.DefaultVolumeSettings;
import org.micromanager.lightsheetmanager.model.AcquisitionSettings;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.model.devices.LightSheetDeviceManager;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;

import java.util.ArrayList;
import java.util.Objects;

public class VolumeSettingsPanel extends Panel {

    private Label lblNumViews_;
    private Label lblFirstView_;
    private Label lblViewDelay_;
    private Label lblSlicesPerView_;
    private Label lblSliceStepSize_;
    private ComboBox cmbNumViews_;
    private ComboBox cmbFirstView_;
    private Spinner spnNumViews_;

    private Spinner spnViewDelay_;
    private Spinner spnSliceStepSize_;
    private Spinner spnSlicesPerSide_;

    private DefaultVolumeSettings.Builder vsb_;

    private LightSheetManagerModel model_;

    public VolumeSettingsPanel(final LightSheetManagerModel model) {
        super("Volume Settings");
        model_ = Objects.requireNonNull(model);
        vsb_ = model_.acquisitions().getVolumeSettingsBuilder();
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {

        final LightSheetDeviceManager lsdm = model_.devices().getDeviceAdapter();
        final int numImagingPaths = lsdm.getNumImagingPaths();

        final DefaultVolumeSettings volumeSettings = model_.acquisitions()
                .getAcquisitionSettings().getVolumeSettings();

        // create labels for combo boxes
        ArrayList<String> labels = new ArrayList<>(numImagingPaths);
        for (int i = 0; i < numImagingPaths; i++) {
            labels.add(String.valueOf(i+1));
        }
        final String[] lbls = labels.toArray(new String[0]);

        lblNumViews_ = new Label("Number of views:");
        lblFirstView_ = new Label("First view:");
        lblViewDelay_ = new Label("Delay before view [ms]:");
        lblSlicesPerView_ = new Label("Slices per view:");
        lblSliceStepSize_ = new Label("Slice step size [\u00B5m]:");

        // if the number of sides has changed and the firstView or numViews is larger
        // than the number of sides, default to 1.
        int numViews = volumeSettings.numViews();
        int firstView = volumeSettings.firstView();
        if (numViews > labels.size()) {
            numViews = 1;
        }
        if (firstView > labels.size()) {
            firstView = 1;
        }

        cmbNumViews_ = new ComboBox(lbls, String.valueOf(numViews), 60, 20);
        cmbFirstView_ = new ComboBox(lbls, String.valueOf(firstView), 60, 20);
        spnViewDelay_ = Spinner.createDoubleSpinner(volumeSettings.delayBeforeView(), 0.0, Double.MAX_VALUE, 0.25);
        spnSliceStepSize_ = Spinner.createDoubleSpinner(volumeSettings.sliceStepSize(), 0.0, 100.0, 0.1);
        spnSlicesPerSide_ = Spinner.createIntegerSpinner(volumeSettings.slicesPerView(), 0, 100, 1);

        add(lblNumViews_, "");
        add(cmbNumViews_, "wrap");
        add(lblFirstView_, "");
        add(cmbFirstView_, "wrap");
        add(lblViewDelay_, "");
        add(spnViewDelay_, "wrap");
        add(lblSlicesPerView_, "");
        add(spnSlicesPerSide_, "wrap");
        add(lblSliceStepSize_, "");
        add(spnSliceStepSize_, "");
    }

    private void createEventHandlers() {
        final AcquisitionSettings acqSettings = model_.acquisitions().getAcquisitionSettings();

        cmbNumViews_.registerListener(e -> {
            vsb_.numViews(Integer.parseInt(cmbNumViews_.getSelected()));
            acqSettings.setVolumeSettings(vsb_.build());
            System.out.println(acqSettings.getVolumeSettings().numViews());
        });

        cmbFirstView_.registerListener(e -> {
            vsb_.firstView(Integer.parseInt(cmbFirstView_.getSelected()));
            acqSettings.setVolumeSettings(vsb_.build());
        });

        spnViewDelay_.registerListener(e -> {
            vsb_.delayBeforeView(spnViewDelay_.getDouble());
            acqSettings.setVolumeSettings(vsb_.build());
        });

        spnSlicesPerSide_.registerListener(e -> {
            vsb_.slicesPerVolume(spnSlicesPerSide_.getInt());
            acqSettings.setVolumeSettings(vsb_.build());
        });

        spnSliceStepSize_.registerListener(e -> {
            vsb_.sliceStepSize(spnSliceStepSize_.getDouble());
            acqSettings.setVolumeSettings(vsb_.build());
        });
    }
}