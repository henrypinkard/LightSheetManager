package org.micromanager.lighsheetmanager.internal;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.micromanager.lightsheetmanager.AutofocusSettings;
import org.micromanager.lightsheetmanager.LightSheetManager;

public class AutofocusSettingsTest {
   private final int numImages_ = 14;
   private final double stepSize_ = 1.6;
   private final AutofocusSettings.AutofocusMode mode_ = AutofocusSettings.AutofocusMode.TYPE1;
   private final AutofocusSettings.AutofocusType type_ = AutofocusSettings.AutofocusType.TYPE2;
   private final double r2_ = 1.6;
   private final int timePointInterval_ = 3;
   private final boolean useEveryStagePass_ = true;
   private final boolean useBeforeAcquisition_ = true;
   private final String channel_ = "GFP";
   private final double maxOffset_ = 3.0;
   private final boolean autoUpdateOffset_ = false;
   private final double autoUpdateMaxOffset_ = 5.6;
   private final double delta_ = 0.00001;

   @Test
   public void testAutofocusSettings() {
      LightSheetManager lsm = new DefaultLightSheetManager();

      AutofocusSettings.Builder asb = lsm.autofocusSettingsBuilder();
      asb.numImages(numImages_)
            .stepSize(stepSize_)
            .mode(mode_)
            .type(type_)
            .r2(r2_)
            .timePointInterval(timePointInterval_)
            .useEveryStagePass(useEveryStagePass_)
            .useBeforeAcquisition(useBeforeAcquisition_)
            .channel(channel_)
            .maxOffset(maxOffset_)
            .autoUpdateOffset(autoUpdateOffset_)
            .autoUpdateMaxOffset(autoUpdateMaxOffset_);
      
      AutofocusSettings as = asb.build();
      assertEquals(numImages_, as.numImages());
      assertEquals(stepSize_, as.stepSize(), delta_);
      assertEquals(mode_, as.mode());
      assertEquals(type_, as.type());
      assertEquals(r2_, as.r2(), delta_);
      assertEquals(timePointInterval_, as.timePointInterval());
      assertEquals(useEveryStagePass_, as.useEveryStagePass());
      assertEquals(useBeforeAcquisition_, as.useBeforeAcquisition());
      assertEquals(channel_, as.channel());
      assertEquals(maxOffset_, as.maxOffset(), delta_);
      assertEquals(autoUpdateOffset_, as.autoUpdateOffset());
      assertEquals(autoUpdateMaxOffset_, as.autoUpdateMaxOffset(), delta_);

      as = as.copyBuilder().build();
      assertEquals(numImages_, as.numImages());
      assertEquals(stepSize_, as.stepSize(), delta_);
      assertEquals(mode_, as.mode());
      assertEquals(type_, as.type());
      assertEquals(r2_, as.r2(), delta_);
      assertEquals(timePointInterval_, as.timePointInterval());
      assertEquals(useEveryStagePass_, as.useEveryStagePass());
      assertEquals(useBeforeAcquisition_, as.useBeforeAcquisition());
      assertEquals(channel_, as.channel());
      assertEquals(maxOffset_, as.maxOffset(), delta_);
      assertEquals(autoUpdateOffset_, as.autoUpdateOffset());
      assertEquals(autoUpdateMaxOffset_, as.autoUpdateMaxOffset(), delta_);
   }

}
