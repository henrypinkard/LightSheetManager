package org.micromanager.lightsheetmanager.gui.frames;

import net.miginfocom.swing.MigLayout;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.gui.navigation.NavigationPanel;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.internal.utils.WindowPositioning;

import javax.swing.JFrame;

public class NavigationFrame extends JFrame {

    private NavigationPanel navigationPanel_;

    public NavigationFrame(final Studio studio, final LightSheetManagerModel model) {
        setLayout(new MigLayout("", "", ""));
        WindowPositioning.setUpBoundsMemory(this, this.getClass(), this.getClass().getSimpleName());
        navigationPanel_ = new NavigationPanel(studio, model.getDeviceManager());
        add(navigationPanel_, "");
    }

    public void init() {
        navigationPanel_.init();
    }
}
