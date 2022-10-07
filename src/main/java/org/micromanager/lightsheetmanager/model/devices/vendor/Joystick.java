package org.micromanager.lightsheetmanager.model.devices.vendor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Joystick {

    public enum JoystickInput {
        NONE("0 - None"),
        JOYSTICK_X("2 - joystick X"),
        JOYSTICK_Y("3 - joystick Y"),
        RIGHT_WHEEL("22 - right wheel"),
        LEFT_WHEEL("23 - left wheel");

        private final String text_;

        private static final Map<String, JoystickInput> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

        JoystickInput(final String text) {
            text_ = text;
        }

        @Override
        public String toString() {
            return text_;
        }

        public static JoystickInput fromString(final String symbol) {
            return stringToEnum.getOrDefault(symbol, JoystickInput.NONE);
        }
    }
}
