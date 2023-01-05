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
    private AcquisitionTab acquisitionTab_;
    private AutofocusTab autofocusTab_;
    private DataTab dataTab_;
    private DeviceTab deviceTab_;
    private CameraTab cameraTab_;
    private SettingsTab settingsTab_;
    private HelpTab helpTab_;

    private final TabbedPane tabbedPane_;

    private LightSheetManagerModel model_;
    private DeviceManager devices_;

    public TabPanel(final Studio studio, final LightSheetManagerModel model, final DeviceManager devices) {
        studio_ = Objects.requireNonNull(studio);
        devices_ = Objects.requireNonNull(devices);
        model_ = Objects.requireNonNull(model);

        tabbedPane_ = new TabbedPane(840, 600);
        createUserInterface();
    }

    public AcquisitionTab getAcquisitionTab() {
        return acquisitionTab_;
    }

    public DataTab getDataTab() {
        return dataTab_;
    }

    public DeviceTab getDeviceTab() {
        return deviceTab_;
    }

    public CameraTab getCameraTab() {
        return cameraTab_;
    }

    /**
     * Create the panel.
     */
    private void createUserInterface() {
        // create panels
        acquisitionTab_ = new AcquisitionTab(model_);
        autofocusTab_ = new AutofocusTab();
        cameraTab_ = new CameraTab(model_, acquisitionTab_.getSliceSettingsPanel());
        dataTab_ = new DataTab(model_);
        deviceTab_ = new DeviceTab(model_, devices_);
        settingsTab_ = new SettingsTab();
        helpTab_ = new HelpTab();

        // add tabs to the pane
        tabbedPane_.addTab(createTabTitle("Acquisition"), acquisitionTab_);
        tabbedPane_.addTab(createTabTitle("Autofocus"), autofocusTab_);
        tabbedPane_.addTab(createTabTitle("Cameras"), cameraTab_);
        tabbedPane_.addTab(createTabTitle("Data"), dataTab_);
        tabbedPane_.addTab(createTabTitle("Devices"), deviceTab_);
        tabbedPane_.addTab(createTabTitle("Settings"), settingsTab_);
        tabbedPane_.addTab(createTabTitle("Help"), helpTab_);

        // add ui elements to the panel
        add(tabbedPane_, "growx, growy");
    }

    // use HTML to make the tab labels look nice
    private String createTabTitle(final String title) {
        return "<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b><font size=4>" + title + "</font></b></body></html>";
    }

}
