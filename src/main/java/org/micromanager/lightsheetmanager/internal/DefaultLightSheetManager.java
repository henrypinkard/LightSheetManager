package org.micromanager.lightsheetmanager.internal;

import org.micromanager.lightsheetmanager.AutofocusSettings;
import org.micromanager.lightsheetmanager.LightSheetManager;

public class DefaultLightSheetManager implements LightSheetManager {
   /**
    * Access to an AutofocusSettingsBuilder.
    *
    * @return AutofocusSettings.Builder with default settings.
    */
   @Override
   public AutofocusSettings.Builder autofocusSettingsBuilder() {
      return new DefaultAutofocusSettings.Builder();
   }
}
