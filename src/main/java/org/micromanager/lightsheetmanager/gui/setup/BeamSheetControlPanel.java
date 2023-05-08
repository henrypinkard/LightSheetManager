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
    private CheckBox cbxAutoSheetWidth_;
    private TextField txtSheetWidth_;
    private Button btnCenterOffset_;

    private Button btnSheetWidthMinus_;
    private Button btnSheetWidthPlus_;

    private Button btnSheetOffsetMinus_;
    private Button btnSheetOffsetPlus_;

    private Slider sdrSheetWidth_;
    private Slider sdrSheetOffset_;

    // layout panels
    private Panel pblFirst_;  // virtual slit camera trigger mode active
    private Panel pnlSecond_; // all other camera trigger modes

    private boolean isVirtualSlitMode_;

    public BeamSheetControlPanel() {
        super("Light Sheet Synchronization");
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        pblFirst_ = new Panel();
        pnlSecond_ = new Panel();

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

        cbxAutoSheetWidth_ = new CheckBox("Automatic", false);
        txtSheetWidth_ = new TextField();
        btnCenterOffset_ = new Button("Center", 100, 26);

        Button.setDefaultSize(40, 30);
        btnSheetWidthMinus_ = new Button("-");
        btnSheetWidthPlus_ = new Button("+");
        btnSheetOffsetMinus_ = new Button("-");
        btnSheetOffsetPlus_ = new Button("+");

        // TODO: set the ranges of these sliders to the micro-mirror's min and max deflection
        UIManager.put("Slider.focus", UIManager.get("Slider.background")); // remove highlight when clicked
        sdrSheetWidth_ = new Slider(0, 8, 4);
        sdrSheetOffset_ = new Slider(-1, 1, 0);

        pblFirst_.add(lblSlope, "");
        pblFirst_.add(txtSlope_, "");
        pblFirst_.add(lblSlopeUnits, "wrap");
        pblFirst_.add(lblOffset, "");
        pblFirst_.add(txtOffset_, "");
        pblFirst_.add(lblOffsetUnits, "");
        pblFirst_.add(btnPlotProfile_, "gapleft 100");

        pnlSecond_.add(lblSheetWidth, "");
        pnlSecond_.add(cbxAutoSheetWidth_, "");
        pnlSecond_.add(txtSheetWidth_, "");
        pnlSecond_.add(lblSlopeUnits2_, "");
        pnlSecond_.add(btnSheetWidthMinus_, "");
        pnlSecond_.add(btnSheetWidthPlus_, "");
        pnlSecond_.add(sdrSheetWidth_, "wrap");
        pnlSecond_.add(lblSheetOffset, "span 3");
        pnlSecond_.add(btnCenterOffset_, "");
        pnlSecond_.add(btnSheetOffsetMinus_, "");
        pnlSecond_.add(btnSheetOffsetPlus_, "");
        pnlSecond_.add(sdrSheetOffset_, "");

        // TODO: select panel based on Microscope Geometry
        // first panel => virtual slit mode
        isVirtualSlitMode_ = true;
        add(pblFirst_, "");
    }

    private void createEventHandlers() {
        // first panel
        btnPlotProfile_.registerListener(e -> {
            swapPanels(); // for testing
        });

        // second panel
        cbxAutoSheetWidth_.registerListener(e -> {
            setEnabledSheetWidth(cbxAutoSheetWidth_.isSelected());
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
            remove(pblFirst_);
            add(pnlSecond_);
            revalidate();
            repaint();
            isVirtualSlitMode_ = false;
        } else {
            remove(pnlSecond_);
            add(pblFirst_);
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
        sdrSheetWidth_.setEnabled(!state);
    }

}
