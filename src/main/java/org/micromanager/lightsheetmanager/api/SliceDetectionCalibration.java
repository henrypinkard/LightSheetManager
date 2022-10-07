package org.micromanager.lightsheetmanager.api;

// detection + illumination calibration => rename
public interface SliceDetectionCalibration {

    void setSliceUnitsPerMicron(final double units);
    double getSliceUnitsPerMicron();

    void setSliceOffset(final double offset);
    double getSliceOffset();

    // not the center of the acquisition ... resting position
    void setNominalDetectionPosition(final double position); // um
    double getNominalDetectionPosition();

}
