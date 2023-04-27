package org.micromanager.lightsheetmanager.api;

/**
 * This interface implements settings for ASI stages with the scan module.
 */
public interface ScanSettings {

    interface Builder {

        Builder scanAccelerationFactor();

        Builder scanOvershootDistance();

        Builder scanRetraceSpeed();

        Builder scanAngleFirstView();

        Builder scanReturnToOriginalPosition(final boolean state);

        Builder scanFromCurrentPosition(final boolean state);

        Builder scanFromNegativeDirection(final boolean state);

        /**
         * Creates an immutable instance of ScanSettings
         *
         * @return Immutable version of ScanSettings
         */
        ScanSettings build();
    }

    int scanAccelerationFactor();

    int scanOvershootDistance();

    double scanRetraceSpeed();

    double scanAngleFirstView();

    boolean scanReturnToOriginalPosition();

    boolean  scanFromCurrentPosition();

    boolean scanFromNegativeDirection();
}
