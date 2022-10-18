package org.micromanager.lightsheetmanager.gui.setup;

import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;

import javax.swing.JLabel;

// TODO: find a better way to organize vendor specific panels/ui elements
public class JoystickPanel extends Panel {

    private ComboBox cmbJoystick_;
    private ComboBox cmbLeftWheel_;
    private ComboBox cmbRightWheel_;

    public JoystickPanel() {
        super("Joystick");
        init();
    }

    private void init() {
        final JLabel lblJoystick = new JLabel("JoysticK:");
        final JLabel lblLeftWheel = new JLabel("Left Wheel:");
        final JLabel lblRightWheel = new JLabel("Right Wheel:");

        // TODO: enum for combo values
        String[] labels = {"Imaging Piezo", "XYStage", "Imaging Slice"};
        cmbJoystick_ = new ComboBox(labels, "XYStage");
        cmbLeftWheel_ = new ComboBox(labels, "Imaging Piezo");
        cmbRightWheel_ = new ComboBox(labels, "Imaging Slice");

        createEventHandlers();

        add(lblJoystick, "");
        add(cmbJoystick_, "wrap");
        add(lblLeftWheel, "");
        add(cmbLeftWheel_, "wrap");
        add(lblRightWheel, "");
        add(cmbRightWheel_, "");
    }

    private void createEventHandlers() {

        cmbJoystick_.registerListener(e -> {

        });

        cmbLeftWheel_.registerListener(e -> {

        });

        cmbRightWheel_.registerListener(e -> {

        });

    }
}
