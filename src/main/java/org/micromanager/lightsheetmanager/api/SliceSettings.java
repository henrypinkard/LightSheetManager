package org.micromanager.lightsheetmanager.api;

// corresponds to slice settings panel on acquisition tab when in light sheet mode (virtual slit)
public interface SliceSettings {

    interface Builder {
        /**
         * Sets the shutter width in microns.
         *
         * @param um the shutter width in microns
         */
        Builder shutterWidth(final double um);

        /**
         * Sets the shutter speed factor.
         *
         * @param factor the shutter speed factor
         */
        Builder shutterSpeedFactor(final double factor);

        /**
         * Sets the scan settle time in milliseconds.
         *
         * @param ms the scan settle time in milliseconds
         */
        Builder scanSettleTime(final double ms);

        /**
         * Sets the scan settle reset in milliseconds.
         *
         * @param ms the scan reset time in milliseconds
         */
        Builder scanResetTime(final double ms);

        /**
         * Creates an immutable instance of SliceSettings
         *
         * @return Immutable version of SliceSettings
         */
        SliceSettings build();
    }

    /**
     * Creates a Builder populated with settings of this SliceSettings instance.
     *
     * @return SliceSettings.Builder pre-populated with settings of this instance.
     */
    SliceSettings.Builder copyBuilder();

    /**
     * Returns the shutter width in microns.
     *
     * @return the shutter width in microns
     */
    double shutterWidth();

    /**
     * Returns the shutter speed factor. (1 / speedFactor)
     *
     * @return the shutter speed factor
     */
    double shutterSpeedFactor();

    /**
     * Returns the scan settle time in milliseconds.
     *
     * @return the scan settle time in milliseconds
     */
    double scanSettleTime();

    /**
     * Returns the scan reset time in milliseconds.
     *
     * @return the scan reset time in milliseconds
     */
    double scanResetTime();
}
