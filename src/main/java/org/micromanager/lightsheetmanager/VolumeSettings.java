package org.micromanager.lightsheetmanager;

/**
 * Volume settings for microscope geometries that support volumetric imaging.
 */
public interface VolumeSettings {

    interface Builder {

        /**
         * Sets the number of views to use during an acquisition.
         *
         * @param numViews the number of views
         */
        Builder numViews(final int numViews); // TODO: rename to numViews

        /**
         * Sets the number of slices per volume.
         *
         * @param numSlices the number of slices
         */
        Builder slicesPerVolume(final int numSlices);

        /**
         * Sets the imaging path to start the acquisition with.
         *
         * @param firstView the first view
         */
        Builder firstView(final String firstView); // TODO: rename to firstView

        /**
         * Sets the delay between switching imaging paths in milliseconds.
         *
         * @param viewDelayMs the delay in milliseconds
         */
        Builder delayBeforeView(final double viewDelayMs); // TODO: rename to delayBeforeView

        /**
         * Sets the step size between each slice in microns.
         *
         * @param stepSizeUm the step size in microns
         */
        Builder sliceStepSize(final double stepSizeUm);

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
     * Return the number of views to use during an acquisition.
     *
     * @return the number of views
     */
    int numViews();

    /**
     * Returns the number of slices per volume.
     *
     * @return the number of slices
     */
    int slicesPerVolume();

    /**
     * Returns the imaging path that the acquisition starts on.
     *
     * @return the first view
     */
    String firstView();

    /**
     * Return the delay in milliseconds before switching imaging paths in an acquisitions.
     *
     * @return the delay in milliseconds
     */
    double delayBeforeView();

    /**
     * Returns the step size in microns.
     *
     * @return the step size in microns
     */
    double sliceStepSize();

}
