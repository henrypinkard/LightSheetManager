package org.micromanager.lightsheetmanager.internal;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.micromanager.lightsheetmanager.VolumeSettings;
import org.micromanager.lightsheetmanager.LightSheetManager;

public class VolumeSettingsTest {
    private final String firstView_ = "B";
    private final int numViews_ = 2;
    private final int numSlices_ = 50;
    private final double viewDelayMs_ = 100;
    private final double stepSizeUm_ = 0.5;

    private final double delta_ = 0.00001;

    @Test
    public void testVolumeSettings() {
        LightSheetManager lsm = new DefaultLightSheetManager();

        VolumeSettings.Builder vsb = lsm.volumeSettingsBuilder();
        vsb.firstView(firstView_)
                .numViews(numViews_)
                .slicesPerVolume(numSlices_)
                .delayBeforeView(viewDelayMs_)
                .sliceStepSize(stepSizeUm_);

        VolumeSettings vs = vsb.build();
        assertEquals(firstView_, vs.firstView());
        assertEquals(numViews_, vs.numViews());
        assertEquals(numSlices_, vs.slicesPerVolume());
        assertEquals(viewDelayMs_, vs.delayBeforeView(), delta_);
        assertEquals(stepSizeUm_, vs.sliceStepSize(), delta_);

        vs = vs.copyBuilder().build();
        assertEquals(firstView_, vs.firstView());
        assertEquals(numViews_, vs.numViews());
        assertEquals(numSlices_, vs.slicesPerVolume());
        assertEquals(viewDelayMs_, vs.delayBeforeView(), delta_);
        assertEquals(stepSizeUm_, vs.sliceStepSize(), delta_);
    }

}
