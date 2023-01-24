package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.AcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.model.data.AcquisitionModes;
import org.micromanager.lightsheetmanager.model.data.MultiChannelModes;

public class DefaultAcquisitionSettingsDISPIM extends DefaultAcquisitionSettings implements AcquisitionSettingsDISPIM {

    public static class Builder extends DefaultAcquisitionSettings.Builder implements AcquisitionSettingsDISPIM.Builder {
        private DefaultTimingSettings.Builder tsb_ = new DefaultTimingSettings.Builder();
        private DefaultVolumeSettings.Builder vsb_ = new DefaultVolumeSettings.Builder();
        private DefaultSliceSettings.Builder ssb_ = new DefaultSliceSettings.Builder();
        private DefaultSliceSettingsLS.Builder ssbLS_ = new DefaultSliceSettingsLS.Builder();

        private AcquisitionModes acquisitionMode_ = AcquisitionModes.NONE;
        private MultiChannelModes channelMode_ = MultiChannelModes.NONE;
        private CameraModes cameraMode_ = CameraModes.INTERNAL;

        private boolean useChannels_ = false;
        private boolean useTimePoints_ = false;
        private boolean useAutofocus_ = false;
        private boolean useMultiplePositions_ = false;
        private boolean useHardwareTimePoints_ = false;
        private boolean useStageScanning_ = false;
        private boolean useAdvancedTiming_ = false;

        private int numTimePoints_ = 1;
        private int timePointInterval_ = 0;
        private int postMoveDelay_ = 0;

        private int numChannels_ = 0;
        private String channelGroup_ = "";
        private ChannelSpec[] channels_ = new ChannelSpec[]{};;

        public Builder() {
        }

        private Builder(
                final DefaultTimingSettings.Builder tsb,
                final DefaultVolumeSettings.Builder vsb,
                final DefaultSliceSettings.Builder  ssb,
                final DefaultSliceSettingsLS.Builder ssbLS,
                final AcquisitionModes acquisitionMode,
                final MultiChannelModes channelMode,
                final CameraModes cameraMode,
                final boolean useChannels,
                final boolean useTimePoints,
                final boolean useAutofocus,
                final boolean useStageScanning,
                final boolean useMultiplePositions,
                final boolean useHardwareTimePoints,
                final boolean useAdvancedTiming,
                final int numTimePoints,
                final int timePointInterval,
                final int postMoveDelayMs,
                final int numChannels,
                final String channelGroup,
                final ChannelSpec[] channels) {
            tsb_ = tsb;
            vsb_ = vsb;
            ssb_ = ssb;
            ssbLS_ = ssbLS;
            acquisitionMode_ = acquisitionMode;
            channelMode_ = channelMode;
            cameraMode_ = cameraMode;
            useChannels_ = useChannels;
            useTimePoints_ = useTimePoints;
            useAutofocus_ = useAutofocus;
            useStageScanning_ = useStageScanning;
            useMultiplePositions_ = useMultiplePositions;
            useHardwareTimePoints_ = useHardwareTimePoints;
            useAdvancedTiming_ = useAdvancedTiming;
            numTimePoints_ = numTimePoints;
            timePointInterval_ = timePointInterval;
            postMoveDelay_ = postMoveDelayMs;
            numChannels_ = numChannels;
            channelGroup_ = channelGroup;
            channels_ = channels;
        }

        /**
         * Sets the acquisition mode.
         *
         * @param acqMode the acquisition mode
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder acquisitionMode(final AcquisitionModes acqMode) {
            acquisitionMode_ = acqMode;
            return this;
        }

        /**
         * Sets the channel mode.
         *
         * @param channelMode the channel mode.
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder channelMode(final MultiChannelModes channelMode) {
            channelMode_ = channelMode;
            return this;
        }

        /**
         * Sets the camera mode.
         *
         * @param cameraMode the camera mode.
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder cameraMode(final CameraModes cameraMode) {
            cameraMode_ = cameraMode;
            return this;
        }

        /**
         * Sets the acquisition to use channels.
         *
         * @param state true to use channels.
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder useChannels(final boolean state) {
            useChannels_ = state;
            return this;
        }

        /**
         * Sets the acquisition to use time points.
         *
         * @param state true to use time points.
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder useTimePoints(final boolean state) {
            useTimePoints_ = state;
            return this;
        }

        /**
         * Sets the acquisition to use autofocus.
         *
         * @param state true to use autofocus.
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder useAutofocus(final boolean state) {
            useAutofocus_ = state;
            return this;
        }

        /**
         * Sets the acquisition to use multiple positions.
         *
         * @param state true to use multiple positions.
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder useMultiplePositions(final boolean state) {
            useMultiplePositions_ = state;
            return this;
        }

        /**
         * Sets the acquisition to use hardware time points.
         *
         * @param state true to use time points.
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder useHardwareTimePoints(final boolean state) {
            useHardwareTimePoints_ = state;
            return this;
        }

        /**
         * Sets the acquisition to use stage scanning.
         *
         * @param state true to use stage scanning.
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder useStageScanning(final boolean state) {
            useStageScanning_ = state;
            return this;
        }

        /**
         * Sets the acquisition to use advanced timing settings.
         *
         * @param state true to use advanced timing settings
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder useAdvancedTiming(final boolean state) {
            useAdvancedTiming_ = state;
            return this;
        }

        /**
         * Sets the number of time points.
         *
         * @param numTimePoints the number of time points
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder numTimePoints(final int numTimePoints) {
            numTimePoints_ = numTimePoints;
            return this;
        }

        /**
         * Sets the time point interval between time points in seconds.
         *
         * @param timePointInterval the time point interval in seconds.
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder timePointInterval(final int timePointInterval) {
            timePointInterval_ = timePointInterval;
            return this;
        }

        /**
         * Sets the delay after a move when using multiple positions.
         *
         * @param postMoveDelay the delay in milliseconds.
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder postMoveDelay(final int postMoveDelay) {
            postMoveDelay_ = postMoveDelay;
            return this;
        }

        /**
         * Sets the number of channels.
         *
         * @param numChannels the number of channels.
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder numChannels(final int numChannels) {
            numChannels_ = numChannels;
            return this;
        }

        /**
         * Sets the channel group.
         *
         * @param channelGroup the channel group.
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder channelGroup(final String channelGroup) {
            channelGroup_ = channelGroup;
            return this;
        }

        /**
         * Sets the channels array.
         *
         * @param channels the channel array
         */
        @Override
        public AcquisitionSettingsDISPIM.Builder channels(final ChannelSpec[] channels) {
            channels_ = channels;
            return this;
        }

        /**
         * Creates an immutable instance of DefaultAcquisitionSettingsDISPIM
         *
         * @return Immutable version of DefaultAcquisitionSettingsDISPIM
         */
        @Override
        public DefaultAcquisitionSettingsDISPIM build() {
            return new DefaultAcquisitionSettingsDISPIM(
                    tsb_.build(),
                    vsb_.build(),
                    ssb_.build(),
                    ssbLS_.build(),
                    acquisitionMode_,
                    channelMode_,
                    cameraMode_,
                    useChannels_,
                    useTimePoints_,
                    useAutofocus_,
                    useStageScanning_,
                    useMultiplePositions_,
                    useHardwareTimePoints_,
                    useAdvancedTiming_,
                    numTimePoints_,
                    timePointInterval_,
                    postMoveDelay_,
                    numChannels_,
                    channelGroup_,
                    channels_
            );
        }
    }

    private DefaultTimingSettings timingSettings_;
    private DefaultVolumeSettings volumeSettings_;
    private DefaultSliceSettingsLS sliceSettingsLS_;
    private DefaultSliceSettings sliceSettings_;

    private AcquisitionModes acquisitionMode_;
    private MultiChannelModes channelMode_;
    private CameraModes cameraMode_;

    private boolean useChannels_;
    private boolean useTimePoints_;
    private boolean useAutofocus_;
    private boolean useMultiplePositions_;
    private boolean useHardwareTimePoints_;
    private boolean useStageScanning_;
    private boolean useAdvancedTiming_;

    private int numTimePoints_;
    private int timePointInterval_;
    private int postMoveDelay_;

    private int numChannels_;
    private String channelGroup_;
    private ChannelSpec[] channels_;

    private DefaultAcquisitionSettingsDISPIM(
            final DefaultTimingSettings timingSettings,
            final DefaultVolumeSettings volumeSettings,
            final DefaultSliceSettings sliceSettings,
            final DefaultSliceSettingsLS sliceSettingsLS,
            final AcquisitionModes acquisitionMode,
            final MultiChannelModes channelMode,
            final CameraModes cameraMode,
            final boolean useChannels,
            final boolean useTimePoints,
            final boolean useAutofocus,
            final boolean useStageScanning,
            final boolean useMultiplePositions,
            final boolean useHardwareTimePoints,
            final boolean useAdvancedTiming,
            final int numTimePoints,
            final int timePointInterval,
            final int postMoveDelayMs,
            final int numChannels,
            final String channelGroup,
            final ChannelSpec[] channels) {
        timingSettings_ = timingSettings;
        volumeSettings_ = volumeSettings;
        sliceSettings_ = sliceSettings;
        sliceSettingsLS_ = sliceSettingsLS;
        acquisitionMode_ = acquisitionMode;
        channelMode_ = channelMode;
        cameraMode_ = cameraMode;
        useChannels_ = useChannels;
        useTimePoints_ = useTimePoints;
        useAutofocus_ = useAutofocus;
        useStageScanning_ = useStageScanning;
        useMultiplePositions_ = useMultiplePositions;
        useHardwareTimePoints_ = useHardwareTimePoints;
        useAdvancedTiming_ = useAdvancedTiming;
        numTimePoints_ = numTimePoints;
        timePointInterval_ = timePointInterval;
        postMoveDelay_ = postMoveDelayMs;
        numChannels_ = numChannels;
        channelGroup_ = channelGroup;
        channels_ = channels;
    }

    /**
     * Creates a Builder populated with settings of this DefaultAcquisitionSettingsDISPIM instance.
     *
     * @return DefaultAcquisitionSettingsDISPIM.Builder pre-populated with settings of this instance.
     */
    @Override
    public DefaultAcquisitionSettingsDISPIM.Builder copyBuilder() {
        return new Builder(
                timingSettings_.copyBuilder(),
                volumeSettings_.copyBuilder(),
                sliceSettings_.copyBuilder(),
                sliceSettingsLS_.copyBuilder(),
                acquisitionMode_,
                channelMode_,
                cameraMode_,
                useChannels_,
                useTimePoints_,
                useAutofocus_,
                useStageScanning_,
                useMultiplePositions_,
                useHardwareTimePoints_,
                useAdvancedTiming_,
                numTimePoints_,
                timePointInterval_,
                postMoveDelay_,
                numChannels_,
                channelGroup_,
                channels_
        );
    }

    /**
     * Returns the immutable DefaultTimingSettings instance.
     *
     * @return immutable DefaultTimingSettings instance.
     */
    @Override
    public DefaultTimingSettings timingSettings() {
        return timingSettings_;
    }

    /**
     * Returns the immutable DefaultVolumeSettings instance.
     *
     * @return immutable DefaultVolumeSettings instance.
     */
    @Override
    public DefaultVolumeSettings volumeSettings() {
        return volumeSettings_;
    }

    /**
     * Returns the immutable DefaultSliceSettings instance.
     *
     * @return immutable DefaultSliceSettings instance.
     */
    @Override
    public DefaultSliceSettings sliceSettings() {
        return sliceSettings_;
    }

    /**
     * Returns the immutable DefaultSliceSettingsLS instance.
     *
     * @return immutable DefaultSliceSettingsLS instance.
     */
    @Override
    public DefaultSliceSettingsLS sliceSettingsLS() {
        return sliceSettingsLS_;
    }

    /**
     * Returns the acquisition mode.
     *
     * @return the acquisition mode.
     */
    @Override
    public AcquisitionModes acquisitionMode() {
        return acquisitionMode_;
    }

    /**
     * Returns the channel mode.
     *
     * @return the channel mode.
     */
    @Override
    public MultiChannelModes channelMode() {
        return channelMode_;
    }

    /**
     * Returns the camera mode.
     *
     * @return the camera mode.
     */
    @Override
    public CameraModes cameraMode() {
        return cameraMode_;
    }

    /**
     * Returns true if using channels.
     *
     * @return true if using channels.
     */
    @Override
    public boolean isUsingChannels() {
        return useChannels_;
    }

    /**
     * Returns true if using time points.
     *
     * @return true if using time points.
     */
    @Override
    public boolean isUsingTimePoints() {
        return useTimePoints_;
    }

    /**
     * Returns true if using autofocus.
     *
     * @return true if using autofocus.
     */
    @Override
    public boolean isUsingAutofocus() {
        return useAutofocus_;
    }

    /**
     * Returns true if using multiple positions.
     *
     * @return true if using multiple positions.
     */
    @Override
    public boolean isUsingMultiplePositions() {
        return useMultiplePositions_;
    }

    /**
     * Returns true if using hardware time points.
     *
     * @return true if using hardware time points.
     */
    @Override
    public boolean isUsingHardwareTimePoints() {
        return useHardwareTimePoints_;
    }

    /**
     * Returns true if using stage scanning.
     *
     * @return true if using stage scanning.
     */
    @Override
    public boolean isUsingStageScanning() {
        return useStageScanning_;
    }

    /**
     * Returns true if using advanced timing settings.
     *
     * @return true if using advanced timing settings.
     */
    @Override
    public boolean isUsingAdvancedTiming() {
        return useAdvancedTiming_;
    }

    /**
     * Returns the number of time points.
     *
     * @return the number of time points.
     */
    @Override
    public int numTimePoints() {
        return numTimePoints_;
    }

    /**
     * Returns the time point interval in seconds.
     *
     * @return the time point interval in seconds.
     */
    @Override
    public int timePointInterval() {
        return timePointInterval_;
    }

    /**
     * Returns the post move delay in milliseconds.
     *
     * @return the post move delay in milliseconds.
     */
    @Override
    public int postMoveDelay() {
        return postMoveDelay_;
    }

    /**
     * Returns the number of channels.
     *
     * @return the number of channels.
     */
    @Override
    public int numChannels() {
        return numChannels_;
    }

    /**
     * Returns the channel group.
     *
     * @return the channel group.
     */
    @Override
    public String channelGroup() {
        return channelGroup_;
    }

    /**
     * Returns the channels as an array.
     *
     * @return the channels as an array.
     */
    @Override
    public ChannelSpec[] channels() {
        return channels_;
    }

}