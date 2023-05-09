package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.navigation.NavigationPanel;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import java.util.Objects;

public class NavigationTab extends Panel  {

    private NavigationPanel navigationPanel_;

    private LightSheetManagerModel model_;

    public NavigationTab(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        navigationPanel_ = new NavigationPanel(model_);
        add(navigationPanel_, "");
        navigationPanel_.init();
    }

    public void init() {
        navigationPanel_.init();
    }

    public void stopTimer() {
        navigationPanel_.stopTimer();
    }

}
