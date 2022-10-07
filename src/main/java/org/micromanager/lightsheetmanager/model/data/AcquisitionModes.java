package org.micromanager.lightsheetmanager.model.data;


/**
 * Acquisition modes for diSPIM.
 */
public enum AcquisitionModes {
   NONE("None"),
   NO_SCAN("No Scan (Fixed Sheet)"),
   STAGE_SCAN("Stage Scan"),
   STAGE_SCAN_INTERLEAVED("Stage Scan Interleaved");

   private final String text_;

   AcquisitionModes(final String text) {
      text_ = text;
   }

   @Override
   public String toString() {
      return text_;
   }
}
