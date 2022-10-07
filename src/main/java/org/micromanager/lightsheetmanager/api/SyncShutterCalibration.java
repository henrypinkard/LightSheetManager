package org.micromanager.lightsheetmanager.api;

// for light sheet mode on camera
public interface SyncShutterCalibration {

    void setScanSpeed(final double speed);
    double getScanSpeed();

    void setScanOffset(final double offset);
    double getSheetOffset();
}
