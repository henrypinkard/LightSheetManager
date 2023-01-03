package org.micromanager.lightsheetmanager.api.data;

import org.micromanager.lightsheetmanager.model.data.MultiChannelModes;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static final Map<String, CameraModes> stringToEnum =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    CameraModes(final String text) {
        this.text = text;
    }

    public static CameraModes fromString(final String symbol) {
        return stringToEnum.getOrDefault(symbol, CameraModes.INTERNAL);
    }

    public static CameraModes getByIndex(final int index) {
        return values()[index];
    }

    public static String[] toArray() {
        return Arrays.stream(values())
                .map(CameraModes::toString)
                .toArray(String[]::new);
    }

    @Override
    public String toString() {
        return text;
    }

}
