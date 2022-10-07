package org.micromanager.lightsheetmanager.model.devices.cameras;

import mmcorej.CMMCore;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.model.devices.DeviceBase;

import java.awt.Rectangle;

public class CameraBase extends DeviceBase  {

    protected CameraModes mode_;

    public CameraBase(final Studio studio, final String deviceName) {
        super(studio, deviceName);
        mode_ = CameraModes.INTERNAL;
    }

    // TODO: take binning into account
    public Rectangle getROI() {
        Rectangle roi = new Rectangle();
        try {
            roi = core_.getROI();
        } catch (Exception e) {
            studio_.logs().showError("could not get camera roi");
        }
        return roi;
    }

    public void setROI(final Rectangle roi) {
        final boolean isLiveModeOn = studio_.live().isLiveModeOn();
        if (isLiveModeOn) {
            studio_.live().setLiveModeOn(false);
            // close the live mode window if it exists
            if (studio_.live().getDisplay() != null) {
                studio_.live().getDisplay().close();
            }
        }
        try {
            core_.setROI(deviceName_, roi.x, roi.y, roi.width, roi.height);
        } catch (Exception e) {
            studio_.logs().showError("could not set camera roi");
        }
        if (isLiveModeOn) {
            studio_.live().setLiveModeOn(true);
        }
    }

    public void setROI() {
        // TODO: set custom roi from camera tab :: store in model
    }

    public int roiVerticalOffset(Rectangle roi, Rectangle sensor) {
        return (roi.y + roi.height / 2) - (sensor.height / 2);
    }

    public int roiReadoutRowsSplitReadout(Rectangle roi, Rectangle sensor) {
        return Math.min(
                Math.abs(roiVerticalOffset(roi, sensor)) + roi.height / 2,  // if ROI overlaps sensor mid-line
                roi.height);                                                // if ROI does not overlap mid-line
    }
}
