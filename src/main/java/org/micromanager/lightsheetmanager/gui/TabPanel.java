package org.micromanager.lightsheetmanager.gui;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.gui.tabs.NavigationTab;
import org.micromanager.lightsheetmanager.gui.tabs.SetupPathTab;
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

import java.util.ArrayList;
import java.util.Objects;

/**
 * The tabbed pane to select between different settings panels.
 *
 */
public class TabPanel extends Panel {

    private Studio studio_;

    private NavigationTab navigationTab_;
    private AcquisitionTab acquisitionTab_;
    private AutofocusTab autofocusTab_;
    private DataTab dataTab_;
    private DeviceTab deviceTab_;
    private CameraTab cameraTab_;
    private SettingsTab settingsTab_;
    private HelpTab helpTab_;
    private ArrayList<SetupPathTab> setupPathTabs_;

    private final TabbedPane tabbedPane_;

    private LightSheetManagerModel model_;
    private DeviceManager devices_;

    public TabPanel(final Studio studio, final LightSheetManagerModel model, final DeviceManager devices) {
        studio_ = Objects.requireNonNull(studio);
        devices_ = Objects.requireNonNull(devices);
        model_ = Objects.requireNonNull(model);

        setupPathTabs_ = new ArrayList<>(2);
        tabbedPane_ = new TabbedPane(900, 700);

        createUserInterface();
    }

    /**
     * Create the ui.
     */
    private void createUserInterface() {
        // create tabs
        navigationTab_ = new NavigationTab(model_);
        acquisitionTab_ = new AcquisitionTab(model_);
        autofocusTab_ = new AutofocusTab(model_);
        cameraTab_ = new CameraTab(model_,this);
        dataTab_ = new DataTab(model_);
        deviceTab_ = new DeviceTab(model_);
        settingsTab_ = new SettingsTab(model_);
        helpTab_ = new HelpTab();

        // add tabs to the pane
        tabbedPane_.addTab(createTabTitle("Navigation"), navigationTab_);

        final int numImagingPaths = devices_.getDeviceAdapter().getNumImagingPaths();
        for (int i = 0; i < numImagingPaths; i++) {
            // create the frame it opens
            SetupPathTab setupPathTab = new SetupPathTab(model_, i + 1);
            tabbedPane_.add(createTabTitle("Setup Path " + (i + 1)), setupPathTab);
            setupPathTabs_.add(setupPathTab);
        }

        tabbedPane_.addTab(createTabTitle("Acquisition"), acquisitionTab_);
        tabbedPane_.addTab(createTabTitle("Autofocus"), autofocusTab_);
        tabbedPane_.addTab(createTabTitle("Cameras"), cameraTab_);
        tabbedPane_.addTab(createTabTitle("Data"), dataTab_);
        tabbedPane_.addTab(createTabTitle("Devices"), deviceTab_);
        tabbedPane_.addTab(createTabTitle("Settings"), settingsTab_);
        tabbedPane_.addTab(createTabTitle("Help"), helpTab_);

        // set acquisition tab to default
        tabbedPane_.setSelectedIndex(numImagingPaths + 1);

        // add ui elements to the panel
        add(tabbedPane_, "growx, growy");
    }

    // use HTML to make the tab labels look nice
    private String createTabTitle(final String title) {
        return "<html><body leftmargin=10 topmargin=8 marginwidth=10 marginheight=5><b><font size=4>" + title + "</font></b></body></html>";
    }

    /**
     * Swaps the control panel on the setup tabs based on the camera trigger mode.
     *
     * @param cameraMode the selected camera trigger mode
     */
    public void swapSetupPathPanels(final CameraModes cameraMode) {
        for (SetupPathTab setupPathTab : setupPathTabs_) {
            setupPathTab.swapPanels(cameraMode);
        }
    }

    public NavigationTab getNavigationTab() {
        return navigationTab_;
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

}
