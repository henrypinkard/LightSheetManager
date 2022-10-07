package org.micromanager.lightsheetmanager.api;

/**
 * The AutofocusManager takes AutofocusSettings and perform calibration.
 */
public interface AutofocusManager {

    CalibrationSettings calibrate(AutofocusSettings settings);
}
