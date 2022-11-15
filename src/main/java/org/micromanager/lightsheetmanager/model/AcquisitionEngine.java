package org.micromanager.lightsheetmanager.model;

import mmcorej.CMMCore;
import mmcorej.TaggedImage;
import org.micromanager.MultiStagePosition;
import org.micromanager.PositionList;
import org.micromanager.Studio;
import org.micromanager.data.Coordinates;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.display.DisplayWindow;
import org.micromanager.lightsheetmanager.api.AcquisitionManager;
import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.api.internal.DefaultSliceSettings;
import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.DefaultVolumeSettings;
import org.micromanager.lightsheetmanager.model.data.MultiChannelModes;
import org.micromanager.lightsheetmanager.model.devices.cameras.AndorCamera;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;
import org.micromanager.lightsheetmanager.model.utils.MyNumberUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AcquisitionEngine implements AcquisitionManager {

    private final Studio studio_;
    private final CMMCore core_;

    private AcquisitionSettings acqSettings_;

    private DataStorage data_;

    // TODO: come up with a way to abstract this for different kinds of microscopes
    // diSPIM builders
    DefaultTimingSettings.Builder tsb_;
    DefaultVolumeSettings.Builder vsb_;
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
        System.out.println(acqSettings_.getTimingSettings());

        // true if the acquisition is running or paused
        isRunning_ = new AtomicBoolean(false);

        // true if the acquisition is paused
        isPaused_ = new AtomicBoolean(false);

        // TODO: put into acqSettings?
        // diSPIM builders
        tsb_ = new DefaultTimingSettings.Builder();
        vsb_ = new DefaultVolumeSettings.Builder();
        ssb_ = new DefaultSliceSettings.Builder();
    }

    public void setAcquisitionSettings(final AcquisitionSettings acqSettings) {
        acqSettings_ = acqSettings;
    }

    // TODO: slice settings as well
    public void setSettingsFromBuilders() {
        acqSettings_.setTimingSettings(tsb_.build());
        acqSettings_.setVolumeSettings(vsb_.build());
    }

    @Override
    public void requestRun() {
        if (isRunning_.get()) {
            studio_.logs().showError("Acquisition is already running.");
            return;
        }
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

    private boolean runAcquisitionDISPIM() {
        // validate settings
        Datastore datastore = data_.getDatastore();


        Coords.CoordsBuilder builder = Coordinates.builder();

        int numTimePointsDone = 0;
        int numPositionsDone = 0;

        final boolean isLiveModeOn = studio_.live().isLiveModeOn();
        if (isLiveModeOn) {
            studio_.live().setLiveModeOn(false);
            // close the live mode window if it exists
            if (studio_.live().getDisplay() != null) {
                studio_.live().getDisplay().close();
            }
        }

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

        // set up XY positions
        int nrPositions = 1;
        PositionList positionList = new PositionList();
        if (acqSettings_.isUsingMultiplePositions()) {
            positionList = studio_.positions().getPositionList();
            nrPositions = positionList.getNumberOfPositions();
            if (nrPositions < 1) {
                studio_.logs().showError("\"Positions\" is checked, but no positions are in position list");
                // TODO: failure => return AcquisitionStatus.FATAL_ERROR;
            }
        }

        AndorCamera camera = (AndorCamera)model_.devices().getDevice("Imaging1Camera");
        CameraModes camMode = camera.getTriggerMode();
        final float cameraReadoutTime = camera.getReadoutTime(camMode);
        final double exposureTime = acqSettings_.getCameraExposure();

        // test acq was here

        double volumeDuration = computeActualVolumeDuration(acqSettings_);
        double timepointDuration = computeTimepointDuration();
        long timepointIntervalMs = Math.round(acqSettings_.getTimepointInterval()*1000);

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

        // setup
        studio_.displays().createDisplay(datastore);
        studio_.displays().manage(datastore);
        Coords.CoordsBuilder cb = Coordinates.builder();

        try {
            core_.clearCircularBuffer();
        } catch (Exception ex) {
            studio_.logs().showError(ex, "Error emptying out the circular buffer");
            return false;
        }

        // TODO: make sure position updater is turned off!

        int nrFrames;   // how many Micro-manager "frames" = time points to take
        int nrRepeats;
        if (acqSettings_.isUsingSeparateTimepoints()) {
            nrFrames = 1;
            nrRepeats = acqSettings_.getNumTimePoints();
        } else {
            nrFrames = acqSettings_.getNumTimePoints();
            nrRepeats = 1;
        }

        double sliceDuration = acqSettings_.getTimingSettings().sliceDuration();
        if (exposureTime + cameraReadoutTime > sliceDuration) {
            // should only possible to mess this up using advanced timing settings
            // or if there are errors in our own calculations
            studio_.logs().showError("Exposure time of " + exposureTime +
                    " is longer than time needed for a line scan with" +
                    " readout time of " + cameraReadoutTime + "\n" +
                    "This will result in dropped frames. " +
                    "Please change input");
            return false;
        }

        final boolean isShutterOpen;
        try {
            isShutterOpen = core_.getShutterOpen();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        boolean shutterOpen = false; // read later
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

        // TODO: make static?
        PLogicDISPIM controller = new PLogicDISPIM(studio_, model_.devices());

        double extraChannelOffset = 0.0;
        controller.prepareControllerForAquisition(acqSettings_, extraChannelOffset);
        final double zStepUm = controller.getActualStepSizeUm();


        // run acq
        boolean nonfatalError = false;
        long acqButtonStart = System.currentTimeMillis();

        String acqName = "";
        // TODO: execute any start-acquisition runnables

        // do not want to return from within this loop => throw exception instead
        // loop is executed once per acquisition (i.e. once if separate viewers isn't selected
        //   or once per time point if separate viewers is selected)
        long repeatStart = System.currentTimeMillis();
        for (int acqNum = 0; !isRunning_.get() && acqNum < nrRepeats; acqNum++) {
            // handle intervals between (software-timed) repeats
            // only applies when doing separate viewers for each timepoint
            // and have multiple timepoints
            long repeatNow = System.currentTimeMillis();
            long repeatdelay = repeatStart + acqNum * timepointIntervalMs - repeatNow;
            while (repeatdelay > 0 && !isRunning_.get()) {
                // TODO: updateAcquisitionStatus(AcquisitionStatus.WAITING, (int) (repeatdelay / 1000));
                long sleepTime = Math.min(1000, repeatdelay);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    // TODO: report error
                    //MyDialogUtils.showError(e, hideErrors);
                }
                repeatNow = System.currentTimeMillis();
                repeatdelay = repeatStart + acqNum * timepointIntervalMs - repeatNow;
            }

            BlockingQueue<TaggedImage> bq = new LinkedBlockingQueue<>(40);  // increased from original 10
            // TODO: MM2 equivalent to => ImageCache imageCache = null; (datastore)

            // try to close last acquisition viewer if there could be one open (only in single acquisition per timepoint mode)
            if (acqSettings_.isUsingSeparateTimepoints() && !isRunning_.get()) {
                try {
                    studio_.displays().closeDisplaysFor(datastore);
                } catch (Exception ex) {
                    // ignore => do nothing on failure
                }
            }

            // TODO: set acq name?

            // TODO: dump errors on ASI hardware (call DU Y and then DU X)

            long extraStageScanTimeout = 0;

            isRunning_.set(true);
            studio_.logs().logMessage("diSPIM plugin starting acquisition with: " + acqSettings_.toPrettyJson());

            // TODO: deal with channels

            // TODO: refreshXYZPositions();

            // get circular buffer ready
            // do once here but not per-trigger; need to ensure ROI changes registered
            try {
                core_.initializeCircularBuffer();
            } catch (Exception e) {
                studio_.logs().logError("Error initializing the circular buffer!");
            }

            try {
                core_.waitForSystem();
            } catch (Exception e) {
                studio_.logs().logError("Error waiting for system!");
            }

            // check if we are raising the SPIM head to the load position between every acquisition
            final boolean raiseSPIMHead = false; // TODO: implement


            // Loop over all the times we trigger the controller's acquisition
            //  (although if multichannel with volume switching is selected there
            //   is inner loop to trigger once per channel)
            // remember acquisition start time for software-timed timepoints
            // For hardware-timed timepoints we only trigger the controller once

            long acqStart = System.currentTimeMillis();
            if (raiseSPIMHead) {
                //ASIdiSPIM.getFrame().getNavigationPanel().raiseSPIMHead();
                //core_.waitForDevice(devices_.getMMDevice(Devices.Keys.UPPERZDRIVE));
            }
            for (int trigNum = 0; trigNum < nrFrames; trigNum++) {
                // handle intervals between (software-timed) time points
                // when we are within the same acquisition
                // (if separate viewer is selected then nothing bad happens here
                // but waiting during interval handled elsewhere)
                long acqNow = System.currentTimeMillis();
                long delay = acqStart + trigNum * timepointIntervalMs - acqNow;
                while (delay > 0 && !isRunning_.get()) {
                    // TODO: updateAcquisitionStatus(AcquisitionStatus.WAITING, (int) (delay / 1000));
                    long sleepTime = Math.min(1000, delay);
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        studio_.logs().logError("error sleeping");
                    }
                    acqNow = System.currentTimeMillis();
                    delay = acqStart + trigNum * timepointIntervalMs - acqNow;
                }

                int timePoint = acqSettings_.isUsingSeparateTimepoints() ? acqNum : trigNum;

                // TODO: execute any start of time point runnables

                // TODO: this is where we autofocus if requested

                numTimePointsDone++;

                // loop over all positions
                for (int positionNum = 0; positionNum < nrPositions; positionNum++) {
                    numPositionsDone = positionNum + 1;
                    // TODO: updateAcquisitionStatus(AcquisitionStatus.ACQUIRING);

                    // TODO: execute any start of position point runnables (different from above)

                    if (acqSettings_.isUsingMultiplePositions()) {
                        // TODO: check for stage scanning

                        final MultiStagePosition nextPosition = positionList.getPosition(positionNum);

                        // move to the next position
                        if (raiseSPIMHead && positionNum == 0) {
                            // TODO: raise diSPIM head
//                            // move to the XY position first
//                            StagePosition posXY = nextPosition.get(devices_.getMMDevice(Devices.Keys.XYSTAGE));
//                            if (posXY != null) {
//                                //core_.setXYPosition(posXY.stageName, posXY.x, posXY.y);
//                                //core_.waitForDevice(devices_.getMMDevice(Devices.Keys.XYSTAGE));
//                            }
//                            // lower SPIM head after we move into position
//                            StagePosition posZ = nextPosition.get(devices_.getMMDevice(Devices.Keys.UPPERZDRIVE));
//                            if (posZ != null) {
//                                //core_.setPosition(posZ.stageName, posZ.x);
//                               //core_.waitForDevice(devices_.getMMDevice(Devices.Keys.UPPERZDRIVE));
//                            }
//                            // handle all remaining stage motion
//                            try {
//                                MultiStagePosition.goToPosition(nextPosition, core_);
//                            } catch (Exception e) {
//                                //throw new RuntimeException(e);
//                            }
                        } else {
                            // blocking call; will wait for stages to move
                            // NB: assume planar correction is handled by marked Z position; this seems better
                            //   than making it impossible for user to select different Z positions e.g. for YZ grid
                            try {
                                MultiStagePosition.goToPosition(nextPosition, core_);
                            } catch (Exception e) {
                                //throw new RuntimeException(e);
                            }
                        }

                        // update local stage positions after move (xPositionUm_, yPositionUm_, zPositionUm_)
                        // TODO: refreshXYZPositions();

                        // TODO: ASI stage scanning

                        final int nrCameras = 2; // TODO: hardcoded to 2 for now
                        final int expectedNrImages = nrSlicesSoftware * nrCameras;
                        int totalImages = 0;

                        int nrOuterLoop = 1;
                        // TODO: can change based on path presets

                        // loop over all the times we trigger the controller for each position
                        // usually just once, but will be the number of channels if we have
                        //  multiple channels and aren't using PLogic to change between them
                        // if we are using path presets then trigger controller for each side and do that in "outer loop"
                        //   (i.e. the channels will run subsequent to each other, then switch sides and run through them again
                        for (int outerLoop = 0; outerLoop < nrOuterLoop; ++outerLoop) {

                            for (int channelNum = 0; channelNum < nrChannelsSoftware; channelNum++) {
                                try {

                                    // deal with shutter before starting acquisition
                                    shutterOpen = core_.getShutterOpen();
                                    if (autoShutter) {
                                        core_.setAutoShutter(false);
                                        if (!shutterOpen) {
                                            core_.setShutterOpen(true);
                                        }
                                    }


                                    studio_.logs().logDebugMessage("Starting time point " + (timePoint+1) + " of " + nrFrames
                                            + " with (software) channel number " + channelNum);

                                    // Wait for first image to create ImageWindow, so that we can be sure about image size
                                    // Do not actually grab first image here, just make sure it is there
                                    long start = System.currentTimeMillis();
                                    long now = start;
                                    final long timeout = Math.max(3000, Math.round(
                                            10*sliceDuration + 2*acqSettings_.getVolumeSettings().delayBeforeView())) + extraStageScanTimeout; //  + extraMultiXYTimeout;
                                    while (core_.getRemainingImageCount() == 0 && (now - start < timeout) && !isRunning_.get()) {
                                        now = System.currentTimeMillis();
                                        Thread.sleep(5);
                                    }
                                    if (now - start >= timeout) {
                                        String msg = "Camera did not send first image within a reasonable time.\n";
                                        studio_.logs().showError(msg);
//                                        if (acqSettings.isStageScanning) {
//                                            msg += "Make sure jumpers are correct on XY card and also micro-micromirror card.";
//                                            ReportingUtils.logMessage("Stage speed is " + props_.getPropValueFloat(Devices.Keys.XYSTAGE, Properties.Keys.STAGESCAN_MOTOR_SPEED_X));
//                                            positions_.getUpdatedPosition(Devices.Keys.XYSTAGE, Directions.X);
//                                        } else {
//                                            msg += "Make sure camera trigger cables are connected properly.";
//                                        }
//                                        throw new Exception(msg);
                                    }

                                    boolean done = false;
                                    long timeout2 = Math.max(1000, Math.round(5*sliceDuration));
                                    totalImages = 0;
                                    start = System.currentTimeMillis();
                                    long last = start;

                                    // TODO: handle multiple cameras
                                    while (core_.getRemainingImageCount() > 0 && core_.isSequenceRunning() && !done) {
                                        now = System.currentTimeMillis();
                                        if (core_.getRemainingImageCount() > 0) {
                                            // an image is ready
                                            TaggedImage taggedImage = core_.popNextTaggedImage();
                                            Image image = studio_.data().convertTaggedImage(taggedImage, builder.time(numTimePointsDone).build(), null);
                                            datastore.putImage(image);
                                            totalImages++;
                                        } else {
                                            // image not ready yet
                                            done = isRunning_.get();
                                            Thread.sleep(1);
                                            if (now - last >= timeout2) {
                                                studio_.logs().logError("Camera did not send all expected images within" +
                                                        " a reasonable period for timepoint " + numTimePointsDone + " and "
                                                        + "position " + numPositionsDone + " .  Continuing anyway.");
                                                done = true;
                                            }

                                        }
                                    }

                                } catch (Exception e) {

                                } finally {
                                    // cleanup at the end of each time we trigger the controller

                                    // put shutter back to original state
                                    core_.setAutoShutter(autoShutter);
                                    try {
                                        core_.setShutterOpen(shutterOpen);
                                    } catch (Exception e) {
                                        studio_.logs().logError("could not set the shutter open state!");
                                    }

                                    // make sure SPIM state machine on micromirror and SCAN of XY card are stopped (should normally be but sanity check)
                                    stopSPIMStateMachines(acqSettings_);

                                    // write message with final results of our counters for debugging
                                    String msg = "Finished acquisition (controller trigger cycle) with counters at Channels: ";
//                                    for (int i=0; i<4*acqSettings.numChannels; ++i) {
//                                        msg += channelImageNr[i] + ", ";
//                                    }
//                                    msg += ",   Cameras: ";
//                                    for (int i=0; i<4; ++i) {
//                                        msg += cameraImageNr[i] + ", ";
//                                    }
//                                    msg += ",   TimePoints: ";
//                                    for (int i=0; i<2*acqSettings.numChannels; ++i) {
//                                        msg += tpNumber[i] + ", ";
//                                    }
                                    studio_.logs().logMessage(msg);

                                }
                            }
                        }

                        // TODO: execute any end-position runnables
                    }
                }
            }
        }

        // cleanup
        studio_.logs().logMessage("diSPIM plugin acquisition " + //+ acqName_ +
                " took: " + (System.currentTimeMillis() - acqButtonStart) + "ms");

        // prevent acquisition data from being modified
        try {
            datastore.freeze();
        } catch (IOException e) {
            studio_.logs().logError("could not freeze the datastore!");
        }

        // clean up controller settings after acquisition
        // want to do this, even with demo cameras, so we can test everything else
        // TODO: figure out if we really want to return piezos to 0 position (maybe center position,
        //   maybe not at all since we move when we switch to setup tab, something else??)
        controller.cleanUpControllerAfterAcquisition(acqSettings_, true);

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
        if (acqSettings.getTimingSettings().useAdvancedTiming()) {
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
                final float shutterWidth = (float) acqSettings_.getSliceSettings().shutterWidth();
                final float shutterSpeed = (float) acqSettings_.getSliceSettings().shutterSpeedFactor();
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
                final float scanSettle = (float) acqSettings_.getSliceSettings().scanSettleTime();
                final float scanReset = (float) acqSettings_.getSliceSettings().scanResetTime();
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
    private double computeTimepointDuration() {
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

    public DefaultSliceSettings.Builder getSliceSettingsBuilder() {
        return ssb_;
    }

    public AcquisitionSettings getAcquisitionSettings() {
        return acqSettings_;
    }
}
