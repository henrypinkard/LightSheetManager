package org.micromanager.lightsheetmanager.model;

public class ScanSettings {

    private boolean scanFromStart_;
    private boolean scanNegative_;

    private float centerXPosition_;
    private float yPosition_;

    private float accelFactor_;

    public ScanSettings() {
        scanFromStart_ = false;
        scanNegative_ = false;
    }

    public boolean scanFromStartPosition() {
        return scanFromStart_;
    }

    public boolean scanNegativeDirection() {
        return scanNegative_;
    }

    public float centerXPosition() {
        return centerXPosition_;
    }

    public float yPosition() {
        return yPosition_;
    }

    public float getAccelFactor() {
        return accelFactor_;
    }

}
