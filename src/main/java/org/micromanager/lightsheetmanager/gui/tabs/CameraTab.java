package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;

import javax.swing.JLabel;

public class CameraTab extends Panel {

    private Button btnUnchangedROI_;
    private Button btnFullROI_;
    private Button btnHalfROI_;
    private Button btnQuarterROI_;
    private Button btnEigthROI_;
    private Button btnCustomROI_;
    private Button btnGetCurrentROI_;

    private ComboBox cmbCameraTriggerMode_;

    public CameraTab() {
        init();
    }

    public void init() {
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

        final String[] labels = {"None"};
        cmbCameraTriggerMode_ = new ComboBox(labels, labels[0]);

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
}