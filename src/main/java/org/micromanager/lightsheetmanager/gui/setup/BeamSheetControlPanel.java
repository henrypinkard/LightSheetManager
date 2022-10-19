package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Slider;
import org.micromanager.lightsheetmanager.gui.components.TextField;

import javax.swing.JLabel;
import javax.swing.UIManager;

public class BeamSheetControlPanel extends Panel {

    // first panel
    private TextField txtSlope_;
    private TextField txtOffset_;
    private Button btnPlotProfile_;
    private JLabel lblSlopeUnits2_;

    // second panel
    private CheckBox chkAutoSheetWidth_;
    private TextField txtSheetWidth_;
    private Button btnCenterOffset_;

    private Button btnSheetWidthMinus_;
    private Button btnSheetWidthPlus_;

    private Button btnSheetOffsetMinus_;
    private Button btnSheetOffsetPlus_;

    private Slider sldSheetWidth_;
    private Slider sldSheetOffset_;

    // layout panels
    private Panel firstPanel_;  // virtual slit camera trigger mode active
    private Panel secondPanel_; // all other camera trigger modes

    private boolean isVirtualSlitMode_;

    public BeamSheetControlPanel() {
        super("Light Sheet Synchronization");
        init();
    }

    private void init() {
        firstPanel_ = new Panel();
        secondPanel_ = new Panel();

        // first panel for virtual slit mode
        final JLabel lblSlope = new JLabel("Speed / slope:");
        final JLabel lblOffset = new JLabel("Start / offset:");
        final JLabel lblSlopeUnits = new JLabel("μ°/px");
        final JLabel lblOffsetUnits = new JLabel("m°");

        btnPlotProfile_ = new Button("Plot Profile", 100, 26);
        txtSlope_ = new TextField();
        txtOffset_ = new TextField();

        // second panel for other camera trigger modes

        final JLabel lblSheetWidth = new JLabel("Sheet width:");
        final JLabel lblSheetOffset = new JLabel("Sheet offset:");
        lblSlopeUnits2_ = new JLabel("μ°/px"); // TODO: reuse labels like this?

        chkAutoSheetWidth_ = new CheckBox("Automatic", false);
        txtSheetWidth_ = new TextField();
        btnCenterOffset_ = new Button("Center", 100, 26);

        Button.setDefaultSize(40, 30);
        btnSheetWidthMinus_ = new Button("-");
        btnSheetWidthPlus_ = new Button("+");
        btnSheetOffsetMinus_ = new Button("-");
        btnSheetOffsetPlus_ = new Button("+");

        // TODO: set the ranges of these sliders to the micro-mirror's min and max deflection
        UIManager.put("Slider.focus", UIManager.get("Slider.background")); // remove highlight when clicked
        sldSheetWidth_ = new Slider(0, 8, 4);
        sldSheetOffset_ = new Slider(-1, 1, 0);

        createEventHandlers();

        firstPanel_.add(lblSlope, "");
        firstPanel_.add(txtSlope_, "");
        firstPanel_.add(lblSlopeUnits, "wrap");
        firstPanel_.add(lblOffset, "");
        firstPanel_.add(txtOffset_, "");
        firstPanel_.add(lblOffsetUnits, "");
        firstPanel_.add(btnPlotProfile_, "gapleft 100");

        secondPanel_.add(lblSheetWidth, "");
        secondPanel_.add(chkAutoSheetWidth_, "");
        secondPanel_.add(txtSheetWidth_, "");
        secondPanel_.add(lblSlopeUnits2_, "");
        secondPanel_.add(btnSheetWidthMinus_, "");
        secondPanel_.add(btnSheetWidthPlus_, "");
        secondPanel_.add(sldSheetWidth_, "wrap");
        secondPanel_.add(lblSheetOffset, "span 3");
        secondPanel_.add(btnCenterOffset_, "");
        secondPanel_.add(btnSheetOffsetMinus_, "");
        secondPanel_.add(btnSheetOffsetPlus_, "");
        secondPanel_.add(sldSheetOffset_, "");

        // TODO: select panel based on Microscope Geometry
        // first panel => virtual slit mode
        isVirtualSlitMode_ = true;
        add(firstPanel_, "");
    }

    private void createEventHandlers() {
        // first panel
        btnPlotProfile_.registerListener(e -> {
            swapPanels(); // for testing
        });

        // second panel
        chkAutoSheetWidth_.registerListener(e -> {
            //final boolean isSelected = chkAutoSheetWidth_.isSelected();
            setEnabledSheetWidth(chkAutoSheetWidth_.isSelected());
        });

        btnCenterOffset_.registerListener(e -> {
            System.out.println("center offset pressed");
            swapPanels(); // for testing
        });

    }

    /**
     * This is called when the camera trigger mode changes and updates the controls accordingly.
     */
    public void swapPanels() {
        if (isVirtualSlitMode_) {
            remove(firstPanel_);
            add(secondPanel_);
            revalidate();
            repaint();
            isVirtualSlitMode_ = false;
        } else {
            remove(secondPanel_);
            add(firstPanel_);
            revalidate();
            repaint();
            isVirtualSlitMode_ = true;
        }
    }

    /**
     * Enable or disable the sheet width controls based on the "Automatic" checkbox.
     *
     * @param state true to enable the ui components
     */
    private void setEnabledSheetWidth(final boolean state) {
        txtSheetWidth_.setEnabled(state);
        lblSlopeUnits2_.setEnabled(state);
        btnSheetWidthMinus_.setEnabled(!state);
        btnSheetWidthPlus_.setEnabled(!state);
        sldSheetWidth_.setEnabled(!state);
    }

}
