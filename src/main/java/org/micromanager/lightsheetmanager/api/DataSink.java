package org.micromanager.lightsheetmanager.api;

import org.micromanager.data.Image;

public interface DataSink {

    void putImage(final Image image);
}
