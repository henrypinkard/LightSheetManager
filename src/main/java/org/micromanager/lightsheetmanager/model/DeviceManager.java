package org.micromanager.lightsheetmanager.model;

import org.micromanager.lightsheetmanager.api.data.CameraLibrary;
import mmcorej.CMMCore;
import mmcorej.DeviceType;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.model.devices.DeviceBase;
import org.micromanager.lightsheetmanager.model.devices.Galvo;
import org.micromanager.lightsheetmanager.model.devices.LightSheetDeviceManager;
import org.micromanager.lightsheetmanager.model.devices.Stage;
import org.micromanager.lightsheetmanager.model.devices.XYStage;
import org.micromanager.lightsheetmanager.model.devices.cameras.AndorCamera;
import org.micromanager.lightsheetmanager.model.devices.cameras.DemoCamera;
import org.micromanager.lightsheetmanager.model.devices.cameras.HamamatsuCamera;
import org.micromanager.lightsheetmanager.model.devices.cameras.PCOCamera;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPLogic;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPiezo;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIXYStage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

//TODO: merge this with LightSheetDeviceManager

/**
 * A utility for extracting information from LightSheetDeviceManager.
 * <p>
 * The pre-init properties are cached as enums and integers.
 *
 */
public class DeviceManager {

    private final Studio studio_;
    private final CMMCore core_;

    private Map<String, DeviceBase> deviceMap_;
    private Map<String, DeviceBase> devicesAdded_;

   // private static final String deviceName = "LightSheetDeviceManager";

    public DeviceManager(final Studio studio) {
        studio_ = Objects.requireNonNull(studio);
        core_ = studio_.core();

        // TODO: ConcurrentHashMap or HashMap?
        deviceMap_ = new ConcurrentHashMap<>();
        devicesAdded_ = new HashMap<>();
    }

    /**
     * Creates the device map from the device adapter properties.
     * <p>
     * Properties that are set to "Undefined" are ignored.
     */
    public void setup() {
        studio_.logs().logMessage("DeviceManager: [Begin Setup]");

        // make sure this is empty
        devicesAdded_.clear();

        // always add an entry for the device adapter
        LightSheetDeviceManager lsm = new LightSheetDeviceManager(studio_);
        lsm.getPreInitProperties();

        deviceMap_.put("LightSheetDeviceManager", lsm);

        // keep track of device we have already added to the map
        // used when multiple properties are mapped to the same device
        //HashMap<String, DeviceBase> devicesAdded = new HashMap<>();

        String[] props = lsm.getDevicePropertyNames();
        String[] properties = lsm.getEditableProperties(props);

        for (String propertyName : properties) {
            final String deviceName = lsm.getProperty(propertyName);

            // skip properties that don't have a device assigned
            if (deviceName.equals(LightSheetDeviceManager.UNDEFINED)) {
                continue;
            }

            final DeviceType deviceType = getDeviceType(deviceName);
            final String deviceLibrary = getDeviceLibrary(deviceName);

            System.out.println(propertyName + " " + deviceType);

            // skip properties with no known DeviceType
            if (deviceType == DeviceType.UnknownType) {
                continue;
            }

            // object was already created so grab a reference to it
            if (devicesAdded_.containsKey(deviceName)) {
                deviceMap_.put(propertyName, devicesAdded_.get(deviceName));
                final String className = devicesAdded_.get(deviceName).getClass().getSimpleName();
                studio_.logs().logMessage("DeviceManager: " + propertyName + " set to "
                        + className + "(" + deviceName + ") (reused)");
                continue;
            }

            // add device objects to the device map
            if (deviceType == DeviceType.XYStageDevice) {
                if (deviceLibrary.equals("ASITiger")) {
                    ASIXYStage xyStage = new ASIXYStage(studio_, deviceName);
                    addDevice(propertyName, deviceName, xyStage);
                } else {
                    // generic XY stage device
                    XYStage xyStage = new XYStage(studio_, deviceName);
                    addDevice(propertyName, deviceName, xyStage);
                }
            } else if (deviceType == DeviceType.StageDevice) {
                if (deviceLibrary.equals("ASITiger")) {
                    if (deviceName.contains("Piezo")) {
                        ASIPiezo piezo = new ASIPiezo(studio_, deviceName);
                        addDevice(propertyName, deviceName, piezo);
                    }
                    // TODO: ASIZStage
                } else {
                    // generic stage device
                    Stage stage = new Stage(studio_, deviceName);
                    addDevice(propertyName, deviceName, stage);
                }
            } else if (deviceType == DeviceType.GalvoDevice) {
                if (deviceLibrary.equals("ASITiger")) {
                    ASIScanner scanner = new ASIScanner(studio_, deviceName);
                    addDevice(propertyName, deviceName, scanner);
                } else {
                    // use generic galvo device
                    Galvo galvo = new Galvo(studio_);
                    addDevice(propertyName, deviceName, galvo);
                }
            } else if (deviceType == DeviceType.ShutterDevice) {
                // TODO: support a generic device
                // Check if ASI PLogic is present
                if (deviceLibrary.equals("ASITiger")) {
                    ASIPLogic plc = new ASIPLogic(studio_, deviceName);
                    addDevice(propertyName, deviceName, plc);
                }
            } else if (deviceType == DeviceType.CameraDevice) {
                createCameraDevice(propertyName, deviceName,
                        CameraLibrary.fromString(deviceLibrary));
            }
            //deviceMap_.put(propertyName, "");
        }
        System.out.println("----------------");

        // we don't need this array anymore
        devicesAdded_.clear();

        studio_.logs().logMessage("DeviceManager: [End Setup]");
    }

    private void addDevice(final String propertyName, final String deviceName, final DeviceBase device) {
        deviceMap_.put(propertyName, device);
        devicesAdded_.put(deviceName, device);
        studio_.logs().logMessage("DeviceManager: " + propertyName + " set to "
                + device.getClass().getSimpleName() + "(" + deviceName + ")");
    }

    private void createCameraDevice(final String propertyName, final String deviceName, CameraLibrary cameraLibrary) {
        switch (cameraLibrary) {
            case ANDORSDK3:
                AndorCamera andorCamera = new AndorCamera(studio_, deviceName);
                addDevice(propertyName, deviceName, andorCamera);
                System.out.println("Added Andor Camera: " + deviceName);
                break;
            case HAMAMATSU:
                HamamatsuCamera hamaCamera = new HamamatsuCamera(studio_, deviceName);
                addDevice(propertyName, deviceName, hamaCamera);
                break;
            case PCOCAMERA:
                PCOCamera pcoCamera = new PCOCamera(studio_, deviceName);
                addDevice(propertyName, deviceName, pcoCamera);
                break;
            case DEMOCAMERA:
                DemoCamera demoCamera = new DemoCamera(studio_, deviceName);
                addDevice(propertyName, deviceName, demoCamera);
                break;
            default:
                studio_.logs().showError("No device type match!");
                // TODO: error "Camera library not supported"
                break;
        }
    }

    private void createShutterDevice() {

    }

    private void createXYStageDevice() {

    }

    // Note: clients should use var when we support Java 11
    public DeviceBase getDevice(final String deviceName) {
        return deviceMap_.get(deviceName);
    }

    /**
     * Returns the device given by {@code deviceName} as type {@code T}.
     * The client is responsible for assigning the returned
     * value to the correct type.
     * <P><P>
     * Typesafe: The client can only cast the return value to a subclass
     * of DeviceBase, avoiding the ClassCastException at compile time.
     *
     * @param deviceName the device name
     * @return the device as a subclass of DeviceBase
     * @param <T> the generic type to cast the result to
     */
    @SuppressWarnings("unchecked")
    public <T extends DeviceBase> T getDevice2(final String deviceName) {
        return (T) deviceMap_.get(deviceName);
    }

    public DeviceBase getImagingCamera() {
        return deviceMap_.get("ImagingCamera");
    }

    public DeviceBase getImagingCamera(final int side) {
        return deviceMap_.get("Imaging" + side + "Camera");
    }

    public DeviceBase getImagingCamera(final int side, final int num) {
        return deviceMap_.get("Imaging" + side + "Camera" + num);
    }

    public LightSheetDeviceManager getDeviceAdapter() {
        return (LightSheetDeviceManager)deviceMap_.get("LightSheetDeviceManager");
    }

    public String getDeviceLibrary(final String deviceName) {
        String result = "";
        try {
            result = core_.getDeviceLibrary(deviceName);
        } catch (Exception e) {
            studio_.logs().logError(e.getMessage());
        }
        return result;
    }

    private DeviceType getDeviceType(final String deviceName) {
        try {
            return core_.getDeviceType(deviceName);
        } catch (Exception e) {
            return DeviceType.UnknownType;
        }
    }

}
