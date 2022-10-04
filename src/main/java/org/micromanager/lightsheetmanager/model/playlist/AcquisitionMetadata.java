package org.micromanager.lightsheetmanager.model.playlist;

/**
 * Maps unique acquisition names to unique position list names and acquisition settings.
 */
public class AcquisitionMetadata {

    private String acquisitionName_;
    private String positionListName_;

    public static final String NO_POSITION_LIST = "None";

    public AcquisitionMetadata(final String acquisitionName) {
        acquisitionName_ = acquisitionName;
        positionListName_ = NO_POSITION_LIST;
    }

    public void setAcquisitionName(final String name) {
        acquisitionName_ = name;
    }

    public String getAcquisitionName() {
        return acquisitionName_;
    }

    public void setPositionListName(final String name) {
        positionListName_ = name;
    }

    public String getPositionListName() {
        return positionListName_;
    }

    @Override
    public String toString() {
        return String.format(
                "%s[acquisitionName=%s, positionListName=%s]",
                getClass().getSimpleName(), acquisitionName_, positionListName_
        );
    }
}
