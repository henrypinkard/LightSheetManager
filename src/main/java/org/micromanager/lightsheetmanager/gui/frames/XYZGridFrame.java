package org.micromanager.lightsheetmanager.gui.frames;

import net.miginfocom.swing.MigLayout;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.internal.utils.WindowPositioning;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.util.Objects;

public class XYZGridFrame extends JFrame {

    private Button btnEditPositionList_;
    private Button btnComputeGrid_;

    private CheckBox chkUseX_;
    private CheckBox chkUseY_;
    private CheckBox chkUseZ_;

    private Spinner spnXStart_;
    private Spinner spnXStop_;
    private Spinner spnXDelta_;

    private Spinner spnYStart_;
    private Spinner spnYStop_;
    private Spinner spnYDelta_;

    private Spinner spnZStart_;
    private Spinner spnZStop_;
    private Spinner spnZDelta_;

    private Spinner spnOverlap_;
    private CheckBox chkClearPositions_;

    private JLabel lblXCount_;
    private JLabel lblYCount_;
    private JLabel lblZCount_;

    private LightSheetManagerModel model_;

    public XYZGridFrame(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        WindowPositioning.setUpBoundsMemory(this, this.getClass(), this.getClass().getSimpleName());
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        setTitle("XYZ Grid");
        setIconImage(Icons.MICROSCOPE.getImage());
        setLayout(new MigLayout("insets 10 10 10 10", "[]10[]", "[]10[]"));
        setResizable(false);

        Button.setDefaultSize(140, 26);
        btnComputeGrid_ = new Button("Compute Grid");
        btnEditPositionList_ = new Button("Edit Position List...");

        chkUseX_ = new CheckBox("Slices from stage coordinates", false);
        chkUseY_ = new CheckBox("Grid in Y", false);
        chkUseZ_ = new CheckBox("Grid in Z", false);

        Panel.setMigLayoutDefault(
                "insets 10 10 10 10",
                "[]15[]",
                "[]10[]"
        );
        final Panel xPanel = new Panel(chkUseX_);
        final Panel yPanel = new Panel(chkUseY_);
        final Panel zPanel = new Panel(chkUseZ_);
        final Panel buttonPanel = new Panel();

        // X
        final JLabel lblXStart = new JLabel("X start [\u00B5m]:");
        final JLabel lblXStop = new JLabel("X stop [\u00B5m]:");
        final JLabel lblXDelta = new JLabel("X delta [\u00B5m]:");
        final JLabel lblXCount = new JLabel("Slice count:");

        spnXStart_ = Spinner.createIntegerSpinner(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 100);
        spnXStop_ = Spinner.createIntegerSpinner(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 100);
        spnXDelta_ = Spinner.createIntegerSpinner(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 100);

        // Y
        final JLabel lblYStart = new JLabel("Y start [\u00B5m]:");
        final JLabel lblYStop = new JLabel("Y stop [\u00B5m]:");
        final JLabel lblYDelta = new JLabel("Y delta [\u00B5m]:");
        final JLabel lblYCount = new JLabel("Y count:");

        spnYStart_ = Spinner.createIntegerSpinner(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 100);
        spnYStop_ = Spinner.createIntegerSpinner(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 100);
        spnYDelta_ = Spinner.createIntegerSpinner(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 100);

        // Z
        final JLabel lblZStart = new JLabel("Z start [\u00B5m]:");
        final JLabel lblZStop = new JLabel("Z stop [\u00B5m]:");
        final JLabel lblZDelta = new JLabel("Z delta [\u00B5m]:");
        final JLabel lblZCount = new JLabel("Z count:");

        spnZStart_ = Spinner.createIntegerSpinner(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 100);
        spnZStop_ = Spinner.createIntegerSpinner(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 100);
        spnZDelta_ = Spinner.createIntegerSpinner(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 100);

        final Panel settingsPanel = new Panel("Grid Settings");
        final JLabel lblOverlap = new JLabel("Overlap (Y and Z) [%]");
        spnOverlap_ = Spinner.createIntegerSpinner(10, 0, 100, 1);
        chkClearPositions_ = new CheckBox("Clear position list if YZ unused", false);

        xPanel.add(lblXStart, "");
        xPanel.add(spnXStart_, "wrap");
        xPanel.add(lblXStop, "");
        xPanel.add(spnXStop_, "wrap");
        xPanel.add(lblXDelta, "");
        xPanel.add(spnXDelta_, "wrap");
        xPanel.add(lblXCount, "");

        yPanel.add(lblYStart, "");
        yPanel.add(spnYStart_, "wrap");
        yPanel.add(lblYStop, "");
        yPanel.add(spnYStop_, "wrap");
        yPanel.add(lblYDelta, "");
        yPanel.add(spnYDelta_, "wrap");
        yPanel.add(lblYCount, "");

        zPanel.add(lblZStart, "");
        zPanel.add(spnZStart_, "wrap");
        zPanel.add(lblZStop, "");
        zPanel.add(spnZStop_, "wrap");
        zPanel.add(lblZDelta, "");
        zPanel.add(spnZDelta_, "wrap");
        zPanel.add(lblZCount, "");

        settingsPanel.add(lblOverlap, "split 2");
        settingsPanel.add(spnOverlap_, "wrap");
        settingsPanel.add(chkClearPositions_, "wrap");

        buttonPanel.add(btnComputeGrid_, "wrap");
        buttonPanel.add(btnEditPositionList_, "");

        add(yPanel, "growx");
        add(zPanel, "wrap");
        add(xPanel, "growx");
        add(settingsPanel, "wrap");
        add(buttonPanel, "");

        pack();
    }

    private void createEventHandlers() {
        btnComputeGrid_.registerListener(e -> System.out.println("compute grid pressed"));
        btnEditPositionList_.registerListener(e -> model_.studio().app().showPositionList());
    }
}
