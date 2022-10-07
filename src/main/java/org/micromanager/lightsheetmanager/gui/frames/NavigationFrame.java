package org.micromanager.lightsheetmanager.gui.frames;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.gui.navigation.NavigationPanel;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.JFrame;

public class NavigationFrame extends JFrame {

    private NavigationPanel navigationPanel_;

    public NavigationFrame(final Studio studio, final LightSheetManagerModel model) {
        navigationPanel_ = new NavigationPanel(studio, model.getDeviceManager());
    }

}
