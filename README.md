## LightSheetManager

LightSheetManager is a plugin for [micro-manager](https://micro-manager.org) that aims to provide an easy interface to all light sheet microscopes, be they completely custom-built or assembled from commercial parts.  Development of this plugin was started by [ASI](https:////www.asiimaging.com) in collaboration with the Micro-Manager team. 

Our basic goals are as follows:
- Leverage the robust foundation of Micro-Manager, while addressing shortcomings specific to light sheet imaging (may necessitate tweaks to Micro-Manager itself).
- Be easily used via Python and other programmming languages by being API-centric (while a GUI is planned it should be straightforward for others to code GUIs in other languages that leverage the business logic of LightSheetManager)
- Be a community effort with contributions from many individuals and institutions.


**High level overview of the design of this code:**

The main units are "Setting" and "Manager" interfaces. Settings are always immutable (i.e., constructed using Builders, and can not be modified). Settings can be handed to Managers to execute a task given those Settings.

The top level Manager is the "LightSheetManager".  Most likely there is never more than one instance. Getting access to LightSheetManager should enable one to execute everything that can be done with the API.

The AcquisitionManager runs acquisitions.  It is created using a Builder, each acquisition get its own AcquisitionManager. Once built, it can start an acquisition (only once), returns status, pause acquisition, unpause, change acquisition settings (only at certain points), and stop acquisition. It sends its data to a DataSink (see below). The AcquisitionBuilder will need AcquisitionSettings, a DataSink, and MMCore.
LightSheeManager ensures that only one AcquisitionManager can run at any one time.  This may involve a "baton" that an entity needs to hold to be allowed to do things with the hardware.

DataSink interface should be very thin, maybe only a "putImage()" function.  its implementation should be able to send images into a processing pipeline, and eventually into a Datastore. It would be good to keep the interface simple, so that it can easily be implemented outside of MM.

A HardwareManager is used to set the hardware to a desired state.  It takes Settings that describe the "Calibration" of the system, for instance, a formula describing how to relate Z-movement with angle from the other side.

LightSheetManager will have a dedicated device adapter which will have a pre-init property called "MicroscopeGeometry" (or similar name).  Possible values for "MicroscopeGeometry" include diSPIM with piezos, iSPIM with piezo, ct-dSPIM, SOLS galvo-scan, SOLS stage-scan, mesoSPIM, OpenSPIM, etc.  Depending on the value of this property other properties are created that connect  logical and physical devices like is done with MultiCamera and other Utility devices.  It is possible to specify "None" for the physical device setting, but many values of "None" indicate that a new possible value for "MicroscopeGeometry" should be added.

Some settings objects:
- AcquisitionSettings contains things like number of images, step size, channels used, whether multi-position, file name, etc.
- CalibrationSettings describe an imaging path e.g. cross-calibration light sheet with detection objective, scan width, center position, etc.
- AutofocusSettings contain information to run autofocus 

Some manager objects:
- AutofocusManager takes AutofocusSettings and produces a CalibrationSettings object.
- HardwareManager takes at least one  Calibration object.  Can be used by the GUI to find the right settings, and it can also be used by the AcquisitionManager.  

TODO The current CalibrationSettings object needs to be split into Calibration Settings and a HardwareManager.
