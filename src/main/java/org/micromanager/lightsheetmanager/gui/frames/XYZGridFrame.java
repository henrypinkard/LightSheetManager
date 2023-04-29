package org.micromanager.lightsheetmanager.gui.frames;

import net.miginfocom.swing.MigLayout;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.internal.utils.WindowPositioning;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.model.XYZGrid;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.util.Objects;

public class XYZGridFrame extends JFrame {

    private Button btnEditPositionList_;
    private Button btnComputeGrid_;
    private Button btnRunOverviewAcq_;

    private CheckBox cbxUseX_;
    private CheckBox cbxUseY_;
    private CheckBox cbxUseZ_;

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
    private CheckBox cbxClearPositions_;

    private JLabel lblXCountValue_;
    private JLabel lblYCountValue_;
    private JLabel lblZCountValue_;

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
        setResizable(false);

        setLayout(new MigLayout(
                "insets 10 10 10 10",
                "[]10[]",
                "[]10[]"
        ));

        Button.setDefaultSize(160, 26);
        btnComputeGrid_ = new Button("Compute Grid");
        btnEditPositionList_ = new Button("Edit Position List...");
        btnRunOverviewAcq_ = new Button("Run Overview Acquisition");

        cbxUseX_ = new CheckBox("Slices from stage coordinates", false);
        cbxUseY_ = new CheckBox("Grid in Y", false);
        cbxUseZ_ = new CheckBox("Grid in Z", false);

        // init all values to zero
        lblXCountValue_ = new JLabel("0");
        lblYCountValue_ = new JLabel("0");
        lblZCountValue_ = new JLabel("0");

        Panel.setMigLayoutDefault(
                "insets 10 10 10 10",
                "[]15[]",
                "[]10[]"
        );
        final Panel xPanel = new Panel(cbxUseX_);
        final Panel yPanel = new Panel(cbxUseY_);
        final Panel zPanel = new Panel(cbxUseZ_);
        final Panel buttonPanel = new Panel();

        // X
        final JLabel lblXStart = new JLabel("X start [\u00B5m]:");
        final JLabel lblXStop = new JLabel("X stop [\u00B5m]:");
        final JLabel lblXDelta = new JLabel("X delta [\u00B5m]:");
        final JLabel lblXCount = new JLabel("Slice count:");

        spnXStart_ = Spinner.createDoubleSpinner(0.0, Double.MIN_VALUE, Double.MAX_VALUE, 100.0);
        spnXStop_ = Spinner.createDoubleSpinner(0.0, Double.MIN_VALUE, Double.MAX_VALUE, 100.0);
        spnXDelta_ = Spinner.createDoubleSpinner(0.0, Double.MIN_VALUE, Double.MAX_VALUE, 100.0);

        // Y
        final JLabel lblYStart = new JLabel("Y start [\u00B5m]:");
        final JLabel lblYStop = new JLabel("Y stop [\u00B5m]:");
        final JLabel lblYDelta = new JLabel("Y delta [\u00B5m]:");
        final JLabel lblYCount = new JLabel("Y count:");

        spnYStart_ = Spinner.createDoubleSpinner(0.0, Double.MIN_VALUE, Double.MAX_VALUE, 100.0);
        spnYStop_ = Spinner.createDoubleSpinner(0.0, Double.MIN_VALUE, Double.MAX_VALUE, 100.0);
        spnYDelta_ = Spinner.createDoubleSpinner(0.0, Double.MIN_VALUE, Double.MAX_VALUE, 100.0);

        // Z
        final JLabel lblZStart = new JLabel("Z start [\u00B5m]:");
        final JLabel lblZStop = new JLabel("Z stop [\u00B5m]:");
        final JLabel lblZDelta = new JLabel("Z delta [\u00B5m]:");
        final JLabel lblZCount = new JLabel("Z count:");

        spnZStart_ = Spinner.createDoubleSpinner(0.0, Double.MIN_VALUE, Double.MAX_VALUE, 100.0);
        spnZStop_ = Spinner.createDoubleSpinner(0.0, Double.MIN_VALUE, Double.MAX_VALUE, 100.0);
        spnZDelta_ = Spinner.createDoubleSpinner(0.0, Double.MIN_VALUE, Double.MAX_VALUE, 100.0);

        final Panel settingsPanel = new Panel("Grid Settings");
        final JLabel lblOverlap = new JLabel("Overlap (Y and Z) [%]");
        spnOverlap_ = Spinner.createIntegerSpinner(10, 0, 100, 1);
        cbxClearPositions_ = new CheckBox("Clear position list if YZ unused", false);

        xPanel.add(lblXStart, "");
        xPanel.add(spnXStart_, "wrap");
        xPanel.add(lblXStop, "");
        xPanel.add(spnXStop_, "wrap");
        xPanel.add(lblXDelta, "");
        xPanel.add(spnXDelta_, "wrap");
        xPanel.add(lblXCount, "");
        xPanel.add(lblXCountValue_, "");

        yPanel.add(lblYStart, "");
        yPanel.add(spnYStart_, "wrap");
        yPanel.add(lblYStop, "");
        yPanel.add(spnYStop_, "wrap");
        yPanel.add(lblYDelta, "");
        yPanel.add(spnYDelta_, "wrap");
        yPanel.add(lblYCount, "");
        yPanel.add(lblYCountValue_, "");

        zPanel.add(lblZStart, "");
        zPanel.add(spnZStart_, "wrap");
        zPanel.add(lblZStop, "");
        zPanel.add(spnZStop_, "wrap");
        zPanel.add(lblZDelta, "");
        zPanel.add(spnZDelta_, "wrap");
        zPanel.add(lblZCount, "");
        zPanel.add(lblZCountValue_, "");

        settingsPanel.add(lblOverlap, "split 2");
        settingsPanel.add(spnOverlap_, "wrap");
        settingsPanel.add(cbxClearPositions_, "wrap");

        buttonPanel.add(btnComputeGrid_, "wrap");
        buttonPanel.add(btnEditPositionList_, "wrap");
        buttonPanel.add(btnRunOverviewAcq_, "");

        add(yPanel, "growx");
        add(zPanel, "wrap");
        add(xPanel, "growx");
        add(settingsPanel, "wrap");
        add(buttonPanel, "");

        pack();
    }

    private void createEventHandlers() {
        final XYZGrid xyzGrid = model_.getXYZGrid();

        // Spinners X
        spnXStart_.registerListener(e ->
                xyzGrid.setStartX(spnXStart_.getDouble()));
        spnXStop_.registerListener(e ->
                xyzGrid.setStopX(spnXStop_.getDouble()));
        spnXDelta_.registerListener(e ->
                xyzGrid.setDeltaX(spnXDelta_.getDouble()));

        // Spinners Y
        spnYStart_.registerListener(e ->
                xyzGrid.setStartY(spnYStart_.getDouble()));
        spnYStop_.registerListener(e ->
                xyzGrid.setStopY(spnYStop_.getDouble()));
        spnYDelta_.registerListener(e ->
                xyzGrid.setDeltaY(spnYDelta_.getDouble()));

        // Spinners Z
        spnZStart_.registerListener(e -> xyzGrid.setStartZ(spnZStart_.getDouble()));
        spnZStop_.registerListener(e -> xyzGrid.setStopZ(spnZStop_.getDouble()));
        spnZDelta_.registerListener(e -> {});

        spnOverlap_.registerListener(e -> {});

        // Buttons
        btnComputeGrid_.registerListener(
                e -> System.out.println("compute grid pressed"));
        btnEditPositionList_.registerListener(
                e -> model_.studio().app().showPositionList());
        btnRunOverviewAcq_.registerListener(
                e -> System.out.println("run overview acq pressed"));
    }
}
