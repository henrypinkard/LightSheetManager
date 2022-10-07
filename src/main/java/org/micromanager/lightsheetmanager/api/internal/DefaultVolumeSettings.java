package org.micromanager.lightsheetmanager.api.internal;


import org.micromanager.lightsheetmanager.api.VolumeSettings;

public class DefaultVolumeSettings implements VolumeSettings {

    public static class Builder implements VolumeSettings.Builder {
        private int firstView_ = 0;
        private int numViews_ = 1;
        private int numSlices_ = 10;
        private double viewDelayMs_ = 50;
        private double stepSizeUm_ = 0.5;
        private double startPosition_ = 0.0;
        private double centerPosition_ = 0.0;
        private double endPosition_ = 0.0;

//        public Builder(final double start, final double end, final int numImages) {
//        }
//
//        public Builder(final double start, final double end, final double sliceStepSize) {
//        }

        public Builder() {
        }

        private Builder(final int firstView,
                        final int numViews,
                        final int numSlices,
                        final double viewDelayMs,
                        final double stepSizeUm,
                        final double startPosition,
                        final double centerPosition,
                        final double endPosition
        ) {
            firstView_ = firstView;
            numViews_ = numViews;
            numSlices_ = numSlices;
            viewDelayMs_ = viewDelayMs;
            stepSizeUm_ = stepSizeUm;
            startPosition_ = startPosition;
            centerPosition_ = centerPosition;
            endPosition_ = endPosition;
        }

        /**
         * Sets the number of views to use during an acquisition.
         *
         * @param numViews the number of view
         */
        @Override
        public VolumeSettings.Builder numViews(final int numViews) {
            numViews_ = numViews;
            return this;
        }

        /**
         * Sets the imaging path to start the acquisition with.
         *
         * @param firstView the first view
         */
        @Override
        public VolumeSettings.Builder firstView(final int firstView) {
            firstView_ = firstView;
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

        @Override
        public VolumeSettings.Builder slicesPerVolume(final int n) {
            numSlices_ = n;
            return this;
        }

        @Override
        public VolumeSettings.Builder sliceStepSize(final double um) {
            stepSizeUm_ = um;
            return this;
        }

        // TODO: what happens when stepSize is not evenly divided by range? maybe just remove?
        /**
         * Sets the volume bounds, automatically computing numSlices and centerPosition.
         *
         * @param startPosition the start position
         * @param endPosition the end position
         * @param stepSizeUm the step size in micron
         */
        @Override
        public VolumeSettings.Builder volumeBounds(final double startPosition, final double endPosition, final double stepSizeUm) {
            startPosition_ = startPosition;
            endPosition_ = endPosition;
            stepSizeUm_ = stepSizeUm;
            centerPosition_ = (startPosition + endPosition) / 2.0;
            numSlices_ = (int)Math.floor((Math.abs(startPosition) + Math.abs(endPosition)) / stepSizeUm);
            return this;
        }

        /**
         * Sets the volume bounds, automatically computing stepSizeUm and centerPosition.
         *
         * @param startPosition the start position
         * @param endPosition the end position
         * @param numSlices the number of slices
         */
        @Override
        public VolumeSettings.Builder volumeBounds(final double startPosition, final double endPosition, final int numSlices) {
            startPosition_ = startPosition;
            endPosition_ = endPosition;
            numSlices_ = numSlices;
            centerPosition_ = (startPosition + endPosition) / 2.0;
            stepSizeUm_ = (Math.abs(startPosition) + Math.abs(endPosition)) / numSlices;
            return this;
        }

        /**
         * Sets the volume bounds, automatically computing startPosition and endPosition.
         *
         * @param centerPosition the center position
         * @param numSlices the number of slices
         * @param stepSizeUm the step size in microns
         */
        @Override
        public VolumeSettings.Builder volumeBounds(final double centerPosition, final int numSlices, final double stepSizeUm) {
            final double halfDistance = (stepSizeUm * numSlices) / 2.0;
            centerPosition_ = centerPosition;
            stepSizeUm_ = stepSizeUm;
            numSlices_ = numSlices;
            startPosition_ = centerPosition - halfDistance;
            endPosition_ = centerPosition + halfDistance;
            return this;
        }

        /**
         * Creates an immutable instance of VolumeSettings
         *
         * @return Immutable version of VolumeSettings
         */
        @Override
        public DefaultVolumeSettings build() {
            return new DefaultVolumeSettings(
                    firstView_,
                    numViews_,
                    numSlices_,
                    viewDelayMs_,
                    stepSizeUm_,
                    startPosition_,
                    centerPosition_,
                    endPosition_
            );
        }
    }

    private final int firstView_;
    private final int numViews_;
    private final int numSlices_;
    private final double viewDelayMs_;
    private final double stepSizeUm_;
    private final double startPosition_;
    private final double centerPosition_;
    private final double endPosition_;

    private DefaultVolumeSettings(final int firstView,
                                  final int numViews,
                                  final int numSlices,
                                  final double viewDelayMs,
                                  final double stepSizeUm,
                                  final double startPosition,
                                  final double centerPosition,
                                  final double endPosition
                                 ) {
        firstView_ = firstView;
        numViews_ = numViews;
        numSlices_ = numSlices;
        viewDelayMs_ = viewDelayMs;
        stepSizeUm_ = stepSizeUm;
        startPosition_ = startPosition;
        centerPosition_ = centerPosition;
        endPosition_ = endPosition;
    }

    public Builder copyBuilder() {
        return new Builder(
                firstView_,
                numViews_,
                numSlices_,
                viewDelayMs_,
                stepSizeUm_,
                startPosition_,
                centerPosition_,
                endPosition_
        );
    }

    /**
     * Returns the imaging path that the acquisition starts on.
     *
     * @return the first view
     */
    public int firstView() {
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

    /**
     * Returns the start position of the volume.
     *
     * @return the start position
     */
    public double startPosition() {
        return startPosition_;
    }

    /**
     * Returns the center position of the volume.
     *
     * @return the center position
     */
    public double centerPosition() {
        return centerPosition_;
    }

    /**
     * Returns the end position of the volume.
     *
     * @return the end position
     */
    public double endPosition() {
        return endPosition_;
    }

    @Override
    public String toString() {
        return String.format(
                "%s[firstView=%s, numViews=%s, numSlices=%s, viewDelayMs=%s, stepSizeUm=%s, "
                        + "startPosition=%s, centerPosition=%s, endPosition=%s]",
                getClass().getSimpleName(),
                firstView_, numViews_, numSlices_, viewDelayMs_, stepSizeUm_,
                startPosition_, centerPosition_, endPosition_
        );
    }

}
