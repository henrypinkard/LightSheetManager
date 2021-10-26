package org.micromanager.lightsheetmanager;

/**
 * Root interface to everything related to Light Sheet Microscope
 *
 */
public interface LightSheetManager {

   /**
    * Access to an AutofocusSettingsBuilder.
    *
    * @return AutofocusSettings.Builder with default settings.
    */
   public AutofocusSettings.Builder autofocusSettingsBuilder();
}
