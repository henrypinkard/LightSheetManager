package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SliceSettings;

public class DefaultSliceSettings implements SliceSettings {

    public static class Builder implements SliceSettings.Builder {

        double slicePeriod_ = 10.0;
        double sampleExposure_ = 1.0;
        boolean minimizeSlicePeriod_ = false;

        public Builder() {
        }

        public Builder(final double slicePeriod,
                       final double sampleExposure,
                       final boolean minimizeSlicePeriod) {
            slicePeriod_ = slicePeriod;
            sampleExposure_ = sampleExposure;
            minimizeSlicePeriod_ = minimizeSlicePeriod;
        }

        @Override
        public SliceSettings.Builder slicePeriod(double slicePeriodMs) {
            return null;
        }

        @Override
        public SliceSettings.Builder sampleExposure(double exposureMs) {
            return null;
        }

        @Override
        public SliceSettings.Builder minimizeSlicePeriod(boolean state) {
            return null;
        }

        @Override
        public DefaultSliceSettings build() {
            return new DefaultSliceSettings(
                    slicePeriod_,
                    sampleExposure_,
                    minimizeSlicePeriod_
            );
        }
    }

    final double slicePeriod_;
    final double sampleExposure_;
    final boolean minimizeSlicePeriod_;

    private DefaultSliceSettings(
            final double slicePeriod,
            final double sampleExposure,
            final boolean minimizeSlicePeriod) {
        slicePeriod_ = slicePeriod;
        sampleExposure_ = sampleExposure;
        minimizeSlicePeriod_ = minimizeSlicePeriod;
    }

    @Override
    public SliceSettings.Builder copyBuilder() {
        return new Builder(
                slicePeriod_,
                sampleExposure_,
                minimizeSlicePeriod_
        );
    }

    @Override
    public double slicePeriod() {
        return slicePeriod_;
    }

    @Override
    public double sampleExposure() {
        return sampleExposure_;
    }

    @Override
    public boolean isSlicePeriodMinimized() {
        return minimizeSlicePeriod_;
    }

    @Override
    public String toString() {
        return String.format(
                "%s[slicePeriod_=%s, sampleExposure_=%s, minimizeSlicePeriod_=%s]",
                getClass().getSimpleName(),
                slicePeriod_, sampleExposure_, minimizeSlicePeriod_
        );
    }
}
