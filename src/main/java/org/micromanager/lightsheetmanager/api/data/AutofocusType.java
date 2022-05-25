package org.micromanager.lightsheetmanager.api.data;

public enum AutofocusType {
    NONE("None"),
    EDGES("Edges"),
    STD_DEV("StdDev"),
    MEAN("Mean"),
    NORMALIZED_VARIANCE("NormalizedVariance"),
    SHARP_EDGES("Sharp Edges"),
    REDONDO("Redondo"),
    VOLATH("Volath"),
    VOLATH5("Volath5"),
    MEDIAN_EDGES("MedianEdges"),
    FFT_BANDPASS("FFTBandpass"),
    TENENGRAD("Tenengrad");

    private final String name;

    AutofocusType(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}