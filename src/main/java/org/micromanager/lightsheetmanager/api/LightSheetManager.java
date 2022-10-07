package org.micromanager.lightsheetmanager.api;

/**
 * Root interface to everything related to Light Sheet Microscope
 *
 */
public interface LightSheetManager {

   /**
    * Access to a AutofocusSettingsBuilder.
    *
    * @return AutofocusSettings.Builder with default settings.
    */
   AutofocusSettings.Builder autofocusSettingsBuilder();
   
   /**
    * Access to a VolumeSettingsBuilder.
    *
    * @return VolumeSettings.Builder with default settings.
    */
   VolumeSettings.Builder volumeSettingsBuilder();

   /**
    * Access to a TimingSettingsBuilder.
    *
    * @return TimingSettings.Builder with default settings.
    */
   TimingSettings.Builder timingSettingsBuilder();
}
