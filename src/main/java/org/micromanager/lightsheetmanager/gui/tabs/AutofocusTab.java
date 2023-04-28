package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;

import java.awt.Font;

public class AutofocusTab extends Panel {

    private Label lblMaxOffsetSetup;
    private Label lblMaxOffsetSetupUM;
    private Spinner spnMaxOffsetSetup;
    private CheckBox cbxAutoUpdateFocusFound;

    public AutofocusTab() {
        setMigLayout(
                "",
                "[]10[]",
                "[]10[]"
        );
        init();
    }

    public void init() {
        final Label lblTitle = new Label("Autofocus Settings", Font.BOLD, 20);

        String[] labels = {"None"};

        final Panel panelGeneralOptions = new Panel("General Autofocus Options");
        final Panel panelAcqActiveOptions = new Panel("Autofocus Options During Acquisition");
        final Panel panelAcqSetupOptions = new Panel("Autofocus Options During Setup");
        final Panel panelMoveCorrection = new Panel("Movement Correction Options");

        // general options
        final Label lblNumImages = new Label("Number of Images:");
        final Label lblStepSize = new Label("Step Size [\u00B5m]:");
        final Label lblMode = new Label("Mode:");
        final Label lblScoringAlgorithm = new Label("Scoring algorithm:");
        final Label lblFittingFunction = new Label("Fit using:");
        final Label lblMinimumR2 = new Label("<html>Minimum R<sup>2</sup></html>");

        final CheckBox cbxShowImages = new CheckBox("Show Images", 12, true, CheckBox.RIGHT);
        final CheckBox cbxShowPlot = new CheckBox("Show Plot", 12, true, CheckBox.RIGHT);

        final Spinner spnNumImages = Spinner.createIntegerSpinner(1, 0, Integer.MAX_VALUE, 1);
        final Spinner spnStepSize = Spinner.createFloatSpinner(0.001f, 0.0f, 100.0f, 1.0f);
        final Spinner spnMinimumR2 = Spinner.createFloatSpinner(0.75f, 0.0f, 1.0f, 0.01f);

        final ComboBox cmbMode = new ComboBox(labels, "None");
        final ComboBox cmbScoringAlgorithm = new ComboBox(labels, "None");
        final ComboBox cmbFittingFunction = new ComboBox(labels, "None");

        // active options
        final Label lblTimePoints = new Label("time points");
        final Label lblMaxOffsetActive = new Label("Max offset change: ");
        final Label lblAutofocusEveryX = new Label("Autofocus every ");
        final Label lblAutofocusChannel = new Label("Autofocus channel:");

        final CheckBox cbxAutofocusEveryPass = new CheckBox("Autofocus every stage pass", 12, false, CheckBox.RIGHT);
        final CheckBox cbxAutofocusBeforeAcq = new CheckBox("Autofocus before starting acquisition", 12, false, CheckBox.RIGHT);

        final ComboBox cmbAutofocusChannel = new ComboBox(labels, "None", 60, 20);
        final Spinner spnAutofocusEveryX = Spinner.createIntegerSpinner(10, 0, 1000, 1);
        final Spinner spnMaxOffset = Spinner.createIntegerSpinner(3, 0, 10, 1);

        // setup options
        lblMaxOffsetSetup = new Label("Max offset change: ");
        lblMaxOffsetSetupUM = new Label("\u00B5m (\u00B1)");
        cbxAutoUpdateFocusFound = new CheckBox("Automatically update offset if focus found", 12, false, CheckBox.RIGHT);
        spnMaxOffsetSetup = Spinner.createIntegerSpinner(3, 0, 10, 1);
        setSetupOptionsState(false);

        // movement correction options
        final Label lblTimePoints2 = new Label("time points"); // TODO: share this value?
        final Label lblCorrectEveryX = new Label("Correct every");
        final Label lblChannel = new Label("Channel:");
        final Label lblMaxDistance = new Label("Max distance:");
        final Label lblMinMovement = new Label("Min movement:");
        final Spinner spnCorrectEveryX = Spinner.createIntegerSpinner(100, 0, 1000, 1);
        final Spinner spnMaxDistance = Spinner.createIntegerSpinner(96, 0, 100, 1);
        final Spinner spnMinMovement = Spinner.createFloatSpinner(1.0f, 0.0f, 10.0f, 0.5f);
        final ComboBox cmbChannel = new ComboBox(labels, "None", 60, 20);

        createEventHandlers();

        // add ui elements to the panel
        add(lblTitle, "span 2, wrap");

        // general options
        panelGeneralOptions.add(cbxShowImages, "");
        panelGeneralOptions.add(cbxShowPlot, "wrap");
        panelGeneralOptions.add(lblNumImages, "");
        panelGeneralOptions.add(spnNumImages, "wrap");
        panelGeneralOptions.add(lblStepSize, "");
        panelGeneralOptions.add(spnStepSize, "wrap");
        panelGeneralOptions.add(lblMinimumR2, "");
        panelGeneralOptions.add(spnMinimumR2, "wrap");
        panelGeneralOptions.add(lblMode, "");
        panelGeneralOptions.add(cmbMode, "wrap");
        panelGeneralOptions.add(lblScoringAlgorithm, "");
        panelGeneralOptions.add(cmbScoringAlgorithm, "wrap");
        panelGeneralOptions.add(lblFittingFunction, "");
        panelGeneralOptions.add(cmbFittingFunction, "");

        // active options
        panelAcqActiveOptions.add(cbxAutofocusEveryPass, "span 3, wrap");
        panelAcqActiveOptions.add(cbxAutofocusBeforeAcq, "span 3, wrap");
        panelAcqActiveOptions.add(lblAutofocusEveryX, "");
        panelAcqActiveOptions.add(spnAutofocusEveryX, "");
        panelAcqActiveOptions.add(lblTimePoints, "wrap");
        panelAcqActiveOptions.add(lblAutofocusChannel, "");
        panelAcqActiveOptions.add(cmbAutofocusChannel, "wrap");
        panelAcqActiveOptions.add(lblMaxOffsetActive, "");
        panelAcqActiveOptions.add(spnMaxOffset, "");
        panelAcqActiveOptions.add(new Label("\u00B5m (\u00B1)"), "");

        // setup options
        panelAcqSetupOptions.add(cbxAutoUpdateFocusFound, "span 3, wrap");
        panelAcqSetupOptions.add(lblMaxOffsetSetup, "");
        panelAcqSetupOptions.add(spnMaxOffsetSetup, "");
        panelAcqSetupOptions.add(lblMaxOffsetSetupUM, "");

        // movement correction
        panelMoveCorrection.add(lblCorrectEveryX, "");
        panelMoveCorrection.add(spnCorrectEveryX, "");
        panelMoveCorrection.add(lblTimePoints2, "wrap");
        panelMoveCorrection.add(lblChannel, "");
        panelMoveCorrection.add(cmbChannel, "wrap");
        panelMoveCorrection.add(lblMaxDistance, "");
        panelMoveCorrection.add(spnMaxDistance, "");
        panelMoveCorrection.add(new Label("\u00B5m (\u00B1)"), "wrap");
        panelMoveCorrection.add(lblMinMovement, "");
        panelMoveCorrection.add(spnMinMovement, "");
        panelMoveCorrection.add(new Label("\u00B5m (\u00B1)"), "");

        // add panels to tab
        add(panelGeneralOptions, "");
        add(panelAcqActiveOptions, "wrap");
        add(panelAcqSetupOptions, "");
        add(panelMoveCorrection, "wrap");
    }

    private void setSetupOptionsState(final boolean state) {
        lblMaxOffsetSetup.setEnabled(state);
        spnMaxOffsetSetup.setEnabled(state);
        lblMaxOffsetSetupUM.setEnabled(state);

    }
    private void createEventHandlers() {
        cbxAutoUpdateFocusFound.registerListener(event -> {
            setSetupOptionsState(cbxAutoUpdateFocusFound.isSelected());
        });
    }

}
