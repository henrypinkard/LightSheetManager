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
import org.micromanager.lightsheetmanager.gui.navigation.NavigationPanel;
import org.micromanager.lightsheetmanager.gui.utils.WindowUtils;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.internal.utils.WindowPositioning;

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
    private NavigationPanel navigationPanel_;

    public LightSheetManagerFrame(final Studio studio, final LightSheetManagerModel model, final boolean isLoaded) {
        studio_ = Objects.requireNonNull(studio);
        model_ = Objects.requireNonNull(model);
        core_ = studio_.core();

        WindowPositioning.setUpBoundsMemory(this, this.getClass(), this.getClass().getSimpleName());

        if (isLoaded) {
            createUserInterface();
        } else {
            createErrorUserInterface();
        }

    }

    /**
     * This is the error window that opens when the device adapter is not detected.
     */
    private void createErrorUserInterface() {
        setTitle(LightSheetManagerPlugin.menuName);
        setResizable(false);

        // use MigLayout as the layout manager
        setLayout(new MigLayout(
                "insets 10 10 10 10",
                "[]10[]",
                "[]10[]"
        ));
        final Label lblTitle = new Label("Light Sheet Manager", Font.BOLD, 16);
        final Label lblError = new Label("Error: " + model_.getErrorText(), Font.BOLD, 14);

        add(lblTitle, "wrap");
        add(lblError, "");

        pack(); // fit window size to layout
        setIconImage(Icons.MICROSCOPE.getImage());

        // clean up resources when the frame is closed
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void createUserInterface() {
        setTitle(LightSheetManagerPlugin.menuName);
        setResizable(false);
        //setLocation(200, 200);
        //setSize(600, 400);

        // use MigLayout as the layout manager
        setLayout(new MigLayout(
            "insets 10 10 10 10",
            "[]20[]",
            "[]10[]"
        ));

        final Label lblTitle = new Label(LightSheetManagerPlugin.menuName, Font.BOLD, 20);
        lblTitle.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));

        tabPanel_ = new TabPanel(studio_, model_, model_.getDeviceManager());
        navigationPanel_ = new NavigationPanel(studio_, model_.getDeviceManager());

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