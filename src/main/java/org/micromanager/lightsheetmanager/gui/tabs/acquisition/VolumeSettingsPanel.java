package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.api.internal.DefaultVolumeSettings;
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
        System.out.println(volumeSettings);

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

        cmbNumViews_ = new ComboBox(lbls, lbls[0], 60, 20);
        cmbFirstView_ = new ComboBox(lbls, lbls[0], 60, 20);
        spnViewDelay_ = Spinner.createDoubleSpinner(volumeSettings.delayBeforeView(), 0.0, Double.MAX_VALUE, 0.25);
        spnSliceStepSize_ = Spinner.createDoubleSpinner(volumeSettings.sliceStepSize(), 0.0, 100.0, 0.1);
        spnSlicesPerSide_ = Spinner.createIntegerSpinner(volumeSettings.slicesPerVolume(), 0, 100, 1);

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
        cmbNumViews_.registerListener(e -> {
            vsb_.numViews(Integer.parseInt(cmbNumViews_.getSelected()));
        });

        cmbFirstView_.registerListener(e -> {
            vsb_.firstView(Integer.parseInt(cmbFirstView_.getSelected()));
        });

        spnViewDelay_.registerListener(e -> {
            vsb_.delayBeforeView(spnViewDelay_.getDouble());
        });

        spnSlicesPerSide_.registerListener(e -> {
            vsb_.slicesPerVolume(spnSlicesPerSide_.getInt());
        });

        spnSliceStepSize_.registerListener(e -> {
            vsb_.sliceStepSize(spnSliceStepSize_.getDouble());
        });
    }
}