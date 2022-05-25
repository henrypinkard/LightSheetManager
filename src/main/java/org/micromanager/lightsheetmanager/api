package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.api.data.CameraModes;

import java.awt.Rectangle;

/**
 * There should be a concrete implementation for each camera device adapter
 * that implements these methods to make them compatible with the plugin.
 */
public interface LightSheetCamera {

    void setTriggerMode(final CameraModes cameraMode);
    CameraModes getTriggerMode();

    void setBinning();
    int getBinning();

    Rectangle getResolution();
    double getRowReadoutTime();
    float getReadoutTime(final CameraModes cameraMode);
    float getResetTime(final CameraModes cameraMode);
}
