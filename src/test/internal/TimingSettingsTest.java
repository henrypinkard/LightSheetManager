//package org.micromanager.lightsheetmanager.internal;
//
//import static org.junit.Assert.assertEquals;
//
//import org.junit.Test;
//import org.micromanager.lightsheetmanager.LightSheetManager;
//import org.micromanager.lightsheetmanager.TimingSettings;
//
//public class TimingSettingsTest {
//    private final boolean useAdvancedTiming_ = true;
//    private final int scanNum_ = 2;
//    private final double scanDelay_ = 2.0;
//    private final double scanPeriod_ = 20.0;
//    private final double laserDelay_ = 10.0;
//    private final double laserDuration_ = 3.0;
//    private final double cameraDelay_ = 5.0;
//    private final double cameraDuration_ = 5.0;
//    private final double cameraExposure_ = 5.0;
//    private final boolean alternateScanDirection_ = true;
//
//    private final double delta_ = 0.00001;
//
//    @Test
//    public void testTimingSettings() {
//        LightSheetManager lsm = new DefaultLightSheetManager();
//
//        TimingSettings.Builder tsb = lsm.timingSettingsBuilder();
//        tsb.scansPerSlice(scanNum_)
//                .useAdvancedTiming(useAdvancedTiming_)
//                .delayBeforeScan(scanDelay_)
//                .scansPerSlice(scanNum_)
//                .scanDuration(scanPeriod_)
//                .delayBeforeLaser(laserDelay_)
//                .laserTriggerDuration(laserDuration_)
//                .delayBeforeCamera(cameraDelay_)
//                .cameraTriggerDuration(cameraDuration_)
//                .cameraExposure(cameraExposure_)
//                .useAlternateScanDirection(alternateScanDirection_);
//
//        TimingSettings ts = tsb.build();
//        assertEquals(ts.useAdvancedTiming(), useAdvancedTiming_);
//        assertEquals(ts.delayBeforeScan(), scanDelay_, delta_);
//        assertEquals(ts.scansPerSlice(), scanNum_);
//        assertEquals(ts.scanDuration(), scanPeriod_, delta_);
//        assertEquals(ts.delayBeforeLaser(), laserDelay_, delta_);
//        assertEquals(ts.laserTriggerDuration(), laserDuration_, delta_);
//        assertEquals(ts.delayBeforeCamera(), cameraDelay_, delta_);
//        assertEquals(ts.cameraTriggerDuration(), cameraDuration_, delta_);
//        assertEquals(ts.cameraExposure(), cameraExposure_, delta_);
//        assertEquals(ts.useAlternateScanDirection(), alternateScanDirection_);
//
//        ts = ts.copyBuilder().build();
//        assertEquals(ts.useAdvancedTiming(), useAdvancedTiming_);
//        assertEquals(ts.delayBeforeScan(), scanDelay_, delta_);
//        assertEquals(ts.scansPerSlice(), scanNum_);
//        assertEquals(ts.scanDuration(), scanPeriod_, delta_);
//        assertEquals(ts.delayBeforeLaser(), laserDelay_, delta_);
//        assertEquals(ts.laserTriggerDuration(), laserDuration_, delta_);
//        assertEquals(ts.delayBeforeCamera(), cameraDelay_, delta_);
//        assertEquals(ts.cameraTriggerDuration(), cameraDuration_, delta_);
//        assertEquals(ts.cameraExposure(), cameraExposure_, delta_);
//        assertEquals(ts.useAlternateScanDirection(), alternateScanDirection_);
//    }
//}
