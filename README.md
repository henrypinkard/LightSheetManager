## LightSheetManager

LightSheetManager is a plugin for [micro-manager](https://micro-manager.org) that aims to provide an easy to interface to all light sheet microscopes, be they completely custom-build or assembled from commercial parts.  Development of this plugin was started by [ASI](https:////www.asiimaging.com) in collaboration with the Micro-Manager team. 



**High level overview of the design of this code:**

The main units are "Setting" and "Manager" interfaces. Settings are always immutable (i.e., constructed using Builders, and can not be modified). Settings can be handed to Managers to execute a task given those Settings.

The top level Manager is the "LightSheetManager".  Most likely there is never more than one instance. Getting access to LightSheetManager should enable one to execute everything that can be done with the API.

The AcquisitionManager runs acquisitions.  It is created using a Builder, each acquisition get its own AcquisitionManager. Once build, it can start an acquisition (only once), returns status, pause acquisition, unpause, change acquisition settings (only at certain points), and stop acquisition. It sends its data to a DataSink (see below). The AcquisitionBuilder will need AcquisitionSettings, a DataSink, and MMCore.
LightSheeManager ensures that only one AcquisitionManager can run at any one time.  This may involve a "baton" that an entity needs to hold to be allowed to do things with the hardware.

DataSink interface should be very thin, maybe only a "putImage()" function.  its implementation should be able to send images into a processing pipeline, and eventually into a Datastore. It would be good to keep the interface simple, so that it can easily be implemented outside of MM.

A HardwareManager is used to set the hardware to a desired state.  It takes Settings that describe the "Calibration" of the system, for instance, a formula describing how to relate Z-movement with angle from the other side.

I have the following notes about CalibrationSettings, and AutofocusSettings/Manager, but I am confused about the exact roles of these:

CalibrationSettings are the settings describing how to execute a Calibration (or Autofocus?)

AutofocusManager takes AutofocusSettings and produces a Calibration object.

CalibrationSettings
    Both Settings directing how to execute a calibration
    Results of a Calibration

AutofocusManager takes AutofocusSettings and produces a Calibration Object.

HardwareManager takes a Calibration object.  Can be used by the GUI to find the right settings, and it can also be used by the AcquisitionManager.  

The current Calibration object needs to be split into Calibration Settings and a HardwareManager.
