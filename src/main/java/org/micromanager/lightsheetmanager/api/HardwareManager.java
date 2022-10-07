package org.micromanager.lightsheetmanager.api;

// piezo => detection objective
public interface HardwareManager {

    /**
     * Moves the piezo and slice to the imaging center position.
     */
    void moveToImagingCenter(); // centerPiezoAndGalvo?

    /**
     * Moves the slice position to 0 degrees.
     */
    void moveSlicePositionToZero(); // zeroSlicePosition()?

    /**
     * Moves the imaging piezo to the 0 position.
     */
    void moveImagingPiezoToZero(); // zeroImagingPosition()?

    /**
     * Moves the illumination piezo to the home position.
     */
    void moveIlluminationPiezoToHome();

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
     * Sets the imaging piezo position in microns.
     *
     * @param position the position of the imaging piezo
     */
    void setImagingPiezoPosition(final double position);

    /**
     * Sets the illumination piezo position in microns.
     *
     * @param position the position of the illumination piezo
     */
    void setIlluminationPiezoPosition(final double position);

    /**
     * Sets the slice position in degrees.
     *
     * @param degrees the slice position
     */
    void setSlicePosition(final double degrees); // TODO: degrees?
}
