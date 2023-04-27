package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.ScanSettings;

public class DefaultScanSettings implements ScanSettings {

    public static class Builder implements ScanSettings.Builder {

        int scanAccelerationFactor_ = 1;
        int scanOvershootDistance_ = 0;
        double scanRetraceSpeed_ =  67.0;
        double scanAngleFirstView_ = 45.0;
        boolean scanReturnToOriginalPosition_ = false;
        boolean scanFromCurrentPosition_ = false;
        boolean scanFromNegativeDirection_ = false;

        @Override
        public ScanSettings.Builder scanAccelerationFactor() {
            return this;
        }

        @Override
        public ScanSettings.Builder scanOvershootDistance() {
            return this;
        }

        @Override
        public ScanSettings.Builder scanRetraceSpeed() {
            return this;
        }

        @Override
        public ScanSettings.Builder scanAngleFirstView() {
            return this;
        }

        @Override
        public ScanSettings.Builder scanReturnToOriginalPosition(final boolean state) {
            scanReturnToOriginalPosition_ = state;
            return this;
        }

        @Override
        public ScanSettings.Builder scanFromCurrentPosition(final boolean state) {
            scanFromCurrentPosition_ = state;
            return this;
        }

        @Override
        public ScanSettings.Builder scanFromNegativeDirection(final boolean state) {
            scanFromNegativeDirection_ = state;
            return this;
        }

        @Override
        public ScanSettings build() {
            return new DefaultScanSettings(this);
        }
    }

    final int scanAccelerationFactor_;
    final int scanOvershootDistance_;
    final double scanRetraceSpeed_;
    final double scanAngleFirstView_;
    final boolean scanReturnToOriginalPosition_;
    final boolean scanFromCurrentPosition_;
    final boolean scanFromNegativeDirection_;

    private DefaultScanSettings(Builder builder) {
        scanAccelerationFactor_ = builder.scanAccelerationFactor_;
        scanOvershootDistance_ = builder.scanOvershootDistance_;
        scanRetraceSpeed_ = builder.scanRetraceSpeed_;
        scanAngleFirstView_ = builder.scanAngleFirstView_;
        scanReturnToOriginalPosition_ = builder.scanReturnToOriginalPosition_;
        scanFromCurrentPosition_ = builder.scanFromCurrentPosition_;
        scanFromNegativeDirection_ = builder.scanFromNegativeDirection_;
    }

    @Override
    public int scanAccelerationFactor() {
        return scanAccelerationFactor_;
    }

    @Override
    public int scanOvershootDistance() {
        return scanOvershootDistance_;
    }

    @Override
    public double scanRetraceSpeed() {
        return scanRetraceSpeed_;
    }

    @Override
    public double scanAngleFirstView() {
        return scanAngleFirstView_;
    }

    @Override
    public boolean scanReturnToOriginalPosition() {
        return scanReturnToOriginalPosition_;
    }

    @Override
    public boolean scanFromCurrentPosition() {
        return scanFromCurrentPosition_;
    }

    @Override
    public boolean scanFromNegativeDirection() {
        return scanFromNegativeDirection_;
    }
}
