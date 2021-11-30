package org.micromanager.lightsheetmanager.internal;

import org.micromanager.lightsheetmanager.VolumeSettings;

public class DefaultVolumeSettings implements VolumeSettings {

    public static class Builder implements VolumeSettings.Builder {
        private String firstView_ = "A";
        private int numViews_ = 1;
        private int numSlices_ = 10;
        private double viewDelayMs_ = 50;
        private double stepSizeUm_ = 1.0;

        public Builder() {
        }

        private Builder(String firstView,
                        int numViews,
                        int numSlices,
                        double viewDelayMs,
                        double stepSizeUm
        ) {
            firstView_ = firstView;
            numViews_ = numViews;
            numSlices_ = numSlices;
            viewDelayMs_ = viewDelayMs;
            stepSizeUm_ = stepSizeUm;
        }

        /**
         * Sets the imaging path to start the acquisition with.
         *
         * @param firstView the first view
         */
        @Override
        public VolumeSettings.Builder firstView(final String firstView) {
            firstView_ = firstView;
            return this;
        }

        /**
         * Sets the number of view to use during an acquisition.
         *
         * @param numViews the number of view
         */
        @Override
        public VolumeSettings.Builder numViews(final int numViews) {
            numViews_ = numViews;
            return this;
        }

        /**
         * Sets the number of slices per volume.
         *
         * @param numSlices the number of slices
         */
        @Override
        public VolumeSettings.Builder slicesPerVolume(final int numSlices) {
            numSlices_ = numSlices;
            return this;
        }

        /**
         * Sets the delay between switching imaging paths in an acquisition.
         *
         * @param viewDelayMs the delay in milliseconds
         */
        @Override
        public VolumeSettings.Builder delayBeforeView(final double viewDelayMs) {
            viewDelayMs_ = viewDelayMs;
            return this;
        }

        /**
         * Sets the step size between each slice in microns.
         *
         * @param stepSizeUm the step size in microns
         */
        @Override
        public VolumeSettings.Builder sliceStepSize(final double stepSizeUm) {
            stepSizeUm_ = stepSizeUm;
            return this;
        }

        /**
         * Creates an immutable instance of VolumeSettings
         *
         * @return Immutable version of VolumeSettings
         */
        @Override
        public VolumeSettings build() {
            return new DefaultVolumeSettings(
                    firstView_,
                    numViews_,
                    numSlices_,
                    viewDelayMs_,
                    stepSizeUm_
            );
        }
    }

    private final String firstView_;
    private final int numViews_;
    private final int numSlices_;
    private final double viewDelayMs_;
    private final double stepSizeUm_;

    private DefaultVolumeSettings(String firstView,
                                 int numViews,
                                 int numSlices,
                                 double viewDelayMs,
                                 double stepSizeUm
                                 ) {
        firstView_ = firstView;
        numViews_ = numViews;
        numSlices_ = numSlices;
        viewDelayMs_ = viewDelayMs;
        stepSizeUm_ = stepSizeUm;
    }

    public Builder copyBuilder() {
        return new Builder(
                firstView_,
                numViews_,
                numSlices_,
                viewDelayMs_,
                stepSizeUm_
        );
    }

    /**
     * Returns the imaging path that the acquisition starts on.
     *
     * @return the first view
     */
    public String firstView() {
        return firstView_;
    }

    /**
     * Return the number of views to use during an acquisition.
     *
     * @return the number of views
     */

    public int numViews() {
        return numViews_;
    }

    /**
     * Returns the number of slices per volume.
     *
     * @return the number of slices
     */
    public int slicesPerVolume() {
        return numSlices_;
    }

    /**
     * Return the delay in milliseconds before switching imaging paths in milliseconds.
     *
     * @return the delay in milliseconds
     */
    public double delayBeforeView() {
        return viewDelayMs_;
    }

    /**
     * Returns the step size in microns.
     *
     * @return the step size in microns
     */
    public double sliceStepSize() {
        return stepSizeUm_;
    }

}
