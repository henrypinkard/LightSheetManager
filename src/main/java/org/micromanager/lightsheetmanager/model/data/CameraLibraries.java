package org.micromanager.lightsheetmanager.model.data;

public enum CameraLibraries {
    HAMCAM("HamamatsuHam"),
    PCOCAM("PCO_Camera"),
    ANDORCAM("AndorSDK3"),
    PVCAM("PVCAM"),
    DEMOCAM("DemoCamera");

    private final String text;

    CameraLibraries(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
