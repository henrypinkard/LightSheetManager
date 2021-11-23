package org.micromanager.lightsheetmanager.internal;

import org.micromanager.lightsheetmanager.AutofocusSettings;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.TimingSettings;
import org.micromanager.lightsheetmanager.VolumeSettings;

public class DefaultLightSheetManager implements LightSheetManager {
   
   /**
    * Access to a AutofocusSettingsBuilder.
    *
    * @return AutofocusSettings.Builder with default settings.
    */
   @Override
   public AutofocusSettings.Builder autofocusSettingsBuilder() {
      return new DefaultAutofocusSettings.Builder();
   }

   /**
    * Access to a VolumeSettingsBuilder.
    *
    * @return VolumeSettings.Builder with default settings.
    */
   @Override
   public VolumeSettings.Builder volumeSettingsBuilder() {
      return new DefaultVolumeSettings.Builder();
   }

   /**
    * Access to a TimingSettingsBuilder.
    *
    * @return TimingSettings.Builder with default settings.
    */
   @Override
   public TimingSettings.Builder timingSettingsBuilder() {
      return new DefaultTimingSettings.Builder();
   }
   
}
