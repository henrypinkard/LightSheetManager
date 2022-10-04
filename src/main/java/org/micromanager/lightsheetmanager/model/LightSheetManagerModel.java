package org.micromanager.lightsheetmanager.model;

import mmcorej.CMMCore;
import mmcorej.StrVector;
import org.micromanager.LogManager;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.AutofocusSettings;
import org.micromanager.lightsheetmanager.api.LightSheetManager;
import org.micromanager.lightsheetmanager.api.TimingSettings;
import org.micromanager.lightsheetmanager.api.VolumeSettings;
import org.micromanager.lightsheetmanager.model.playlist.AcquisitionTableData;

import java.util.Arrays;
import java.util.Objects;

/**
 * This is the container for all the data needed to operate a microscope with light sheet manager.
 */
public class LightSheetManagerModel implements LightSheetManager {

    private final Studio studio_;
    private final CMMCore core_;
    private final LogManager logs_;

    private UserSettings settings_;

    private DeviceManager deviceManager_;

    private AcquisitionEngine acqEngine_;
    private AcquisitionTableData acqTableData_;

    public LightSheetManagerModel(final Studio studio) {
        studio_ = Objects.requireNonNull(studio);
        core_ = studio_.core();
        logs_ = studio_.logs();

        settings_ = new UserSettings(studio_, this);

        acqEngine_ = new AcquisitionEngine(studio_, this);

    }

    // TODO: private and in constructor?
    public void setup() {
        // get info about devices
        deviceManager_ = new DeviceManager(studio_);
        deviceManager_.setup();

        // load settings from JSON
        settings_.load();

        //boolean isUsingPLogic = deviceManager_.isUsingPLogic();
        //System.out.println("isUsingPLogic: " + isUsingPLogic);
    }

    public AcquisitionEngine getAcquisitionEngine() {
        return acqEngine_;
    }

    public AcquisitionEngine acquisitions() {
        return acqEngine_;
    }

    public DeviceManager getDeviceManager() {
        return deviceManager_;
    }

    public DeviceManager devices() {
        return deviceManager_;
    }

    public UserSettings getUserSettings() {
        return settings_;
    }

    public String[] getLoadedDevices() {
        StrVector loadedDevices = new StrVector();
        try {
            loadedDevices = core_.getLoadedDevices();
        } catch (Exception e) {
            studio_.logs().logError(e.getMessage());
        }
        return loadedDevices.toArray();
    }

    public boolean hasLightSheetManagerDeviceAdapter() {
        final String[] devices = getLoadedDevices();
        return Arrays.asList(devices).contains("LightSheetDeviceManager");
    }

    @Override
    public AutofocusSettings.Builder autofocusSettingsBuilder() {
        return null;
    }

    @Override
    public VolumeSettings.Builder volumeSettingsBuilder() {
        return null;
    }

    @Override
    public TimingSettings.Builder timingSettingsBuilder() {
        return null;
    }
}
