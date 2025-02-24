package org.micromanager.lightsheetmanager.model.devices.cameras;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.LightSheetCamera;
import org.micromanager.lightsheetmanager.api.data.CameraModes;

import java.awt.Rectangle;

public class DemoCamera extends CameraBase implements LightSheetCamera {

    public DemoCamera(Studio studio, String deviceName) {
        super(studio, deviceName);
    }

    @Override
    public void setBinning() {

    }

    @Override
    public int getBinning() {
        return 0;
    }

    @Override
    public Rectangle getResolution() {
        return null;
    }

    @Override
    public double getRowReadoutTime() {
        return 0;
    }

    @Override
    public float getResetTime(CameraModes cameraMode) {
        return 0;
    }

    @Override
    public float getReadoutTime(CameraModes cameraMode) {
        return 0;
    }
}
