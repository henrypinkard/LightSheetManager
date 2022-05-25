package org.micromanager.lightsheetmanager.internal;

import org.micromanager.lightsheetmanager.AutofocusSettings;

public class DefaultAutofocusSettings implements AutofocusSettings {

   public static class Builder implements AutofocusSettings.Builder {
      private int numImages_ = 4;
      private double stepSize_ = 1.0;
      private AutofocusMode mode_;
      private AutofocusType type_;
      private double r2_ = 1.0;
      private int timePointInterval_ = 1;
      private boolean useEveryStagePass_ = false;
      private boolean useBeforeAcquisition_ = false;
      private String channel_ = "";
      private double maxOffset_ = 1.0;
      private boolean autoUpdateOffset_ = true;
      private double autoUpdateMaxOffset_ = 5.0;

      public Builder() {
      }

      private Builder(int numImages,
                     double stepSize,
                     AutofocusMode mode,
                     AutofocusType type,
                     double r2,
                     int timePointInterval,
                     boolean useEveryStagePass,
                     boolean useBeforeAcquisition,
                     String channel,
                     double maxOffset,
                     boolean autoUpdateOffset,
                     double autoUpdateMaxOffset) {
         numImages_ = numImages;
         stepSize_ = stepSize;
         mode_ = mode;
         type_ = type;
         r2_ = r2;
         timePointInterval_ = timePointInterval;
         useEveryStagePass_ = useEveryStagePass;
         useBeforeAcquisition_ = useBeforeAcquisition;
         channel_ = channel;
         maxOffset_ = maxOffset;
         autoUpdateOffset_ = autoUpdateOffset;
         autoUpdateMaxOffset_ = autoUpdateMaxOffset;
      }

      /**
       * Sets the number of images to capture in the autofocus routine.
       *
       * @param numImages the number of images
       */
      @Override
      public AutofocusSettings.Builder numImages(int numImages) {
         numImages_ = numImages;
         return this;
      }

      /**
       * Sets the spacing between images in the autofocus routine.
       *
       * @param stepSize the step size in microns
       */
      @Override
      public AutofocusSettings.Builder stepSize(final double stepSize) {
         stepSize_ = stepSize;
         return this;
      }

      /**
       * Selects whether to fix the piezo or the sheet for an autofocus routine.
       *
       * @param mode the autofocus mode
       */
      @Override
      public AutofocusSettings.Builder mode(final AutofocusMode mode) {
         mode_ = mode;
         return this;
      }

      /**
       * Sets the type of scoring algorithm to use when running autofocus.
       *
       * @param type the scoring algorithm
       */
      @Override
      public AutofocusSettings.Builder type(final AutofocusType type) {
         type_ = type;
         return this;
      }

      /**
       * Sets the coefficient of determination for the autofocus algorithm.
       *
       * @param value the coefficient of determination
       */
      @Override
      public AutofocusSettings.Builder r2(final double value) {
         r2_ = value;
         return this;
      }

      /**
       * Sets the number of images to capture before running an autofocus routine during an acquisition.
       *
       * @param timePointInterval
       */
      @Override
      public AutofocusSettings.Builder timePointInterval(final int timePointInterval) {
         timePointInterval_ = timePointInterval;
         return this;
      }

      /**
       * Run autofocus every time we move to the next channel during an acquisition.
       *
       * @param state true to enable autofocus every stage pass
       */
      @Override
      public AutofocusSettings.Builder useEveryStagePass(final boolean state) {
         useEveryStagePass_ = state;
         return this;
      }

      /**
       * Run an autofocus routine before starting the acquisition.
       *
       * @param state true or false
       */
      @Override
      public AutofocusSettings.Builder useBeforeAcquisition(final boolean state) {
         useBeforeAcquisition_ = state;
         return this;
      }

      /**
       * Set the channel to run the autofocus routine on.
       *
       * @param channel the channel to run autofocus on
       */
      @Override
      public AutofocusSettings.Builder channel(final String channel) {
         channel_ = channel;
         return this;
      }

      @Override
      public AutofocusSettings.Builder maxOffset(final double maxOffset) {
         maxOffset_ = maxOffset;
         return this;
      }

      @Override
      public AutofocusSettings.Builder autoUpdateOffset(final boolean state) {
         autoUpdateOffset_ = state;
         return this;
      }

      @Override
      public AutofocusSettings.Builder autoUpdateMaxOffset(final double um) {
         autoUpdateMaxOffset_ = um;
         return this;
      }

      /**
       * Creates an immutable instance of AutofocusSettings
       *
       * @return Immutable version of AutofocusSettings
       */
      @Override
      public AutofocusSettings build() {
         return new DefaultAutofocusSettings(
               numImages_,
               stepSize_,
               mode_,
               type_,
               r2_,
               timePointInterval_,
               useEveryStagePass_,
               useBeforeAcquisition_,
               channel_,
               maxOffset_,
               autoUpdateOffset_,
               autoUpdateMaxOffset_);
      }
   }


   private final int numImages_;
   private final double stepSize_;
   private final AutofocusMode mode_;
   private final AutofocusType type_;
   private final double r2_;
   private final int timePointInterval_;
   private final boolean useEveryStagePass_;
   private final boolean useBeforeAcquisition_;
   private final String channel_;
   private final double maxOffset_;
   private final boolean autoUpdateOffset_;
   private final double autoUpdateMaxOffset_;


   private DefaultAutofocusSettings(int numImages,
                                    double stepSize,
                                    AutofocusMode mode,
                                    AutofocusType type,
                                    double r2,
                                    int timePointInterval,
                                    boolean useEveryStagePass,
                                    boolean useBeforeAcquisition,
                                    String channel,
                                    double maxOffset,
                                    boolean autoUpdateOffset,
                                    double autoUpdateMaxOffset
                                    ) {
      numImages_ = numImages;
      stepSize_ = stepSize;
      mode_ = mode;
      type_ = type;
      r2_ = r2;
      timePointInterval_ = timePointInterval;
      useEveryStagePass_ = useEveryStagePass;
      useBeforeAcquisition_ = useBeforeAcquisition;
      channel_ = channel;
      maxOffset_ = maxOffset;
      autoUpdateOffset_ = autoUpdateOffset;
      autoUpdateMaxOffset_ = autoUpdateMaxOffset;
   }

   public Builder copyBuilder() {
      return new Builder(numImages_,
            stepSize_,
            mode_,
            type_,
            r2_,
            timePointInterval_,
            useEveryStagePass_,
            useBeforeAcquisition_,
            channel_,
            maxOffset_,
            autoUpdateOffset_,
            autoUpdateMaxOffset_);
   }

   /**
    * Returns the number of images used for autofocus routine.
    *
    * @return the number of images
    */
   @Override
   public int numImages() {
      return numImages_;
   }

   /**
    * Returns the step size between images in microns.
    *
    * @return the step size in microns
    */
   @Override
   public double stepSize() {
      return stepSize_;
   }

   /**
    * Returns the autofocus mode being used.
    *
    * @return the autofocus mode
    */
   @Override
   public AutofocusMode mode() {
      return mode_;
   }

   /**
    * Returns the type of scoring algorithm used for autofocus.
    *
    * @return the type of scoring algorithm
    */
   @Override
   public AutofocusType type() {
      return type_;
   }

   /**
    * Returns the coefficient of determination used in the autofocus routine.
    *
    * @return the coefficient of determination
    */
   @Override
   public double r2() {
      return r2_;
   }

   /**
    * Returns the number of images between autofocus routines.
    *
    * @return the number of images between autofocus routines
    */
   @Override
   public int timePointInterval() {
      return timePointInterval_;
   }

   /**
    * Returns true if autofocus is run every stage pass.
    *
    * @return true if autofocus is run every stage pass
    */
   @Override
   public boolean useEveryStagePass() {
      return useEveryStagePass_;
   }

   /**
    * Returns true if we run an autofocus routine before starting an acquisition.
    *
    * @return true if enabled
    */
   @Override
   public boolean useBeforeAcquisition() {
      return useBeforeAcquisition_;
   }

   /**
    * Returns the channel autofocus is being run on.
    *
    * @return the autofocus channel
    */
   @Override
   public String channel() {
      return channel_;
   }

   /**
    * What is this?
    *
    * @return
    */
   @Override
   public double maxOffset() {
      return maxOffset_;
   }

   @Override
   public boolean autoUpdateOffset() {
      return autoUpdateOffset_;
   }

   @Override
   public double autoUpdateMaxOffset() {
      return autoUpdateMaxOffset_;
   }

}
