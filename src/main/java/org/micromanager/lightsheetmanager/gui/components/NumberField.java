package org.micromanager.lightsheetmanager.gui.components;

import javax.swing.JFormattedTextField;
import java.text.NumberFormat;

public class NumberField extends JFormattedTextField {

    private static int DEFAULT_SIZE = 5;

    public NumberField() {
        super(NumberFormat.getNumberInstance());
        setColumns(DEFAULT_SIZE);
    }

    public static void setDefaultSize(final int size) {
        DEFAULT_SIZE = size;
    }
}
