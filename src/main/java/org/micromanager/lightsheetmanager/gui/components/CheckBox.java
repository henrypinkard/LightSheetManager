package org.micromanager.lightsheetmanager.gui.components;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import java.awt.Font;

public class CheckBox extends JCheckBox {

    public static final int LEFT = SwingConstants.LEFT;
    public static final int RIGHT = SwingConstants.RIGHT;

    public CheckBox(final String text, final boolean defaultState) {
        super(text, defaultState);
        setHorizontalTextPosition(RIGHT);
        setFocusPainted(false);
    }

    public CheckBox(final String text, final boolean defaultState, final int constant) {
        super(text, defaultState);
        setHorizontalTextPosition(constant);
        setFocusPainted(false);
    }

    public CheckBox(final String text, final int fontSize, final boolean defaultState, final int constant) {
        super(text, defaultState);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));
        setHorizontalTextPosition(constant);
        setFocusPainted(false);
    }

    public void registerListener(final Method method) {
        addItemListener(method::run);
    }

}
