package org.micromanager.lightsheetmanager.api;

/**
 * Acquisition settings for SCAPE microscope geometries.
 */
public interface AcquisitionSettingsSCAPE extends AcquisitionSettings {

    interface Builder<T extends AcquisitionSettings.Builder<T>> extends AcquisitionSettings.Builder<T> {

        /**
         * Creates an immutable instance of AcquisitionSettingsSCAPE
         *
         * @return Immutable version of AcquisitionSettingsSCAPE
         */
        AcquisitionSettingsSCAPE build();
    }

}
