package org.micromanager.lightsheetmanager;

/**
 * Volume settings for microscope geometries that support volumetric imaging.
 */
public interface VolumeSettings {

    interface Builder {

        /**
         * Sets the number of sides to use during an acquisition.
         *
         * @param numSides the number of sides
         */
        Builder numSides(final int numSides);

        /**
         * Sets the number of slices per volume.
         *
         * @param numSlices the number of slices
         */
        Builder slicesPerVolume(final int numSlices);

        /**
         * Sets the imaging path to start the acquisition with.
         *
         * @param firstSide the first side
         */
        Builder firstSide(final String firstSide);

        /**
         * Sets the delay between switching imaging paths in milliseconds.
         *
         * @param sideDelayMs the delay in milliseconds
         */
        Builder delayBeforeSide(final double sideDelayMs);

        /**
         * Sets the step size between each slice in microns.
         *
         * @param stepSizeUm the step size in microns
         */
        Builder sliceStepSize(final double stepSizeUm);

        /**
         * Sets the slice period in milliseconds.
         *
         * @param slicePeriodMs the slice period in milliseconds
         */
        Builder slicePeriod(final double slicePeriodMs);

        /**
         * Sets the sample exposure time in milliseconds.
         *
         * @param exposureMs the exposure time in milliseconds
         */
        Builder sampleExposure(final double exposureMs);

        /**
         * Sets the slice period automatically.
         *
         * @param state true to minimize the slice period
         */
        Builder minimizeSlicePeriod(final boolean state);

        /**
         * Creates an immutable instance of VolumeSettings
         *
         * @return Immutable version of VolumeSettings
         */
        VolumeSettings build();
    }

    /**
     * Creates a Builder populated with settings of this VolumeSettings instance.
     *
     * @return VolumeSettings.Builder pre-populated with settings of this instance.
     */
    Builder copyBuilder();

    /**
     * Return the number of sides to use during an acquisition.
     *
     * @return the number of sides
     */
    int numSides();

    /**
     * Returns the number of slices per volume.
     *
     * @return the number of slices
     */
    int slicesPerVolume();

    /**
     * Returns the imaging path that the acquisition starts on.
     *
     * @return the first side
     */
    String firstSide();

    /**
     * Return the delay in milliseconds before switching imaging paths in an acquisitions.
     *
     * @return the delay in milliseconds
     */
    double delayBeforeSide();

    /**
     * Returns the step size in microns.
     *
     * @return the step size in microns
     */
    double sliceStepSize();

    /**
     * Returns the slice period in milliseconds.
     *
     * @return the slice period in milliseconds
     */
    double slicePeriod();

    /**
     * Returns the sample exposure time in milliseconds.
     *
     * @return the exposure time in milliseconds
     */
    double sampleExposure();

    /**
     * Returns true if the slice period is minimized.
     *
     * @return true if slice period is minimized
     */
    boolean isSlicePeriodMinimized();
}
