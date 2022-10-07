package org.micromanager.lightsheetmanager.gui.channels;

import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;

import javax.swing.JLabel;

public class ChannelTablePanel extends Panel {

    private JLabel lblChannelGroup_;
    private JLabel lblChangeChannel_;

    private Button btnAddChannel_;
    private Button btnRemoveChannel_;

    private ComboBox cmbChannelGroup_;
    private ComboBox cmbChangeChannel_;

    private ChannelTable table_;

    public ChannelTablePanel(final CheckBox checkBox) {
        super(checkBox);
        init();
    }

    private void init() {
        lblChannelGroup_ = new JLabel("Channel group:");
        lblChangeChannel_ = new JLabel("Change channel:");

        table_ = new ChannelTable();

        Button.setDefaultSize(26, 26);
        btnAddChannel_ = new Button("Add");
        btnRemoveChannel_ = new Button("Remove");

        final String[] groupLabels = {"None"};
        cmbChannelGroup_ = new ComboBox(groupLabels, groupLabels[0]);

        final String[] changeLabels = {"Every volume"};
        cmbChangeChannel_ = new ComboBox(changeLabels, changeLabels[0]);

        createEventHandlers();

        add(lblChannelGroup_, "");
        add(cmbChannelGroup_, "wrap");
        add(table_, "span 2");
        add(btnAddChannel_, "");
        add(btnRemoveChannel_, "wrap");
        add(lblChangeChannel_, "");
        add(cmbChangeChannel_, "");
    }

    /**
     * Enable or disable items in the channel table panel.
     *
     * @param state enabled or disabled
     */
    public void setItemsEnabled(final boolean state) {
        lblChannelGroup_.setEnabled(state);
        cmbChannelGroup_.setEnabled(state);
        btnAddChannel_.setEnabled(state);
        btnRemoveChannel_.setEnabled(state);
        lblChangeChannel_.setEnabled(state);
        cmbChangeChannel_.setEnabled(state);
        table_.setEnabled(state);
        table_.getTable().setEnabled(state);
    }

    private void createEventHandlers() {

        btnAddChannel_.registerListener(e -> {
            table_.getData().addEmptyChannel();
//            revalidate(); // updates JScrollBar when adding elements
            table_.refreshData();
//            table_.repaint();
//            repaint();
            System.out.println("add");
            table_.getData().printChannelData();
        });

        btnRemoveChannel_.registerListener(e -> {
            final int row = table_.getTable().getSelectedRow();
            if (row != -1) {
                table_.getData().removeChannel(row);
                System.out.println("remove row index: " + row);
                table_.refreshData();
            }
        });
    }

}
