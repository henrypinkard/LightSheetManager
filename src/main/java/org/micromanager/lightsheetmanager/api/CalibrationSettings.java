package org.micromanager.lightsheetmanager.api;

// TODO: this is unused, maybe put the methods into hardware manager?

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

    interface Builder {

        /**
         * Sets the current position as the new center position.
         * This is the center of the acquisition volume.
         *
         * @param position the center position
         */
        Builder imagingCenter(final double position);

//        /**
//         * Sets the imaging piezo position in microns.
//         *
//         * @param position the position of the imaging piezo
//         */
//        Builder imagingPiezoPosition(final double position);
//
//        /**
//         * Sets the illumination piezo position in microns.
//         *
//         * @param position the position of the illumination piezo
//         */
//        Builder illuminationPiezoPosition(final double position);

        /**
         * Sets the home position of the illumination piezo.
         *
         * @param position the home position
         */
        Builder illuminationPiezoHome(final double position);

        /**
         * Automatically compute the width of the light sheet.
         *
         * @param state true to automatically set sheet width
         */
        Builder useAutoSheetWidth(final boolean state);

        /**
         * Sets the width of the light sheet.
         *
         * @param width the width of the light sheet
         */
        Builder sheetWidth(final double width);

//        /**
//         * Sets the slice position in degrees.
//         *
//         * @param degrees the slice position
//         */
//        Builder slicePosition(final double degrees); // TODO: degrees?

        /**
         * Sets the slope of the light sheet if auto sheet width is enabled.
         *
         * @param slope the slope in degrees
         */
        Builder sheetSlope(final double slope);

        /**
         * Sets the light sheet starting position offset.
         *
         * @param offset the light sheet starting position
         */
        Builder sheetOffset(final double offset);

        /**
         * Sets the slice offset.
         *
         * @param offset the slice offset
         */
        Builder sliceOffset(final double offset);

        /**
         * Sets the slope of the slice.
         *
         * @param slope the slope of the slice
         */
        Builder sliceSlope(final double slope);

        /**
         * Creates an immutable instance of CalibrationSettings
         *
         * @return Immutable version of CalibrationSettings
         */
        CalibrationSettings build();
    }

    /**
     * Returns the imaging center position.
     *
     * @return the imaging center position
     */
    double imagingCenter();

//    /**
//     * Returns the imaging piezo position in microns.
//     *
//     * @return imaging piezo position in microns
//     */
//    double imagingPiezoPosition();
//
//    /**
//     * Returns the illumination piezo position in microns.
//     *
//     * @return illumination piezo position in microns
//     */
//    double illuminationPiezoPosition();
//
//    /**
//     * Returns the slice position in degrees.
//     *
//     * @return the slice position in degrees
//     */
//    double slicePosition();

    /**
     * Returns true if the light sheet width is being automatically computed.
     *
     * @return true if auto sheet width is enabled
     */
    boolean isAutoSheetWidthEnabled();

    /**
     * Returns the width of the light sheet.
     *
     * @return the width of the light sheet
     */
    double sheetWidth();

    /**
     * Returns the slope of the light sheet in degrees.
     *
     * @return the slope in degrees
     */
    double sheetSlope();

    /**
     * Returns the light sheet starting position offset.
     *
     * @return the light sheet starting position offset
     */
    double sheetOffset();

    /**
     * Returns the slice offset position in microns.
     *
     * @return the slice offset position in microns
     */
    double sliceOffset();

    /**
     * Returns the slope of the slice.
     *
     * @return the slope of the slice
     */
    double sliceSlope();

    ///////////////////////////////////////////
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
