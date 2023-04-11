package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SliceSettingsLS;

public class DefaultSliceSettingsLS implements SliceSettingsLS {


    public static class Builder implements SliceSettingsLS.Builder {

        private double scanResetTime_ = 3.0;
        private double scanSettleTime_ = 1.0;
        private double shutterWidth_ = 5.0;
        private double shutterSpeedFactor_ = 1.0;

        public Builder() {
        }

        public Builder(final double scanResetTime,
                       final double scanSettleTime,
                       final double shutterWidth,
                       final double shutterSpeedFactor) {
            scanResetTime_ = scanResetTime;
            scanSettleTime_ = scanSettleTime;
            shutterWidth_ = shutterWidth;
            shutterSpeedFactor_ = shutterSpeedFactor;
        }

        public Builder(final DefaultSliceSettingsLS sliceSettings) {
            scanResetTime_ = sliceSettings.scanResetTime();
            scanSettleTime_ = sliceSettings.scanSettleTime();
            shutterWidth_ = sliceSettings.shutterWidth();
            shutterSpeedFactor_ = sliceSettings.shutterSpeedFactor();
        }

        @Override
        public SliceSettingsLS.Builder shutterWidth(final double um) {
            shutterWidth_ = um;
            return this;
        }

        @Override
        public SliceSettingsLS.Builder shutterSpeedFactor(final double factor) {
            shutterSpeedFactor_ = factor;
            return this;
        }

        @Override
        public SliceSettingsLS.Builder scanSettleTime(final double ms) {
            scanSettleTime_ = ms;
            return this;
        }

        @Override
        public SliceSettingsLS.Builder scanResetTime(final double ms) {
            scanResetTime_ = ms;
            return this;
        }

        public double shutterWidth() {
            return shutterWidth_;
        }

        public double shutterSpeedFactor() {
            return shutterSpeedFactor_;
        }

        public double scanSettleTime() {
            return scanSettleTime_;
        }

        public double scanResetTime() {
            return scanResetTime_;
        }

        @Override
        public DefaultSliceSettingsLS build() {
            return new DefaultSliceSettingsLS(
                    scanResetTime_,
                    scanSettleTime_,
                    shutterWidth_,
                    shutterSpeedFactor_
            );
        }
    }

    private final double scanResetTime_;
    private final double scanSettleTime_;
    private final double shutterWidth_;
    private final double shutterSpeedFactor_;

    private DefaultSliceSettingsLS(
            final double scanResetTime,
            final double scanSettleTime,
            final double shutterWidth,
            final double shutterSpeedFactor) {
        scanResetTime_ = scanResetTime;
        scanSettleTime_ = scanSettleTime;
        shutterWidth_ = shutterWidth;
        shutterSpeedFactor_ = shutterSpeedFactor;
    }

    @Override
    public DefaultSliceSettingsLS.Builder copyBuilder() {
        return new Builder(
                scanResetTime_,
                scanSettleTime_,
                shutterWidth_,
                shutterSpeedFactor_
        );
    }

    @Override
    public double scanResetTime() {
        return scanResetTime_;
    }

    @Override
    public double scanSettleTime() {
        return scanSettleTime_;
    }

    @Override
    public double shutterWidth() {
        return shutterWidth_;
    }

    @Override
    public double shutterSpeedFactor() {
        return shutterSpeedFactor_;
    }

    @Override
    public String toString() {
        return String.format(
                "%s[scanResetTime=%s, scanSettleTime=%s, shutterWidth=%s, shutterSpeedFactor=%s]",
                getClass().getSimpleName(),
                scanResetTime_, scanSettleTime_, shutterWidth_, shutterSpeedFactor_
        );
    }
}
