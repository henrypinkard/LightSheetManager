package org.micromanager.lightsheetmanager.gui.components;

import java.util.EventObject;

@FunctionalInterface
public interface Method {
    void run(EventObject event);
}