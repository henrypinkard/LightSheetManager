package org.micromanager.lightsheetmanager.api;

public interface ScanSettings {

    void scanResetTime();
    void scanSettleTime();
    void shutterWidth(final double um);
    void shutterSpeedSlowdownFactor(final int factor);
}
