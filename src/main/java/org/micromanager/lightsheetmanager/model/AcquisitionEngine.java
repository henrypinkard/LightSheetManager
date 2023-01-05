package org.micromanager.lightsheetmanager.model;

import java.util.function.Function;
import mmcorej.CMMCore;
import org.micromanager.PositionList;
import org.micromanager.Studio;
import org.micromanager.acqj.api.AcquisitionHook;
import org.micromanager.acqj.main.Acquisition;
import org.micromanager.acqj.main.AcquisitionEvent;
import org.micromanager.internal.MMStudio;
import org.micromanager.lightsheetmanager.api.AcquisitionManager;
import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettingsLS;
import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.DefaultVolumeSettings;
import org.micromanager.lightsheetmanager.model.data.AcquisitionModes;
import org.micromanager.lightsheetmanager.model.data.MultiChannelModes;
import org.micromanager.lightsheetmanager.model.devices.cameras.AndorCamera;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;
import org.micromanager.lightsheetmanager.model.utils.MyNumberUtils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class AcquisitionEngine implements AcquisitionManager {

    private final Studio studio_;
    private final CMMCore core_;

    private AcquisitionSettings acqSettings_;

    private DataStorage data_;

    // TODO: these are not related to acqSettings, probably needs to be clarified
    // (used to get builder objects for acqSettings related objects)
    // TODO: come up with a way to abstract this for different kinds of microscopes
    // diSPIM builders
    DefaultTimingSettings.Builder tsb_;
    DefaultVolumeSettings.Builder vsb_;
    DefaultSliceSettingsLS.Builder ssbLS_;
    DefaultSliceSettings.Builder ssb_;

    // acquisition status
    private final AtomicBoolean isRunning_;
    private final AtomicBoolean isPaused_;

    private LightSheetManagerModel model_;

    public AcquisitionEngine(final Studio studio, final LightSheetManagerModel model) {
        studio_ = Objects.requireNonNull(studio);
        core_ = studio_.core();
        model_ = model;

        data_ = new DataStorage(studio_);
        //System.out.println(data_.getSavePath());

        acqSettings_ = new AcquisitionSettings();

        // true if the acquisition is running or paused
        isRunning_ = new AtomicBoolean(false);

        // true if the acquisition is paused
        isPaused_ = new AtomicBoolean(false);
    }

    /**
     * Create builders from loaded acquisition settings.
     */
    public void setupBuilders() {
        tsb_ = new DefaultTimingSettings.Builder();
        vsb_ = new DefaultVolumeSettings.Builder(acqSettings_.getVolumeSettings());
        ssb_ = new DefaultSliceSettings.Builder(acqSettings_.getSliceSettings());
        ssbLS_ = new DefaultSliceSettingsLS.Builder(acqSettings_.getSliceSettingsLS());
    }

    public void setAcquisitionSettings(final AcquisitionSettings acqSettings) {
        acqSettings_ = acqSettings;
    }

    /**
     * Used make sure the acquisition settings and updated to the current builder settings.
     */
    public void setAcqSettingsFromBuilders() {
        acqSettings_.setTimingSettings(tsb_.build());
        acqSettings_.setVolumeSettings(vsb_.build());
        acqSettings_.setSliceSettings(ssb_.build());
        acqSettings_.setSliceSettingsLS(ssbLS_.build());
    }

    @Override
    public void requestRun() {
        if (isRunning_.get()) {
            studio_.logs().showError("Acquisition is already running.");
            return;
        }

        // TODO: build the settings objects here...
        // build settings objects
        //model_.acquisitions().getAcquisitionSettings().build

        GeometryType geometryType = model_.devices().getDeviceAdapter().getMicroscopeGeometry();
        switch (geometryType) {
            case DISPIM:
                runAcquisitionDISPIM();
                break;
            case SCAPE:
                runAcquisitionSCAPE();
                break;
            default:
                // TODO: error "Acquisition Engine is not implemented for " + geometryType
                break;
        }
    }

    @Override
    public void requestStop() {
        if (!isRunning_.get()) {
            studio_.logs().showError("Acquisition is not running.");
            return;
        }
        isRunning_.set(false);
    }

    @Override
    public void requestPause() {
        if (!isRunning_.get()) {
            studio_.logs().showError("Acquisition is not running.");
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning_.get();
    }

    private boolean runAcquisitionSCAPE() {
        return false;
    }

    /**
     * This is a bunch of logic copied from the diSPIM plugin
     * @return
     */
    private PLogicDISPIM doHardwareCalculations() {

        // make sure slice timings are up-to-date

        recalculateSliceTiming(acqSettings_);
        System.out.println(acqSettings_.getTimingSettings());


        // TODO: was only checked in light sheet mode
//        if (core_.getPixelSizeUm() < 1e-6) {
//            studio_.logs().showError("Need to set the pixel size in Micro-Manager.");
//        }

        // setup channels
        int nrChannelsSoftware = acqSettings_.getNumChannels();  // how many times we trigger the controller per stack
        int nrSlicesSoftware = acqSettings_.getVolumeSettings().slicesPerVolume();
        // TODO: channels



        AndorCamera camera = (AndorCamera)model_.devices().getDevice("Imaging1Camera");
        CameraModes camMode = camera.getTriggerMode();
        final float cameraReadoutTime = camera.getReadoutTime(camMode);
        final double exposureTime = acqSettings_.getCameraExposure();

        // test acq was here

        double volumeDuration = computeActualVolumeDuration(acqSettings_);
        double timepointDuration = computeTimePointDuration();
        long timepointIntervalMs = Math.round(acqSettings_.getTimePointInterval()*1000);

        // use hardware timing if < 1 second between time points
        // experimentally need ~0.5 sec to set up acquisition, this gives a bit of cushion
        // cannot do this in getCurrentAcquisitionSettings because of mutually recursive
        // call with computeActualVolumeDuration()
        if (acqSettings_.isUsingTimePoints()
              && acqSettings_.getNumTimePoints() > 1
              && timepointIntervalMs < (timepointDuration + 750)
              && !acqSettings_.isStageScanning()) {
            acqSettings_.setHardwareTimesPoints(true);
        }

        if (acqSettings_.isUsingMultiplePositions()) {
            if ((acqSettings_.isUsingHardwareTimePoints()
                  || acqSettings_.getNumTimePoints() > 1)
                  && (timepointIntervalMs < timepointDuration*1.2)) {
                acqSettings_.setHardwareTimesPoints(false);
                // TODO: WARNING
            }
        }


        // TODO: make sure position updater is turned off!



        double sliceDuration = acqSettings_.getTimingSettings().sliceDuration();
        if (exposureTime + cameraReadoutTime > sliceDuration) {
            // should only possible to mess this up using advanced timing settings
            // or if there are errors in our own calculations
            studio_.logs().showError("Exposure time of " + exposureTime +
                  " is longer than time needed for a line scan with" +
                  " readout time of " + cameraReadoutTime + "\n" +
                  "This will result in dropped frames. " +
                  "Please change input");
            return null;
        }


        // TODO: diSPIM has the following code, which is apparently needed for autofocusing
//        boolean sideActiveA, sideActiveB;
//        final boolean twoSided = acqSettingsOrig.numSides > 1;
//        if (twoSided) {
//            sideActiveA = true;
//            sideActiveB = true;
//        } else {
//            if (!acqSettingsOrig.acquireBothCamerasSimultaneously) {
//                secondCamera = null;
//            }
//            if (firstSideA) {
//                sideActiveA = true;
//                sideActiveB = false;
//            } else {
//                sideActiveA = false;
//                sideActiveB = true;
//            }
//        }




        // TODO: make static?
        PLogicDISPIM controller = new PLogicDISPIM(studio_, model_.devices());

        double extraChannelOffset = 0.0;
        controller.prepareControllerForAquisition(acqSettings_, extraChannelOffset);
        return controller;
    }

    private boolean runAcquisitionDISPIM() {
        // validate settings
        isRunning_.set(true);

        //TODO remove
        acqSettings_.setDemoMode(true);

        final boolean isLiveModeOn = studio_.live().isLiveModeOn();
        if (isLiveModeOn) {
            studio_.live().setLiveModeOn(false);
            // close the live mode window if it exists
            if (studio_.live().getDisplay() != null) {
                studio_.live().getDisplay().close();
            }
        }

        PLogicDISPIM controller = null;
        if (!acqSettings_.getDemoMode()) {
            controller = doHardwareCalculations();
        }

        // Create and NDViewer and NDTiffStorage wrapped in a package that implements
        // the org.micromanager.acqj.api.DataSink interface that the acquisition engine
        // requires
//        String saveDir = acqSettings_.getSaveDirectoryRoot();
//        String saveName = acqSettings_.getSaveNamePrefix();

        //TODO delete this, just for testing
        String saveName = "test";
        String saveDir = "C:\\Users\\henry\\Desktop\\datadump";

        boolean showViewer = true;
        NDTiffAndViewerAdapter adapter = new NDTiffAndViewerAdapter(showViewer, saveDir, saveName
              ,  50);

        //////////////////////////////////////
        // Begin AcqEngJ integration
        //      The acqSettings object should be static at this point, it will now
        //      be parsed and used to create acquisition events, each of which
        //      will "order" the acquisiton of 1 image (per each camera)
        //////////////////////////////////////
        // Create acquisition
        Acquisition acquisition = new Acquisition(adapter);

        long acqButtonStart = System.currentTimeMillis();


        ////////////  Acquisition hooks ////////////////////
        // These functions will be run on different threads during the acquisiton process
        //    Hooks will run on the Acquisition Engine thread--the one that controls all hardware

        // TODO add any code that needs to be executed on the acquisition thread (i.e. the one
        //  that controls hardware)

        // TODO: autofocus
        acquisition.addHook(new AcquisitionHook() {
            @Override
            public AcquisitionEvent run(AcquisitionEvent event) {
                // TODO: does the Tiger controller need to be cleared and/or checked for errors here?


                // Translate event to timeIndex/channel/etc
                AcquisitionEvent firstAcqEvent = event.getSequence().get(0);
                int timePoint = firstAcqEvent.getTIndex();

                ////////////////////////////////////
                ///////// Run autofocus ////////////
                ///////////////////////////////////

                // TODO: where should these come from? In diSPIM they appear to come from preferences,
                //  not settings...
                boolean autofocusAtT0 = false;
                // TODO: this is where they come from in diSPIM?
//                prefs_.getBoolean(org.micromanager.asidispim.Data.MyStrings.PanelNames.AUTOFOCUS.toString(),
//                      org.micromanager.asidispim.Data.Properties.Keys.PLUGIN_AUTOFOCUS_ACQBEFORESTART, false);
                boolean autofocusEveryStagePass = false;
                boolean autofocusEachNFrames = false;
                boolean autofocusChannel = false;

                  // TODO: this is the diSPIM plugin's autofocus code, which needs to be reimplemented
                  //   and translated. There are also currently no autofocus related things in the acqSettings_
//                if (acqSettings_.isUsingAutofocus()) {
//                    // (Copied from diSPIM): Note that we will not autofocus as expected when using hardware
//                    // timing.  Seems OK, since hardware timing will result in short
//                    // acquisition times that do not need autofocus.
//                    if ( (autofocusAtT0 && timePoint == 0) || ( (timePoint > 0) &&
//                          (timePoint % autofocusEachNFrames == 0 ) ) ) {
//                        if (acqSettings.useChannels) {
//                            multiChannelPanel_.selectChannel(autofocusChannel);
//                        }
//                        if (sideActiveA) {
//                            if (acqSettings.usePathPresets) {
//                                controller_.setPathPreset(org.micromanager.asidispim.Data.Devices.Sides.A);
//                                // blocks until all devices done
//                            }
//                            org.micromanager.asidispim.Utils.AutofocusUtils.FocusResult score = autofocus_.runFocus(
//                                  this, org.micromanager.asidispim.Data.Devices.Sides.A, false,
//                                  sliceTiming_, false);
//                            updateCalibrationOffset(org.micromanager.asidispim.Data.Devices.Sides.A, score);
//                        }
//                        if (sideActiveB) {
//                            if (acqSettings.usePathPresets) {
//                                controller_.setPathPreset(org.micromanager.asidispim.Data.Devices.Sides.B);
//                                // blocks until all devices done
//                            }
//                            org.micromanager.asidispim.Utils.AutofocusUtils.FocusResult score = autofocus_.runFocus(
//                                  this, org.micromanager.asidispim.Data.Devices.Sides.B, false,
//                                  sliceTiming_, false);
//                            updateCalibrationOffset(org.micromanager.asidispim.Data.Devices.Sides.B, score);
//                        }
//                        // Restore settings of the controller
//                        controller_.prepareControllerForAquisition(acqSettings, extraChannelOffset_);
//                        if (acqSettings.useChannels && acqSettings.channelMode != org.micromanager.asidispim.Data.MultichannelModes.Keys.VOLUME) {
//                            controller_.setupHardwareChannelSwitching(acqSettings, hideErrors);
//                        }
//                    }
//                }

                return event;
            }

            @Override
            public void close() {

            }
        }, Acquisition.BEFORE_HARDWARE_HOOK);



        // TODO This after camera hook is called after the camera has been readied to acquire a
        //  sequence. I assume we want to tell the Tiger to start sending TTLs etc here
        acquisition.addHook(new AcquisitionHook() {
            @Override
            public AcquisitionEvent run(AcquisitionEvent event) {
                // TODO: Cameras are now ready to recieve triggers, so we can send (software) trigger
                //  to the tiger to tell it to start outputting TTLs

                return event;
            }

            @Override
            public void close() {

            }
        }, Acquisition.AFTER_CAMERA_HOOK);


        ///////////// Event monitor /////////////////////////
        //    The event monitor will be run on the thread that gets successive acquisition
        //    events from the lazy sequences produced by Iterator<AcquisitonEvent> objects created
        //    below. This can be used to keeps tabs on the acquisition process. The monitor
        //    will not fire until the AcquisitonEvent is supplied by the Iterator in preparation
        //    for the hardware being set up to execute it.
        Function<AcquisitionEvent, AcquisitionEvent> eventMonitor = new Function<AcquisitionEvent, AcquisitionEvent>() {
            @Override
            public AcquisitionEvent apply(AcquisitionEvent acquisitionEvent) {
                // TODO: this will execute as each acquisition event is generated. Add code here
                //  can be used, for example, to provide status updates to the GUI about what is
                //  happening, by reading the contect of each acquisiton event

                return acquisitionEvent;
            }
        };


        ///////////// Turn off autoshutter /////////////////
        final boolean isShutterOpen;
        try {
            isShutterOpen = core_.getShutterOpen();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // TODO: should the shutter be left open for the full duratio of acquisition?
        //  because that's what this code currently does
        boolean autoShutter = core_.getAutoShutter();
        if (autoShutter) {
            core_.setAutoShutter(false);
            if (!isShutterOpen) {
                try {
                    core_.setShutterOpen(true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        acquisition.start();

        ////////////  Create and submit acquisition events ////////////////////
        // Create iterators of acquisition events and submit them to the engine for execution
        // The engine will (try to) automatically iterate over the AcquisitionEvents of each
        // iterator, but not over multiple iterators. So there should be one iterator submitted for
        // each expected triggering of the Tiger controller.

        // TODO: execute any start-acquisition runnables


        // Loop 1: XY positions
        PositionList pl = MMStudio.getInstance().positions().getPositionList();
        if (acqSettings_.isUsingMultiplePositions() && (pl.getNumberOfPositions() == 0)) {
            throw new RuntimeException("XY positions expected but position list is empty");
        }
        for (int positionIndex = 0; positionIndex < (acqSettings_.isUsingMultiplePositions() ?
              pl.getNumberOfPositions() : 1); positionIndex++) {
            AcquisitionEvent baseEvent = new AcquisitionEvent(acquisition);
            if (acqSettings_.isUsingMultiplePositions()) {
                baseEvent.setAxisPosition(LSMAcquisitionEvents.POSITION_AXIS, positionIndex);
            }
            // TODO: what to do if multiple positions not defined: acquire at current stage position?
            //  If yes, then nothing more to do here.

            if (acqSettings_.isUsingHardwareTimePoints()) {
                // create a full iterator of TCZ acquisition events, and Tiger controller
                // will handle everything else
                acquisition.submitEventIterator(
                      LSMAcquisitionEvents.createTimelapseMultiChannelVolumeAcqEvents(
                      baseEvent, acquisition, acqSettings_, eventMonitor));
            } else {
                // Loop 2: Multiple time points
                for (int timeIndex = 0; timeIndex < (acqSettings_.isUsingTimePoints() ?
                      acqSettings_.getNumTimePoints() : 1); timeIndex++) {
                    baseEvent.setTimeIndex(timeIndex);
                    // Loop 3: Channels; Loop 4: Z slices (non-interleaved)
                    // Loop 3: Channels; Loop 4: Z slices (interleaved)
                    acquisition.submitEventIterator(
                              LSMAcquisitionEvents.createMultiChannelVolumeAcqEvents(
                                    baseEvent, acquisition, acqSettings_, eventMonitor,
                                    acqSettings_.getAcquisitionMode() == AcquisitionModes.STAGE_SCAN_INTERLEAVED));
                }
            }
        }


        // No more instructions (i.e. AcquisitionEvents); tell the acquisition to initiate shutdown
        // once everything finishes
        acquisition.finish();
        acquisition.waitForCompletion();

        // cleanup
        studio_.logs().logMessage("diSPIM plugin acquisition " +
              " took: " + (System.currentTimeMillis() - acqButtonStart) + "ms");


        // clean up controller settings after acquisition
        // want to do this, even with demo cameras, so we can test everything else
        // TODO: figure out if we really want to return piezos to 0 position (maybe center position,
        //   maybe not at all since we move when we switch to setup tab, something else??)
        if (controller != null) {
            controller.cleanUpControllerAfterAcquisition(acqSettings_, true);
        }

        // Restore shutter/autoshutter to original state
        try {
            core_.setShutterOpen(isShutterOpen);
            core_.setAutoShutter(autoShutter);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't restore shutter to original state");
        }


        // TODO: execute any end-acquisition runnables

        isRunning_.set(false);
        return true;
    }

    private void stopSPIMStateMachines(AcquisitionSettings acqSettings) {
        final int numViews = acqSettings_.getVolumeSettings().numViews();
        if (numViews == 1) {
            ASIScanner scanner = (ASIScanner) model_.devices().getDevice("IllumBeam");
            scanner.setSPIMState(ASIScanner.SPIMState.IDLE);
        }
        for (int i = 1; i <= numViews; i++) {
            ASIScanner scanner = (ASIScanner) model_.devices().getDevice("Illum" + i + "Beam");
            scanner.setSPIMState(ASIScanner.SPIMState.IDLE);
        }
        // TODO: ASI stage scanning conditionals
    }

    public void recalculateSliceTiming(AcquisitionSettings acqSettings) {
        // don't change timing settings if user is using advanced timing
        if (acqSettings.isUsingAdvancedTiming()) {
            return;
        }
        acqSettings.setTimingSettings(getTimingFromPeriodAndLightExposure(acqSettings));
        // TODO: update gui elements
    }

    // TODO: only using acqSettings for cameraExposure which maybe should not exist?
    public DefaultTimingSettings getTimingFromPeriodAndLightExposure(AcquisitionSettings acqSettings) {
        // uses algorithm Jon worked out in Octave code; each slice period goes like this:
        // 1. camera readout time (none if in overlap mode, 0.25ms in pseudo-overlap)
        // 2. any extra delay time
        // 3. camera reset time
        // 4. start scan 0.25ms before camera global exposure and shifted up in time to account for delay introduced by Bessel filter
        // 5. turn on laser as soon as camera global exposure, leave laser on for desired light exposure time
        // 7. end camera exposure in final 0.25ms, post-filter scan waveform also ends now
        ASIScanner scanner1 = (ASIScanner) model_.devices().getDevice("Illum1Beam");
        ASIScanner scanner2 = (ASIScanner) model_.devices().getDevice("Illum2Beam");

        AndorCamera camera = (AndorCamera)model_.devices().getDevice("Imaging1Camera"); //.getImagingCamera(0);
        if (camera == null) {
            // just a dummy to test demo mode
            return new DefaultTimingSettings.Builder().build();
        }
        System.out.println(camera.getDeviceName());
        CameraModes camMode = camera.getTriggerMode();
        System.out.println(camMode);

        DefaultTimingSettings.Builder tsb = new DefaultTimingSettings.Builder();

        final float scanLaserBufferTime = MyNumberUtils.roundToQuarterMs(0.25f);  // below assumed to be multiple of 0.25ms

        final float cameraResetTime = camera.getResetTime(camMode);      // recalculate for safety, 0 for light sheet
        final float cameraReadoutTime = camera.getReadoutTime(camMode);  // recalculate for safety, 0 for overlap

        final float cameraReadoutMax = MyNumberUtils.ceilToQuarterMs(cameraReadoutTime);
        final float cameraResetMax = MyNumberUtils.ceilToQuarterMs(cameraResetTime);

        // we will wait cameraReadoutMax before triggering camera, then wait another cameraResetMax for global exposure
        // this will also be in 0.25ms increment
        final float globalExposureDelayMax = cameraReadoutMax + cameraResetMax;

        float laserDuration = MyNumberUtils.roundToQuarterMs((float)acqSettings.getCameraExposure());
        float scanDuration = laserDuration + 2*scanLaserBufferTime;
        // scan will be longer than laser by 0.25ms at both start and end

        // account for delay in scan position due to Bessel filter by starting the scan slightly earlier
        // than we otherwise would (Bessel filter selected b/c stretches out pulse without any ripples)
        // delay to start is (empirically) 0.07ms + 0.25/(freq in kHz)
        // delay to midpoint is empirically 0.38/(freq in kHz)
        // group delay for 5th-order bessel filter ~0.39/freq from theory and ~0.4/freq from IC datasheet
        final float scanFilterFreq = Math.max(scanner1.getFilterFreqX(), scanner2.getFilterFreqX());

        float scanDelayFilter = 0;
        if (scanFilterFreq != 0) {
            scanDelayFilter = MyNumberUtils.roundToQuarterMs(0.39f/scanFilterFreq);
        }

        // If the PLogic card is used, account for 0.25ms delay it introduces to
        // the camera and laser trigger signals => subtract 0.25ms from the scanner delay
        // (start scanner 0.25ms later than it would be otherwise)
        // this time-shift opposes the Bessel filter delay
        // scanDelayFilter won't be negative unless scanFilterFreq is more than 3kHz which shouldn't happen
        // TODO: assume PLogic exists // if (devices_.isValidMMDevice(Devices.Keys.PLOGIC))
        scanDelayFilter -= 0.25f;

        float delayBeforeScan = globalExposureDelayMax - scanLaserBufferTime   // start scan 0.25ms before camera's global exposure
                - scanDelayFilter; // start galvo moving early due to card's Bessel filter and delay of TTL signals via PLC
        float delayBeforeLaser = globalExposureDelayMax; // turn on laser as soon as camera's global exposure is reached
        float delayBeforeCamera = cameraReadoutMax; // camera must readout last frame before triggering again
        int scansPerSlice = 1;

        float cameraDuration = 0; // set in the switch statement below
        double sliceDuration = 0;

        // figure out desired time for camera to be exposing (including reset time)
        // because both camera trigger and laser on occur on 0.25ms intervals (i.e. we may not
        //    trigger the laser until 0.24ms after global exposure) use cameraReset_max
        // special adjustment for Photometrics cameras that possibly has extra clear time which is counted in reset time
        //    but not in the camera exposure time
        // TODO: skipped PVCAM case, update comment
        float cameraExposure = MyNumberUtils.ceilToQuarterMs(cameraResetTime) + laserDuration;

        switch (acqSettings_.getCameraMode()) {
            case EDGE:
                cameraDuration = 1;  // doesn't really matter, 1ms should be plenty fast yet easy to see for debugging
                cameraExposure += 0.1f; // add 0.1ms as safety margin, may require adding an additional 0.25ms to slice
                // slight delay between trigger and actual exposure start
                //   is included in exposure time for Hamamatsu and negligible for Andor and PCO cameras
                // ensure not to miss triggers by not being done with readout in time for next trigger, add 0.25ms if needed
                sliceDuration = getSliceDuration(delayBeforeScan, scanDuration, scansPerSlice, delayBeforeLaser, laserDuration, delayBeforeCamera, cameraDuration);
                if (sliceDuration < (cameraExposure + cameraReadoutTime)) {
                    delayBeforeCamera += 0.25f;
                    delayBeforeLaser += 0.25;
                    delayBeforeScan += 0.25f;
                }
                break;
            case LEVEL: // AKA "bulb mode", TTL rising starts exposure, TTL falling ends it
                cameraDuration = MyNumberUtils.ceilToQuarterMs(cameraExposure);
                cameraExposure = 1; // doesn't really matter, controlled by TTL
                break;
            case OVERLAP: // only Hamamatsu or Andor
                cameraDuration = 1;  // doesn't really matter, 1ms should be plenty fast yet easy to see for debugging
                cameraExposure = 1;  // doesn't really matter, controlled by interval between triggers
                break;
            case PSEUDO_OVERLAP:// PCO or Photometrics, enforce 0.25ms between end exposure and start of next exposure by triggering camera 0.25ms into the slice
                cameraDuration = 1;  // doesn't really matter, 1ms should be plenty fast yet easy to see for debugging
                // TODO: not dealing with PVCAM (maybe throw error on unknown cam lib)
                sliceDuration = getSliceDuration(delayBeforeScan, scanDuration, scansPerSlice, delayBeforeLaser, laserDuration, delayBeforeCamera, cameraDuration);
                cameraExposure = (float)sliceDuration - delayBeforeCamera;  // s.cameraDelay should be 0.25ms for PCO
                if (cameraReadoutMax < 0.24f) {
                    // TODO: report error "Camera delay should be at least 0.25ms for pseudo-overlap mode."
                }
                break;
            case VIRTUAL_SLIT:
                // each slice period goes like this:
                // 1. scan reset time (use to add any extra settling time to the start of each slice)
                // 2. start scan, wait scan settle time
                // 3. trigger camera/laser when scan settle time elapses
                // 4. scan for total of exposure time plus readout time (total time some row is exposing) plus settle time plus extra 0.25ms to prevent artifacts
                // 5. laser turns on 0.25ms before camera trigger and stays on until exposure is ending
                // TODO revisit this after further experimentation
                cameraDuration = 1;  // only need to trigger camera
                final float shutterWidth = (float) acqSettings_.getSliceSettingsLS().shutterWidth();
                final float shutterSpeed = (float) acqSettings_.getSliceSettingsLS().shutterSpeedFactor();
                ///final float shutterWidth = props_.getPropValueFloat(Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_LS_SHUTTER_WIDTH);
                //final int shutterSpeed = props_.getPropValueInteger(Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_LS_SHUTTER_SPEED);
                float pixelSize = (float) core_.getPixelSizeUm();
                if (pixelSize < 1e-6) {  // can't compare equality directly with floating point values so call < 1e-9 is zero or negative
                    pixelSize = 0.1625f;  // default to pixel size of 40x with sCMOS = 6.5um/40
                }
                final double rowReadoutTime = camera.getRowReadoutTime();
                cameraExposure = (float)(rowReadoutTime * (int)(shutterWidth/pixelSize) * shutterSpeed);
                // s.cameraExposure = (float) (rowReadoutTime * shutterWidth / pixelSize * shutterSpeed);
                final float totalExposureMax = MyNumberUtils.ceilToQuarterMs(cameraReadoutTime + cameraExposure + 0.05f);  // 50-300us extra cushion time
                final float scanSettle = (float) acqSettings_.getSliceSettingsLS().scanSettleTime();
                final float scanReset = (float) acqSettings_.getSliceSettingsLS().scanResetTime();
                delayBeforeScan = scanReset - scanDelayFilter;
                scanDuration = scanSettle + (totalExposureMax*shutterSpeed) + scanLaserBufferTime;
                delayBeforeCamera = scanReset + scanSettle;
                delayBeforeLaser = delayBeforeCamera - scanLaserBufferTime; // trigger laser just before camera to make sure it's on already
                laserDuration = (totalExposureMax*shutterSpeed) + scanLaserBufferTime; // laser will turn off as exposure is ending
                break;
            case INTERNAL: // case fall through
            default:
                studio_.logs().showError("Invalid camera mode");
                // FIXME: set to invalid!
                break;
        }

        // fix corner case of negative calculated scanDelay
        if (delayBeforeScan < 0) {
            delayBeforeCamera-= delayBeforeScan;
            delayBeforeLaser -= delayBeforeScan;
            delayBeforeScan = 0;  // same as (-= delayBeforeScan)
        }

        // fix corner case of (exposure time + readout time) being greater than the slice duration
        // most of the time the slice duration is already larger
        sliceDuration = getSliceDuration(delayBeforeScan, scanDuration, scansPerSlice, delayBeforeLaser, laserDuration, delayBeforeCamera, cameraDuration);
        float globalDelay = MyNumberUtils.ceilToQuarterMs((cameraExposure + cameraReadoutTime) - (float)sliceDuration);
        if (globalDelay > 0) {
            delayBeforeCamera += globalDelay;
            delayBeforeLaser += globalDelay;
            delayBeforeScan += globalDelay;
        }

        // update the slice duration based on our new values
        sliceDuration = getSliceDuration(delayBeforeScan, scanDuration, scansPerSlice, delayBeforeLaser, laserDuration, delayBeforeCamera, cameraDuration);

        tsb.scansPerSlice(scansPerSlice);
        tsb.scanDuration(scanDuration);
        tsb.cameraExposure(cameraExposure);
        tsb.laserTriggerDuration(laserDuration);
        tsb.cameraTriggerDuration(cameraDuration);
        tsb.delayBeforeCamera(delayBeforeCamera);
        tsb.delayBeforeLaser(delayBeforeLaser);
        tsb.delayBeforeScan(delayBeforeScan);
        tsb.sliceDuration(sliceDuration);
        return tsb.build();
    }

    public double getSliceDuration(
            final double delayBeforeScan,
            final double scanDuration,
            final double scansPerSlice,
            final double delayBeforeLaser,
            final double laserDuration,
            final double delayBeforeCamera,
            final double cameraDuration) {
        // slice duration is the max out of the scan time, laser time, and camera time
        return Math.max(Math.max(
                delayBeforeScan + (scanDuration * scansPerSlice),   // scan time
                delayBeforeLaser + laserDuration                    // laser time
            ),
            delayBeforeCamera + cameraDuration                      // camera time
        );
    }

    /**
     * Compute the volume duration in ms based on controller's timing settings.
     * Includes time for multiple channels.  However, does not include for multiple positions.
     * @param acqSettings Settings for the acquisition
     * @return duration in ms
     */
    public double computeActualVolumeDuration(final AcquisitionSettings acqSettings) {
        final MultiChannelModes channelMode = acqSettings.getChannelMode();
        final int numChannels = acqSettings.getNumChannels();
        final int numViews = acqSettings.getVolumeSettings().numViews();
        final double delayBeforeSide = acqSettings.getVolumeSettings().delayBeforeView();
        int numCameraTriggers = acqSettings.getVolumeSettings().slicesPerVolume();
        if (acqSettings.getCameraMode() == CameraModes.OVERLAP) {
            numCameraTriggers += 1;
        }
        // stackDuration is per-side, per-channel, per-position

        final double stackDuration = numCameraTriggers * acqSettings.getTimingSettings().sliceDuration();
        if (acqSettings.isStageScanning()) { // || acqSettings.isStageStepping) {
            // TODO: stage scanning code
            return 0;
        } else {
            // piezo scan
            double channelSwitchDelay = 0;
            if (channelMode == MultiChannelModes.VOLUME) {
                channelSwitchDelay = 500;   // estimate channel switching overhead time as 0.5s
                // actual value will be hardware-dependent
            }
            if (channelMode == MultiChannelModes.SLICE_HW) {
                return numViews * (delayBeforeSide + stackDuration * numChannels);  // channelSwitchDelay = 0
            } else {
                return numViews * numChannels
                        * (delayBeforeSide + stackDuration)
                        + (numChannels - 1) * channelSwitchDelay;
            }
        }
    }

    /**
     * Compute the time point duration in ms.  Only difference from computeActualVolumeDuration()
     * is that it also takes into account the multiple positions, if any.
     * @return duration in ms
     */
    private double computeTimePointDuration() {
        final double volumeDuration = computeActualVolumeDuration(acqSettings_);
        if (acqSettings_.isUsingMultiplePositions()) {
            // use 1.5 seconds motor move between positions
            // (could be wildly off but was estimated using actual system
            // and then slightly padded to be conservative to avoid errors
            // where positions aren't completed in time for next position)
            // could estimate the actual time by analyzing the position's relative locations
            //   and using the motor speed and acceleration time
            return studio_.positions().getPositionList().getNumberOfPositions() *
                    (volumeDuration + 1500 + acqSettings_.getPostMoveDelay());
        }
        return volumeDuration;
    }

    public void updateAcquisitionStatus() {

    }

    public DefaultTimingSettings.Builder getTimingSettingsBuilder() {
        return tsb_;
    }

    public DefaultVolumeSettings.Builder getVolumeSettingsBuilder() {
        return vsb_;
    }

    public DefaultSliceSettingsLS.Builder getSliceSettingsBuilderLS() {
        return ssbLS_;
    }

    public DefaultSliceSettings.Builder getSliceSettingsBuilder() {
        return ssb_;
    }


    public AcquisitionSettings getAcquisitionSettings() {
        return acqSettings_;
    }
}
