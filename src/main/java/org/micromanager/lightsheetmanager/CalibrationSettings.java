package org.micromanager.lightsheetmanager;

// TODO: determine units for each of the methods (include units in arguments or method name?)

/**
 * LightSheet Calibration settings.
 *
 * For diSPIM there are calibration settings for imaging paths A and B.
 *
 * Slice is controlled by the scanner y-axis.
 * Sheet is controlled by the scanner x-axis.
 *
 * Illumination position is the scanner side piezo.
 * Imaging position is the camera side piezo.
 *
 */
public interface CalibrationSettings {

    /**
     * Sets the current position as the new center position.
     * This is the center of the acquisition volume.
     *
     * @param position the center position
     */
    void setImagingCenter(final double position);

    /**
     * Returns the imaging center position.
     *
     * @return the imaging center position
     */
    double getImagingCenter();

    /**
     * Moves the piezo and slice to the imaging center position.
     */
    void moveToImagingCenter(); // centerPiezoAndGalvo?

    /**
     * Moves the slice position to 0 degrees.
     */
    void moveSlicePositionToZero(); // zeroSlicePosition()?

    /**
     * Set the imaging piezo position in microns.
     *
     * @param position the position of the imaging piezo
     */
    void setImagingPiezoPosition(final double position);

    /**
     * Returns the imaging piezo position in microns.
     *
     * @return imaging piezo position in microns
     */
    double getImagingPiezoPosition();

    /**
     * Moves the imaging piezo to the 0 position.
     */
    void moveImagingPiezoToZero(); // zeroImagingPosition()?

    /**
     * Set the illumination piezo position in microns.
     *
     * @param position the position of the illumination piezo
     */
    void setIlluminationPiezoPosition(final double position);

    /**
     * Returns the illumination piezo position in microns.
     *
     * @return illumination piezo position in microns
     */
    double getIlluminationPiezoPosition();

    /**
     * Set the home position of the illumination piezo.
     *
     * @param position the home position
     */
    void setIlluminationPiezoHome(final double position);

    /**
     * Moves the illumination piezo to the home position.
     */
    void moveIlluminationPiezoToHome();

    /**
     * Set the slice position in degrees.
     *
     * @param degrees the slice position
     */
    void setSlicePosition(final double degrees); // degrees?

    /**
     * Returns the slice position in degrees.
     *
     * @return the slice position in degrees
     */
    double getSlicePosition();

    /**
     * Automatically compute the width of the light sheet.
     *
     * @param state true to automatically set sheet width
     */
    void useAutoSheetWidth(final boolean state);

    /**
     * Returns true if the light sheet width is being automatically computed.
     *
     * @return true if auto sheet width is enabled
     */
    boolean isAutoSheetWidthEnabled();

    /**
     * Set the width of the light sheet.
     *
     * @param width the width of the light sheet
     */
    void setSheetWidth(final double width); // units?

    /**
     * Returns the width of the light sheet.
     *
     * @return the width of the light sheet
     */
    double getSheetWidth();

    /**
     * Sets the slope of the light sheet if using auto sheet width is enabled.
     *
     * @param slope the slope in degrees
     */
    void setSheetSlope(final double slope);

    /**
     * Returns the slope of the light sheet in degrees.
     *
     * @return the slope in degrees
     */
    double getSheetSlope();

    /**
     * Sets the light sheet starting position offset.
     *
     * @param offset the light sheet starting position
     */
    void setSheetOffset(final double offset);

    /**
     * Returns the light sheet starting position offset.
     *
     * @return the light sheet starting position offset
     */
    double getSheetOffset();

    /**
     * Sets the slice offset.
     *
     * @param offset the slice offset
     */
    void setSliceOffset(final double offset);

    /**
     * Returns the slice offset position in microns.
     *
     * @return the slice offset position in microns
     */
    double getSliceOffset();

    /**
     * Sets the slope of the slice.
     *
     * @param slope the slope of the slice
     */
    void setSliceSlope(final double slope);

    /**
     * Returns the slope of the slice.
     *
     * @return the slope of the slice
     */
    double getSliceSlope();

    /**
     * Performs a relative move of the slice and piezo together according to their calibration relationship.
     *
     * @param um the distance to move
     */
    void moveSliceAndPiezoRelative(final double um);

    /**
     * Performs an absolute move of the slice and piezo together according to their calibration relationship.
     *
     * @param um the position to move to
     */
    void moveSliceAndPiezoAbsolute(final double um);

    /**
     * Runs the calibration routine that determines the slice and piezo calibration relationship.
     */
    void calibrateSliceAndPiezo();

    void setCalibrationStart();
    void getCalibrationStart();
    void moveToCalibrationStart();

    void setCalibrationEnd();
    void getCalibrationEnd();
    void moveToCalibrationEnd();

    // Note: taken from the settings panel
    void setScanPeriodMs(final int milliseconds);
    int getScanPeriodMs();

}
