package org.micromanager.lightsheetmanager.model.devices.vendor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DoItType {
    DO_IT("Do it"),
    NOT_DONE("Not done"),
    DONE("Done");

    private final String text_;

    private static final Map<String, DoItType> stringToEnum =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    DoItType(final String text) {
        text_ = text;
    }

    @Override
    public String toString() {
        return text_;
    }

    public static DoItType fromString(final String symbol) {
        return stringToEnum.getOrDefault(symbol, DoItType.NOT_DONE);
    }
}