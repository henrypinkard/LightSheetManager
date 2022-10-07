package org.micromanager.lightsheetmanager.gui.channels;

import org.micromanager.lightsheetmanager.model.channels.ChannelTableData;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class ChannelTable extends JScrollPane {

    private JTable table_;
    private ChannelTableData data_;
    private ChannelTableModel model_;

    public ChannelTable() {
        data_ = new ChannelTableData();
        model_ = new ChannelTableModel(data_);
        table_ = new JTable(model_);
        //table_ = new JTable(new ChannelTableModel(data_));

        // cancel JTable edits when focus is lost to prevent errors
        table_.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // only select a single row at a time
        table_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // disable drag to reorder columns
        table_.getTableHeader().setReorderingAllowed(false);

        // display the JTable in the JScrollPane
        setViewportView(table_);
    }

    /**
     * Load table data from JSON.
     *
     * @param data the channel data
     */
    public void setTableData(final ChannelTableData data) {
        this.data_ = data;
        model_ = new ChannelTableModel(data_);
        table_ = new JTable(model_);
        //initTableSettings();
    }

    public void refreshData() {
        model_.fireTableDataChanged();
        //((AbstractTableModel)table_.getModel()).fireTableDataChanged();
    }

    public ChannelTableData getData() {
        return data_;
    }

    public JTable getTable() {
        return table_;
    }

}
