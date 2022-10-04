package org.micromanager.lightsheetmanager.gui.components;

import javax.swing.event.DocumentEvent;

@FunctionalInterface
public interface DocumentMethod {
    void run(DocumentEvent event);
}