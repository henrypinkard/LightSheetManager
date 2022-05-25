package org.micromanager.lightsheetmanager.internal;

import org.micromanager.lightsheetmanager.TimingSettings;

public class DefaultTimingSettings implements TimingSettings {

    public static class Builder implements TimingSettings.Builder {
        int scanNum_ = 1;
        double scanDelay_ = 0.0;
        double scanPeriod_ = 10.0;
        double laserDelay_ = 1.0;
        double laserDuration_ = 1.0;
        double cameraDelay_ = 0.0;
        double cameraDuration_ = 1.0;
        double cameraExposure_ = 1.0;
        //double sliceDuration_ = 0.0; // TODO: uses formula? SliceTiming settings
        boolean alternateScanDirection_ = false;
        boolean useAdvancedTiming_ = false;
        // boolean isValid_ = false;

        public Builder() {
        }

        private Builder(final int scanNum,
                        final double scanDelay,
                        final double scanPeriod,
                        final double laserDelay,
                        final double laserDuration,
                        final double cameraDelay,
                        final double cameraDuration,
                        final double cameraExposure,
                        final boolean alternateScanDirection,
                        final boolean useAdvancedTiming) {
            scanNum_ = scanNum;
            scanDelay_ = scanDelay;
            scanPeriod_ = scanPeriod;
            laserDelay_ = laserDelay;
            laserDuration_ = laserDuration;
            cameraDelay_ = cameraDelay;
            cameraDuration_ = cameraDuration;
            cameraExposure_ = cameraExposure;
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
            scanDelay_ = delayMs;
            return this;
        }

        /**
         * Sets the number of one way beam scans per slice
         *
         * @param numLineScans the number of scans
         */
        @Override
        public TimingSettings.Builder scansPerSlice(int numLineScans) {
            scanNum_ = numLineScans;
            return this;
        }

        /**
         * Sets the duration of a one way scan.
         *
         * @param durationMs the duration in milliseconds
         */
        @Override
        public TimingSettings.Builder scanDuration(double durationMs) {
            scanPeriod_ = durationMs;
            return this;
        }

        /**
         * Sets the delay time before the laser trigger.
         *
         * @param delayMs the delay in milliseconds
         */
        @Override
        public TimingSettings.Builder delayBeforeLaser(double delayMs) {
            laserDelay_ = delayMs;
            return this;
        }

        /**
         * Sets the duration of the laser trigger.
         *
         * @param durationMs the duration in milliseconds
         */
        @Override
        public TimingSettings.Builder laserTriggerDuration(double durationMs) {
            laserDuration_ = durationMs;
            return this;
        }

        /**
         * Sets the delay before the camera trigger is fired.
         *
         * @param delayMs the delay in milliseconds
         */
        @Override
        public TimingSettings.Builder delayBeforeCamera(double delayMs) {
            cameraDelay_ = delayMs;
            return this;
        }

        /**
         * Sets the duration of the camera trigger.
         *
         * @param durationMs the duration in milliseconds
         */
        @Override
        public TimingSettings.Builder cameraTriggerDuration(double durationMs) {
            cameraDuration_ = durationMs;
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
        public TimingSettings build() {
            return new DefaultTimingSettings(
                    useAdvancedTiming_,
                    scanNum_,
                    scanDelay_,
                    scanPeriod_,
                    laserDelay_,
                    laserDuration_,
                    cameraDelay_,
                    cameraDuration_,
                    cameraExposure_,
                    alternateScanDirection_
            );
        }
    }

    final boolean useAdvancedTiming_;
    final int scanNum_;
    final double scanDelay_;
    final double scanPeriod_;
    final double laserDelay_;
    final double laserDuration_;
    final double cameraDelay_;
    final double cameraDuration_;
    final double cameraExposure_;
    //final double sliceDuration_;
    final boolean alternateScanDirection_;

    private DefaultTimingSettings(final boolean useAdvancedTiming,
                    final int scanNum,
                    final double scanDelay,
                    final double scanPeriod,
                    final double laserDelay,
                    final double laserDuration,
                    final double cameraDelay,
                    final double cameraDuration,
                    final double cameraExposure,
                    final boolean alternateScanDirection) {
        useAdvancedTiming_ = useAdvancedTiming;
        scanNum_ = scanNum;
        scanDelay_ = scanDelay;
        scanPeriod_ = scanPeriod;
        laserDelay_ = laserDelay;
        laserDuration_ = laserDuration;
        cameraDelay_ = cameraDelay;
        cameraDuration_ = cameraDuration;
        cameraExposure_ = cameraExposure;
        alternateScanDirection_ = alternateScanDirection;
    }

    /**
     * Creates a Builder populated with settings of this TimingSettings instance.
     *
     * @return TimingSettings.Builder pre-populated with settings of this instance.
     */
    @Override
    public TimingSettings.Builder copyBuilder() {
        return new Builder(scanNum_,
                scanDelay_,
                scanPeriod_,
                laserDelay_,
                laserDuration_,
                cameraDelay_,
                cameraDuration_,
                cameraExposure_,
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
        return scanDelay_;
    }

    /**
     * Returns the number of one way beam scans per slice.
     *
     * @return the number of one way beam scans per slice
     */
    @Override
    public int scansPerSlice() {
        return scanNum_;
    }

    /**
     * Returns the time in milliseconds of one beam scan sweep.
     *
     * @return the time in milliseconds of one beam scan sweep
     */
    @Override
    public double scanDuration() {
        return scanPeriod_;
    }

    /**
     * Returns the delay time in milliseconds before the laser trigger.
     *
     * @return the delay time in milliseconds before the laser trigger
     */
    @Override
    public double delayBeforeLaser() {
        return laserDelay_;
    }

    /**
     * Returns the laser trigger duration in milliseconds.
     *
     * @return the laser trigger duration in milliseconds
     */
    @Override
    public double laserTriggerDuration() {
        return laserDuration_;
    }

    /**
     * Returns the delay time in milliseconds before the camera is triggered.
     *
     * @return the delay time in milliseconds before the camera is triggered
     */
    @Override
    public double delayBeforeCamera() {
        return cameraDelay_;
    }

    /**
     * Returns the camera trigger duration in milliseconds.
     *
     * @return the camera trigger duration in milliseconds
     */
    @Override
    public double cameraTriggerDuration() {
        return cameraDuration_;
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
     * Returns true if the scan direction is inverted.
     *
     * @return true if the scan direction is inverted
     */
    @Override
    public boolean useAlternateScanDirection() {
        return alternateScanDirection_;
    }
}
