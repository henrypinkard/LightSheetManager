package org.micromanager.lightsheetmanager.gui.setup;


import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.TextField;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.JLabel;
import java.util.Objects;

public class PiezoCalibrationPanel extends Panel {

    private Button btnTwoPoint_;
    private Button btnUpdate_;
    private Button btnRunAutofocus_;

    private TextField txtSlope_;
    private TextField txtOffset_;

    private Button btnStepUp_;
    private Button btnStepDown_;
    private TextField txtStepSize_;

    private final int pathNum_;

    private LightSheetManagerModel model_;

    public PiezoCalibrationPanel(final LightSheetManagerModel model, final int pathNum) {
        super("Piezo/Slice Calibration");
        model_ = Objects.requireNonNull(model);
        pathNum_ = pathNum;
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {

        final JLabel lblSlope = new JLabel("Slope:");
        final JLabel lblOffset = new JLabel("Offset:");
        final JLabel lblStepSize = new JLabel("Step Size:");
        //final JLabel lblMicrons = new JLabel("μm");
        final JLabel lblMicronsPerDeg = new JLabel("μm/°");

        setMigLayout(
                "",
                "[]5[]",
                "[]5[]"
        );

        Button.setDefaultSize(80, 26);
        btnTwoPoint_ = new Button("2-point");
        btnUpdate_ = new Button("Update");
        btnRunAutofocus_ = new Button("Run Autofocus", 120, 26);

        final DefaultAcquisitionSettingsDISPIM acqSettings =
                model_.acquisitions().getAcquisitionSettings();

        txtSlope_ = new TextField();
        txtOffset_ = new TextField();
        txtStepSize_ = new TextField();

        if (pathNum_ == 1) {
            txtSlope_.setText(String.valueOf(acqSettings.sliceCalibration().sliceSlope()));
            txtOffset_.setText(String.valueOf(acqSettings.sliceCalibration().sliceOffset()));
        } else {
            txtSlope_.setText(String.valueOf(acqSettings.sliceCalibration2().sliceSlope()));
            txtOffset_.setText(String.valueOf(acqSettings.sliceCalibration2().sliceOffset()));
        }
        Button.setDefaultSize(26, 26);
        btnStepUp_ = new Button(Icons.ARROW_UP);
        btnStepDown_ = new Button(Icons.ARROW_DOWN);

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
        final DefaultAcquisitionSettingsDISPIM.Builder asb =
                model_.acquisitions().getAcquisitionSettingsBuilder();

        btnTwoPoint_.registerListener(e -> {

        });

        btnUpdate_.registerListener(e -> {

        });

        btnStepUp_.registerListener(e -> {

        });

        btnStepDown_.registerListener(e -> {

        });

        txtSlope_.addDocumentListener(e -> {
            System.out.println("Here1");
            if (pathNum_ == 1) {
                asb.sliceCalibrationBuilder().sliceSlope(Double.parseDouble(txtSlope_.getText()));
            } else {
                asb.sliceCalibrationBuilder2().sliceSlope(Double.parseDouble(txtSlope_.getText()));
            }
        });

        txtOffset_.addDocumentListener(e -> {
            System.out.println("Here2");
            if (pathNum_ == 1) {
                asb.sliceCalibrationBuilder().sliceOffset(Double.parseDouble(txtOffset_.getText()));
            } else {
                asb.sliceCalibrationBuilder2().sliceOffset(Double.parseDouble(txtOffset_.getText()));
            }
        });

        txtStepSize_.registerListener(e -> {

        });

        btnRunAutofocus_.registerListener(e -> {

        });
    }

}
