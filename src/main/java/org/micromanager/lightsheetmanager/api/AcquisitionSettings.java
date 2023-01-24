package org.micromanager.lightsheetmanager.api;

/**
 * Base acquisition settings for all microscopes.
 */
public interface AcquisitionSettings {

    interface Builder {

        /**
         * Sets the save directory.
         *
         * @param directory the directory
         */
        Builder saveDirectory(final String directory);

        /**
         * Sets the folder name.
         *
         * @param name the name of the folder
         */
        Builder saveNamePrefix(final String name);

        /**
         * Sets the acquisition to demo mode.
         *
         * @param state true if in demo mode
         */
        Builder demoMode(final boolean state);

        /**
         * Creates an immutable instance of DefaultAcquisitionSettings
         *
         * @return Immutable version of DefaultAcquisitionSettings
         */
        AcquisitionSettings build();
    }

    /**
     * Creates a Builder populated with settings of this AcquisitionSettings instance.
     *
     * @return AcquisitionSettings.Builder pre-populated with settings of this instance.
     */
    Builder copyBuilder();

    /**
     * Returns the save name prefix.
     *
     * @return the save name prefix.
     */
    String saveNamePrefix();

    /**
     * Returns the save directory.
     *
     * @return the save directory.
     */
    String saveDirectory();

    /**
     * Returns true if using demo mode.
     *
     * @return true if using demo mode
     */
    boolean demoMode();
}
