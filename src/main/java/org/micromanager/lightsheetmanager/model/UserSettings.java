package org.micromanager.lightsheetmanager.model;

import org.micromanager.Studio;
import org.micromanager.UserProfile;
import org.micromanager.propertymap.MutablePropertyMapView;

import java.util.Objects;

public class UserSettings {

    private final String userName;
    private final UserProfile profile;
    private final MutablePropertyMapView settings;

    // JSON key for LightSheetDeviceManager selected device save settings
    private static final String DEVICES_KEY = "Devices";

    private final LightSheetManagerModel model_;

    public UserSettings(final Studio studio, final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        Objects.requireNonNull(studio);
        // setup user profile
        profile = studio.getUserProfile();
        userName = profile.getProfileName();
        settings = profile.getSettings(UserSettings.class);
    }

    /**
     * Returns an object to save and retrieve settings.
     *
     * @return a reference to MutablePropertyMapView
     */
    public MutablePropertyMapView get() {
        return settings;
    }

    /**
     * Returns the name of the user profile.
     *
     * @return a String containing the name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Clears all user settings associated with this class name.
     */
    public void clear() {
        settings.clear();
    }

    /**
     * Load user settings.
     */
    public void load() {
        final String json = settings.getString(DEVICES_KEY, ""); // TODO: better default
        System.out.println("loaded json: " + json);
        // use default settings if no saved data found
        if (!json.equals("")) {
            AcquisitionSettings acqSettings = AcquisitionSettings.fromJson(json);
            model_.acquisitions().setAcquisitionSettings(acqSettings);
            //System.out.println("acqSettings: " + acqSettings);
        }
    }

    /**
     * Save user settings.
     */
    public void save() {
        System.out.println("saved json: " + model_.acquisitions().getAcquisitionSettings().toPrettyJson());
        settings.putString(DEVICES_KEY,
                model_.acquisitions().getAcquisitionSettings().toPrettyJson());
    }

}
