package org.micromanager.lightsheetmanager.utils;

import java.util.EventObject;

@FunctionalInterface
public interface WindowEventMethod {
    void run(EventObject event);
}
