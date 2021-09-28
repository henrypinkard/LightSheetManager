package api;

import data.AutofocusMode;
import data.AutofocusType;

/**
 * Autofocus settings.
 *
 */
public interface AutofocusSettings {

    /**
     * Sets the number of images to capture in the autofocus routine.
     *
     * @param numImages the number of images
     */
    void setNumImages(final int numImages);

    /**
     * Returns the number of images used in for autofocus routine.
     *
     * @return the number of images
     */
    int getNumImages();

    /**
     * Sets the spacing between images in the autofocus routine.
     *
     * @param stepSize the step size in microns
     */
    void setStepSize(final double stepSize);

    /**
     * Returns the step size between images in microns.
     *
     * @return the step size in microns
     */
    double getStepSize();

    /**
     * Selects whether to fix the piezo or the sheet for an autofocus routine.
     *
     * @param mode the autofocus mode
     */
    void setMode(final AutofocusMode mode);

    /**
     * Returns the autofocus mode being used.
     *
     * @return the autofocus mode
     */
    AutofocusMode getMode();

    /**
     * Sets the type of scoring algorithm to use when running autofocus.
     *
     * @param type the scoring algorithm
     */
    void setType(final AutofocusType type);

    /**
     * Returns the type of scoring algorithm used for autofocus.
     *
     * @return the type of scoring algorithm
     */
    AutofocusType getType();

    /**
     * Sets the coefficient of determination for the autofocus algorithm.
     *
     * @param value the coefficient of determination
     */
    void setR2(final double value);

    /**
     * Returns the coefficient of determination used in the autofocus routine.
     *
     * @return the coefficient of determination
     */
    double getR2();

    /**
     * Sets the number of images to capture before running an autofocus routine during an acquisition.
     */
    void setTimePointInterval(final int numTimePoints);

    /**
     * Returns the number of images between autofocus routines.
     *
     * @return the number of images between autofocus routines
     */
    int getTimePointInterval();

    /**
     * Run autofocus every time we move to the next channel during an acquisition.
     *
     * @param state true to enable autofocus every stage pass
     */
    void useEveryStagePass(final boolean state);

    /**
     * Returns true if autofocus is run every stage pass.
     *
     * @return true if autofocus is run every stage pass
     */
    boolean isUseEveryStagePassEnabled();

    /**
     * Run an autofocus routine before starting the acquisition.
     *
     * @param state true or false
     */
    void useBeforeAcquisition(final boolean state);

    /**
     * Returns true if we run an autofocus routine before starting an acquisition.
     *
     * @return true if enabled
     */
    boolean isUseBeforeAcquisitionEnabled();

    /**
     * Set the channel to run the autofocus routine on.
     *
     * @param channel the channel to run autofocus on
     */
    void setChannel(final String channel);

    /**
     * Returns the channel autofocus is being run on.
     *
     * @return the autofocus channel
     */
    String getChannel();

    void setMaxOffset(final double maxOffset); // +/- um
    double getMaxOffset();

    void useAutoUpdateOffset(final boolean state);
    boolean isAutoUpdateOffsetEnabled();

    void setAutoUpdateMaxOffset(final double um);
    double getAutoUpdateMaxOffset();

    // TODO: do we need these methods for viewing images/plots?
    void showImages(final boolean state);
    boolean getShowImages();

    void showPlot(final boolean state);
    boolean getShowPlot();

}
