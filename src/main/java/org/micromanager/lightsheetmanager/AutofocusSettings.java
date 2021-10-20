package org.micromanager.lightsheetmanager;


/**
 * Autofocus settings.
 *
 */
public interface AutofocusSettings {

   // TODO: meaningful modes
    enum AutofocusMode {
        TYPE1,
        TYPE2
    }

   // TODO: meaningful types
    enum AutofocusType {
        TYPE1,
        TYPE2
    }

    interface Builder {

        /**
         * Sets the number of images to capture in the autofocus routine.
         *
         * @param numImages the number of images
         */
        Builder numImages(final int numImages);

        /**
         * Sets the spacing between images in the autofocus routine.
         *
         * @param stepSize the step size in microns
         */
        Builder stepSize(final double stepSize);

        /**
         * Selects whether to fix the piezo or the sheet for an autofocus routine.
         *
         * @param mode the autofocus mode
         */
        Builder mode(final AutofocusMode mode);

        /**
         * Sets the type of scoring algorithm to use when running autofocus.
         *
         * @param type the scoring algorithm
         */
        Builder type(final AutofocusType type);

        /**
         * Sets the coefficient of determination for the autofocus algorithm.
         *
         * @param value the coefficient of determination
         */
        Builder r2(final double value);

        /**
         * Sets the number of images to capture before running an autofocus routine during an acquisition.
         * TODO: what does this measure?
         */
        Builder timePointInterval(final int timePointInterval);

        /**
         * Run autofocus every time we move to the next channel during an acquisition.
         *
         * @param state true to enable autofocus every stage pass
         */
        Builder useEveryStagePass(final boolean state);

        /**
         * Run an autofocus routine before starting the acquisition.
         *
         * @param state true or false
         */
        Builder useBeforeAcquisition(final boolean state);

        /**
         * Set the channel to run the autofocus routine on.
         *
         * @param channel the channel to run autofocus on
         */
        Builder channel(final String channel);

        Builder maxOffset(final double maxOffset); // +/- um

        Builder autoUpdateOffset(final boolean state);

        Builder autoUpdateMaxOffset(final double um);

        /**
         * Creates an immutable instance of AutofocusSettings
         *
         * @return Immutable version of AutofocusSettings
         */
        AutofocusSettings build();
    }


    /**
     * Returns the number of images used in for autofocus routine.
     *
     * @return the number of images
     */
    int numImages();

    /**
     * Returns the step size between images in microns.
     *
     * @return the step size in microns
     */
    double stepSize();

    /**
     * Returns the autofocus mode being used.
     *
     * @return the autofocus mode
     */
    AutofocusMode mode();

    /**
     * Returns the type of scoring algorithm used for autofocus.
     *
     * @return the type of scoring algorithm
     */
    AutofocusType type();

    /**
     * Returns the coefficient of determination used in the autofocus routine.
     *
     * @return the coefficient of determination
     */
    double r2();

    /**
     * Returns the number of images between autofocus routines.
     *
     * @return the number of images between autofocus routines
     */
    int timePointInterval();

    /**
     * Returns true if autofocus is run every stage pass.
     *
     * @return true if autofocus is run every stage pass
     */
    boolean useEveryStagePass();

    /**
     * Returns true if we run an autofocus routine before starting an acquisition.
     *
     * @return true if enabled
     */
    boolean useBeforeAcquisition();

    /**
     * Returns the channel autofocus is being run on.
     *
     * @return the autofocus channel
     */
    String channel();

   /**
    * What is this?
    *
    * @return
    */
    double maxOffset();

    boolean autoUpdateOffset();

    double autoUpdateMaxOffset();

    // TODO: do we need these methods for viewing images/plots?
    // NS: these should not be part of the AutofocusSettings, but rather be parameters
    // to the entity using the AutofocusSettings to autofocus.
    // void showImages(final boolean state);
    // boolean getShowImages();

    // void showPlot(final boolean state);
    // boolean getShowPlot();

}
