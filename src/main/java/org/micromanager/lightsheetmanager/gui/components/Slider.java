package org.micromanager.lightsheetmanager.gui.components;

import javax.swing.JLabel;
import javax.swing.JSlider;
import java.util.Hashtable;


// TODO: cross reference the JSlider from original diSPIM plugin
public class Slider extends JSlider {

    public Slider(final int min, final int max, final int value) {
        super(min, max, value);
        setMajorTickSpacing(max-min);
        setMinorTickSpacing(1);

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(max, new JLabel(Double.toString(max)));
        labelTable.put(min, new JLabel(Double.toString(min)));

        setLabelTable(labelTable);
        setPaintTicks(true);
        setPaintLabels(true);
    }

}
