//package org.micromanager.lightsheetmanager.internal;
//
//
//import static org.junit.Assert.assertEquals;
//
//import org.junit.Test;
//import org.micromanager.lightsheetmanager.VolumeSettings;
//import org.micromanager.lightsheetmanager.LightSheetManager;
//
//public class VolumeSettingsTest {
//    private final String firstView_ = "B";
//    private final double viewDelayMs_ = 100;
//    private final int numViews_ = 2;
//
//    private final double delta_ = 0.00001;
//
//    @Test
//    public void testVolumeSettings() {
//        LightSheetManager lsm = new DefaultLightSheetManager();
//
//        final double startPosition = -4.0;
//        final double endPosition = 4.0;
//        final double stepSizeUm = 0.5;
//        final double expectedSlices = 16;
//        final double expectedCenter = 0;
//
//        // test the first volumeBound method
//        VolumeSettings.Builder vsb = lsm.volumeSettingsBuilder();
//        vsb.volumeBounds(startPosition, endPosition, stepSizeUm)
//                .firstView(firstView_)
//                .numViews(numViews_)
//                .delayBeforeView(viewDelayMs_);
//
//        VolumeSettings vs = vsb.build();
//        assertEquals(firstView_, vs.firstView());
//        assertEquals(numViews_, vs.numViews());
//        assertEquals(viewDelayMs_, vs.delayBeforeView(), delta_);
//        assertEquals(startPosition, vs.startPosition(), delta_);
//        assertEquals(expectedCenter, vs.centerPosition(), delta_);
//        assertEquals(endPosition, vs.endPosition(), delta_);
//        assertEquals(stepSizeUm, vs.sliceStepSize(), delta_);
//        assertEquals(expectedSlices, vs.slicesPerVolume(), delta_);
//
//        // test the second volumeBound method
//        final int numSlices = 16;
//        final double expectedStepSize = 0.5;
//        vsb.volumeBounds(startPosition, endPosition, numSlices)
//                .firstView(firstView_)
//                .numViews(numViews_)
//                .delayBeforeView(viewDelayMs_);
//
//        vs = vsb.build();
//        assertEquals(firstView_, vs.firstView());
//        assertEquals(numViews_, vs.numViews());
//        assertEquals(viewDelayMs_, vs.delayBeforeView(), delta_);
//        assertEquals(startPosition, vs.startPosition(), delta_);
//        assertEquals(expectedCenter, vs.centerPosition(), delta_);
//        assertEquals(endPosition, vs.endPosition(), delta_);
//        assertEquals(expectedStepSize, vs.sliceStepSize(), delta_);
//        assertEquals(numSlices, vs.slicesPerVolume());
//
//        // test the third volumeBound method
//        final double centerPosition = 0.0;
//        vsb.volumeBounds(centerPosition, numSlices, stepSizeUm)
//                .firstView(firstView_)
//                .numViews(numViews_)
//                .delayBeforeView(viewDelayMs_);
//
//        vs = vsb.build();
//        assertEquals(firstView_, vs.firstView());
//        assertEquals(numViews_, vs.numViews());
//        assertEquals(viewDelayMs_, vs.delayBeforeView(), delta_);
//        assertEquals(startPosition, vs.startPosition(), delta_);
//        assertEquals(centerPosition, vs.centerPosition(), delta_);
//        assertEquals(endPosition, vs.endPosition(), delta_);
//        assertEquals(expectedStepSize, vs.sliceStepSize(), delta_);
//        assertEquals(numSlices, vs.slicesPerVolume());
//
//        // test copy builder method
//        vs = vs.copyBuilder().build();
//        assertEquals(firstView_, vs.firstView());
//        assertEquals(numViews_, vs.numViews());
//        assertEquals(viewDelayMs_, vs.delayBeforeView(), delta_);
//        assertEquals(startPosition, vs.startPosition(), delta_);
//        assertEquals(centerPosition, vs.centerPosition(), delta_);
//        assertEquals(endPosition, vs.endPosition(), delta_);
//        assertEquals(expectedStepSize, vs.sliceStepSize(), delta_);
//        assertEquals(numSlices, vs.slicesPerVolume());
//    }
//
//}
