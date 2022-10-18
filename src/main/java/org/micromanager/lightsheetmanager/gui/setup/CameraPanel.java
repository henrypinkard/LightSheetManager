package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.data.Icons;

/**
 * Select which camera is being used.
 */
public class CameraPanel extends Panel {

    private Button btnImagingPath_;
    private Button btnEpiPath_;
    private Button btnMultiPath_;
    private Button btnInvertedPath_;
    private Button btnLiveMode_;

    public CameraPanel() {
        super("Cameras");
        init();
    }

    private void init() {
        Button.setDefaultSize(75, 26);
        btnImagingPath_ = new Button("Imaging");
        btnMultiPath_ = new Button("Multi");
        btnEpiPath_ = new Button("Epi");
        btnInvertedPath_ = new Button("Inverted");

        Button.setDefaultSize(155, 26);
        btnLiveMode_ = new Button("Live", Icons.CAMERA);

        createEventHandlers();

        add(btnImagingPath_, "");
        add(btnMultiPath_, "wrap");
        add(btnEpiPath_, "");
        add(btnInvertedPath_, "wrap");
        add(btnLiveMode_, "span 2");
    }

    private void createEventHandlers() {
        btnImagingPath_.registerListener(e -> {

        });
    }

}
