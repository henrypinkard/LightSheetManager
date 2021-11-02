package org.micromanager.lightsheetmanager.internal;

import org.micromanager.lightsheetmanager.VolumeSettings;

public class DefaultVolumeSettings implements VolumeSettings {

    public static class Builder implements VolumeSettings.Builder {
        private String firstSide_ = "A";
        private int numSides_ = 1;
        private int numSlices_ = 10;
        private double sideDelayMs_ = 50;
        private double stepSizeUm_ = 1.0;
        private double slicePeriodMs_ = 30;
        private double sampleExposureMs_ = 10;
        private boolean minimizeSlicePeriod_ = false;

        public Builder() {
        }

        private Builder(String firstSide,
                        int numSides,
                        int numSlices,
                        double sideDelayMs,
                        double stepSizeUm,
                        double slicePeriodMs,
                        double sampleExposureMs,
                        boolean minimizeSlicePeriod) {
            firstSide_ = firstSide;
            numSides_ = numSides;
            numSlices_ = numSlices;
            sideDelayMs_ = sideDelayMs;
            stepSizeUm_ = stepSizeUm;
            slicePeriodMs_ = slicePeriodMs;
            sampleExposureMs_ = sampleExposureMs;
            minimizeSlicePeriod_ = minimizeSlicePeriod;
        }

        /**
         * Sets the imaging path to start the acquisition with.
         *
         * @param firstSide the first side
         */
        @Override
        public VolumeSettings.Builder firstSide(final String firstSide) {
            firstSide_ = firstSide;
            return this;
        }

        /**
         * Sets the number of sides to use during an acquisition.
         *
         * @param numSides the number of sides
         */
        @Override
        public VolumeSettings.Builder numSides(final int numSides) {
            numSides_ = numSides;
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
         * @param sideDelayMs the delay in milliseconds
         */
        @Override
        public VolumeSettings.Builder delayBeforeSide(final double sideDelayMs) {
            sideDelayMs_ = sideDelayMs;
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
         * Sets the slice period in milliseconds.
         *
         * @param slicePeriodMs the slice period in milliseconds
         */
        @Override
        public VolumeSettings.Builder slicePeriod(final double slicePeriodMs) {
            slicePeriodMs_ = slicePeriodMs;
            return this;
        }

        /**
         * Sets the sample exposure time in milliseconds.
         *
         * @param exposureMs the exposure time in milliseconds
         */
        @Override
        public VolumeSettings.Builder sampleExposure(final double exposureMs) {
            sampleExposureMs_ = exposureMs;
            return this;
        }

        /**
         * Sets the slice period automatically.
         *
         * @param state true to minimize the slice period
         */
        @Override
        public VolumeSettings.Builder minimizeSlicePeriod(final boolean state) {
            minimizeSlicePeriod_ = state;
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
                    firstSide_,
                    numSides_,
                    numSlices_,
                    sideDelayMs_,
                    stepSizeUm_,
                    slicePeriodMs_,
                    sampleExposureMs_,
                    minimizeSlicePeriod_
            );
        }
    }

    private final String firstSide_;
    private final int numSides_;
    private final int numSlices_;
    private final double sideDelayMs_;
    private final double stepSizeUm_;
    private final double slicePeriodMs_;
    private final double sampleExposureMs_;
    private final boolean minimizeSlicePeriod_;

    public DefaultVolumeSettings(String firstSide,
                                 int numSides,
                                 int numSlices,
                                 double sideDelayMs,
                                 double stepSizeUm,
                                 double slicePeriodMs,
                                 double sampleExposureMs,
                                 boolean minimizeSlicePeriod
                                 ) {
        firstSide_ = firstSide;
        numSides_ = numSides;
        numSlices_ = numSlices;
        sideDelayMs_ = sideDelayMs;
        stepSizeUm_ = stepSizeUm;
        slicePeriodMs_ = slicePeriodMs;
        sampleExposureMs_ = sampleExposureMs;
        minimizeSlicePeriod_ = minimizeSlicePeriod;
    }

    public Builder copyBuilder() {
        return new Builder(
                firstSide_,
                numSides_,
                numSlices_,
                sideDelayMs_,
                stepSizeUm_,
                slicePeriodMs_,
                sampleExposureMs_,
                minimizeSlicePeriod_
        );
    }

    /**
     * Returns the imaging path that the acquisition starts on.
     *
     * @return the first side
     */
    public String firstSide() {
        return firstSide_;
    }

    /**
     * Return the number of sides to use during an acquisition.
     *
     * @return the number of sides
     */

    public int numSides() {
        return numSides_;
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
    public double delayBeforeSide() {
        return sideDelayMs_;
    }

    /**
     * Returns the step size in microns.
     *
     * @return the step size in microns
     */
    public double sliceStepSize() {
        return stepSizeUm_;
    }

    /**
     * Returns the slice period in milliseconds.
     *
     * @return the slice period in milliseconds
     */
    public double slicePeriod() {
        return slicePeriodMs_;
    }

    /**
     * Returns the sample exposure time in milliseconds.
     *
     * @return the exposure time in milliseconds
     */
    public double sampleExposure() {
        return sampleExposureMs_;
    }

    /**
     * Returns true if the slice period is minimized.
     *
     * @return true if slice period is minimized
     */
    public boolean isSlicePeriodMinimized() {
        return minimizeSlicePeriod_;
    }

}
