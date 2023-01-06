package org.micromanager.lightsheetmanager.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettingsLS;
import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultVolumeSettings;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.model.data.AcquisitionModes;
import org.micromanager.lightsheetmanager.model.data.MultiChannelModes;

// TODO: merge DefaultSliceSettings and DefaultSliceSettingsLS?
/**
 * Acquisition settings for diSPIM.
 * There are two versions of slice settings, one for virtual slit mode, and one for other camera trigger modes.
 */
public class AcquisitionSettings {

    private DefaultTimingSettings timingSettings_;
    private DefaultVolumeSettings volumeSettings_;
    private DefaultSliceSettingsLS sliceSettingsLS_;
    private DefaultSliceSettings sliceSettings_;

    private ScanSettings scanSettings_;
    private AcquisitionModes acquisitionMode_;
    private MultiChannelModes channelMode_;

    private boolean useAdvancedTiming_; // should we use the DefaultTimingSettings
    private CameraModes cameraModes_;
    private double cameraExposure_; // TODO: maybe only use the cameraExposure on DefaultTimingSettings

    public ChannelSpec[] channels_;
    private String channelGroup_;

    private String saveNamePrefix_;
    private String saveDirectory_;
    private boolean saveWhileAcquiring_;

    private boolean useChannels_;
    private boolean useTimePoints_;
    private boolean useAutofocus_;

    private boolean useMultiplePositions_;
    private int postMoveDelayMs_;

    private boolean isStageScanning_;
    private boolean isUsingHardwareTimePoints_;

    private int numChannels_;
    private int numTimePoints_;
    private int timepointInterval_;

    private boolean demoMode_;

    public AcquisitionSettings() {
        demoMode_ = false;
        saveNamePrefix_ = "";
        saveDirectory_ = "";
        channelGroup_ = "";
        cameraExposure_ = 10.0f;
        isStageScanning_ = false;
        useChannels_ = false;
        acquisitionMode_ = AcquisitionModes.NONE;
        cameraModes_ = CameraModes.EDGE;
        channelMode_ = MultiChannelModes.VOLUME;
        timingSettings_ = new DefaultTimingSettings.Builder().build();
        volumeSettings_ = new DefaultVolumeSettings.Builder().build();
        sliceSettingsLS_ = new DefaultSliceSettingsLS.Builder().build();
        sliceSettings_ = new DefaultSliceSettings.Builder().build();
    }

    public void setTimingSettings(final DefaultTimingSettings settings) {
        timingSettings_ = settings;
    }

    public void setVolumeSettings(final DefaultVolumeSettings settings) {
        volumeSettings_ = settings;
    }

    public void setSliceSettingsLS(final DefaultSliceSettingsLS settings) {
        sliceSettingsLS_ = settings;
    }

    public void setSliceSettings(final DefaultSliceSettings settings) {
        sliceSettings_ = settings;
        System.out.println(sliceSettings_);
    }

    public DefaultTimingSettings getTimingSettings() {
        return timingSettings_;
    }

    public DefaultVolumeSettings getVolumeSettings() {
        return volumeSettings_;
    }

    public DefaultSliceSettingsLS getSliceSettingsLS() {
        return sliceSettingsLS_;
    }

    public DefaultSliceSettings getSliceSettings() {
        return sliceSettings_;
    }

    public ScanSettings getScanSettings() {
        return scanSettings_;
    }

    public boolean getDemoMode() {
        return demoMode_;
    }

    public void setDemoMode(boolean demoMode) {
        demoMode_ = demoMode;
    }

    public void setSaveWhileAcquiring(final boolean state) {
        saveWhileAcquiring_ = state;
    }

    public boolean isSavingWhileAcquiring() {
        return saveWhileAcquiring_;
    }

    public void setUseAdvancedTiming(final boolean state) {
        useAdvancedTiming_ = state;
    }

    public boolean isUsingAdvancedTiming() {
        return useAdvancedTiming_;
    }

    public void setPostMoveDelay(final int milliseconds) {
        postMoveDelayMs_ = milliseconds;
    }

    public int getPostMoveDelay() {
        return postMoveDelayMs_;
    }

    // TODO: should this be milliseconds instead?
    public void setTimePointInterval(final int seconds) {
        timepointInterval_ = seconds;
    }

    public int getTimePointInterval() {
        return timepointInterval_;
    }

    public String getSaveNamePrefix() {
        return saveNamePrefix_;
    }

    public String getSaveDirectory() {
        return saveDirectory_;
    }

    public void setSaveNamePrefix(final String prefix) {
        saveNamePrefix_ = prefix;
    }

    public void setSaveDirectory(final String directory) {
        saveDirectory_ = directory;
    }

    public int getNumChannels() {
        return numChannels_;
    }

    public void setNumTimePoints(final int numTimePoints) {
        numTimePoints_ = numTimePoints;
    }

    public int getNumTimePoints() {
        return numTimePoints_;
    }

    public void setAcquisitionMode(final AcquisitionModes acquisitionMode) {
        acquisitionMode_ = acquisitionMode;
    }

    public AcquisitionModes getAcquisitionMode() {
        return acquisitionMode_;
    }

    public void setChannelMode(final MultiChannelModes channelMode) {
        channelMode_ = channelMode;
    }

    public MultiChannelModes getChannelMode() {
        return channelMode_;
    }

    public void setCameraMode(final CameraModes triggerMode) {
        cameraModes_ = triggerMode;
    }

    public CameraModes getCameraMode() {
        return cameraModes_;
    }

    public boolean isStageScanning() {
        return isStageScanning_;
    }

    public void setUsingMultiplePositions(final boolean state) {
        useMultiplePositions_ = state;
    }

    public boolean isUsingMultiplePositions() {
        return useMultiplePositions_;
    }

    public boolean isUsingTimePoints() {
        return useTimePoints_;
    }

    public void setUsingChannels(final boolean state) {
        useChannels_ = state;
    }

    public boolean isUsingChannels() {
        return useChannels_;
    }

    public void setChannels(final ChannelSpec[] channels) {
        channels_ = channels;
    }

    public ChannelSpec[] getChannels() {
        return channels_;
    }

    public void setChannelGroup(final String channelGroup) {
        channelGroup_= channelGroup;
    }

    public String getChannelGroup() {
        return channelGroup_;
    }

    public double getCameraExposure() {
        return cameraExposure_;
    }

    public void setUsingTimePoints(final boolean state) {
        useTimePoints_ = state;
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
