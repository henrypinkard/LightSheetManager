package org.micromanager.lightsheetmanager.api.data;

/**
 * Camera trigger modes.
 */
public enum CameraModes {
    INTERNAL("Internal"),
    EDGE("Edge Trigger"),
    OVERLAP("Overlap/Synchronous"),
    LEVEL("Level Trigger"),
    PSEUDO_OVERLAP("Pseudo Overlap"),
    VIRTUAL_SLIT("Virtual Slit");

    private final String text;

    CameraModes(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
