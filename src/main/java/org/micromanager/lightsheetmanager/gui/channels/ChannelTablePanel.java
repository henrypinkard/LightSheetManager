package org.micromanager.lightsheetmanager.gui.channels;

import mmcorej.StrVector;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.model.data.MultiChannelModes;

import javax.swing.JLabel;
import java.util.ArrayList;
import java.util.Objects;

public class ChannelTablePanel extends Panel {

    private JLabel lblChannelGroup_;
    private JLabel lblChangeChannel_;

    private Button btnAddChannel_;
    private Button btnRemoveChannel_;

    private ComboBox cmbChannelGroup_;
    private ComboBox cmbChannelMode_;

    private ChannelTable table_;

    private LightSheetManagerModel model_;

    public ChannelTablePanel(final LightSheetManagerModel model, final CheckBox checkBox) {
        super(checkBox);
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        lblChannelGroup_ = new JLabel("Channel group:");
        lblChangeChannel_ = new JLabel("Change channel:");

        table_ = new ChannelTable();

        Button.setDefaultSize(26, 26);
        btnAddChannel_ = new Button("Add");
        btnRemoveChannel_ = new Button("Remove");

        final String[] groupLabels = getAvailableGroups();
        cmbChannelGroup_ = new ComboBox(groupLabels, groupLabels[0]);


        cmbChannelMode_ = new ComboBox(MultiChannelModes.toArray(),
                model_.acquisitions().getAcquisitionSettings().getChannelMode().toString());

        add(lblChannelGroup_, "");
        add(cmbChannelGroup_, "wrap");
        add(table_, "span 2");
        add(btnAddChannel_, "");
        add(btnRemoveChannel_, "wrap");
        add(lblChangeChannel_, "");
        add(cmbChannelMode_, "");
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
        cmbChannelMode_.setEnabled(state);
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
            System.out.println("add channel");
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

        cmbChannelMode_.registerListener(e -> {
            final int index = cmbChannelMode_.getSelectedIndex();
            model_.acquisitions().getAcquisitionSettings().setChannelMode(MultiChannelModes.getByIndex(index));
            System.out.println("getChannelMode: " + model_.acquisitions().getAcquisitionSettings().getChannelMode());
        });

        cmbChannelGroup_.registerListener(e -> {
            model_.acquisitions().getAcquisitionSettings().setChannelGroup(cmbChannelGroup_.getSelected());
            System.out.println("getChannelGroup: " + model_.acquisitions().getAcquisitionSettings().getChannelGroup());
        });
    }

    // TODO: probably should be in the model not gui
    private String[] getAvailableGroups() {
        StrVector groups;
        try {
            groups = model_.studio().core().getAllowedPropertyValues("Core", "ChannelGroup");
        } catch (Exception e) {
            model_.studio().logs().logError(e);
            return new String[0];
        }
        ArrayList<String> strGroups = new ArrayList<>();
        strGroups.add("None");
        for (String group : groups) {
            if (model_.studio().core().getAvailableConfigs(group).size() > 1) {
                strGroups.add(group);
            }
        }
        return strGroups.toArray(new String[0]);
    }
}
