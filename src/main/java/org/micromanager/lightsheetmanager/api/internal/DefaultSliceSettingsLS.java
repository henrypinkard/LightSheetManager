package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SliceSettingsLS;

public class DefaultSliceSettingsLS implements SliceSettingsLS {


    public static class Builder implements SliceSettingsLS.Builder {

        double scanResetTime_ = 3.0;
        double scanSettleTime_ = 1.0;
        double shutterWidth_ = 5.0;
        double shutterSpeedFactor_ = 1.0;

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
        public SliceSettingsLS.Builder shutterWidth(double um) {
            shutterWidth_ = um;
            return this;
        }

        @Override
        public SliceSettingsLS.Builder shutterSpeedFactor(double factor) {
            shutterSpeedFactor_ = factor;
            return this;
        }

        @Override
        public SliceSettingsLS.Builder scanSettleTime(double ms) {
            scanSettleTime_ = ms;
            return this;
        }

        @Override
        public SliceSettingsLS.Builder scanResetTime(double ms) {
            scanResetTime_ = ms;
            return this;
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

    final double scanResetTime_;
    final double scanSettleTime_;
    final double shutterWidth_;
    final double shutterSpeedFactor_;

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
    public SliceSettingsLS.Builder copyBuilder() {
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
