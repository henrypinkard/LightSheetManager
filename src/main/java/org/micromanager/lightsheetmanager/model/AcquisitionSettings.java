package org.micromanager.lightsheetmanager.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultVolumeSettings;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.model.data.AcquisitionModes;
import org.micromanager.lightsheetmanager.model.data.MultiChannelModes;

/**
 * Acquisition settings for diSPIM.
 */
public class AcquisitionSettings {

    private DefaultTimingSettings timingSettings_;
    private DefaultVolumeSettings volumeSettings_;
    private DefaultSliceSettings sliceSettings_;

    private ScanSettings scanSettings_;
    private AcquisitionModes spimMode_;
    private MultiChannelModes channelMode_;

    private CameraModes cameraModes_;
    private double cameraExposure_; // TODO: maybe only use the cameraExposure on DefaultTimingSettings

    public ChannelSpec[] channels_;
    private String channelGroup_;

    private String saveNamePrefix_;
    private String saveDirectoryRoot_;

    private boolean useChannels_;
    private boolean useTimepoints_;
    private boolean useAutofocus_;

    private boolean useMultiplePositions_;
    private int postMoveDelayMs_;

    private boolean isStageScanning_;
    private boolean isUsingHardwareTimePoints_;
    private boolean useSeparateTimepoints_;

    private int numChannels_;
    private int numTimePoints_;
    private int timepointIntervalMs_;

    public AcquisitionSettings() {
        saveNamePrefix_ = "";
        saveDirectoryRoot_ = "";
        channelGroup_ = "";
        cameraExposure_ = 10.0f;
        isStageScanning_ = false;
        useSeparateTimepoints_ = false;
        spimMode_ = AcquisitionModes.NONE;
        cameraModes_ = CameraModes.EDGE;
        channelMode_ = MultiChannelModes.VOLUME_HW;
        timingSettings_ = new DefaultTimingSettings.Builder().build();
        volumeSettings_ = new DefaultVolumeSettings.Builder().build();
        sliceSettings_ = new DefaultSliceSettings.Builder().build();
    }

    public void setTimingSettings(final DefaultTimingSettings settings) {
        timingSettings_ = settings;
    }

    public void setVolumeSettings(final DefaultVolumeSettings settings) {
        volumeSettings_ = settings;
    }

    public DefaultTimingSettings getTimingSettings() {
        return timingSettings_;
    }

    public DefaultVolumeSettings getVolumeSettings() {
        return volumeSettings_;
    }

    public DefaultSliceSettings getSliceSettings() {
        return sliceSettings_;
    }

    public ScanSettings getScanSettings() {
        return scanSettings_;
    }

    public int getPostMoveDelay() {
        return postMoveDelayMs_;
    }

    public int getTimepointInterval() {
        return timepointIntervalMs_;
    }

    public String getSaveNamePrefix() {
        return saveNamePrefix_;
    }

    public String getSaveDirectoryRoot() {
        return saveDirectoryRoot_;
    }

    public void setSaveNamePrefix(final String prefix) {
        saveNamePrefix_ = prefix;
    }

    public void setSaveDirectoryRoot(final String directory) {
        saveDirectoryRoot_ = directory;
    }

    public int getNumChannels() {
        return numChannels_;
    }

    public int getNumTimePoints() {
        return numTimePoints_;
    }

    public AcquisitionModes getSPIMMode() {
        return spimMode_;
    }

    public MultiChannelModes getChannelMode() {
        return channelMode_;
    }

    public CameraModes getCameraMode() {
        return cameraModes_;
    }

    public boolean isStageScanning() {
        return isStageScanning_;
    }

    public boolean isUsingMultiplePositions() {
        return useMultiplePositions_;
    }

    public boolean isUsingTimePoints() {
        return useTimepoints_;
    }

    public boolean isUsingSeparateTimepoints() {
        return useSeparateTimepoints_;
    }

    public ChannelSpec[] getChannels() {
        return channels_;
    }

    public String getChannelGroup() {
        return channelGroup_;
    }

    public double getCameraExposure() {
        return cameraExposure_;
    }

    public void setHardwareTimesPoints(final boolean state) {
        isUsingHardwareTimePoints_ = state;
    }

    public boolean isUsingHardwareTimePoints() {
        return isUsingHardwareTimePoints_;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String toPrettyJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public static AcquisitionSettings fromJson(final String json) {
        return new Gson().fromJson(json, AcquisitionSettings.class);
    }
}
