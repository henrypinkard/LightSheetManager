package org.micromanager.lightsheetmanager.gui.frames;

import net.miginfocom.swing.MigLayout;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.gui.setup.SetupPanel;

import javax.swing.JFrame;
import java.awt.Font;

public class SetupPathFrame extends JFrame {

    private SetupPanel setupPanel_;

    private int pathNum_;

    public SetupPathFrame(final int pathNum) {
        setLayout(new MigLayout("", "", ""));
        pathNum_ = pathNum;
        createUserInterface();
    }

    private void createUserInterface() {
        final String title = "Setup Path " + pathNum_;

        setTitle(title);

        final Label lblTitle = new Label(title, Font.BOLD, 16);

        setupPanel_ = new SetupPanel();

        setIconImage(Icons.MICROSCOPE.getImage());

        add(lblTitle, "wrap");
        add(setupPanel_, "wrap");

        // call after adding ui elements
        pack();
    }

    public int getPathNum() {
        return pathNum_;
    }

}
