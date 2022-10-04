package org.micromanager.lightsheetmanager.gui;

import com.google.common.eventbus.Subscribe;
import mmcorej.CMMCore;
import net.miginfocom.swing.MigLayout;

import org.micromanager.Studio;
import org.micromanager.events.ExposureChangedEvent;
import org.micromanager.events.LiveModeEvent;

import org.micromanager.lightsheetmanager.LightSheetManagerPlugin;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.gui.navigation.NavigationTab;
import org.micromanager.lightsheetmanager.gui.utils.WindowUtils;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.JFrame;
import java.awt.Font;
import java.util.Objects;

/**
 * Main GUI frame.
 *
 */
public class LightSheetManagerFrame extends JFrame {

    private final Studio studio_;
    private final CMMCore core_;

    private LightSheetManagerModel model_;

    private TabPanel tabPanel_;
    private NavigationTab navigationPanel_;

    public LightSheetManagerFrame(final Studio studio, final LightSheetManagerModel model) {
        studio_ = Objects.requireNonNull(studio);
        model_ = Objects.requireNonNull(model);
        core_ = studio_.core();

        final boolean hasDeviceAdapter = model_.hasLightSheetManagerDeviceAdapter();
        if (!hasDeviceAdapter) {
            createErrorUI();
            return; // early exit => device adapter not found
        }


//        ASIPLogic plc = (ASIPLogic)model_.devices().getDevice("TriggerCamera");
//        System.out.println(plc.getDeviceName());
//        //plc.setPreset();
//        System.out.println("preset: " + plc.getPreset());
//        System.out.println("code: " + plc.getPresetCode());
//        plc.setPreset(0);
//        System.out.println("preset: " + plc.getPreset());
//        System.out.println("code: " + plc.getPresetCode());

        //LightSheetDeviceManager lsdm = (LightSheetDeviceManager) model_.devices().getDevice("LightSheetDeviceManager");
       //lsdm.getPreInitProperties();
            //System.out.println("lsdm: " + lsdm.getPreInitProperties());
//        String[] p = deviceManager_.getLSMProperties();
//        List<String> cameras = deviceManager_.getCameraProperties(p);
//        for (String s : cameras) {
//            System.out.println(s);
//        }



//        final String json = model_.acquisitions().getAcquisitionSettings().toPrettyJson();
//        System.out.println(json);
//        System.out.println("--------------------------");
//        AcquisitionSettings acqSettings = AcquisitionSettings.fromJson(json);
//        System.out.println(acqSettings.toPrettyJson());

//        Stage stage = (Stage)model_.devices().getDevice("SampleZ");
//        System.out.println(stage);
//
//        //Integer stage2 = model_.devices().getDeviceType("SampleZ"); // causes ClassCastException
//        Stage stage2 = model_.devices().getDevice2("SampleZ");
//        System.out.println(stage2);

            // TODO: what if a bug fix increments the minor version #???
            // validate plugin versions
//        final String version = deviceManager_.getVersionNumber();
//        if (!version.equals(LSMPlugin.version)) {
//            studio_.logs().showError(
//                    "Device adapter and plugin version numbers must be the same!" +
//                    "\nLightSheetDeviceManager: " + version +
//                    "\nLightSheetManager: " + LSMPlugin.version
//            );
//        }

//        Map<String, String> m = deviceManager_.getSettingsMap();
//        for (String s : m.keySet())
//            System.out.println(s + " " + m.get(s));

        tabPanel_ = new TabPanel(studio_, model_, model_.getDeviceManager());
        navigationPanel_ = new NavigationTab(studio_, model_.getDeviceManager());

        createUserInterface();

    }

    /**
     * This is the error window that opens when the device adapter is not detected.
     */
    private void createErrorUI() {
        setTitle(LightSheetManagerPlugin.menuName);
        setResizable(false);
        setSize(600, 100);
        setLocation(200, 200);

        // use MigLayout as the layout manager
        setLayout(new MigLayout(
                "insets 10 10 10 10",
                "[]10[]",
                "[]10[]"
        ));
        final String warning = "Please add LightSheetDeviceManager to your hardware configuration to use this plugin.";
        final Label lblTitle = new Label(warning, Font.BOLD, 16);
        add(lblTitle, "");
        //pack(); // fit window size to layout
        setIconImage(Icons.MICROSCOPE.getImage());

        // clean up resources when the frame is closed
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void createUserInterface() {
        setTitle(LightSheetManagerPlugin.menuName);
        setResizable(false);
        setLocation(200, 200);
        //setSize(600, 400);

        // use MigLayout as the layout manager
        setLayout(new MigLayout(
            "insets 10 10 10 10",
            "[]20[]",
            "[]10[]"
        ));

        final Label lblTitle = new Label(LightSheetManagerPlugin.menuName, Font.BOLD, 20);
        lblTitle.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));

        // add ui elements to the panel
        add(lblTitle, "wrap");

        // build the navigation panel
        navigationPanel_.init();

        // GeometryType geometryType = model_.devices().getDeviceAdapter().getMicroscopeGeometry();

        add(tabPanel_, "wrap");

        pack(); // fit window size to layout
        setIconImage(Icons.MICROSCOPE.getImage());

        // clean up resources when the frame is closed
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // register micro-manager events
        studio_.events().registerForEvents(this);

        WindowUtils.registerWindowClosingEvent(this, event -> {
                navigationPanel_.stopTimer();
                model_.getAcquisitionEngine().setSettingsFromBuilders();
                model_.getUserSettings().save();
                System.out.println("main window closed!");
        });

    }

    public Studio getStudio_() {
        return studio_;
    }

    public void toggleLiveMode() {
        if (studio_.live().isLiveModeOn()) {
            studio_.live().setLiveModeOn(false);
            // close the live mode window if it exists
            if (studio_.live().getDisplay() != null) {
                studio_.live().getDisplay().close();
            }
        } else {
            studio_.live().setLiveModeOn(true);
        }
    }

    // Note: doesn't seem to work for all cameras
    @Subscribe
    public void liveModeListener(LiveModeEvent event) {
        if (!studio_.live().isLiveModeOn()) {
        }
        //System.out.println("LIVE MODE!");
    }


    @Subscribe
    public void onExposureChanged(ExposureChangedEvent event) {
        System.out.println("Exposure changed!");
    }

}