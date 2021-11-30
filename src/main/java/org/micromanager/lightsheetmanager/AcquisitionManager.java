package org.micromanager.lightsheetmanager;

public interface AcquisitionManager {

    /**
     * Request that an acquisition is run.
     */
    void requestRun();

    /**
     * Request the running acquisition to stop.
     */
    void requestStop();

    /**
     * Request the running acquisition to pause.
     */
    void requestPause();

    /**
     * Returns true if the acquisition is running.
     *
     * @return true if the acquisition is running
     */
    boolean isRunning();
  
}
