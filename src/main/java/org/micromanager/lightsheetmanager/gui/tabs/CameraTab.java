package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.SliceSettingsPanel;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.JLabel;
import java.util.Objects;

public class CameraTab extends Panel {

    private Button btnUnchangedROI_;
    private Button btnFullROI_;
    private Button btnHalfROI_;
    private Button btnQuarterROI_;
    private Button btnEigthROI_;
    private Button btnCustomROI_;
    private Button btnGetCurrentROI_;

    private ComboBox cmbCameraTriggerMode_;

    private SliceSettingsPanel sliceSettingsPanel_;
    private LightSheetManagerModel model_;

    public CameraTab(final LightSheetManagerModel model, final SliceSettingsPanel sliceSettingsPanel) {
        sliceSettingsPanel_ = Objects.requireNonNull(sliceSettingsPanel);
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        JLabel lblTitle = new JLabel("Cameras");

        final Panel panelROI = new Panel("Imaging ROI");
        final Panel panelCameraTrigger = new Panel("Camera Trigger Mode");

        final Label lblXOffset = new Label("X Offset:");
        final Label lblYOffset = new Label("Y Offset:");
        final Label lblWidth = new Label("Width:");
        final Label lblHeight = new Label("Height:");

        btnUnchangedROI_ = new Button("Unchanged", 120, 30);
        btnFullROI_ = new Button("Full", 60, 30);
        btnHalfROI_ = new Button("1/2", 60, 30);
        btnQuarterROI_ = new Button("1/4", 60, 30);
        btnEigthROI_ = new Button("1/8", 60, 30);
        btnCustomROI_ = new Button("Custom", 120, 30);
        btnGetCurrentROI_ = new Button("Get Current ROI", 120, 30);

        cmbCameraTriggerMode_ = new ComboBox(CameraModes.toArray(),
              model_.acquisitions().getAcquisitionSettings().cameraMode() != null ?
                model_.acquisitions().getAcquisitionSettings().cameraMode().toString() : null);

        panelROI.add(btnUnchangedROI_, "span 2, wrap");
        panelROI.add(btnFullROI_, "");
        panelROI.add(btnHalfROI_, "wrap");
        panelROI.add(btnQuarterROI_, "");
        panelROI.add(btnEigthROI_, "wrap");
        panelROI.add(btnCustomROI_, "span 2, wrap");
        panelROI.add(lblXOffset, "wrap");
        panelROI.add(lblYOffset, "wrap");
        panelROI.add(lblWidth, "wrap");
        panelROI.add(lblHeight, "wrap");
        panelROI.add(btnGetCurrentROI_, "span 2");

        panelCameraTrigger.add(cmbCameraTriggerMode_, "");

        add(lblTitle, "wrap");
        add(panelROI, "wrap");
        add(panelCameraTrigger, "growx");
    }

    private void createEventHandlers() {
        cmbCameraTriggerMode_.registerListener(e -> {
            final CameraModes cameraMode = CameraModes.fromString(cmbCameraTriggerMode_.getSelected());
            model_.acquisitions().getAcquisitionSettingsBuilder().cameraMode(cameraMode);
            sliceSettingsPanel_.switchUI(cameraMode);
            //System.out.println("getCameraMode: " + model_.acquisitions().getAcquisitionSettings().getCameraMode());
        });
    }

}