package org.micromanager.lightsheetmanager.internal;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.micromanager.lightsheetmanager.VolumeSettings;
import org.micromanager.lightsheetmanager.LightSheetManager;

public class VolumeSettingsTest {
    private final String firstSide_ = "B";
    private final int numSides_ = 2;
    private final int numSlices_ = 50;
    private final double sideDelayMs_ = 100;
    private final double stepSizeUm_ = 0.5;
    private final double slicePeriodMs_ = 50;
    private final double sampleExposureMs_ = 8.5;
    private final boolean minimizeSlicePeriod_ = true;

    private final double delta_ = 0.00001;

    @Test
    public void testVolumeSettings() {
        LightSheetManager lsm = new DefaultLightSheetManager();

        VolumeSettings.Builder vsb = lsm.volumeSettingsBuilder();
        vsb.firstSide(firstSide_)
                .numSides(numSides_)
                .slicesPerVolume(numSlices_)
                .delayBeforeSide(sideDelayMs_)
                .sliceStepSize(stepSizeUm_)
                .slicePeriod(slicePeriodMs_)
                .sampleExposure(sampleExposureMs_)
                .minimizeSlicePeriod(minimizeSlicePeriod_);

        VolumeSettings vs = vsb.build();
        assertEquals(firstSide_, vs.firstSide());
        assertEquals(numSides_, vs.numSides());
        assertEquals(numSlices_, vs.slicesPerVolume());
        assertEquals(sideDelayMs_, vs.delayBeforeSide(), delta_);
        assertEquals(stepSizeUm_, vs.sliceStepSize(), delta_);
        assertEquals(slicePeriodMs_, vs.slicePeriod(), delta_);
        assertEquals(sampleExposureMs_, vs.sampleExposure(), delta_);
        assertEquals(minimizeSlicePeriod_, vs.isSlicePeriodMinimized());

        vs = vs.copyBuilder().build();
        assertEquals(firstSide_, vs.firstSide());
        assertEquals(numSides_, vs.numSides());
        assertEquals(numSlices_, vs.slicesPerVolume());
        assertEquals(sideDelayMs_, vs.delayBeforeSide(), delta_);
        assertEquals(stepSizeUm_, vs.sliceStepSize(), delta_);
        assertEquals(slicePeriodMs_, vs.slicePeriod(), delta_);
        assertEquals(sampleExposureMs_, vs.sampleExposure(), delta_);
        assertEquals(minimizeSlicePeriod_, vs.isSlicePeriodMinimized());
    }

}
