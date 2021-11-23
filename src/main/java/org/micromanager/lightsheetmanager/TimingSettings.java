package org.micromanager.lightsheetmanager;

public interface TimingSettings {

    interface Builder {

        /**
         * Sets the advanced timing mode.
         *
         * @param state true if using advanced timing
         */
        Builder useAdvancedTiming(final boolean state);

        /**
         * Sets the delay time before scanning.
         *
         * @param delayMs the delay time in milliseconds
         */
        Builder delayBeforeScan(final double delayMs);

        /**
         * Sets the number of one way beam scans per slice
         *
         * @param numLineScans the number of scans
         */
        Builder lineScansPerSlice(final int numLineScans);

        /**
         * Sets the duration of a one way scan.
         *
         * @param durationMs the duration in milliseconds
         */
        Builder lineScanDuration(final double durationMs);

        /**
         * Sets the delay time before the laser trigger.
         *
         * @param delayMs the delay in milliseconds
         */
        Builder delayBeforeLaser(final double delayMs);

        /**
         * Sets the duration of the laser trigger.
         *
         * @param durationMs the duration in milliseconds
         */
        Builder laserTriggerDuration(final double durationMs);

        /**
         * Sets the delay before the camera trigger is fired.
         *
         * @param delayMs the delay in milliseconds
         */
        Builder delayBeforeCamera(final double delayMs);

        /**
         * Sets the duration of the camera trigger.
         *
         * @param durationMs the duration in milliseconds
         */
        Builder cameraTriggerDuration(final double durationMs);

        /**
         * Sets the camera exposure time.
         *
         * @param exposureMs the exposure time in milliseconds
         */
        Builder cameraExposure(final double exposureMs);

        /**
         * Sets the scan direction.
         *
         * @param state true to invert the scan direction
         */
        Builder useAlternateScanDirection(final boolean state);

        /**
         * Creates an immutable instance of TimingSettings
         *
         * @return Immutable version of TimingSettings
         */
        TimingSettings build();
    }

    /**
     * Creates a Builder populated with settings of this TimingSettings instance.
     *
     * @return TimingSettings.Builder pre-populated with settings of this instance.
     */
    TimingSettings.Builder copyBuilder();

    /**
     * Returns true if using custom timing settings.
     *
     * @return true if using custom timing settings
     */
    boolean useAdvancedTiming();

    /**
     * Return the delay time in milliseconds before the scan begins.
     *
     * @return the delay time in milliseconds
     */
    double delayBeforeScan();

    /**
     * Returns the number of one way beam scans per slice.
     *
     * @return the number of one way beam scans per slice
     */
    int lineScansPerSlice();

    /**
     * Returns the time in milliseconds of one beam scan sweep.
     *
     * @return the time in milliseconds of one beam scan sweep
     */
    double lineScanDuration();

    /**
     * Returns the delay time in milliseconds before the laser trigger.
     *
     * @return the delay time in milliseconds before the laser trigger
     */
    double delayBeforeLaser();

    /**
     * Returns the laser trigger duration in milliseconds.
     *
     * @return the laser trigger duration in milliseconds
     */
    double laserTriggerDuration();

    /**
     * Returns the delay time in milliseconds before the camera is triggered.
     *
     * @return the delay time in milliseconds before the camera is triggered
     */
    double delayBeforeCamera();

    /**
     * Returns the camera trigger duration in milliseconds.
     *
     * @return the camera trigger duration in milliseconds
     */
    double cameraTriggerDuration();

    /**
     * Returns the duration in milliseconds that the camera shutter is open.
     *
     * @return the duration in milliseconds that the camera shutter is open
     */
    double cameraExposure();

    /**
     * Returns true if the scan direction is inverted.
     *
     * @return true if the scan direction is inverted
     */
    boolean useAlternateScanDirection();
}
