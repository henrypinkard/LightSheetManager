package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.data.Icons;

import java.awt.Font;

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
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {

        setMigLayout(
                "",
                "[]5[]",
                "[]5[]"
        );

        Button.setDefaultSize(80, 26);
        btnImagingPath_ = new Button("Imaging");
        btnMultiPath_ = new Button("Multi");
        btnEpiPath_ = new Button("Epi");
        btnInvertedPath_ = new Button("Inverted");

        Button.setDefaultSize(165, 26);
        btnLiveMode_ = new Button("Live", Icons.CAMERA);

        add(btnImagingPath_, "");
        add(btnMultiPath_, "wrap");
        add(btnEpiPath_, "");
        add(btnInvertedPath_, "wrap");
        add(btnLiveMode_, "span 2");
    }

    private void createEventHandlers() {
        btnImagingPath_.registerListener(e -> {

        });

        btnMultiPath_.registerListener(e -> {

        });

        btnEpiPath_.registerListener(e -> {

        });

        btnInvertedPath_.registerListener(e -> {

        });

        btnLiveMode_.registerListener(e -> {

        });
    }

}
