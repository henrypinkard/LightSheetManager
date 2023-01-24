package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.AcquisitionSettings;

/**
 * Base acquisition settings for all microscopes.
 */
public class DefaultAcquisitionSettings implements AcquisitionSettings {

    public static class Builder implements AcquisitionSettings.Builder {

        private String saveDirectory_;
        private String saveNamePrefix_;
        private boolean demoMode_;

        public Builder() {
        }

        public Builder(final String saveDirectory,
                       final String saveNamePrefix,
                       final boolean demoMode) {
            saveDirectory_ = saveDirectory;
            saveNamePrefix_ = saveNamePrefix;
            demoMode_ = demoMode;
        }

        public Builder(final AcquisitionSettings acqSettings) {
            saveDirectory_ = acqSettings.saveDirectory();
            saveNamePrefix_ = acqSettings.saveNamePrefix();
            demoMode_ = acqSettings.demoMode();
        }

        /**
         * Sets the save directory.
         *
         * @param directory the directory
         */
        @Override
        public AcquisitionSettings.Builder saveDirectory(final String directory) {
            saveDirectory_ = directory;
            return this;
        }

        /**
         * Sets the folder name.
         *
         * @param name the name of the folder
         */
        @Override
        public AcquisitionSettings.Builder saveNamePrefix(final String name) {
            saveNamePrefix_ = name;
            return this;
        }

        /**
         * Sets the acquisition to demo mode.
         *
         * @param state true if in demo mode
         */
        @Override
        public AcquisitionSettings.Builder demoMode(final boolean state) {
            demoMode_ = state;
            return this;
        }

        /**
         * Creates an immutable instance of DefaultAcquisitionSettings
         *
         * @return Immutable version of DefaultAcquisitionSettings
         */
        @Override
        public AcquisitionSettings build() {
            return new DefaultAcquisitionSettings();
        }
    }

    /**
     * Creates a Builder populated with settings of this AcquisitionSettings instance.
     *
     * @return AcquisitionSettings.Builder pre-populated with settings of this instance.
     */
    @Override
    public AcquisitionSettings.Builder copyBuilder() {
        return new DefaultAcquisitionSettings.Builder(
                saveDirectory_, saveNamePrefix_, demoMode_
        );
    }

    private final String saveNamePrefix_;
    private final String saveDirectory_;
    private final boolean demoMode_;

    public DefaultAcquisitionSettings() {
        saveNamePrefix_ = "";
        saveDirectory_ = "";
        demoMode_ = false;
    }

    private DefaultAcquisitionSettings(final String saveDirectory,
                                       final String saveNamePrefix,
                                       final boolean demoMode) {
        saveDirectory_ = saveDirectory;
        saveNamePrefix_ = saveNamePrefix;
        demoMode_ = demoMode;
    }

    /**
     * Returns the save name prefix.
     *
     * @return the save name prefix.
     */
    @Override
    public String saveNamePrefix() {
        return saveNamePrefix_;
    }

    /**
     * Returns the save directory.
     *
     * @return the save directory.
     */
    @Override
    public String saveDirectory() {
        return saveDirectory_;
    }

    /**
     * Returns true if using demo mode.
     *
     * @return true if using demo mode
     */
    @Override
    public boolean demoMode() {
        return demoMode_;
    }

    @Override
    public String toString() {
        return String.format(
                "%s[saveDirectory=%s, saveNamePrefix=%s, demoMode=%s]",
                getClass().getSimpleName(), saveDirectory_, saveNamePrefix_, demoMode_
        );
    }
}