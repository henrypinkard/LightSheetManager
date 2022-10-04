package org.micromanager.lightsheetmanager.gui.playlist;//package org.micromanager.lightsheetmanager.table;

import javax.swing.table.AbstractTableModel;

import org.micromanager.lightsheetmanager.model.playlist.AcquisitionMetadata;
import org.micromanager.lightsheetmanager.model.playlist.AcquisitionTableData;
import org.micromanager.lightsheetmanager.gui.utils.DialogUtils;

import java.util.List;
import java.util.Objects;

/**
 * A custom TableModel to represent the AcquisitionTable.
 */
public class AcquisitionTableModel extends AbstractTableModel {

    /**Column names for the acquisition table. */
    private final String[] columnNames_ = {
            "#",
            "Acquisition Name",
            "Save Name Prefix",
            "Save Directory",
            "Position List Name"
    };

    /**A reference to the acquisition table data. */
    private final AcquisitionTableData tableData_;
    private final List<AcquisitionMetadata> metadata_;

    public AcquisitionTableModel(final AcquisitionTableData tableData) {
        tableData_ = Objects.requireNonNull(tableData);
        metadata_ = tableData_.getMetadataList();
    }

    @Override
    public int getRowCount() {
        return metadata_.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames_.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames_[col];
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    // TODO: remove prints
    @Override
    public Object getValueAt(int row, int col) {
        final AcquisitionMetadata info = metadata_.get(row);
        final String acqName = info.getAcquisitionName();
        switch (col) {
            case 0:
                return row + 1;
            case 1:
                //System.out.println(info.getAcquisitionName());
                return info.getAcquisitionName();
            case 2:
               // System.out.println(tableData_.getAcquisitionSettings(acqName).saveNamePrefix);
                return tableData_.getAcquisitionSettings(acqName).getSaveNamePrefix();
            case 3:
                //System.out.println(tableData_.getAcquisitionSettings(acqName).saveDirectoryRoot);
                return tableData_.getAcquisitionSettings(acqName).getSaveDirectoryRoot();
            case 4:
                //System.out.println(info.getPositionListName());
                return info.getPositionListName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        final AcquisitionMetadata info = metadata_.get(row);
        final String acqName = info.getAcquisitionName();
        switch (col) {
            case 0:
                break;
            case 1:
                info.setAcquisitionName((String)value);
                break;
            case 2:
                final String val = (String)value;
                if (AcquisitionTable.isNameValid(val)) {
                    tableData_.getAcquisitionSettings(acqName).setSaveNamePrefix(val);
                } else {
                    DialogUtils.showErrorMessage(null, "Name Error",
                            "Name can only contain characters, digits, and underscores.");
                }
                break;
            case 3:
                tableData_.getAcquisitionSettings(acqName).setSaveDirectoryRoot((String)value);
                break;
            case 4:
                info.setPositionListName((String)value);
                break;
            default:
                break;
        }
        fireTableCellUpdated(row, col);
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col > 0 && col < 3;
        // return (col > 0 && col < 3) ? true : false;
    }

    /**
     * Reorders the metadata list to match the display in the acquisition table.<P>
     * This method is called from the AcquisitionTableTransferHandler.
     *
     * @param fromIndex the index to move from
     * @param toIndex the index to move to
     */
    public void reorder(int fromIndex, int toIndex) {
        final AcquisitionMetadata item = metadata_.get(fromIndex);
        metadata_.remove(fromIndex);
        if (fromIndex < toIndex) {
            metadata_.add(toIndex-1, item);
        } else {
            metadata_.add(toIndex, item);
        }
        //System.out.println("fromIndex: " + fromIndex);
        //System.out.println("toIndex: " + toIndex + "\n");
    }

}
