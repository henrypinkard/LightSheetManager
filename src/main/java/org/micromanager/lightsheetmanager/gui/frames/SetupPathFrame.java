package org.micromanager.lightsheetmanager.gui.frames;

import net.miginfocom.swing.MigLayout;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.gui.setup.ExcitationPanel;
import org.micromanager.lightsheetmanager.gui.setup.SetupPanel;

import javax.swing.JFrame;
import java.awt.Dimension;
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
        setSize(new Dimension(600, 400));

        final Label lblTitle = new Label(title, Font.BOLD, 16);

        setupPanel_ = new SetupPanel();

        setIconImage(Icons.MICROSCOPE.getImage());

        add(lblTitle, "wrap");
        add(setupPanel_, "wrap");
    }

    public int getPathNum() {
        return pathNum_;
    }

}
