package org.micromanager.lightsheetmanager.model;

import mmcorej.CMMCore;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.model.data.AcquisitionModes;
import org.micromanager.lightsheetmanager.model.data.MultiChannelModes;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPLogic;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPiezo;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIXYStage;
import org.micromanager.lightsheetmanager.model.devices.vendor.SingleAxis;

import java.awt.geom.Point2D;
import java.util.Objects;

// Replacement for ControllerUtils.java

public class PLogicDISPIM {

    private Studio studio_;
    private CMMCore core_;
    private ASIPLogic plc_;

    // TODO: needed for case of 2 plcs?
    private ASIPLogic plc2_;

    private DeviceManager devices_;
    double scanDistance_;   // in microns; cached value from last call to prepareControllerForAcquisition()
    double actualStepSizeUm_;  // cached value from last call to prepareControllerForAcquisition()
    boolean zSpeedZero_;  // cached value from last call to prepareStageScanForAcquisition()
    String lastDistanceStr_;  // cached value from last call to prepareControllerForAcquisition()
    String lastPosStr_;       // cached value from last call to prepareControllerForAcquisition()
    double supOrigSpeed_;

    final int triggerStepDurationTics = 10;  // 2.5ms with 0.25ms tics
    final int acquisitionFlagAddr = 1;
    final int counterLSBAddr = 3;
    final int counterMSBAddr = 4;
    final int triggerStepEdgeAddr = 6;
    final int triggerStepPulseAddr = 7;
    final int triggerStepOutputAddr = 40;  // BNC #8
    final int triggerInAddr = 35;  // BNC #3
    final int triggerSPIMAddr = 46;  // backplane signal, same as XY card's TTL output
    final int laserTriggerAddress = 10;  // this should be set to (42 || 8) = (TTL1 || manual laser on)

    public PLogicDISPIM(final Studio studio, final DeviceManager devices) {
        studio_ = Objects.requireNonNull(studio);
        core_ = studio_.core();
        devices_ = devices;

        scanDistance_ = 0;
        actualStepSizeUm_ = 0;
        zSpeedZero_ = true;
        lastDistanceStr_ = "";
        lastPosStr_ = "";
    }

    //ASIPiezo piezo1 = (ASIPiezo)devices_.getDevice("Imaging1Focus");

    public boolean prepareControllerForAquisition(final AcquisitionSettings settings, final double channelOffset) {
        // turn off beam and scan on both sides (they are turned off by SPIM state machine anyway)
        // also ensures that properties match reality at end of acquisition
        // SPIM state machine restores position of beam at end of SPIM state machine, now it
        // will be restored to blanking position
        ASIPLogic plc = (ASIPLogic) devices_.getDevice("TriggerCamera");
        ASIPLogic plc_laser = (ASIPLogic) devices_.getDevice("TriggerLaser");
        ASIScanner scanner1 = (ASIScanner) devices_.getDevice("Illum1Beam");
        ASIScanner scanner2 = (ASIScanner) devices_.getDevice("Illum2Beam");

        scanner1.setBeamOn(false);
        scanner2.setBeamOn(false);
        scanner1.sa().setModeX(SingleAxis.Mode.DISABLED);
        scanner2.sa().setModeX(SingleAxis.Mode.DISABLED);

        final int numViews = settings.getVolumeSettings().numViews();
        final int firstView = settings.getVolumeSettings().firstView();

        // set up controller with appropriate SPIM parameters for each active side
        // some of these things only need to be done once if the same micro-mirror
        //   card is used (as is typical) but keeping code universal to handle
        //   case where MM devices reside on different controller cards
        if (numViews > 1 || firstView == 0) {
            // prepareControllerForAcquisitionSide();
            boolean success = true;
            if (!success) {
                return false;
            }
        }

        if (numViews > 1 || !(firstView == 0)) { // TODO: just do firstView != 0 ???
            // prepareControllerForAcquisitionSide();
            boolean success = true;
            if (!success) {
                return false;
            }
        }

        if (settings.isStageScanning() && settings.getSPIMMode() == AcquisitionModes.STAGE_SCAN_INTERLEAVED) {
            if (numViews != 2) {
                // TODO: error "Interleaved stage scan only possible for 2-sided acquisition."
                return false;
            }
            if (settings.getCameraMode() == CameraModes.OVERLAP) {
                // TODO: error "Interleaved stage scan not compatible with overlap camera mode"
                return false;
            }
        }

        // make sure set to use TTL signal from backplane in case PLOGIC_LASER is set to PLogicMode different from diSPIM shutter
        plc.setPreset(12);
        plc_laser.setPreset(12);

        // make sure shutter is set to the PLOGIC_LASER device
        try {
            core_.setShutterDevice(plc_laser.getDeviceName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (settings.isStageScanning()) {
            // scanning with ASI stage
            // algorithm is as follows:
            // use the # of slices and slice spacing that the user specifies
            // because the XY stage is 45 degrees from the objectives have to move it sqrt(2) * slice step size
            // for now use the current X position as the start of acquisition and always start in positive X direction
            // for now always do serpentine scan with 2 passes at the same Y location, one pass each direction over the sample
            // => total scan distance = # slices * slice step size * sqrt(2)
            //    scan start position = current X position
            //    scan stop position = scan start position + total distance
            //    slow axis start = current Y position
            //    slow axis stop = current Y position
            //    X motor speed = slice step size * sqrt(2) / slice duration
            //    number of scans = number of sides (1 or 2)
            //    scan mode = serpentine for 2-sided non-interleaved, raster otherwise (need to revisit for 2D stage scanning)
            //    X acceleration time = use whatever current setting is
            //    scan settling time = delay before side

            double actualMotorSpeed;
            ASIXYStage xyStage = (ASIXYStage) devices_.getDevice("SampleXY");

            // figure out the speed we should be going according to slice period, slice spacing, geometry, etc.
            final double requestedMotorSpeed = computeScanSpeed(settings, scanner1.getSPIMNumScansPerSlice());  // in mm/sec

            final boolean isInterleaved = (settings.getSPIMMode() == AcquisitionModes.STAGE_SCAN_INTERLEAVED);

            final float maxSpeed = xyStage.getMaxSpeedX();
            if (requestedMotorSpeed > maxSpeed * 0.8) {  // trying to go near max speed smooth scanning will be compromised
                // TODO: error
                //MyDialogUtils.showError("Required stage speed is too fast, please reduce step size or increase sample exposure.");
                return false;
            }
            if (requestedMotorSpeed < maxSpeed / 2000) {  // 1/2000 of the max speed is approximate place where smooth scanning breaks down (speed quantum is ~1/12000 max speed); this also prevents setting to 0 which the controller rejects
                // TODO: error
                //MyDialogUtils.showError("Required stage speed is too slow, please increase step size or decrease sample exposure.");
                return false;
            }
            xyStage.setSpeedX((float) requestedMotorSpeed);

            // ask for the actual speed to calculate the actual step size
            actualMotorSpeed = xyStage.getSpeedXUm() / 1000;

            // set the acceleration to a reasonable value for the (usually very slow) scan speed
            xyStage.setAccelerationX((float) computeScanAcceleration(actualMotorSpeed,
                    xyStage.getMaxSpeedX(), settings.getScanSettings().getAccelFactor()));

            int numLines = settings.getVolumeSettings().numViews();
            if (isInterleaved) {
                numLines = 1;  // assure in acquisition code that we can't have single-sided interleaved
            }
            numLines *= ((double) settings.getNumChannels() / computeScanChannelsPerPass(settings));
            xyStage.setScanNumLines(numLines);

            final boolean isStageScan2Sided = (settings.getSPIMMode() == AcquisitionModes.STAGE_SCAN) && settings.getVolumeSettings().numViews() == 2;
            xyStage.setScanPattern(isStageScan2Sided ? ASIXYStage.ScanPattern.SERPENTINE : ASIXYStage.ScanPattern.RASTER);

            if (xyStage.getAxisPolarityX() != ASIXYStage.AxisPolarity.NORMAL) {
                // TODO: error "Stage scanning requires X axis polarity set to normal"
                return false;
            }

            if (settings.isUsingMultiplePositions()) {
                // use current position as center position for stage scanning
                // multi-position situation is handled in position-switching code instead
                Point2D.Double p = xyStage.getXYPosition();
                // TODO: error if getXYPosition fails (return null?)
                // TODO: prepareStageScanForAcquisition(p.x, p.y, settings.getSPIMMode())
                prepareStageScanForAcquisition(p.x, p.y, settings);
            }

        } else {
            scanDistance_ = 0;
        }

        // sets PLogic "acquisition running" flag
        plc.setPreset(3);
        plc_laser.setPreset(3);

//        ReportingUtils.logMessage("Finished preparing controller for acquisition with offset " + channelOffset +
//                " with mode " + settings.spimMode.toString() + " and settings " + settings.toString());

        return true;
    }

    // Compute appropriate motor speed in mm/s for the given stage scanning settings
    public double computeScanSpeed(AcquisitionSettings settings, final int numScansPerSlice) {
        //double sliceDuration = settings.sliceTiming.sliceDuration;
        //double sliceDuration = 0.0; // TODO: get from SliceTiming
        double sliceDuration = getSliceDuration(settings.getTimingSettings(), numScansPerSlice);
        if (settings.getSPIMMode() == AcquisitionModes.STAGE_SCAN_INTERLEAVED) {
            // pretend like our slice takes twice as long so that we move the correct speed
            // this has the effect of halving the motor speed
            // but keeping the scan distance the same
            sliceDuration *= 2;
        }
        final int channelsPerPass = computeScanChannelsPerPass(settings);

        //return settings.getStepSize() * du.getStageGeometricSpeedFactor(settings.firstSideIsA) / sliceDuration / channelsPerPass;
        return settings.getVolumeSettings().sliceStepSize();
    }

    // compute how many channels we do in each one-way scan
    private int computeScanChannelsPerPass(AcquisitionSettings settings) {
        return settings.getChannelMode() == MultiChannelModes.SLICE_HW ? settings.getNumChannels() : 1;
    }

    // TODO: scanNum was part of SliceSettings (now TimingSettings)
    // scanNum was populated from numScansPerSlice_ which is the scanner SPIM_NUM_SCANSPERSLICE("SPIMNumScansPerSlice")
    // labeled "Lines scans per slice:" in advanced timing tab
    //    * gets the correct value for the slice timing's sliceDuration field based on other values of slice timing

    // slice duration is the max out of the scan time, laser time, and camera time
    public double getSliceDuration(final DefaultTimingSettings s, final int scanNum) {
        return Math.max(Math.max(
                        s.delayBeforeScan() +
                                (s.scanDuration() * scanNum),     // scan time
                        s.delayBeforeLaser() + s.laserTriggerDuration()  // laser time
                ),
                s.delayBeforeCamera() + s.cameraTriggerDuration() // camera time
        );
    }

    /**
     * Compute appropriate acceleration time in ms for the specified motor speed.
     * Set to be 10ms + 0-100ms depending on relative speed to max, all scaled by factor specified on the settings panel
     *
     * @param motorSpeed
     * @return
     */
    public double computeScanAcceleration(final double motorSpeed, final double maxMotorSpeed, final double stageScanAccelFactor) {
//        final double maxMotorSpeed = props_.getPropValueFloat(Devices.Keys.XYSTAGE, Properties.Keys.STAGESCAN_MAX_MOTOR_SPEED_X);
//        final double accelFactor = props_.getPropValueFloat(Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_STAGESCAN_ACCEL_FACTOR);
//        return (10 + 100 * (motorSpeed / maxMotorSpeed) ) * accelFactor;
        return (10 + 100 * (motorSpeed / maxMotorSpeed)) * stageScanAccelFactor;
    }

    public boolean prepareStageScanForAcquisition(final double x, final double y, AcquisitionSettings settings) {
        final boolean scanFromStart = settings.getScanSettings().scanFromStartPosition();
        final boolean scanNegative = settings.getScanSettings().scanNegativeDirection();
        ASIXYStage xyStage = (ASIXYStage) devices_.getDevice("SampleXY");
        double xStartUm;
        double xStopUm;
        if (scanFromStart) {
            if (scanNegative) {
                xStartUm = x;
                xStopUm = x - scanDistance_;
            } else {
                xStartUm = x;
                xStopUm = x + scanDistance_;
            }
        } else {
            // centered
            if (scanNegative) {
                xStartUm = x + (scanDistance_ / 2);
                xStopUm = x - (scanDistance_ / 2);
            } else { // the original implementation
                xStartUm = x - (scanDistance_ / 2);
                xStopUm = x + (scanDistance_ / 2);
            }
        }

        xyStage.setFastAxisStart((float) (xStartUm / 1000d));
        xyStage.setFastAxisStop((float) (xStopUm / 1000d));
        xyStage.setSlowAxisStart((float) (y / 1000d));
        xyStage.setSlowAxisStop((float) (y / 1000d));

        zSpeedZero_ = true;  // will turn false if we are doing planar correction
        return false;
        // return preparePlanarCorrectionForAcquisition(); TODO: needed?
    }

    // TODO: implement?
//    public boolean preparePlanarCorrectionForAcquisition() {
//    }


    public boolean cleanUpControllerAfterAcquisition(final AcquisitionSettings settings, final boolean centerPiezos) {
        ASIPLogic plc = (ASIPLogic) devices_.getDevice("TriggerCamera");
        ASIPLogic plc_laser = (ASIPLogic) devices_.getDevice("TriggerLaser");

        // clear "acquisition running" flag on PLC
        plc.setPreset(2);
        plc_laser.setPreset(2);

        final int numViews = settings.getVolumeSettings().numViews();
        final int firstView = settings.getVolumeSettings().firstView();


        if (numViews > 1 || firstView == 0) {
            // boolean success = cleanUpControllerAfterAcquisition_Side(Devices.Sides.A, centerPiezos, 0.0f);
            //boolean success = cleanUpControllerAfterAcquisitionSide(Devices.Sides.A, centerPiezos, 0.0f);
            boolean success = true; // FIXME: call the right method
            if (!success) {
                return false;
            }
        }
        if (numViews > 1 || !(firstView == 0)) {
            //boolean success = cleanUpControllerAfterAcquisition_Side(Devices.Sides.B, centerPiezos, 0.0f);
            boolean success = true;
            if (!success) {
                return false;
            }
        }

        // TODO: message ReportingUtils.logMessage("Finished controller cleanup after acquisition");

        return true;
    }

    public boolean cleanUpControllerAfterAcquisitionSide(final boolean movePiezo, final float piezoPosition) {
        ASIScanner scanner1 = (ASIScanner) devices_.getDevice("Illum1Beam");
        ASIPiezo piezo1 = (ASIPiezo) devices_.getDevice("Imaging1Focus");
        // TODO: getSkipScannerWarnings?

        // make sure SPIM state machine is stopped; device adapter takes care of querying
        scanner1.setSPIMState(ASIScanner.SPIMState.IDLE);

        /// FIXME: BIG SECTION MISSING

        // restore sheet width and offset in case they got clobbered by the code implementing light sheet mode
        //scanner1.sa().setAmplitudeX();
        //final float offsetX = scanner1.sa().getOffsetX();

        // move piezo back to desired position
        if (movePiezo) {
            piezo1.setPosition(piezoPosition);
        }

        // make sure we stop SPIM and SCAN state machines every time we trigger controller (in AcquisitionPanel code)

        return true;
    }

    public boolean setupHardwareChannelSwitching(final AcquisitionSettings settings) {

        MultiChannelModes channelMode = settings.getChannelMode();

        // PLogic can only handle up to 4 channels
        if ((settings.getNumChannels() > 4) &&
                (channelMode == MultiChannelModes.VOLUME_HW || channelMode == MultiChannelModes.SLICE_HW)) {
            // TODO: error: "PLogic card cannot handle more than 4 channels for hardware switching."
            return false;
        }

        ASIPLogic plc = (ASIPLogic) devices_.getDevice("TriggerCamera");
        ASIPLogic plcLaser = (ASIPLogic) devices_.getDevice("TriggerLaser");

        switch (channelMode) {
            case SLICE_HW:
                plcLaser.setPreset(17);
                break;
            case VOLUME_HW:
                if (settings.getVolumeSettings().firstView() == 0) {
                    plcLaser.setPreset(18); // A first
                } else {
                    plcLaser.setPreset(26); // B first
                }
                break;
            default:
                // TODO: error: "Unknown multichannel mode for hardware switching."
                return false;
        }

        switch (settings.getNumChannels()) {
            case 1:
                plcLaser.setPreset(22); // no counter
                break;
            case 2:
                plcLaser.setPreset(21); // mod2 counter
                break;
            case 3:
                plcLaser.setPreset(16); // mod3 counter
                break;
            case 4:
                plcLaser.setPreset(15); // mod4 counter
                break;
            default:
                // TODO: error: "Hardware channel switching only supports 1-4 channels"
                return false;
        }

        // speed things up by turning off updates, will restore value later
        final boolean editCellUpdates = plcLaser.isAutoUpdateCellsOn();
        if (editCellUpdates) {
            plcLaser.setAutoUpdateCells(false);
        }

        // make sure the counters get reset on the acquisition start flag
        // turns out we can only do this for 2-counter and 4-counter implemented with D-flops
        // TODO: figure out alternative for 3-position counter
        if (settings.getNumChannels() != 3) {
            plcLaser.setPointerPosition(counterLSBAddr);
            plcLaser.setCellInput(3, acquisitionFlagAddr + ASIPLogic.addrEdge);
            plcLaser.setPointerPosition(counterMSBAddr);
            plcLaser.setCellInput(3, acquisitionFlagAddr + ASIPLogic.addrEdge);
        }

        if (plcLaser.getShutterMode() == ASIPLogic.ShutterMode.SEVEN_CHANNEL_SHUTTER) {
            // special 7-channel case
            if (plcLaser.getNumCells() < 24) {
                // restore update setting
                plcLaser.setAutoUpdateCells(editCellUpdates);
                // TODO error "Require 24-cell PLC firmware to use hardware channel swiching with 7-channel shutter"
                return false;
            }

            // make sure cells 17-24 are controlling BNCs 1-8
            plcLaser.setPreset(ASIPLogic.Preset.BNC1_8_ON_17_24);

            // now set cells 17-22 so they reflect the counter state used to track state as well as the global laser trigger
            // NB that this only uses 6 lasers (we need 2 free BNCs, BNC#7 for FW trigger and BNC#8 for supplemental X trigger
            for (int laserNum = 1; laserNum < 7; ++laserNum) {
                plcLaser.setPointerPosition(laserNum + 16);
                plcLaser.setCellType(ASIPLogic.CellType.LUT3);
                int lutValue = 0;
                // populate a 3-input lookup table with the combinations of lasers present
                // the LUT "MSB" is the laserTrigger, then the counter MSB, then the counter LSB
                for (int channelNum = 0; channelNum < settings.getNumChannels(); ++channelNum) {
                    if (doesPLogicChannelIncludeLaser(laserNum, settings.getChannels()[channelNum], settings.getChannelGroup())) {
                        lutValue += Math.pow(2, channelNum + 4);  // LUT adds 2^(code in decimal) for each setting, but trigger is MSB of this code
                    }
                }
                plcLaser.setCellConfig(lutValue);
                plcLaser.setCellInput(1, counterLSBAddr);
                plcLaser.setCellInput(2, counterMSBAddr);
                plcLaser.setCellInput(3, laserTriggerAddress);
            }

        } else {
            // original 4-channel case

            // initialize cells 13-16 which control BNCs 5-8
            for (int cellNum = 13; cellNum <= 16; cellNum++) {
                plcLaser.setPointerPosition(cellNum);
                plcLaser.setCellType(ASIPLogic.CellType.AND4);
                plcLaser.setCellInput(2, laserTriggerAddress);
                // note that PLC diSPIM assumes "laser + side" output mode is selected for micro-mirror card
            }

            // identify BNC from the preset and set counter inputs for 13-16 appropriately
            boolean[] hardwareChannelUsed = new boolean[4]; // initialized to all false
            for (int channelNum = 0; channelNum < settings.getNumChannels(); channelNum++) {
                // we already know there are between 1 and 4 channels
                int outputNum = getPLogicOutputFromChannel(settings.getChannels()[channelNum], settings.getChannelGroup());
                // TODO: handle case where we have multiple simultaneous outputs, e.g. outputs 6/7 together
                if (outputNum < 5) {  // check for error in getPLogicOutputFromChannel()
                    // restore update setting
                    plcLaser.setAutoUpdateCells(editCellUpdates);
                    return false;  // already displayed error
                }
                // make sure we don't have multiple Micro-Manager channels using same hardware channel
                if (hardwareChannelUsed[outputNum - 5]) {
                    // restore update setting
                    plcLaser.setAutoUpdateCells(editCellUpdates);
                    // TODO: error "Multiple channels cannot use same laser for PLogic triggering"
                    return false;
                } else {
                    hardwareChannelUsed[outputNum - 5] = true;
                }
                plcLaser.setPointerPosition(outputNum + 8);
                plcLaser.setCellInput(1, ASIPLogic.addrInvert);  // enable this AND4
                // if we are doing per-volume switching with side B first then counter will start at 1 instead of 0
                // the following lines account for this by incrementing the channel number "match" by 1 in this special case
                int adjustedChannelNum = channelNum;
                if (channelMode == MultiChannelModes.VOLUME_HW && !(settings.getVolumeSettings().firstView() == 0)) {
                    adjustedChannelNum = (channelNum + 1) % settings.getNumChannels();
                }
                // map the channel number to the equivalent addresses for the AND4
                // inputs should be either 3 (for LSB high) or 67 (for LSB low)
                //                     and 4 (for MSB high) or 68 (for MSB low)
                final int in3 = (adjustedChannelNum & 0x01) > 0 ? counterLSBAddr : counterLSBAddr + ASIPLogic.addrInvert;
                final int in4 = (adjustedChannelNum & 0x02) > 0 ? counterMSBAddr : counterMSBAddr + ASIPLogic.addrInvert;
                plcLaser.setCellInput(3, in3);
                plcLaser.setCellInput(4, in4);

                // make sure cells 13-16 are controlling BNCs 5-8
                plcLaser.setPreset(ASIPLogic.Preset.BNC5_8_ON_13_16);
            }

        }

        // restore update setting
        plcLaser.setAutoUpdateCells(editCellUpdates);

        return true;
    }

    public boolean triggerControllerStartAcquisition(final AcquisitionModes spimMode) {
        ASIXYStage xyStage = (ASIXYStage) devices_.getDevice("SampleXY");
        ASIScanner scanner1 = (ASIScanner) devices_.getDevice("Illum1Beam");
        switch (spimMode) {
            case STAGE_SCAN:
            case STAGE_SCAN_INTERLEAVED:
                // for stage scan we send trigger to stage card, which sends
                // hardware trigger to the micro-mirror card
                scanner1.setSPIMState(ASIScanner.SPIMState.ARMED);
                xyStage.setScanState(ASIXYStage.ScanState.RUNNING);
                break;
            case NO_SCAN:
                // in actuality only matters which device we trigger if there are
                // two micro-mirror cards, which hasn't ever been done in practice yet
                scanner1.setSPIMState(ASIScanner.SPIMState.RUNNING);
                break;
            default:
                // TODO: error "Unknown acquisition mode"
                return false;
        }
        return true;
    }

    // TODO: implement
    private int getPLogicOutputFromChannel(final ChannelSpec channel, final String channelGroup) {
        return -1;
    }

    // TODO: implement
    private boolean doesPLogicChannelIncludeLaser(final int laserNum, final ChannelSpec channel, final String channelGroup) {
        return false;
    }

}


