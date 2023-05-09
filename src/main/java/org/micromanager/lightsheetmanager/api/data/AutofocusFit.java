package org.micromanager.lightsheetmanager.api.data;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The curve fitting algorithm for the general autofocus settings.
 */
public enum AutofocusFit {
    NO_FIT("No fit (take max)"),
    GAUSSIAN("Gaussian");

    private final String text_;

    private static final Map<String, AutofocusFit> stringToEnum =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    AutofocusFit(final String text) {
        this.text_ = text;
    }

    public static String[] toArray() {
        return Arrays.stream(values())
                .map(AutofocusFit::toString)
                .toArray(String[]::new);
    }

    public static AutofocusFit fromString(final String symbol) {
        return stringToEnum.getOrDefault(symbol, AutofocusFit.NO_FIT);
    }

    @Override
    public String toString() {
        return text_;
    }

}
