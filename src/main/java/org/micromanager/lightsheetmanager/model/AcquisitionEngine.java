package org.micromanager.lightsheetmanager.model;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import mmcorej.CMMCore;
import mmcorej.TaggedImage;
import mmcorej.org.json.JSONArray;
import mmcorej.org.json.JSONException;
import mmcorej.org.json.JSONObject;
import org.micromanager.PositionList;
import org.micromanager.Studio;
import org.micromanager.acqj.api.AcquisitionHook;
import org.micromanager.acqj.main.Acquisition;
import org.micromanager.acqj.main.AcquisitionEvent;
import org.micromanager.acquisition.internal.MMAcquisition;
//import org.micromanager.acquisition.internal.MMAcquistionControlCallbacks;
//import org.micromanager.acquisition.internal.acqengjcompat.AcqEngJMDADataSink;
import org.micromanager.acquisition.internal.MMAcquistionControlCallbacks;
import org.micromanager.acquisition.internal.acqengjcompat.AcqEngJMDADataSink;
import org.micromanager.acquisition.internal.acqengjcompat.speedtest.SpeedTest;
import org.micromanager.data.DataProvider;
import org.micromanager.data.Datastore;
import org.micromanager.data.Pipeline;
import org.micromanager.data.internal.DefaultDatastore;
import org.micromanager.data.internal.DefaultSummaryMetadata;
import org.micromanager.data.internal.PropertyKey;
import org.micromanager.data.internal.ndtiff.NDTiffAdapter;
import org.micromanager.internal.MMStudio;
import org.micromanager.lightsheetmanager.api.AcquisitionManager;
import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.model.data.AcquisitionModes;
import org.micromanager.lightsheetmanager.model.data.MultiChannelModes;
import org.micromanager.lightsheetmanager.model.devices.cameras.AndorCamera;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;
import org.micromanager.lightsheetmanager.model.utils.MyNumberUtils;

import java.util.Objects;

public class AcquisitionEngine implements AcquisitionManager, MMAcquistionControlCallbacks {

    private final Studio studio_;
    private final CMMCore core_;

    private DefaultAcquisitionSettingsDISPIM.Builder asb_;
    private DefaultAcquisitionSettingsDISPIM acqSettings_;

    private DataStorage data_;

    private ExecutorService acquistitonExecutor_ = Executors.newSingleThreadExecutor(
            r -> new Thread(r, "Acquisition Thread"));
    private LightSheetManagerModel model_;
    private volatile Acquisition currentAcquisition_ = null;

    private Datastore curStore_;
    private Pipeline curPipeline_;
    private long nextWakeTime_ = -1;

    public AcquisitionEngine(final Studio studio, final LightSheetManagerModel model) {
        studio_ = Objects.requireNonNull(studio);
        model_ = Objects.requireNonNull(model);
        core_ = studio_.core();

        data_ = new DataStorage(studio_);

        // default settings
        asb_ = new DefaultAcquisitionSettingsDISPIM.Builder();
        acqSettings_ = asb_.build();
    }

    /**
     * Sets the acquisition settings and populates the acquisition settings builder with its values.
     *
     * @param acqSettings the acquisition settings to set
     */
    public void setAcquisitionSettings(final DefaultAcquisitionSettingsDISPIM acqSettings) {
        asb_ = new DefaultAcquisitionSettingsDISPIM.Builder(acqSettings);
        acqSettings_ = acqSettings;
    }

    @Override
    public Future requestRun(boolean speedTest) {
        // Run on a new thread, so it doesn't block the EDT
        Future acqFinished = acquistitonExecutor_.submit(() -> {
            if (currentAcquisition_ != null) {
                studio_.logs().showError("Acquisition is already running.");
                return;
            }

            // make sure AcquisitionSettings are up-to-date
            acqSettings_ = asb_.build();

            if (speedTest) {
                try {
                    SpeedTest.runSpeedTest(acqSettings_.saveDirectory(), acqSettings_.saveNamePrefix(),
                            core_, acqSettings_.numTimePoints(), true);
                } catch (Exception e) {
                    studio_.logs().showError(e);
                }
            } else {
                // Every one of these modality-specific functions should block until the
                // acquisition is complete
                GeometryType geometryType =
                      model_.devices().getDeviceAdapter().getMicroscopeGeometry();
                switch (geometryType) {
                    case DISPIM:
                        runAcquisitionDISPIM();
                        break;
                    case SCAPE:
                        runAcquisitionSCAPE();
                        break;
                    default:
                        studio_.logs().showError(
                              "Acquisition Engine is not implemented for " + geometryType);
                        break;
                }
            }
        });
        return acqFinished;
    }

    @Override
    public void requestStop() {
        if (currentAcquisition_ == null || currentAcquisition_.areEventsFinished()) {
            studio_.logs().showError("Acquisition is not running.");
            return;
        }
        currentAcquisition_.abort();
    }

    @Override
    public void requestPause() {
        if (currentAcquisition_ == null) {
            studio_.logs().showError("Acquisition is not running.");
        } else {
            currentAcquisition_.setPaused(true);
        }
    }

    @Override
    public void requestResume() {
        if (currentAcquisition_ != null) {
            if (currentAcquisition_.isPaused()) {
                currentAcquisition_.setPaused(false);
            }
        }
    }

    @Override
    public boolean isRunning() {
        return currentAcquisition_ != null && !currentAcquisition_.areEventsFinished();
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
        System.out.println(acqSettings_.timingSettings());


        // TODO: was only checked in light sheet mode
//        if (core_.getPixelSizeUm() < 1e-6) {
//            studio_.logs().showError("Need to set the pixel size in Micro-Manager.");
//        }

        // setup channels
        int nrChannelsSoftware = acqSettings_.numChannels();  // how many times we trigger the controller per stack
        int nrSlicesSoftware = acqSettings_.volumeSettings().slicesPerView();
        // TODO: channels



        AndorCamera camera = model_.devices().getDevice("Imaging1Camera");
        CameraModes camMode = camera.getTriggerMode();
        final float cameraReadoutTime = camera.getReadoutTime(camMode);
        final double exposureTime = 1.0f; // TODO: camera exposure? acqSettings_.getCameraExposure();

        // test acq was here

        double volumeDuration = computeActualVolumeDuration(acqSettings_);
        double timepointDuration = computeTimePointDuration();
        long timepointIntervalMs = Math.round(acqSettings_.timePointInterval()*1000);

        // use hardware timing if < 1 second between time points
        // experimentally need ~0.5 sec to set up acquisition, this gives a bit of cushion
        // cannot do this in getCurrentAcquisitionSettings because of mutually recursive
        // call with computeActualVolumeDuration()
        if (acqSettings_.isUsingTimePoints()
              && acqSettings_.numTimePoints() > 1
              && timepointIntervalMs < (timepointDuration + 750)
              && !acqSettings_.isUsingStageScanning()) {
           // acqSettings_.useHardwareTimesPoints(true);
            asb_.useHardwareTimePoints(true);
        }

        if (acqSettings_.isUsingMultiplePositions()) {
            if ((acqSettings_.isUsingHardwareTimePoints()
                  || acqSettings_.numTimePoints() > 1)
                  && (timepointIntervalMs < timepointDuration*1.2)) {
                //acqSettings_.setHardwareTimesPoints(false);
                asb_.useHardwareTimePoints(false);
                // TODO: WARNING
            }
        }


        // TODO: make sure position updater is turned off!



        double sliceDuration = acqSettings_.timingSettings().sliceDuration();
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

    /**
     * Higher level stuff in MM may depend on many hidden, poorly documented
     * ways on summary metadata generated by the acquisition engine.
     * This function adds in its fields in order to achieve compatibility.
     */
    private void addMMSummaryMetadata(JSONObject summaryMetadata) {
        try {
            // These are the ones from the clojure engine that may yet need to be translated
            //        "Channels" -> {Long@25854} 2

            summaryMetadata.put(PropertyKey.CHANNEL_GROUP.key(), acqSettings_.channelGroup());
            JSONArray chNames = new JSONArray();
            JSONArray chColors = new JSONArray();
            if (acqSettings_.isUsingChannels() && acqSettings_.channels().length > 0) {
                for (ChannelSpec c : acqSettings_.channels()) {
                    chNames.put(c.getName());
//                chColors.put(c.getRGB());
                }
            } else {
                chNames.put("Default");
            }
            summaryMetadata.put(PropertyKey.CHANNEL_NAMES.key(), chNames);
            summaryMetadata.put(PropertyKey.CHANNEL_COLORS.key(), chColors);

            // MM MDA acquisitions have a defined number of
            // frames/slices/channels/positions at the outset
            summaryMetadata.put(PropertyKey.FRAMES.key(), acqSettings_.isUsingTimePoints() ?
                  acqSettings_.numTimePoints() : 1);
            summaryMetadata
                  .put(PropertyKey.SLICES.key(), acqSettings_.volumeSettings().slicesPerView());
            summaryMetadata.put(PropertyKey.CHANNELS.key(), acqSettings_.isUsingChannels() ?
                  acqSettings_.channels().length : 1);
            summaryMetadata
                  .put(PropertyKey.POSITIONS.key(), acqSettings_.isUsingMultiplePositions() ?
                        studio_.positions().getPositionList().getNumberOfPositions() : 1);

            // MM MDA acquisitions have a defined order
            summaryMetadata.put(PropertyKey.SLICES_FIRST.key(),
                  true); // currently only channel, slice ordering
            summaryMetadata.put(PropertyKey.TIME_FIRST.key(),
                  false); // currently only position, time ordering

            DefaultSummaryMetadata dsmd = new DefaultSummaryMetadata.Builder().build();
            summaryMetadata.put(PropertyKey.MICRO_MANAGER_VERSION.key(),
                  dsmd.getMicroManagerVersion());
        } catch (JSONException e) {
            studio_.logs().logError(e);
            throw new RuntimeException(e);
        }
    }

    private void runAcquisitionDISPIM() {

        // create the datastore
//        final String saveDirectory = model_.acquisitions().getAcquisitionSettings().getSaveDirectory();
//        final String saveNamePrefix = model_.acquisitions().getAcquisitionSettings().getSaveNamePrefix();
//        data_.createDatastore(saveDirectory + File.separator + saveNamePrefix);
//        System.out.println("getDatastoreSavePath: " + data_.getDatastoreSavePath());

        final boolean isLiveModeOn = studio_.live().isLiveModeOn();
        if (isLiveModeOn) {
            studio_.live().setLiveModeOn(false);
            // close the live mode window if it exists
            if (studio_.live().getDisplay() != null) {
                studio_.live().getDisplay().close();
            }
        }

        PLogicDISPIM controller = null;
        controller = doHardwareCalculations();

        String saveDir = acqSettings_.saveDirectory();
        String saveName = acqSettings_.saveNamePrefix();

        // TODO: where is the Datastore type listed in the acq settings that is present
        //  on the LSM GUI? For now, just use ND_TIFF
        DefaultDatastore.setPreferredSaveMode(studio_, Datastore.SaveMode.ND_TIFF);

        DefaultDatastore result = new DefaultDatastore(studio_);
        try {
            result.setStorage(new NDTiffAdapter(result, saveDir, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        studio_.displays().createDisplay(result);


        //////////////////////////////////////
        // Begin AcqEngJ integration
        //      The acqSettings object should be static at this point, it will now
        //      be parsed and used to create acquisition events, each of which
        //      will "order" the acquisition of 1 image (per each camera)
        //////////////////////////////////////
        // Create acquisition
       AcqEngJMDADataSink sink = new AcqEngJMDADataSink(studio_.events());

       currentAcquisition_ = new Acquisition(sink);

        JSONObject summaryMetadata = currentAcquisition_.getSummaryMetadata();
        addMMSummaryMetadata(summaryMetadata);

        // MMAcquisition
        MMAcquisition acq = new MMAcquisition(studio_,
              saveDir, saveName, summaryMetadata,
              this, true);
        curStore_ = acq.getDatastore();
        curPipeline_ = acq.getPipeline();
        sink.setDatastore(curStore_);
        sink.setPipeline(curPipeline_);

        studio_.events().registerForEvents(this);
        // commented because this is prob specific to MM MDAs
//        studio_.events().post(new DefaultAcquisitionStartedEvent(curStore_, this,
//              acquisitionSettings));

        // Start pumping images through the pipeline and into the datastore.

        // TODO if position time ordering ever implemented, this should be reactivated and the
        //  timelapse hook copied from org.micromanager.acquisition.internal.acqengjcompat.AcqEngJAdapter
//        if (sequenceSettings_.acqOrderMode() == AcqOrderMode.POS_TIME_CHANNEL_SLICE
//              || sequenceSettings_.acqOrderMode() == AcqOrderMode.POS_TIME_SLICE_CHANNEL) {
//            // Pos_time ordered acquisistion need their timelapse minimum start time to be
//            // adjusted for each position.  The only place to do that seems to be a hardware hook.
//            currentAcquisition_.addHook(timeLapseHook(acquisitionSettings),
//                  AcquisitionAPI.BEFORE_HARDWARE_HOOK);
//        }



        long acqButtonStart = System.currentTimeMillis();


        ////////////  Acquisition hooks ////////////////////
        // These functions will be run on different threads during the acquisition process
        //    Hooks will run on the Acquisition Engine thread--the one that controls all hardware

        // TODO add any code that needs to be executed on the acquisition thread (i.e. the one
        //  that controls hardware)

        // TODO: autofocus
        currentAcquisition_.addHook(new AcquisitionHook() {
            @Override
            public AcquisitionEvent run(AcquisitionEvent event) {
                // TODO: does the Tiger controller need to be cleared and/or checked for errors here?

                if (event.isAcquisitionFinishedEvent()) {
                    // Acquisition is finished, pass along event so things shut down properly
                    return event;
                }

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
        currentAcquisition_.addHook(new AcquisitionHook() {
            @Override
            public AcquisitionEvent run(AcquisitionEvent event) {
                // TODO: Cameras are now ready to receive triggers, so we can send (software) trigger
                //  to the tiger to tell it to start outputting TTLs

                return event;
            }

            @Override
            public void close() {

            }
        }, Acquisition.AFTER_CAMERA_HOOK);


        ///////////// Event monitor /////////////////////////
        //    The event monitor will be run on the thread that gets successive acquisition
        //    events from the lazy sequences produced by Iterator<AcquisitionEvent> objects created
        //    below. This can be used to keep tabs on the acquisition process. The monitor
        //    will not fire until the AcquisitionEvent is supplied by the Iterator in preparation
        //    for the hardware being set up to execute it.
        Function<AcquisitionEvent, AcquisitionEvent> eventMonitor = acquisitionEvent -> {
            // TODO: this will execute as each acquisition event is generated. Add code here
            //  can be used, for example, to provide status updates to the GUI about what is
            //  happening, by reading the context of each acquisition event
            if (acquisitionEvent.getMinimumStartTimeAbsolute() != null) {
                nextWakeTime_ = acquisitionEvent.getMinimumStartTimeAbsolute();
            }

            return acquisitionEvent;
        };


        ///////////// Turn off autoshutter /////////////////
        final boolean isShutterOpen;
        try {
            isShutterOpen = core_.getShutterOpen();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // TODO: should the shutter be left open for the full duration of acquisition?
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

        currentAcquisition_.start();

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
            AcquisitionEvent baseEvent = new AcquisitionEvent(currentAcquisition_);
            if (acqSettings_.isUsingMultiplePositions()) {
                baseEvent.setAxisPosition(LSMAcquisitionEvents.POSITION_AXIS, positionIndex);
            }
            // TODO: what to do if multiple positions not defined: acquire at current stage position?
            //  If yes, then nothing more to do here.

            if (acqSettings_.isUsingHardwareTimePoints()) {
                // create a full iterator of TCZ acquisition events, and Tiger controller
                // will handle everything else
                if (acqSettings_.isUsingChannels()) {
                    currentAcquisition_.submitEventIterator(
                          LSMAcquisitionEvents.createTimelapseMultiChannelVolumeAcqEvents(
                                baseEvent.copy(), acqSettings_, eventMonitor));
                } else {
                    currentAcquisition_.submitEventIterator(
                          LSMAcquisitionEvents.createTimelapseVolumeAcqEvents(
                                baseEvent.copy(), acqSettings_, eventMonitor));
                }
            } else {
                // Loop 2: Multiple time points
                for (int timeIndex = 0; timeIndex < (acqSettings_.isUsingTimePoints() ?
                      acqSettings_.numTimePoints() : 1); timeIndex++) {
                    baseEvent.setTimeIndex(timeIndex);
                    // Loop 3: Channels; Loop 4: Z slices (non-interleaved)
                    // Loop 3: Channels; Loop 4: Z slices (interleaved)
                    if (acqSettings_.isUsingChannels()) {
                        currentAcquisition_.submitEventIterator(
                              LSMAcquisitionEvents.createMultiChannelVolumeAcqEvents(
                                    baseEvent.copy(), acqSettings_, eventMonitor,
                                    acqSettings_.acquisitionMode() ==
                                          AcquisitionModes.STAGE_SCAN_INTERLEAVED));
                    } else {
                        currentAcquisition_.submitEventIterator(
                              LSMAcquisitionEvents.createVolumeAcqEvents(
                                    baseEvent.copy(), acqSettings_, eventMonitor));
                    }
                }
            }
        }



        // No more instructions (i.e. AcquisitionEvents); tell the acquisition to initiate shutdown
        // once everything finishes
        currentAcquisition_.finish();


        currentAcquisition_.waitForCompletion();

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

        // Check if acquisition ended due to an exception and show error to user if it did
        try {
            currentAcquisition_.checkForExceptions();
        } catch (Exception e){
            studio_.logs().showError(e);
        }

        // TODO: execute any end-acquisition runnables

        currentAcquisition_ = null;
    }

    private void stopSPIMStateMachines(DefaultAcquisitionSettingsDISPIM acqSettings) {
        final int numViews = acqSettings_.volumeSettings().numViews();
        if (numViews == 1) {
            ASIScanner scanner = model_.devices().getDevice("IllumBeam");
            scanner.setSPIMState(ASIScanner.SPIMState.IDLE);
        }
        for (int i = 1; i <= numViews; i++) {
            ASIScanner scanner = model_.devices().getDevice("Illum" + i + "Beam");
            scanner.setSPIMState(ASIScanner.SPIMState.IDLE);
        }
        // TODO: ASI stage scanning conditionals
    }

    public void recalculateSliceTiming(DefaultAcquisitionSettingsDISPIM acqSettings) {
        // don't change timing settings if user is using advanced timing
        if (acqSettings.isUsingAdvancedTiming()) {
            return;
        }
        // TODO: update builder here
        //acqSettings.timingSettings(getTimingFromPeriodAndLightExposure(acqSettings));
        // TODO: update gui (but not in the model)
    }

    // TODO: only using acqSettings for cameraExposure which maybe should not exist?
    public DefaultTimingSettings getTimingFromPeriodAndLightExposure(DefaultAcquisitionSettingsDISPIM acqSettings) {
        // uses algorithm Jon worked out in Octave code; each slice period goes like this:
        // 1. camera readout time (none if in overlap mode, 0.25ms in pseudo-overlap)
        // 2. any extra delay time
        // 3. camera reset time
        // 4. start scan 0.25ms before camera global exposure and shifted up in time to account for delay introduced by Bessel filter
        // 5. turn on laser as soon as camera global exposure, leave laser on for desired light exposure time
        // 7. end camera exposure in final 0.25ms, post-filter scan waveform also ends now
        ASIScanner scanner1 = model_.devices().getDevice("Illum1Beam");
        ASIScanner scanner2 = model_.devices().getDevice("Illum2Beam");

        AndorCamera camera = model_.devices().getDevice("Imaging1Camera"); //.getImagingCamera(0);
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

        float laserDuration = 1.0f; // TODO: camera exposure? //MyNumberUtils.roundToQuarterMs((float)acqSettings.getCameraExposure());
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

        switch (acqSettings_.cameraMode()) {
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
                final float shutterWidth = (float) acqSettings_.sliceSettingsLS().shutterWidth();
                final float shutterSpeed = (float) acqSettings_.sliceSettingsLS().shutterSpeedFactor();
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
                final float scanSettle = (float) acqSettings_.sliceSettingsLS().scanSettleTime();
                final float scanReset = (float) acqSettings_.sliceSettingsLS().scanResetTime();
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
    public double computeActualVolumeDuration(final DefaultAcquisitionSettingsDISPIM acqSettings) {
        final MultiChannelModes channelMode = acqSettings.channelMode();
        final int numChannels = acqSettings.numChannels();
        final int numViews = acqSettings.volumeSettings().numViews();
        final double delayBeforeSide = acqSettings.volumeSettings().delayBeforeView();
        int numCameraTriggers = acqSettings.volumeSettings().slicesPerView();
        if (acqSettings.cameraMode() == CameraModes.OVERLAP) {
            numCameraTriggers += 1;
        }
        // stackDuration is per-side, per-channel, per-position

        final double stackDuration = numCameraTriggers * acqSettings.timingSettings().sliceDuration();
        if (acqSettings.isUsingStageScanning()) { // || acqSettings.isStageStepping) {
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
                    (volumeDuration + 1500 + acqSettings_.postMoveDelay());
        }
        return volumeDuration;
    }

    public void updateAcquisitionStatus() {

    }


    public DefaultAcquisitionSettingsDISPIM getAcquisitionSettings() {
        return acqSettings_;
    }

    public DefaultAcquisitionSettingsDISPIM.Builder getAcquisitionSettingsBuilder() {
        return asb_;
    }


//////////////////////// AcquisitionControl Callback methods ////////////////////////
    @Override
    public void stop(boolean interrupted) {
        // unclear that this parameter is used in other code
        if (currentAcquisition_ != null) {
            currentAcquisition_.abort();
        }
    }

    @Override
    public boolean isAcquisitionRunning() {
        return currentAcquisition_ != null && !currentAcquisition_.areEventsFinished();
    }

    @Override
    public double getFrameIntervalMs() {
        return acqSettings_.timePointInterval();
    }

    @Override
    public long getNextWakeTime() {
        return nextWakeTime_;
    }

    @Override
    public boolean isPaused() {
        if (currentAcquisition_ != null) {
            return currentAcquisition_.isPaused();
        }
        return false;
    }

    @Override
    public void setPause(boolean b) {
        if (currentAcquisition_ != null) {
            currentAcquisition_.setPaused(b);
        }
    }

    @Override
    public boolean abortRequest() {
        if (currentAcquisition_ != null) {
            currentAcquisition_.abort();
        }
        return true;
    }

    @Override
    public DataProvider getAcquisitionDatastore() {
        return curStore_;
    }
}
