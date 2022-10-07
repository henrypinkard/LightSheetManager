package org.micromanager.lightsheetmanager.api;

public interface ScanSheetCalibration {

    void setSheetWidth(final double width);
    double getSheetWidth();

    void useAutoSheetWidth(final boolean state);
    boolean isAutoSheetWidthEnabled();

    // was slope
    void setAutoSheetWidthPerPixel(final double widthPerPixel);
    double getAutoSheetWidthPerPixel();

    void setSheetOffset(final double offset);
    double getSheetOffset();

    void setLiveScanPeriodMs(final int milliseconds);
    int getLiveScanPeriodMs();
}
