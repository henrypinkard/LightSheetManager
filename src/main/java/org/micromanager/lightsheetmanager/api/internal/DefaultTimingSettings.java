package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.TimingSettings;

public class DefaultTimingSettings implements TimingSettings {

    public static class Builder implements TimingSettings.Builder {
        int scansPerSlice_ = 1;
        double delayBeforeScan_ = 0.0;
        double scanDuration_ = 10.0;
        double delayBeforeLaser_ = 1.0;
        double laserTriggerDuration_ = 1.0;
        double delayBeforeCamera_ = 0.0;
        double cameraTriggerDuration_ = 1.0;
        double cameraExposure_ = 1.0;
        double sliceDuration_ = 0.0;
        boolean alternateScanDirection_ = false;
        boolean useAdvancedTiming_ = false;

        public Builder() {
        }

        private Builder(final int scansPerSlice,
                        final double delayBeforeScan,
                        final double scanDuration,
                        final double delayBeforeLaser,
                        final double laserTriggerDuration,
                        final double delayBeforeCamera,
                        final double cameraDuration,
                        final double cameraExposure,
                        final double sliceDuration,
                        final boolean alternateScanDirection,
                        final boolean useAdvancedTiming) {
            scansPerSlice_ = scansPerSlice;
            delayBeforeScan_ = delayBeforeScan;
            scanDuration_ = scanDuration;
            delayBeforeLaser_ = delayBeforeLaser;
            laserTriggerDuration_ = laserTriggerDuration;
            delayBeforeCamera_ = delayBeforeCamera;
            cameraTriggerDuration_ = cameraDuration;
            cameraExposure_ = cameraExposure;
            sliceDuration_ = sliceDuration;
            alternateScanDirection_ = alternateScanDirection;
            useAdvancedTiming_ = useAdvancedTiming;
        }

        /**
         * Sets the delay time before scanning.
         *
         * @param delayMs the delay time in milliseconds
         */
        @Override
        public TimingSettings.Builder delayBeforeScan(double delayMs) {
            delayBeforeScan_ = delayMs;
            return this;
        }

        /**
         * Sets the number of one way beam scans per slice
         *
         * @param numScans the number of scans
         */
        @Override
        public TimingSettings.Builder scansPerSlice(int numScans) {
            scansPerSlice_ = numScans;
            return this;
        }

        /**
         * Sets the duration of a one way scan.
         *
         * @param durationMs the duration in milliseconds
         */
        @Override
        public TimingSettings.Builder scanDuration(double durationMs) {
            scanDuration_ = durationMs;
            return this;
        }

        /**
         * Sets the delay time before the laser trigger.
         *
         * @param delayMs the delay in milliseconds
         */
        @Override
        public TimingSettings.Builder delayBeforeLaser(double delayMs) {
            delayBeforeLaser_ = delayMs;
            return this;
        }

        /**
         * Sets the duration of the laser trigger.
         *
         * @param durationMs the duration in milliseconds
         */
        @Override
        public TimingSettings.Builder laserTriggerDuration(double durationMs) {
            laserTriggerDuration_ = durationMs;
            return this;
        }

        /**
         * Sets the delay before the camera trigger is fired.
         *
         * @param delayMs the delay in milliseconds
         */
        @Override
        public TimingSettings.Builder delayBeforeCamera(double delayMs) {
            delayBeforeCamera_ = delayMs;
            return this;
        }

        /**
         * Sets the duration of the camera trigger.
         *
         * @param durationMs the duration in milliseconds
         */
        @Override
        public TimingSettings.Builder cameraTriggerDuration(double durationMs) {
            cameraTriggerDuration_ = durationMs;
            return this;
        }

        /**
         * Sets the camera exposure time.
         *
         * @param exposureMs the exposure time in milliseconds
         */
        @Override
        public TimingSettings.Builder cameraExposure(double exposureMs) {
            cameraExposure_ = exposureMs;
            return this;
        }

        /**
         * Sets the slice duration of each slice.
         *
         * @param durationMs the duration in milliseconds
         */
        @Override
        public TimingSettings.Builder sliceDuration(double durationMs) {
            sliceDuration_ = durationMs;
            return this;
        }

        /**
         * Sets the scan direction.
         *
         * @param state true to invert the scan direction
         */
        @Override
        public TimingSettings.Builder useAlternateScanDirection(boolean state) {
            alternateScanDirection_ = state;
            return this;
        }

        /**
         * Sets the advanced timing mode.
         *
         * @param state true if using advanced timing
         */
        @Override
        public TimingSettings.Builder useAdvancedTiming(boolean state) {
            useAdvancedTiming_ = state;
            return this;
        }

        /**
         * Creates an immutable instance of TimingSettings
         *
         * @return Immutable version of TimingSettings
         */
        @Override
        public DefaultTimingSettings build() {
            return new DefaultTimingSettings(
                    scansPerSlice_,
                    delayBeforeScan_,
                    scanDuration_,
                    delayBeforeLaser_,
                    laserTriggerDuration_,
                    delayBeforeCamera_,
                    cameraTriggerDuration_,
                    cameraExposure_,
                    sliceDuration_,
                    alternateScanDirection_,
                    useAdvancedTiming_
            );
        }
    }

    final int scansPerSlice_;
    final double delayBeforeScan_;
    final double scanDuration_;
    final double delayBeforeLaser_;
    final double laserTriggerDuration_;
    final double delayBeforeCamera_;
    final double cameraTriggerDuration_;
    final double cameraExposure_;
    final double sliceDuration_;
    final boolean alternateScanDirection_;
    final boolean useAdvancedTiming_;

    private DefaultTimingSettings(
                final int scansPerSlice,
                final double delayBeforeScan,
                final double scanDuration,
                final double delayBeforeLaser,
                final double laserTriggerDuration,
                final double delayBeforeCamera,
                final double cameraTriggerDuration,
                final double cameraExposure,
                final double sliceDuration,
                final boolean alternateScanDirection,
                final boolean useAdvancedTiming) {
        scansPerSlice_ = scansPerSlice;
        delayBeforeScan_ = delayBeforeScan;
        scanDuration_ = scanDuration;
        delayBeforeLaser_ = delayBeforeLaser;
        laserTriggerDuration_ = laserTriggerDuration;
        delayBeforeCamera_ = delayBeforeCamera;
        cameraTriggerDuration_ = cameraTriggerDuration;
        cameraExposure_ = cameraExposure;
        sliceDuration_ = sliceDuration;
        alternateScanDirection_ = alternateScanDirection;
        useAdvancedTiming_ = useAdvancedTiming;
    }

    /**
     * Creates a Builder populated with settings of this TimingSettings instance.
     *
     * @return TimingSettings.Builder pre-populated with settings of this instance.
     */
    @Override
    public TimingSettings.Builder copyBuilder() {
        return new Builder(
                scansPerSlice_,
                delayBeforeScan_,
                scanDuration_,
                delayBeforeLaser_,
                laserTriggerDuration_,
                delayBeforeCamera_,
                cameraTriggerDuration_,
                cameraExposure_,
                sliceDuration_,
                alternateScanDirection_,
                useAdvancedTiming_);
    }

    /**
     * Returns true if using custom timing settings.
     *
     * @return true if using custom timing settings
     */
    @Override
    public boolean useAdvancedTiming() {
        return useAdvancedTiming_;
    }

    /**
     * Return the delay time in milliseconds before the scan begins.
     *
     * @return the delay time in milliseconds
     */
    @Override
    public double delayBeforeScan() {
        return delayBeforeScan_;
    }

    /**
     * Returns the number of one way beam scans per slice.
     *
     * @return the number of one way beam scans per slice
     */
    @Override
    public int scansPerSlice() {
        return scansPerSlice_;
    }

    /**
     * Returns the time in milliseconds of one beam scan sweep.
     *
     * @return the time in milliseconds of one beam scan sweep
     */
    @Override
    public double scanDuration() {
        return scanDuration_;
    }

    /**
     * Returns the delay time in milliseconds before the laser trigger.
     *
     * @return the delay time in milliseconds before the laser trigger
     */
    @Override
    public double delayBeforeLaser() {
        return delayBeforeLaser_;
    }

    /**
     * Returns the laser trigger duration in milliseconds.
     *
     * @return the laser trigger duration in milliseconds
     */
    @Override
    public double laserTriggerDuration() {
        return laserTriggerDuration_;
    }

    /**
     * Returns the delay time in milliseconds before the camera is triggered.
     *
     * @return the delay time in milliseconds before the camera is triggered
     */
    @Override
    public double delayBeforeCamera() {
        return delayBeforeCamera_;
    }

    /**
     * Returns the camera trigger duration in milliseconds.
     *
     * @return the camera trigger duration in milliseconds
     */
    @Override
    public double cameraTriggerDuration() {
        return cameraTriggerDuration_;
    }

    /**
     * Returns the duration in milliseconds that the camera shutter is open.
     *
     * @return the duration in milliseconds that the camera shutter is open
     */
    @Override
    public double cameraExposure() {
        return cameraExposure_;
    }

    /**
     * Returns the duration in milliseconds of each slice.
     *
     * @return the duration in milliseconds of each slice
     */
    @Override
    public double sliceDuration() {
        return sliceDuration_;
    }

    /**
     * Returns true if the scan direction is inverted.
     *
     * @return true if the scan direction is inverted
     */
    @Override
    public boolean useAlternateScanDirection() {
        return alternateScanDirection_;
    }

    @Override
    public String toString() {
        return String.format(
                "%s[scansPerSlice=%s, delayBeforeScan=%s, scanDuration=%s,"
                        + " delayBeforeLaser=%s, laserTriggerDuration=%s,"
                        + " delayBeforeCamera=%s, cameraTriggerDuration=%s, cameraExposure=%s,"
                        + " sliceDuration=%s, alternateScanDirection=%s, useAdvancedTiming=%s]",
                getClass().getSimpleName(),
                scansPerSlice_, delayBeforeScan_, scanDuration_, delayBeforeLaser_, laserTriggerDuration_,
                delayBeforeCamera_, cameraTriggerDuration_, cameraExposure_,
                sliceDuration_, alternateScanDirection_, useAdvancedTiming_
        );
    }

//    public double getSliceDuration() {
//        // slice duration is the max out of the scan time, laser time, and camera time
//        return Math.max(Math.max(
//                scanDelay_ + (scanDuration_ * scanNum_),    // scan time
//                laserDelay_ + laserDuration_),              // laser time
//            cameraDelay_ + cameraDuration_                  // camera time
//        );
//    }
}
