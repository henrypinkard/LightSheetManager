package org.micromanager.lightsheetmanager.gui.utils;

import java.util.EventObject;

@FunctionalInterface
public interface WindowEventMethod {
    void run(EventObject event);
}
