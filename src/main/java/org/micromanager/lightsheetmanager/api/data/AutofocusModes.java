package org.micromanager.lightsheetmanager.api.data;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The autofocus mode for the general autofocus settings.
 */
public enum AutofocusModes {
    FIXED_PIEZO_SWEEP_SLICE("Fixed piezo, sweep slice"),
    FIXED_SLICE_SWEEP_PIEZO("Fixed slice, sweep piezo");

    private final String name;

    private static final Map<String, AutofocusModes> stringToEnum =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    AutofocusModes(final String name) {
        this.name = name;
    }

    public static String[] toArray() {
        return Arrays.stream(values())
                .map(AutofocusModes::toString)
                .toArray(String[]::new);
    }

    public static AutofocusModes fromString(final String symbol) {
        return stringToEnum.getOrDefault(symbol, AutofocusModes.FIXED_PIEZO_SWEEP_SLICE);
    }

    @Override
    public String toString() {
        return name;
    }

}
