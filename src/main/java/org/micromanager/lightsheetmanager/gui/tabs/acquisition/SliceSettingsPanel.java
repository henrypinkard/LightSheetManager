package org.micromanager.lightsheetmanager.gui.tabs.acquisition;

import org.micromanager.lightsheetmanager.gui.frames.AdvancedTimingFrame;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;

import java.util.Objects;

// TODO: changes based on camera trigger mode
public class SliceSettingsPanel extends Panel {

    private CheckBox chkUseAdvancedTiming_;
    private CheckBox chkMinimizeSlicePeriod_;
    private Label lblSlicePeriod_;
    private Label lblSampleExposure_;
    private Spinner spnSlicePeriod_;
    private Spinner spnSampleExposure_;

    private AdvancedTimingFrame advTimingFrame_;
    public SliceSettingsPanel(final AdvancedTimingFrame advTimingFrame) {
        super("Slice Settings");
        advTimingFrame_ = Objects.requireNonNull(advTimingFrame);
        createUserInterface();
    }

    private void createUserInterface() {
        lblSlicePeriod_ = new Label("Slice period [ms]");
        lblSampleExposure_ = new Label("Sample exposure [ms]");
        chkMinimizeSlicePeriod_ = new CheckBox("Minimize slice period", 12, false, CheckBox.RIGHT);
        chkUseAdvancedTiming_ = new CheckBox("Use advanced timing settings", 12, false, CheckBox.RIGHT);
        spnSlicePeriod_ = Spinner.createFloatSpinner(30.0f, 0.0f, Float.MAX_VALUE, 0.25f);
        spnSampleExposure_ = Spinner.createFloatSpinner(10.0f, 0.0f, Float.MAX_VALUE, 0.25f);

        createEventHandlers();

        add(chkMinimizeSlicePeriod_, "wrap");
        add(lblSlicePeriod_, "");
        add(spnSlicePeriod_, "wrap");
        add(lblSampleExposure_, "");
        add(spnSampleExposure_, "wrap");
        add(chkUseAdvancedTiming_, "span 2");
    }

    private void createEventHandlers() {
        chkMinimizeSlicePeriod_.registerListener(e -> {
            final boolean selected = !chkMinimizeSlicePeriod_.isSelected();
            lblSlicePeriod_.setEnabled(selected);
            spnSlicePeriod_.setEnabled(selected);
        });

        spnSlicePeriod_.registerListener(e -> {

        });

        spnSampleExposure_.registerListener(e -> {

        });

        chkUseAdvancedTiming_.registerListener(e -> {
            // disable ui elements
            final boolean selected = !chkUseAdvancedTiming_.isSelected();
            chkMinimizeSlicePeriod_.setEnabled(selected);
            lblSlicePeriod_.setEnabled(selected);
            spnSlicePeriod_.setEnabled(selected);
            lblSampleExposure_.setEnabled(selected);
            spnSampleExposure_.setEnabled(selected);
            // show frame
            if (!advTimingFrame_.isVisible()) {
                advTimingFrame_.setVisible(true);
            }
        });
    }
}
