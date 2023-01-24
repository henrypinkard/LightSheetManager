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

        @Override
        public AcquisitionSettings.Builder saveDirectory(final String directory) {
            saveDirectory_ = directory;
            return this;
        }

        @Override
        public AcquisitionSettings.Builder saveNamePrefix(final String name) {
            saveNamePrefix_ = name;
            return this;
        }

        @Override
        public AcquisitionSettings.Builder demoMode(final boolean state) {
            demoMode_ = state;
            return this;
        }

        @Override
        public AcquisitionSettings build() {
            return new DefaultAcquisitionSettings();
        }
    }

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

    @Override
    public String saveNamePrefix() {
        return saveNamePrefix_;
    }

    @Override
    public String saveDirectory() {
        return saveDirectory_;
    }

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