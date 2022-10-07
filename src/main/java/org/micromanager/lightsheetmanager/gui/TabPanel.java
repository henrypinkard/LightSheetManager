package org.micromanager.lightsheetmanager.gui;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.model.DeviceManager;
import org.micromanager.lightsheetmanager.gui.tabs.AcquisitionTab;
import org.micromanager.lightsheetmanager.gui.tabs.AutofocusTab;
import org.micromanager.lightsheetmanager.gui.tabs.CameraTab;
import org.micromanager.lightsheetmanager.gui.tabs.DataTab;
import org.micromanager.lightsheetmanager.gui.tabs.DeviceTab;
import org.micromanager.lightsheetmanager.gui.tabs.HelpTab;
import org.micromanager.lightsheetmanager.gui.tabs.SettingsTab;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.TabbedPane;

import java.util.Objects;

/**
 * The tabbed pane to select between different settings panels.
 *
 */
public class TabPanel extends Panel {

    private Studio studio_;
    private AcquisitionTab acquisitionPanel_;
    private AutofocusTab autofocusPanel_;
    private DataTab dataPanel_;
    private DeviceTab devicePanel_;
    private CameraTab cameraPanel_;
    private SettingsTab settingsPanel_;
    private HelpTab helpPanel_;

    private final TabbedPane tabbedPane_;

    private LightSheetManagerModel model_;
    private DeviceManager devices_;

    public TabPanel(final Studio studio, final LightSheetManagerModel model, final DeviceManager devices) {
        studio_ = Objects.requireNonNull(studio);
        devices_ = Objects.requireNonNull(devices);
        model_ = Objects.requireNonNull(model);

        //tabbedPane_ = new TabbedPane();
        tabbedPane_ = new TabbedPane(800, 600);
        init();
    }

    /**
     * Create the panel.
     */
    private void init() {
        // create panels
        acquisitionPanel_ = new AcquisitionTab(studio_, model_);
        autofocusPanel_ = new AutofocusTab();
        cameraPanel_ = new CameraTab();
        dataPanel_ = new DataTab();
        devicePanel_ = new DeviceTab(devices_);
        settingsPanel_ = new SettingsTab();
        helpPanel_ = new HelpTab();

        // add tabs to the pane
        tabbedPane_.addTab(createTabTitle("Acquisition"), acquisitionPanel_);
        tabbedPane_.addTab(createTabTitle("Autofocus"), autofocusPanel_);
        tabbedPane_.addTab(createTabTitle("Cameras"), cameraPanel_);
        tabbedPane_.addTab(createTabTitle("Data"), dataPanel_);
        tabbedPane_.addTab(createTabTitle("Devices"), devicePanel_);
        tabbedPane_.addTab(createTabTitle("Settings"), settingsPanel_);
        tabbedPane_.addTab(createTabTitle("Help"), helpPanel_);
//        tabbedPane.addTab(createTabTitle("Main"), mainPanel);
//        tabbedPane.addTab(createTabTitle("Camera"), cameraPanel);
//        tabbedPane.addTab(createTabTitle("Data"), dataPanel);

        // add ui elements to the panel
        add(tabbedPane_, "growx, growy");
    }

    // use HTML to make the tab labels look nice
    private String createTabTitle(final String title) {
        return "<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b><font size=4>" + title + "</font></b></body></html>";
    }

}
