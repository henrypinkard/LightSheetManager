package org.micromanager.lightsheetmanager.gui.setup;


import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.TextField;

import javax.swing.JLabel;

public class PiezoCalibrationPanel extends Panel {

    private Button btnTwoPoint_;
    private Button btnUpdate_;
    private Button btnRunAutofocus_;

    private TextField txtSlope_;
    private TextField txtOffset_;

    private Button btnStepUp_;
    private Button btnStepDown_;
    private TextField txtStepSize_;


    public PiezoCalibrationPanel() {
        super("Piezo/Slice Calibration");
        init();
    }

    private void init() {

        final JLabel lblSlope = new JLabel("Slope:");
        final JLabel lblOffset = new JLabel("Offset:");
        final JLabel lblStepSize = new JLabel("Step Size:");
        //final JLabel lblMicrons = new JLabel("μm");
        final JLabel lblMicronsPerDeg = new JLabel("μm/°");

        Button.setDefaultSize(80, 26);
        btnTwoPoint_ = new Button("2-point");
        btnUpdate_ = new Button("Update");
        btnRunAutofocus_ = new Button("Run Autofocus", 120, 26);

        txtSlope_ = new TextField();
        txtOffset_ = new TextField();
        txtStepSize_ = new TextField();

        Button.setDefaultSize(26, 26);
        btnStepUp_ = new Button(Icons.ARROW_UP);
        btnStepDown_ = new Button(Icons.ARROW_DOWN);

        createEventHandlers();

        add(lblSlope, "");
        add(txtSlope_, "");
        add(lblMicronsPerDeg, "");
        add(btnTwoPoint_, "wrap");
        add(lblOffset, "");
        add(txtOffset_, "");
        add(new JLabel("μm"), "");
        add(btnUpdate_, "wrap");
        add(lblStepSize, "");
        add(txtStepSize_, "");
        add(new JLabel("μm"), "");
        add(btnStepDown_, "split 2");
        add(btnStepUp_, "wrap");
        add(btnRunAutofocus_, "span 3");
    }

    private void createEventHandlers() {

        btnTwoPoint_.registerListener(e -> {

        });

        btnUpdate_.registerListener(e -> {

        });

        btnStepUp_.registerListener(e -> {

        });

        btnStepDown_.registerListener(e -> {

        });

        txtSlope_.registerListener(e -> {

        });

        txtOffset_.registerListener(e -> {

        });

        txtStepSize_.registerListener(e -> {

        });

        btnRunAutofocus_.registerListener(e -> {

        });
    }

}
