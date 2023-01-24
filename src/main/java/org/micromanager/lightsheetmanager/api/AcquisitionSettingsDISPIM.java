package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettingsLS;
import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultVolumeSettings;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.model.data.AcquisitionModes;
import org.micromanager.lightsheetmanager.model.data.MultiChannelModes;

/**
 * Acquisition settings for diSPIM microscopes.
 */
public interface AcquisitionSettingsDISPIM extends AcquisitionSettings {

    interface Builder extends AcquisitionSettings.Builder {

        /**
         * Sets the acquisition mode.
         *
         * @param acqMode the acquisition mode
         */
        Builder acquisitionMode(final AcquisitionModes acqMode);

        /**
         * Sets the channel mode.
         *
         * @param channelMode the channel mode.
         */
        Builder channelMode(final MultiChannelModes channelMode);

        /**
         * Sets the camera mode.
         *
         * @param cameraMode the camera mode.
         */
        Builder cameraMode(final CameraModes cameraMode);

        /**
         * Sets the acquisition to use channels.
         *
         * @param state true to use channels.
         */
        Builder useChannels(final boolean state);

        /**
         * Sets the acquisition to use time points.
         *
         * @param state true to use time points.
         */
        Builder useTimePoints(final boolean state);

        /**
         * Sets the acquisition to use autofocus.
         *
         * @param state true to use autofocus.
         */
        Builder useAutofocus(final boolean state);

        /**
         * Sets the acquisition to use multiple positions.
         *
         * @param state true to use multiple positions.
         */
        Builder useMultiplePositions(final boolean state);

        /**
         * Sets the acquisition to use hardware time points.
         *
         * @param state true to use time points.
         */
        Builder useHardwareTimePoints(final boolean state);

        /**
         * Sets the acquisition to use stage scanning.
         *
         * @param state true to use stage scanning.
         */
        Builder useStageScanning(final boolean state);

        /**
         * Sets the acquisition to use advanced timing settings.
         *
         * @param state true to use advanced timing settings
         */
        Builder useAdvancedTiming(final boolean state);

        /**
         * Sets the number of time points.
         *
         * @param numTimePoints the number of time points
         */
        Builder numTimePoints(final int numTimePoints);

        /**
         * Sets the time point interval between time points in seconds.
         *
         * @param timePointInterval the time point interval in seconds.
         */
        Builder timePointInterval(final int timePointInterval);

        /**
         * Sets the delay after a move when using multiple positions.
         *
         * @param postMoveDelay the delay in milliseconds.
         */
        Builder postMoveDelay(final int postMoveDelay);

        /**
         * Sets the number of channels.
         *
         * @param numChannels the number of channels.
         */
        Builder numChannels(final int numChannels);

        /**
         * Sets the channel group.
         *
         * @param channelGroup the channel group.
         */
        Builder channelGroup(final String channelGroup);

        /**
         * Sets the channels array.
         *
         * @param channels the channel array
         */
        Builder channels(final ChannelSpec[] channels);

        /**
         * Creates an immutable instance of AcquisitionSettingsDISPIM
         *
         * @return Immutable version of AcquisitionSettingsDISPIM
         */
        AcquisitionSettingsDISPIM build();
    }

    /**
     * Creates a Builder populated with settings of this AcquisitionSettingsDISPIM instance.
     *
     * @return AcquisitionSettingsDISPIM.Builder pre-populated with settings of this instance.
     */
    Builder copyBuilder();

    /**
     * Returns the immutable DefaultTimingSettings instance.
     *
     * @return immutable DefaultTimingSettings instance.
     */
    DefaultTimingSettings timingSettings();

    /**
     * Returns the immutable DefaultVolumeSettings instance.
     *
     * @return immutable DefaultVolumeSettings instance.
     */
    DefaultVolumeSettings volumeSettings();

    /**
     * Returns the immutable DefaultSliceSettings instance.
     *
     * @return immutable DefaultSliceSettings instance.
     */
    DefaultSliceSettings sliceSettings();

    /**
     * Returns the immutable DefaultSliceSettingsLS instance.
     *
     * @return immutable DefaultSliceSettingsLS instance.
     */
    DefaultSliceSettingsLS sliceSettingsLS();

    /**
     * Returns the acquisition mode.
     *
     * @return the acquisition mode.
     */
    AcquisitionModes acquisitionMode();

    /**
     * Returns the channel mode.
     *
     * @return the channel mode.
     */
    MultiChannelModes channelMode();

    /**
     * Returns the camera mode.
     *
     * @return the camera mode.
     */
    CameraModes cameraMode();

    /**
     * Returns true if using channels.
     *
     * @return true if using channels.
     */
    boolean isUsingChannels();

    /**
     * Returns true if using time points.
     *
     * @return true if using time points.
     */
    boolean isUsingTimePoints();

    /**
     * Returns true if using autofocus.
     *
     * @return true if using autofocus.
     */
    boolean isUsingAutofocus();

    /**
     * Returns true if using multiple positions.
     *
     * @return true if using multiple positions.
     */
    boolean isUsingMultiplePositions();

    /**
     * Returns true if using hardware time points.
     *
     * @return true if using hardware time points.
     */
    boolean isUsingHardwareTimePoints();

    /**
     * Returns true if using stage scanning.
     *
     * @return true if using stage scanning.
     */
    boolean isUsingStageScanning();

    /**
     * Returns true if using advanced timing settings.
     *
     * @return true if using advanced timing settings.
     */
    boolean isUsingAdvancedTiming();

    /**
     * Returns the number of time points.
     *
     * @return the number of time points.
     */
    int numTimePoints();

    /**
     * Returns the time point interval in seconds.
     *
     * @return the time point interval in seconds.
     */
    int timePointInterval();

    /**
     * Returns the post move delay in milliseconds.
     *
     * @return the post move delay in milliseconds.
     */
    int postMoveDelay();

    /**
     * Returns the number of channels.
     *
     * @return the number of channels.
     */
    int numChannels();

    /**
     * Returns the channel group.
     *
     * @return the channel group.
     */
    String channelGroup();

    /**
     * Returns the channels as an array.
     *
     * @return the channels as an array.
     */
    ChannelSpec[] channels();
}
