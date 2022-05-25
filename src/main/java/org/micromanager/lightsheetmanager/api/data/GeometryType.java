package org.micromanager.lightsheetmanager.api.data;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum GeometryType {
    UNKNOWN("Unknown"),
    DISPIM("diSPIM"),
    ISPIM("iSPIM"),
    OSPIM("oSPIM"),
    MESOSPIM("mesoSPIM"),
    OPENSPIML("OpenSPIM-L"),
    SCAPE("SCAPE");

    private static final Map<String, GeometryType> stringToEnum =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    private final String label;

    GeometryType(final String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static GeometryType fromString(final String symbol) {
        return stringToEnum.getOrDefault(symbol, GeometryType.UNKNOWN);
    }
}
