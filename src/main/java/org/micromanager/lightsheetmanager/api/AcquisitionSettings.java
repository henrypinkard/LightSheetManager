package org.micromanager.lightsheetmanager.api;

/**
 * Base acquisition settings for all microscopes.
 */
public interface AcquisitionSettings {

    interface Builder {

        Builder saveNamePrefix(final String name);

        Builder saveDirectory(final String directory);

        Builder demoMode(final boolean state);

        AcquisitionSettings build();
    }

    Builder copyBuilder();

    String saveNamePrefix();

    String saveDirectory();

    /**
     * Returns true if using demo mode.
     *
     * @return true if using demo mode
     */
    boolean demoMode();
}
