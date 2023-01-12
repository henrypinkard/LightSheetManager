package org.micromanager.lightsheetmanager.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import org.micromanager.MultiStagePosition;
import org.micromanager.PositionList;
import org.micromanager.acqj.internal.Engine;
import org.micromanager.acqj.main.AcqEngMetadata;
import org.micromanager.acqj.main.Acquisition;
import org.micromanager.acqj.main.AcquisitionEvent;
import org.micromanager.acqj.util.AcquisitionEventIterator;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;

/**
 * This function creates lazy sequences (i.e. iterators) of acquisition events by translating
 * LightSheetManager's AcquisitionSettings into AcquisitionEvents (instructions for AcqEngJ)
 */
public class LSMAcquisitionEvents {


   public static final String POSITION_AXIS = "position";


   public static Iterator<AcquisitionEvent> createTimelapseMultiChannelVolumeAcqEvents(
         AcquisitionEvent baseEvent, AcquisitionSettings acquisitionSettings,
         Function<AcquisitionEvent, AcquisitionEvent> eventMonitor) {

      if (acquisitionSettings.getNumTimePoints() <= 1) {
         throw new RuntimeException("timelapse selected but only one timepoint");
      }
      Function<AcquisitionEvent, Iterator<AcquisitionEvent>> timelapse =
            timelapse(acquisitionSettings.getNumTimePoints(),
                  (double) acquisitionSettings.getTimePointInterval());

      if (acquisitionSettings.getNumChannels() == 1) {
         throw new RuntimeException("Expected multiple channels but only one found");
      }

      Function<AcquisitionEvent, Iterator<AcquisitionEvent>> channels = null;
      channels = channels(acquisitionSettings.getChannels());

      Function<AcquisitionEvent, Iterator<AcquisitionEvent>> zStack = zStack(0,
            acquisitionSettings.getVolumeSettings().slicesPerView() - 1);

      ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>> acqFunctions
            = new ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>>();

      acqFunctions.add(timelapse);
      acqFunctions.add(channels);
      acqFunctions.add(zStack);
      return new AcquisitionEventIterator(baseEvent, acqFunctions, eventMonitor);
   }

   public static Iterator<AcquisitionEvent> createTimelapseVolumeAcqEvents(
         AcquisitionEvent baseEvent, AcquisitionSettings acquisitionSettings,
         Function<AcquisitionEvent, AcquisitionEvent> eventMonitor) {

      if (acquisitionSettings.getNumTimePoints() <= 1) {
         throw new RuntimeException("timelapse selected but only one timepoint");
      }
      Function<AcquisitionEvent, Iterator<AcquisitionEvent>> timelapse =
            timelapse(acquisitionSettings.getNumTimePoints(), null);

      Function<AcquisitionEvent, Iterator<AcquisitionEvent>> zStack = zStack(0,
            acquisitionSettings.getVolumeSettings().slicesPerView() - 1);

      ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>> acqFunctions
            = new ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>>();

      acqFunctions.add(timelapse);
      acqFunctions.add(zStack);
      return new AcquisitionEventIterator(baseEvent, acqFunctions, eventMonitor);
   }

   /**
    *
    * @param interleaved true: do we want to do every channel at each z slice before moving to
    *                    the next z slice
    *                    false: do an entire volume in one channel, then the next one
    */
   public static Iterator<AcquisitionEvent> createMultiChannelVolumeAcqEvents(
         AcquisitionEvent baseEvent, AcquisitionSettings acquisitionSettings,
         Function<AcquisitionEvent, AcquisitionEvent> eventMonitor, boolean interleaved) {

      Function<AcquisitionEvent, Iterator<AcquisitionEvent>> channels = null;
      channels = channels(acquisitionSettings.getChannels());

      Function<AcquisitionEvent, Iterator<AcquisitionEvent>> zStack = zStack(0,
            acquisitionSettings.getVolumeSettings().slicesPerView() - 1);

      ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>> acqFunctions
            = new ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>>();

      if (interleaved) {
         acqFunctions.add(zStack);
         acqFunctions.add(channels);
      } else {
         acqFunctions.add(channels);
         acqFunctions.add(zStack);
      }
      return new AcquisitionEventIterator(baseEvent, acqFunctions, eventMonitor);
   }



   public static Iterator<AcquisitionEvent> createVolumeAcqEvents(
         AcquisitionEvent baseEvent, AcquisitionSettings acquisitionSettings,
         Function<AcquisitionEvent, AcquisitionEvent> eventMonitor) {

      Function<AcquisitionEvent, Iterator<AcquisitionEvent>> zStack = zStack(0,
            acquisitionSettings.getVolumeSettings().slicesPerView() - 1);

      ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>> acqFunctions
            = new ArrayList<Function<AcquisitionEvent, Iterator<AcquisitionEvent>>>();
      acqFunctions.add(zStack);
      return new AcquisitionEventIterator(baseEvent, acqFunctions, eventMonitor);
   }


   public static Function<AcquisitionEvent, Iterator<AcquisitionEvent>> zStack(int startSliceIndex,
                                                                               int stopSliceIndex) {
      return (AcquisitionEvent event) -> {
         return new Iterator<AcquisitionEvent>() {

            private int zIndex_ = startSliceIndex;

            @Override
            public boolean hasNext() {
               return zIndex_ < stopSliceIndex;
            }

            @Override
            public AcquisitionEvent next() {
               AcquisitionEvent sliceEvent = event.copy();
               sliceEvent.setAxisPosition(AcqEngMetadata.Z_AXIS, zIndex_);
               // The tiger controller handles Z axis, so no need to add the actual Z position
               zIndex_++;
               return sliceEvent;
            }
         };
      };
   }

   public static Function<AcquisitionEvent, Iterator<AcquisitionEvent>> timelapse(
         int numTimePoints, Double intervalMs) {
      return (AcquisitionEvent event) -> {
         return new Iterator<AcquisitionEvent>() {

            int frameIndex_ = 0;

            @Override
            public boolean hasNext() {
               return frameIndex_ == 0 || frameIndex_ < numTimePoints;
            }

            @Override
            public AcquisitionEvent next() {
               AcquisitionEvent timePointEvent = event.copy();
               if (intervalMs != null) {
                  timePointEvent.setMinimumStartTime((long) (intervalMs * frameIndex_));
               }
               timePointEvent.setTimeIndex(frameIndex_);
               frameIndex_++;

               return timePointEvent;
            }
         };
      };
   }

   /**
    * Make an iterator for events for each active channel
    *
    * @param channelList
    * @return
    */
   public static Function<AcquisitionEvent, Iterator<AcquisitionEvent>> channels(
         ChannelSpec[] channelList) {
      return (AcquisitionEvent event) -> {
         return new Iterator<AcquisitionEvent>() {
            int index = 0;

            @Override
            public boolean hasNext() {
               return index < channelList.length;
            }

            @Override
            public AcquisitionEvent next() {
               AcquisitionEvent channelEvent = event.copy();
               channelEvent.setConfigGroup(channelList[index].getGroup());
               channelEvent.setConfigPreset(channelList[index].getName());
               channelEvent.setChannelName(channelList[index].getName());

               Double zPos;
               if (channelEvent.getZPosition() == null) {
                  try {
                     zPos = Engine.getCore().getPosition() + channelList[index].getOffset();
                  } catch (Exception e) {
                     throw new RuntimeException(e);
                  }
               } else {
                  zPos = channelEvent.getZPosition() + channelList[index].getOffset();
               }
               channelEvent.setZ(channelEvent.getZIndex(), zPos);

               // TODO: do channels have different exposures?
//               channelEvent.setExposure(channelList.get(index).exposure());
               index++;
               return channelEvent;
            }
         };
      };
   }

   /**
    * Iterate over an arbitrary list of positions. Adds in position indices to
    * the axes that assume the order in the list provided correspond to the
    * desired indices
    *
    * @param positionList
    * @return
    */
   public static Function<AcquisitionEvent, Iterator<AcquisitionEvent>> positions(
         PositionList positionList) {
      return (AcquisitionEvent event) -> {
         Stream.Builder<AcquisitionEvent> builder = Stream.builder();
         if (positionList == null) {
            builder.accept(event);
         } else {
            for (int index = 0; index < positionList.getNumberOfPositions(); index++) {
               AcquisitionEvent posEvent = event.copy();
               MultiStagePosition msp = positionList.getPosition(index);
               posEvent.setX(msp.getX());
               posEvent.setY(msp.getY());
               posEvent.setAxisPosition(POSITION_AXIS, index);
               builder.accept(posEvent);
            }
         }
         return builder.build().iterator();
      };
   }


}


