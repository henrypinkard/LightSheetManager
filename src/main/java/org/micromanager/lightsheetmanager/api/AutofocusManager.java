package org.micromanager.lightsheetmanager.api;

/**
 * The AutofocusManager takes AutofocusSettings and perform calibration.
 */
public interface AutofocusManager {

    CalibrationSettings calibrate(AutofocusSettings settings);
}

// TODO: moved from AutofocusSettings
// NS: these should not be part of the AutofocusSettings, but rather be parameters
// to the entity using the AutofocusSettings to autofocus.
// void showImages(final boolean state);
// boolean getShowImages();

// void showPlot(final boolean state);
// boolean getShowPlot();